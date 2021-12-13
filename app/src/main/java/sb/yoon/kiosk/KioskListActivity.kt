package sb.yoon.kiosk

import sb.yoon.kiosk.controller.HttpNetworkController
import androidx.appcompat.app.AppCompatActivity
import sb.yoon.kiosk.model.CartMenu
import sb.yoon.kiosk.controller.CartListAdapter
import sb.yoon.kiosk.controller.DbQueryController
import com.google.android.material.tabs.TabLayout
import android.widget.ToggleButton
import android.widget.TextView
import android.app.PendingIntent
import android.hardware.usb.UsbManager
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbDeviceConnection
import android.hardware.usb.UsbEndpoint
import android.os.Bundle
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.LinearLayoutManager
import android.os.Parcelable
import android.widget.Toast
import android.view.LayoutInflater
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import androidx.annotation.RequiresApi
import android.os.Build
import android.content.*
import android.graphics.Color
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.google.gson.GsonBuilder
import org.json.JSONObject
import org.json.JSONArray
import com.google.gson.reflect.TypeToken
import org.json.JSONException
import sb.yoon.kiosk.libs.*
import sb.yoon.kiosk.model.Category
import java.io.IOException
import java.lang.Exception
import java.net.InetSocketAddress
import java.net.Socket
import java.net.SocketAddress
import java.util.*

//배경색 #081832 == 11호관 마크 색상으로 추정
class KioskListActivity : AppCompatActivity() {
    private var fragmentManager: FragmentManager? = null
    private var fragmentTransaction: FragmentTransaction? = null
    private var itemListFragment: ItemListFragment? = null
    private var categories: List<Category> = ArrayList<Category>()
    private var cartMenuList: MutableList<CartMenu> = ArrayList()
    private var cartListAdapter: CartListAdapter? = null

    // 프론트에서 쓰기 편하게 DB 관련 메서드들 제공
    private var dbQueryController: DbQueryController? = null
    private var tagNum = 0
    private var categoryPage = 0
    var categorySize = 0
    var maxCategoryPage = 0f
    var searchButtonClicked = false
    @JvmField
    var menuOptionPopupButtonClicked = false
    private var categoryTab: TabLayout? = null
    private val toggleButtons = ArrayList<ToggleButton>()
    private var leftButton: Button? = null
    private var rightButton: Button? = null
    val eleSize = 4
    private var idleTimer: IdleTimer? = null
    private var totalPriceView: TextView? = null
    private val CARD_INTENT_NUM = 811
    private var i2: Intent? = null
    var statusThread: Thread? = null
    var status: TextView? = null
    var count: TextView? = null
    var readBuffer: ByteArray? = null
    var readBufferPosition = 0
    var prt_count = 0
    var permissionIntent: PendingIntent? = null
    private var mConnection: UsbDeviceConnection? = null
    private var mUsbManager: UsbManager? = null
    private var mDevice: UsbDevice? = null
    private var mEndpointBulk: UsbEndpoint? = null

    //------------------------------------------------------
    private var card_approval_num: String? = null
    private var card_approval_date: String? = null
    private var httpController: HttpNetworkController? = null
    private val purchaseButton: Button? = null
    private var totalPrice = 0
    private var card_name: String? = null
    private var acc_name: String? = null
    private var card_num: String? = null

    companion object {
        @JvmField
        var statusloop = true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_kiosk_main)
        val kioskApplication = application as KioskApplication
        kioskApplication.kioskListActivity = this
        httpController = HttpNetworkController(
                this@KioskListActivity, resources.getString(R.string.server_ip))

        // DB 컨트롤러 (프론트에서 쓸 메서드들 모음)
        dbQueryController = kioskApplication.dbQueryController

        // DB 초기화
        // dbQueryController.initDB();

        // 카테고리 리스트 불러오기
        categories = dbQueryController!!.getCategoriesList()

        //버튼 객체 미리 생성
        tagNum = 0
        categorySize = categories.size

        // 카테고리 버튼들 생성
        categoryTab = findViewById(R.id.categoryTab)
        categoryTab!!.addOnTabSelectedListener(categoryTabClickListener())
        categoryTab!!.setSelectedTabIndicatorColor(Color.parseColor("#FFFFFF")) // 언더바 나오길래 안보이도록 만들었습니다.
        leftButton = findViewById(R.id.left_button)
        rightButton = findViewById(R.id.right_button)
        maxCategoryPage = (categorySize / eleSize).toFloat()


        // 기본으로 보여줄 플래그먼트 (첫번째 카테고리)
        fragmentManager = supportFragmentManager
        itemListFragment = ItemListFragment(dbQueryController!!.getMenuList(categories.get(0)))
        fragmentTransaction = fragmentManager!!.beginTransaction()
        fragmentTransaction!!.replace(R.id.list_fragment, itemListFragment!!).commitAllowingStateLoss()

        // 카트 리사이클러뷰
        val cartRecyclerView = findViewById<RecyclerView>(R.id.cart_recycler_list)
        val mLinearLayoutManager = LinearLayoutManager(this)
        mLinearLayoutManager.orientation = LinearLayoutManager.HORIZONTAL
        cartRecyclerView.layoutManager = mLinearLayoutManager
        cartListAdapter = CartListAdapter(cartMenuList)
        cartRecyclerView.adapter = cartListAdapter

        // 계산버튼 리스너
        var purchaseButton = findViewById<Button>(R.id.purchase_button)
        purchaseButton.setOnClickListener(purchaseButtonClickListener())
        updateCategoryTab(categoryPage)
        totalPriceView = findViewById(R.id.total_price)
        idleTimer = IdleTimer(this, 150000, 1000) //잠깐만 115초로 변경좀 해놓겠습니다. -jojo
        idleTimer!!.start()
        try {
            //Printer-------------------------------------------
            mUsbManager = getSystemService(USB_SERVICE) as UsbManager
            permissionIntent = PendingIntent.getBroadcast(this, 0, Intent(ACTION_USB_PERMISSION), 0)
            val filter = IntentFilter(ACTION_USB_PERMISSION)
            registerReceiver(usbReceiver, filter)
            mConnection = setDevice(this@KioskListActivity).second
            mUsbManager!!.requestPermission(mDevice, permissionIntent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    //Printer ---------------------------------------------------
    private val usbReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            if (ACTION_USB_PERMISSION == action) {
                synchronized(this) {
                    val device = intent.getParcelableExtra<Parcelable>(UsbManager.EXTRA_DEVICE) as UsbDevice?
                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                        if (device != null) {
                            //status_thread();
                        } else {
                            Log.d(TAG, "permission denied for device$device")
                        }
                    }
                }
            }
            if (UsbManager.ACTION_USB_ACCESSORY_DETACHED == action) {
                val device = intent.getParcelableExtra<Parcelable>(UsbManager.EXTRA_DEVICE) as UsbDevice?
                if (device != null) {
                    Log.e("error", "디바이스가 없습니다")
                }
            }
        }
    }

    /***
     * 응답 없을 때 자동으로 메인화면으로 나가게
     */
    override fun onUserInteraction() {
        idleTimer!!.cancel()
        idleTimer!!.start()
        super.onUserInteraction()
    }

    /***
     * 카테고리 탭 업데이트
     */
    private fun updateCategoryTab(page: Int) {
        Log.d("page", page.toString())
        categoryTab!!.removeAllTabs()
        val limit = page * eleSize + eleSize
        toggleButtons.clear()
        for (i in page * eleSize until limit) {
            if (i >= categorySize) {
                break
            }
            val tab = categoryTab!!.newTab()
            tab.customView = createTabView(categories[i].name, i)
            categoryTab!!.addTab(tab.setText(categories[i].name))
        }
        leftButton!!.isEnabled = page != 0
        rightButton!!.isEnabled = maxCategoryPage > page
    }

    fun updateCategoryTab(view: View) {
        if (view.id == R.id.left_button) {
            categoryPage -= 1
            updateCategoryTab(categoryPage)
        } else if (view.id == R.id.right_button) {
            categoryPage += 1
            updateCategoryTab(categoryPage)
        }
        itemListFragment = ItemListFragment(dbQueryController!!.getMenuList(categories[categoryPage * eleSize]))
        fragmentTransaction = fragmentManager!!.beginTransaction()
        fragmentTransaction!!.replace(R.id.list_fragment, itemListFragment!!).commitAllowingStateLoss()
        toggleButtons[0].text = categories[categoryPage * eleSize].name
    }

    private fun createTabView(tabName: String, tag: Int): View {
        val tabView = LayoutInflater.from(this).inflate(R.layout.custom_tab, null)
        val toggleButton = tabView.findViewById<ToggleButton>(R.id.txt_name)
        toggleButton.isChecked = false
        toggleButton.setBackgroundColor(Color.parseColor("#ffffff"))
        toggleButton.setTextColor(Color.parseColor("#081832"))
        toggleButton.text = tabName
        toggleButton.textOff = tabName
        toggleButton.textOn = tabName
        toggleButton.tag = tag
        toggleButton.setOnClickListener {
            val tagNo = it.tag as Int
            categoryTab!!.getTabAt(tagNo % eleSize)!!.select()
        }
        toggleButtons.add(toggleButton)
        return tabView
    }

    fun clickReturnButton() {
        finish()
    }

    private fun updateCartTotalPrice() {
        var uTotalPrice = 0
        val totalPriceView = findViewById<TextView>(R.id.total_price)
        for (cartMenu in cartMenuList) {
            uTotalPrice += cartMenu.totalPrice
        }
        totalPriceView.tag = uTotalPrice
        val text = Integer.toString(uTotalPrice) + " 원"
        totalPriceView.text = text
        totalPriceView.textSize = 70f
        totalPrice = uTotalPrice
    }

    fun delCartMenuList(position: Int) {
        cartMenuList.removeAt(position)
        cartListAdapter!!.notifyDataSetChanged()
        updateCartTotalPrice()
    }

    fun addCartMenuList(cartMenu: CartMenu) {
        cartMenuList.add(cartMenu)
        cartListAdapter!!.notifyDataSetChanged()
        updateCartTotalPrice()
    }

    fun setCartMenuList(cartMenuList: MutableList<CartMenu>) {
        this.cartMenuList = cartMenuList
        cartListAdapter!!.notifyDataSetChanged()
        Log.d("카트에는: ", this.cartMenuList.toString())
        updateCartTotalPrice()
    }

    fun clickSearchIcon(view: View?) {
        Log.d("검색버튼", java.lang.Boolean.toString(searchButtonClicked))
        if (searchButtonClicked) return
        searchButtonClicked = true
        val intent = Intent(this@KioskListActivity, SearchActivity::class.java)
        startActivityForResult(intent, 2)
    }

    private fun setAdapter() {
        // 인덱스 표시 어댑터 설정
        cartListAdapter = CartListAdapter(cartMenuList)
    }

    // 버튼 누르면 버튼의 순서를 확인하고 플래그먼트에 삽입
    internal inner class categoryTabClickListener : OnTabSelectedListener {
        override fun onTabSelected(tab: TabLayout.Tab) {
            val tagNo = tab.position
            itemListFragment = ItemListFragment(dbQueryController!!.getMenuList(categories!![tagNo + eleSize * categoryPage]))
            fragmentTransaction = fragmentManager!!.beginTransaction()
            fragmentTransaction!!.replace(R.id.list_fragment, itemListFragment!!).commitAllowingStateLoss()
            for (i in toggleButtons.indices) {
                val index = i + categoryPage * eleSize
                toggleButtons[i].isChecked = false
                toggleButtons[i].setBackgroundColor(Color.parseColor("#ffffff"))
                toggleButtons[i].setTextColor(Color.parseColor("#081832"))
                toggleButtons[i].text = categories!![index].name
            }
            toggleButtons[tagNo].isChecked = true
            toggleButtons[tagNo].setBackgroundResource(R.drawable.togglebutton_on)
            toggleButtons[tagNo].setTextColor(Color.parseColor("#ffffff"))
            toggleButtons[tagNo].text = categories!![tagNo + eleSize * categoryPage].name
        }

        override fun onTabUnselected(tab: TabLayout.Tab) {}
        override fun onTabReselected(tab: TabLayout.Tab) {}
    }

    internal inner class purchaseButtonClickListener : View.OnClickListener {
        override fun onClick(view: View) {
            if (cartMenuList.isEmpty()) return
            println("결제버튼: 클릭함")
            HttpCheckThread().start()
            view.isEnabled = false
        }
    }

    internal inner class HttpCheckThread : Thread() {
        override fun run() {
            try {
                val sockaddr: SocketAddress = InetSocketAddress("192.168.0.15", 8080)
                // Create an unbound socket
                val sock = Socket()

                // This method will block no more than timeoutMs.
                // If the timeout occurs, SocketTimeoutException is thrown.
                val timeoutMs = 2000 // 2 seconds
                sock.connect(sockaddr, timeoutMs)
                doPurchase()
            } catch (e: IOException) {
                connectionFailed()
            }
        }
    }

    fun connectionFailed() {
        Toast.makeText(this.applicationContext, "서버와 연결이 불가능합니다. 직원에게 문의하세요/", Toast.LENGTH_SHORT).show()
        finish()
    }

    fun doPurchase() {
        //custom dialog
        val dialogView = layoutInflater.inflate(R.layout.dialog_custom, null)
        val price_tv = dialogView.findViewById<TextView>(R.id.price_text)
        val totalPriceView = findViewById<TextView>(R.id.total_price)
        price_tv.text = totalPriceView.text.toString()
        setTranData("D1", "")
    }

    // 결제 내용 카드리더기로
    fun setTranData(tran_types: String, ADD_FIELD: String?) {
        val compName = ComponentName("kr.co.kicc.easycarda", "kr.co.kicc.easycarda.CallPopup")
        val intent = Intent(Intent.ACTION_MAIN)
        intent.putExtra("TRAN_NO", "1") // 거래 구분을 위한 용도
        intent.putExtra("TRAN_TYPE", tran_types) // 거래 타입. 'D1' 승인. 'D4' 거래 취소
        if ("M8" == tran_types) {                                                // 포인트 조회 용도. 필요 없음
            intent.putExtra("TERMINAL_TYPE", "CE")
            intent.putExtra("TEXT_DECLINE", "포인트 조회가 거절되었습니다")
        } else {
            intent.putExtra("TERMINAL_TYPE", "40") // '40' = 일반 거래
        }
        intent.putExtra("TOTAL_AMOUNT", totalPriceView!!.tag.toString()) // 총 금액
        intent.putExtra("TAX", "0") // 세금 금액
        intent.putExtra("TAX_OPTION", "A") // M = manual. 입력값으로 처리.  A = auto. 10% 자동으로 계산
        intent.putExtra("TIP", "0") // 팁 금액
        //intent.putExtra("TIP_OPTION","N");
//        if("D4".equals(tran_types)||"B2".equals(tran_types) || "B4".equals(tran_types)) {	// 필요 없는 부분
//            intent.putExtra("APPROVAL_NUM", this.appr_num.getText().toString());
//            intent.putExtra("APPROVAL_DATE", this.appr_date.getText().toString());
//            intent.putExtra("TRAN_SERIALNO", this.tran_serialno.getText().toString());
//        }
        if ("B1" == tran_types || "B2" == tran_types || "B3" == tran_types || "B4" == tran_types) { // 필요 없는 부분
            intent.putExtra("CASHAMOUNT", "00")
        } else {
            intent.putExtra("INSTALLMENT", "0") // 할부여부. 0 = 일시불
        }

        intent.putExtra("ADD_FIELD", ADD_FIELD) // 모르겠음
        intent.putExtra("TIMEOUT", "80") // 아마 결제 시 타임아웃 지정
        intent.putExtra("TEXT_PROCESS", "결제 진행중입니다") // 결제 진행중 문구
        intent.putExtra("TEXT_COMPLETE", "결제가 완료되었습니다") // 결제 완료시 문구

        // 테마 커스터마이징
        intent.putExtra("IMG_BG_PATH", "/sdcard/kicc/background_kicc.png")
        intent.putExtra("IMG_CARD_PATH", "/sdcard/kicc/card_kicc.png")
        intent.putExtra("IMG_CLOSE_PATH", "/sdcard/kicc/close_kicc.png")
        intent.putExtra("TEXT_MAIN_SIZE", 18)
        intent.putExtra("TEXT_MAIN_COLOR", "#303030")
        intent.putExtra("TEXT_SUB1_SIZE", 12)
        intent.putExtra("TEXT_SUB1_COLOR", "#ff752a")
        intent.putExtra("TEXT_SUB2_SIZE", 10)
        intent.putExtra("TEXT_SUB2_COLOR", "#ff752a")
        intent.putExtra("TEXT_SUB3_SIZE", 16)
        intent.putExtra("TEXT_SUB3_COLOR", "#909090")

        intent.component = compName
        printInent(intent)
        startActivityForResult(intent, CARD_INTENT_NUM)
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    fun popUpOrderNumberAndQuit(orderNumber: Int, orderSucceess: Boolean) {
        i2!!.putExtra("orderNumber", orderNumber)
        startActivity(i2)
        //Todo
        if (orderSucceess) {
            onClick_usb_text2(
                totalPrice = this.totalPrice,
                orderNumber = orderNumber,
                cartMenuList = this.cartMenuList,

                context = this@KioskListActivity,
                mConnection = this.mConnection!!,
                mDevice = this.mDevice!!,

                acc_name = this.acc_name!!,
                card_name = this.card_name!!,
                card_num = this.card_num!!,
                card_approval_num = this.card_approval_num!!,
                card_approval_date = this.card_approval_date!!,
            )
        }
        finish()
    }

    //프린트 내용-------------------------
    @RequiresApi(api = Build.VERSION_CODES.N)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        //데이터 넘겨줄 때 씀
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            if (requestCode == CARD_INTENT_NUM) {
                val extras = data!!.extras
                if ("9977" == data.getStringExtra("RESULT_CODE")) // 승인 처리 시간 종료
                    Toast.makeText(this, "승인처리 시간 종료", Toast.LENGTH_SHORT).show()
                i2 = Intent(this, OrderNumberPopupActivity::class.java)
                if (extras != null) {
                    val keys: Set<*> = extras.keySet()
                    for (_key in extras.keySet()) {
                        if (extras[_key] == null) {
                            i2!!.putExtra(_key, "null")
                        } else {
                            if (_key == "APPROVAL_NUM") {
                                card_approval_num = extras[_key].toString()
                            } else if (_key == "APPROVAL_DATE") {
                                card_approval_date = extras[_key].toString()
                            } else if (_key == "CARD_NAME") {
                                card_name = extras[_key].toString()
                            } else if (_key == "ACQUIRER_NAME") {
                                acc_name = extras[_key].toString()
                            } else if (_key == "CARD_NUM") {
                                card_num = extras[_key].toString()
                            }
                            i2!!.putExtra(_key, extras[_key].toString())
                        }
                    }
                }

                try {
                    Log.d("할부", data.getStringExtra("INSTALLMENT")!!)
                    Log.d("승인날짜", data.getStringExtra("APPROVAL_DATE")!!)
                    Log.d("승인넘버", data.getStringExtra("APPROVAL_NUM")!!)
                    Log.d("TRAN_SERIALNO", data.getStringExtra("TRAN_SERIALNO")!!)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                printInent(data)
                if (extras!!["RESULT_CODE"] == "0000") {
                    // 라즈베리파이 서버에 전송
                    val gson = GsonBuilder()
                            .excludeFieldsWithoutExposeAnnotation()
                            .create()
                    val jsonObject = JSONObject()
                    try {
                        //                                       총 가격 숫자만
                        jsonObject.put("totalPrice", totalPriceView!!.tag)
                        jsonObject.put("approvalNumber", card_approval_num)
                        jsonObject.put("approvalDate", card_approval_date)
                        Collections.sort(cartMenuList)
                        jsonObject.put("menus", JSONArray(gson.toJson(cartMenuList,
                                object : TypeToken<List<CartMenu?>?>() {}.type)))
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                    //Log.d("결제", jsonObject.toString());
                    // Toast.makeText(KioskListActivity.this, jsonObject.toString(), Toast.LENGTH_LONG).show();
                    httpController!!.postJsonCartData(jsonObject)
                } else {
                    Toast.makeText(this.applicationContext, "에러 :: " + extras["RESULT_CODE"].toString(), Toast.LENGTH_SHORT).show()
                    popUpOrderNumberAndQuit(extras.getInt("RESULT_CODE"), false)
                }
            }
        }
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                //데이터 받기
                val result = data!!.getStringExtra("result")
                if (result != null) {
                    Log.d("데이터", result)
                }
            }
            menuOptionPopupButtonClicked = false
        }
        if (requestCode == 2) {
            if (resultCode == RESULT_OK) {
                val searchedMenuIdList = data!!.getIntegerArrayListExtra("searchedMenuIdList")
                val queryResult = dbQueryController!!.getMenuListByIdArray(searchedMenuIdList)
                try {
                    itemListFragment = ItemListFragment(queryResult)
                    fragmentTransaction = fragmentManager!!.beginTransaction()
                    fragmentTransaction!!.replace(R.id.list_fragment, itemListFragment!!).commitAllowingStateLoss()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            searchButtonClicked = false
        }
    }

    override fun onPause() {
        super.onPause()
        idleTimer!!.cancel()
    }

    override fun onResume() {
        super.onResume()
        idleTimer!!.start()
        readStatus(this@KioskListActivity, mDevice = this.mDevice, mConnection = this.mConnection)
    }
}