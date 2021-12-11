package sb.yoon.kiosk

import sb.yoon.kiosk.controller.HttpNetworkController.postJsonCartData
import androidx.appcompat.app.AppCompatActivity
import sb.yoon.kiosk.ItemListFragment
import sb.yoon.kiosk.model.CartMenu
import sb.yoon.kiosk.controller.CartListAdapter
import sb.yoon.kiosk.controller.DbQueryController
import com.google.android.material.tabs.TabLayout
import android.widget.ToggleButton
import sb.yoon.kiosk.libs.IdleTimer
import android.widget.TextView
import android.app.PendingIntent
import android.hardware.usb.UsbManager
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbDeviceConnection
import android.hardware.usb.UsbEndpoint
import sb.yoon.kiosk.controller.HttpNetworkController
import android.os.Bundle
import sb.yoon.kiosk.R
import sb.yoon.kiosk.KioskApplication
import sb.yoon.kiosk.KioskListActivity.categoryTabClickListener
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.LinearLayoutManager
import sb.yoon.kiosk.KioskListActivity.purchaseButtonClickListener
import sb.yoon.kiosk.KioskListActivity
import android.os.Parcelable
import android.widget.Toast
import sb.yoon.kiosk.noPaperDlg
import android.hardware.usb.UsbInterface
import android.hardware.usb.UsbConstants
import android.view.LayoutInflater
import sb.yoon.kiosk.KioskListActivity.onClickToggleButton
import sb.yoon.kiosk.SearchActivity
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import androidx.annotation.RequiresApi
import android.os.Build
import android.app.Activity
import android.content.*
import android.graphics.Color
import android.os.Handler
import android.util.Log
import android.view.Menu
import android.view.View
import android.widget.Button
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import sb.yoon.kiosk.OrderNumberPopupActivity
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import org.json.JSONObject
import org.json.JSONArray
import com.google.gson.reflect.TypeToken
import org.json.JSONException
import sb.yoon.kiosk.model.Category
import java.io.IOException
import java.lang.Exception
import java.lang.StringBuilder
import java.net.InetSocketAddress
import java.net.Socket
import java.net.SocketAddress
import java.nio.charset.Charset
import java.util.*
import java.util.regex.Pattern

//배경색 #081832 == 11호관 마크 색상으로 추정
class KioskListActivity : AppCompatActivity() {
    private var fragmentManager: FragmentManager
    private var fragmentTransaction: FragmentTransaction
    private var itemListFragment: ItemListFragment
    private var categories: List<Category>
    private var cartMenuList: MutableList<CartMenu> = ArrayList()
    private var cartListAdapter: CartListAdapter

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
    var readBuffer: ByteArray
    var readBufferPosition = 0
    var prt_count = 0
    var permissionIntent: PendingIntent? = null
    private var mUsbManager: UsbManager? = null
    private var mDevice: UsbDevice? = null
    private var mConnection: UsbDeviceConnection? = null
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
    override fun onCreatePanelMenu(featureId: Int, menu: Menu): Boolean {
        return super.onCreatePanelMenu(featureId, menu)
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
        categories = dbQueryController.getCategoriesList()

        //버튼 객체 미리 생성
        tagNum = 0
        categorySize = categories.size

        // 카테고리 버튼들 생성
        //todo
        categoryTab = findViewById(R.id.categoryTab)
        categoryTab.addOnTabSelectedListener(categoryTabClickListener())
        categoryTab.setSelectedTabIndicatorColor(Color.parseColor("#FFFFFF")) // 언더바 나오길래 안보이도록 만들었습니다.
        leftButton = findViewById(R.id.left_button)
        rightButton = findViewById(R.id.right_button)
        maxCategoryPage = (categorySize / eleSize).toFloat()


        // 기본으로 보여줄 플래그먼트 (첫번째 카테고리)
        fragmentManager = supportFragmentManager
        itemListFragment = ItemListFragment(dbQueryController.getMenuList(categories.get(0)))
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
            setDevice()
            mUsbManager!!.requestPermission(mDevice, permissionIntent)
            purchaseButton = findViewById(R.id.purchase_button)
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
                }
            }
        }
    }

    //디바이스 설정
    private fun setDevice(): Char {
        var Title: String
        var Message: String
        var mDeviceNameStr: String
        var mDeviceName: UsbDevice
        var device_cnt = 0.toChar() // hwasung device detect counter
        mUsbManager = getSystemService(USB_SERVICE) as UsbManager
        val deviceList = mUsbManager!!.deviceList
        val deviceIterator: Iterator<UsbDevice> = deviceList.values.iterator()
        while (deviceIterator.hasNext()) {
            val device = deviceIterator.next()
            val mVendorId = device.vendorId.toString()
            var b: Boolean
            b = Pattern.matches(HWASUNG_VENDOR_ID, mVendorId)
            if (b == true) {
                mDevice = device
                device_cnt++
                break
            } else {
                device_cnt = 0.toChar()
            }
        }
        if (device_cnt.toInt() == 0) {
//        	Title = "USB Connection";
//     		Message = "Can not find Hwasung USB Device!";
//            showDialog(Title, Message);
            return 0
        }
        val intf = mDevice!!.getInterface(0)
        // endpoint type BULK
        val ep = intf.getEndpoint(0)
        if (ep.type != UsbConstants.USB_ENDPOINT_XFER_BULK) {
            Log.e(TAG, "endpoint is not BULK type")
            return 0
        }
        mEndpointBulk = ep
        if (mDevice != null) {
            val connection = mUsbManager!!.openDevice(mDevice)
            if (connection != null && connection.claimInterface(intf, true)) {
                mConnection = connection // USB OPEN SUCCESS
            }
        }
        return device_cnt
    }

    //커맨드 보내기
    private fun sendCommand(device: UsbDevice?, mIntBuf: IntArray, mDataCnt: Int) {
        val mRet: Int
        if (mConnection != null) {
            val intf = device!!.getInterface(0)
            val ep = intf.getEndpoint(0)
            var mByteBuf = ByteArray(mDataCnt)
            var i: Int

            // convert integer to byte
            mByteBuf = ByteArray(mDataCnt)
            i = 0
            while (i < mDataCnt) {
                mByteBuf[i] = mIntBuf[i].toByte()
                i++
            }
            mRet = mConnection!!.bulkTransfer(ep, mByteBuf, mDataCnt, 5000)
            if (mRet == -1) Log.d("send command fail", "" + mRet)
        }
    }

    //커맨드 받았을 때
    private fun receiveCommand(device: UsbDevice?, mIntBuf: IntArray) {
        var Title: String
        var Message: String
        val mRet: Int
        var i: Int
        if (mConnection != null) {
            val intf = device!!.getInterface(0)
            // receive BULK-IN endpoint6(mAddress:0x86)
            val ep = intf.getEndpoint(2) //엡손 호환 테스트용 수정됨
            val mByteBuf = ByteArray(1)

            // 1byte read, time out 1second
            mRet = mConnection!!.bulkTransfer(ep, mByteBuf, 1, 5000)
            if (mRet == -1) {
                Log.d("status fail", "수신 실패")
                Log.d("receive command ", "" + mRet)
            }
            // set 1byte status data to integer buffer and return
            mIntBuf[0] = mByteBuf[0].toInt()
        }
    }

    fun readStatus() {
        var Title: String
        var Message: String
        val device_cnt: Char
        val mIntBuf: IntArray
        val mIntBuf3 = IntArray(12)
        val mDataCnt: Int
        var i: Int
        var mRet: Int
        device_cnt = setDevice()
        if (device_cnt.toInt() != 0) {
            mDataCnt = 6
            mIntBuf = IntArray(mDataCnt)
            mIntBuf[0] = 0x10
            mIntBuf[1] = 0xAA
            mIntBuf[2] = 0x55
            mIntBuf[3] = 0x80
            mIntBuf[4] = 0x54
            mIntBuf[5] = 0xAB
            sendCommand(mDevice, mIntBuf, mDataCnt)
            receiveCommand(mDevice, mIntBuf3)

            // display status value of printer
            Log.d(TAG, "status : " + mIntBuf3[0])
            if (mIntBuf3[0] == 0x09) {
                val noPaperDlg = noPaperDlg(this@KioskListActivity)
                noPaperDlg.callFunction()
            }
        }
    }

    //--------------------------------------------------------------printer
    override fun onUserInteraction() {
        idleTimer!!.cancel()
        idleTimer!!.start()
        super.onUserInteraction()
    }

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
            tab.customView = createTabView(categories!![i].name, i)
            categoryTab!!.addTab(tab.setText(categories!![i].name))
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
        itemListFragment = ItemListFragment(dbQueryController!!.getMenuList(categories!![categoryPage * eleSize]))
        fragmentTransaction = fragmentManager!!.beginTransaction()
        fragmentTransaction!!.replace(R.id.list_fragment, itemListFragment!!).commitAllowingStateLoss()
        toggleButtons[0].text = categories!![categoryPage * eleSize].name
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
        toggleButton.setOnClickListener(onClickToggleButton())
        toggleButtons.add(toggleButton)
        return tabView
    }

    fun clickReturnButton(view: View?) {
        finish()
    }

    internal inner class onClickToggleButton : View.OnClickListener {
        override fun onClick(view: View) {
            val tagNo = view.tag as Int
            categoryTab!!.getTabAt(tagNo % eleSize)!!.select()
        }
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
        //            View dialogView = getLayoutInflater().inflate(R.layout.dialog_custom, null);
//            TextView price_tv = dialogView.findViewById(R.id.price_text);
//            TextView totalPriceView = KioskListActivity.this.findViewById(R.id.total_price);
//            // Log.d("가격텍스트", (String) totalPriceView.getText());
//            price_tv.setText(totalPriceView.getText().toString());
//
//            AlertDialog.Builder builder = new AlertDialog.Builder(KioskListActivity.this);
//            builder.setView(dialogView);
//
//            final AlertDialog alertDialog = builder.create();
//            alertDialog.show();

//            Button ok_btn = dialogView.findViewById(R.id.ok_btn);
//            ok_btn.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    // 라즈베리파이 서버에 전송
//                    final Gson gson = new GsonBuilder()
//                            .excludeFieldsWithoutExposeAnnotation()
//                            .create();
//                    JSONObject jsonObject = new JSONObject();
//                    try {
//                        //                                       총 가격 숫자만
//                        jsonObject.put("totalPrice", totalPriceView.getTag());
//                        jsonObject.put("menus", new JSONArray(gson.toJson(cartMenuList,
//                                new TypeToken<List<CartMenu>>() {
//                                }.getType())));
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                    //Log.d("결제", jsonObject.toString());
//                    // Toast.makeText(KioskListActivity.this, jsonObject.toString(), Toast.LENGTH_LONG).show();
//                    HttpNetworkController httpController = new HttpNetworkController(
//                            KioskListActivity.this, getResources().getString(R.string.server_ip));
//                    httpController.postJsonCartData(jsonObject);
//                }
//            });

//            Button cancle_btn = dialogView.findViewById(R.id.cancle_btn);
//            cancle_btn.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Toast.makeText(KioskListActivity.this, "결제를 취소하셨습니다.", Toast.LENGTH_SHORT).show();
//                    alertDialog.dismiss();
//                }
//            });
        //todo master에 merge 이후 intent result받는 곳으로 옮길 것
        //purchaseButtonClicked = false;
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
        //        if("PT".equals(tran_types)){ 															// 프린트 관련. 현재 필요 없음
//            String printmsg = printMessage.receiptPrint("160516120000", "IC신용구매", false, false, 1004, 91, 0, 0, "1234-56**-****-1234", "테스트점", "1234567890", "홍길동", "02-1234-5678", "1234567", "서울 테스트구 테스트동", "발급사", "00001111", "", "", "12345678", "매입사", "", "", true);
//            try {
//                intent.putExtra("PRINT_DATA", printmsg.getBytes("EUC-KR"));
//            } catch (UnsupportedEncodingException e) {
//                e.printStackTrace();
//            }
//        }
        intent.putExtra("ADD_FIELD", ADD_FIELD) // 모르겠음
        intent.putExtra("TIMEOUT", "80") // 아마 결제 시 타임아웃 지정
        intent.putExtra("TEXT_PROCESS", "결제 진행중입니다") // 결제 진행중 문구
        intent.putExtra("TEXT_COMPLETE", "결제가 완료되었습니다") // 결제 완료시 문구
        //		intent.putExtra("TEXT_FALLBACK", "카드의 마그네틱부분으로\n읽어주세요");
//
//		intent.putExtra("TEXT_MAIN_SIZE", 60);
//		intent.putExtra("IMG_BG_WIDTH", 1000);
//		intent.putExtra("IMG_CARD_WIDTH", 600);
//		intent.putExtra("IMG_CLOSE_WIDTH", 100);

//		intent.putExtra("FALLBACK_FLAG","N");

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


//		if(barcode.getText().toString().isEmpty()==false)
//		{
//			intent.putExtra("DONGLE_FLAG",this.dongleflag.getText().toString());
//			intent.putExtra("BARCODE",this.barcode.getText().toString());
//
//		}
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
            onClick_usb_text2(orderNumber)
        }
        finish()
    }

    //프린트 내용-------------------------
    @RequiresApi(api = Build.VERSION_CODES.N)
    fun onClick_usb_text2(orderNumber: Int) {
        val mPrice: String
        var mTotalPrice: String
        val mEndMsg: String
        var mStr: String
        var mIntBuf: IntArray
        var mDataCnt: Int
        var i: Int
        var mByteBuf: ByteArray
        val orderNum: String = """
             주문번호 : ${Integer.toString(orderNumber)}
             
             """.trimIndent()
        val mCafeName: String = "\n11호관 커피숍\n"
        val mCafeInfo: String = """
             울산시 남구 대학로 93 11호관 305호
             TEL.052-220-5757
             
             """.trimIndent()
        val mBar: String = " ------------------------ \n"
        val mPriceInfo: String = "   메뉴\t\t\t\t수량\t    가격\n"
        val quantityTable = HashMap<String, Int>()
        val priceTable = HashMap<String, Int>()
        for (cartMenu in cartMenuList) {
            val name = cartMenu.name
            quantityTable[name] = quantityTable.getOrDefault(name, 0) + 1
            priceTable[name] = priceTable.getOrDefault(name, 0) + cartMenu.totalPrice
        }
        val mStrBodyBuilder: StringBuilder = StringBuilder()
        for (name in quantityTable.keys) {
            if (name.length > 6) {
                mStrBodyBuilder.append(String.format("  %s\t\t%d\t    \\%d\n", name, quantityTable[name], priceTable[name]))
            } else {
                mStrBodyBuilder.append(String.format("  %s\t\t\t%d\t    \\%d\n", name, quantityTable[name], priceTable[name]))
            }
        }
        mPrice = mStrBodyBuilder.toString()

        // set Hwasung Syatem usb device
        val device_cnt: Char = setDevice()
        if (device_cnt.toInt() != 0) {
            // 주문번호 부분
            //centering print position
            mIntBuf = IntArray(3)
            mIntBuf[0] = CMD_ESC.toInt()
            mIntBuf[1] = 0x61
            mIntBuf[2] = 0x01
            mDataCnt = 3
            // call send data
            sendCommand(mDevice, mIntBuf, mDataCnt)

            // 가로세로 2배 사이즈 조절
            mIntBuf = IntArray(8)
            mIntBuf[0] = CMD_GS.toInt()
            mIntBuf[1] = '!'.code
            mIntBuf[2] = 0x11
            mDataCnt = 3

            // 데이터 전송 호출
            sendCommand(mDevice, mIntBuf, mDataCnt)

            //주문번호 전송
            mByteBuf = orderNum.toByteArray(Charset.forName("EUC-KR"))
            mDataCnt = mByteBuf.size
            mIntBuf = IntArray(mDataCnt)
            // 정수 버퍼에 데이터 설정
            i = 0
            while (i < mDataCnt) {
                mIntBuf[i] = mByteBuf[i].toInt()
                i++
            }
            // 데이터 전송 호출
            sendCommand(mDevice, mIntBuf, mDataCnt)

            // 세로 2배사이즈로 변경
            mIntBuf[0] = CMD_GS.toInt()
            mIntBuf[1] = '!'.code
            mIntBuf[2] = 0x01
            mDataCnt = 3

            // 데이터 전송 호출
            sendCommand(mDevice, mIntBuf, mDataCnt)

            //Todo mCafeName 부분
            //mCafeName 호출
            mByteBuf = mCafeName.toByteArray(Charset.forName("EUC-KR"))
            mDataCnt = mByteBuf.size
            mIntBuf = IntArray(mDataCnt)

            // set data to integer buffer
            i = 0
            while (i < mDataCnt) {
                mIntBuf[i] = mByteBuf[i].toInt()
                i++
            }
            // call send data
            sendCommand(mDevice, mIntBuf, mDataCnt)

            //2배 사이즈 끝
            mIntBuf[0] = CMD_GS.toInt()
            mIntBuf[1] = '!'.code
            mIntBuf[2] = 0x00
            mDataCnt = 3

            // 데이터 전송 호출
            sendCommand(mDevice, mIntBuf, mDataCnt)

            //Todo mCafeInfo 부분
            //mCafeInfo 호출
            mByteBuf = mCafeInfo.toByteArray(Charset.forName("EUC-KR"))
            mDataCnt = mByteBuf.size
            mIntBuf = IntArray(mDataCnt)
            i = 0
            while (i < mDataCnt) {
                mIntBuf[i] = mByteBuf[i].toInt()
                i++
            }
            sendCommand(mDevice, mIntBuf, mDataCnt)

            // 중앙정렬 끝
            mIntBuf[0] = CMD_ESC.toInt()
            mIntBuf[1] = 0x61
            mIntBuf[2] = 0x00
            mDataCnt = 3
            sendCommand(mDevice, mIntBuf, mDataCnt)

            //Todo mBar부분
            //2배 사이즈 끝
            mIntBuf[0] = CMD_GS.toInt()
            mIntBuf[1] = '!'.code
            mIntBuf[2] = 0x11
            mDataCnt = 3

            // 데이터 전송 호출
            sendCommand(mDevice, mIntBuf, mDataCnt)

            //call mBar
            mByteBuf = mBar.toByteArray(Charset.forName("EUC-KR"))
            mDataCnt = mByteBuf.size
            mIntBuf = IntArray(mDataCnt)

            // set data to integer buffer
            i = 0
            while (i < mDataCnt) {
                mIntBuf[i] = mByteBuf[i].toInt()
                i++
            }
            // call send data
            sendCommand(mDevice, mIntBuf, mDataCnt)

            //2배 사이즈 끝
            mIntBuf[0] = CMD_GS.toInt()
            mIntBuf[1] = '!'.code
            mIntBuf[2] = 0x00
            mDataCnt = 3

            // 데이터 전송 호출
            sendCommand(mDevice, mIntBuf, mDataCnt)
            //Todo mPriceInfo 부분
            mByteBuf = mPriceInfo.toByteArray(Charset.forName("EUC-KR"))
            mDataCnt = mByteBuf.size
            mIntBuf = IntArray(mDataCnt)
            i = 0
            while (i < mDataCnt) {
                mIntBuf[i] = mByteBuf[i].toInt()
                i++
            }
            sendCommand(mDevice, mIntBuf, mDataCnt)
            //Todo mBar 부분

            // 가로 2배
            mIntBuf[0] = CMD_GS.toInt()
            mIntBuf[1] = '!'.code
            mIntBuf[2] = 0x11
            mDataCnt = 3
            // call send data
            sendCommand(mDevice, mIntBuf, mDataCnt)

            // mBar 출력
            mByteBuf = mBar.toByteArray(Charset.forName("EUC-KR"))
            mDataCnt = mByteBuf.size
            mIntBuf = IntArray(mDataCnt)
            // set data to integer buffer
            i = 0
            while (i < mDataCnt) {
                mIntBuf[i] = mByteBuf[i].toInt()
                i++
            }
            // call send data
            sendCommand(mDevice, mIntBuf, mDataCnt)
            mIntBuf[0] = CMD_GS.toInt()
            mIntBuf[1] = '!'.code
            mIntBuf[2] = 0x00
            mDataCnt = 3
            // call send data
            sendCommand(mDevice, mIntBuf, mDataCnt)

            // Todo mPrice 부분
            // Bold On
            mIntBuf[0] = CMD_ESC.toInt()
            mIntBuf[1] = 0x45
            mIntBuf[2] = 0x01
            mDataCnt = 3
            sendCommand(mDevice, mIntBuf, mDataCnt)
            mByteBuf = mPrice.toByteArray(Charset.forName("EUC-KR"))
            mDataCnt = mByteBuf.size
            mIntBuf = IntArray(mDataCnt)
            // set data to integer buffer
            i = 0
            while (i < mDataCnt) {
                mIntBuf[i] = mByteBuf[i].toInt()
                i++
            }
            // call send data
            sendCommand(mDevice, mIntBuf, mDataCnt)

            //Bold Off
            mIntBuf[0] = CMD_ESC.toInt()
            mIntBuf[1] = 0x45
            mIntBuf[2] = 0x00
            mDataCnt = 3
            sendCommand(mDevice, mIntBuf, mDataCnt)

            // Todo mBar
            mIntBuf[0] = CMD_GS.toInt()
            mIntBuf[1] = '!'.code
            mIntBuf[2] = 0x10
            mDataCnt = 3
            // call send data
            sendCommand(mDevice, mIntBuf, mDataCnt)

            // mBar 출력
            mByteBuf = mBar.toByteArray(Charset.forName("EUC-KR"))
            mDataCnt = mByteBuf.size
            mIntBuf = IntArray(mDataCnt)
            // set data to integer buffer
            i = 0
            while (i < mDataCnt) {
                mIntBuf[i] = mByteBuf[i].toInt()
                i++
            }
            // call send data
            sendCommand(mDevice, mIntBuf, mDataCnt)


            // 소비세
            val p: Int = Math.toIntExact(Math.round(totalPrice / 1.1))
            mIntBuf[0] = CMD_ESC.toInt()
            mIntBuf[1] = 0x61
            mIntBuf[2] = 0x02
            mDataCnt = 3
            sendCommand(mDevice, mIntBuf, mDataCnt)
            mIntBuf[0] = CMD_GS.toInt()
            mIntBuf[1] = '!'.code
            mIntBuf[2] = 0x11
            mDataCnt = 3
            sendCommand(mDevice, mIntBuf, mDataCnt)
            mTotalPrice = "  합계금액 : "
            mTotalPrice += "$totalPrice 원 \n"
            mByteBuf = mTotalPrice.toByteArray(Charset.forName("EUC-KR"))
            mDataCnt = mByteBuf.size
            mIntBuf = IntArray(mDataCnt)
            // set data to integer buffer
            i = 0
            while (i < mDataCnt) {
                mIntBuf[i] = mByteBuf[i].toInt()
                i++
            }
            // call send data
            sendCommand(mDevice, mIntBuf, mDataCnt)
            mTotalPrice = "  공급가액 : "
            mTotalPrice += "$p 원 \n"
            mByteBuf = mTotalPrice.toByteArray(Charset.forName("EUC-KR"))
            mDataCnt = mByteBuf.size
            mIntBuf = IntArray(mDataCnt)
            // set data to integer buffer
            i = 0
            while (i < mDataCnt) {
                mIntBuf[i] = mByteBuf[i].toInt()
                i++
            }
            // call send data
            sendCommand(mDevice, mIntBuf, mDataCnt)
            mTotalPrice = "   부가세 : "
            mTotalPrice += "${totalPrice - p} 원 \n"
            mByteBuf = mTotalPrice.toByteArray(Charset.forName("EUC-KR"))
            mDataCnt = mByteBuf.size
            mIntBuf = IntArray(mDataCnt)
            // set data to integer buffer
            i = 0
            while (i < mDataCnt) {
                mIntBuf[i] = mByteBuf[i].toInt()
                i++
            }
            // call send data
            sendCommand(mDevice, mIntBuf, mDataCnt)


            // Size width 2x
            mIntBuf[0] = CMD_GS.toInt()
            mIntBuf[1] = '!'.code
            mIntBuf[2] = 0x10
            mDataCnt = 3
            // call send data
            sendCommand(mDevice, mIntBuf, mDataCnt)
            mIntBuf[0] = CMD_ESC.toInt()
            mIntBuf[1] = 0x61
            mIntBuf[2] = 0x00
            mDataCnt = 3
            sendCommand(mDevice, mIntBuf, mDataCnt)


            // Todo mBar
            mByteBuf = mBar.toByteArray(Charset.forName("EUC-KR"))
            mDataCnt = mByteBuf.size
            mIntBuf = IntArray(mDataCnt)
            // set data to integer buffer
            i = 0
            while (i < mDataCnt) {
                mIntBuf[i] = mByteBuf[i].toInt()
                i++
            }
            // call send data
            sendCommand(mDevice, mIntBuf, mDataCnt)

            // Todo 사업자 정보
            //centering print position
            mIntBuf[0] = CMD_ESC.toInt()
            mIntBuf[1] = 0x61
            mIntBuf[2] = 0x01
            mDataCnt = 3
            // call send data
            sendCommand(mDevice, mIntBuf, mDataCnt)

            // Size height 2x
            mIntBuf[0] = CMD_GS.toInt()
            mIntBuf[1] = '!'.code
            mIntBuf[2] = 0x01
            mDataCnt = 3
            // call send data
            sendCommand(mDevice, mIntBuf, mDataCnt)
            mStr = "\n신용카드 승인 정보\n"
            mByteBuf = mStr.toByteArray(Charset.forName("EUC-KR"))
            mDataCnt = mByteBuf.size
            mIntBuf = IntArray(mDataCnt)
            // set data to integer buffer
            i = 0
            while (i < mDataCnt) {
                mIntBuf[i] = mByteBuf[i].toInt()
                i++
            }
            // call send data
            sendCommand(mDevice, mIntBuf, mDataCnt)

            // Size 1x
            mIntBuf[0] = CMD_GS.toInt()
            mIntBuf[1] = '!'.code
            mIntBuf[2] = 0x00
            mDataCnt = 3
            // call send data
            sendCommand(mDevice, mIntBuf, mDataCnt)

            // Bold On
            mIntBuf[0] = CMD_ESC.toInt()
            mIntBuf[1] = 0x45
            mIntBuf[2] = 0x01
            mDataCnt = 3
            sendCommand(mDevice, mIntBuf, mDataCnt)
            mStr = "카드명: "
            mByteBuf = mStr.toByteArray(Charset.forName("EUC-KR"))
            mDataCnt = mByteBuf.size
            mIntBuf = IntArray(mDataCnt)
            // set data to integer buffer
            i = 0
            while (i < mDataCnt) {
                mIntBuf[i] = mByteBuf[i].toInt()
                i++
            }
            // call send data
            sendCommand(mDevice, mIntBuf, mDataCnt)

            // Bold Off
            mIntBuf[0] = CMD_ESC.toInt()
            mIntBuf[1] = 0x45
            mIntBuf[2] = 0x00
            mDataCnt = 3
            sendCommand(mDevice, mIntBuf, mDataCnt)
            mStr = """
                $card_name
                
                """.trimIndent()
            mByteBuf = mStr.toByteArray(Charset.forName("EUC-KR"))
            mDataCnt = mByteBuf.size
            mIntBuf = IntArray(mDataCnt)
            // set data to integer buffer
            i = 0
            while (i < mDataCnt) {
                mIntBuf[i] = mByteBuf[i].toInt()
                i++
            }
            // call send data
            sendCommand(mDevice, mIntBuf, mDataCnt)
            mIntBuf[0] = CMD_ESC.toInt()
            mIntBuf[1] = 0x45
            mIntBuf[2] = 0x01
            mDataCnt = 3
            sendCommand(mDevice, mIntBuf, mDataCnt)
            mStr = "카드번호 : "
            mByteBuf = mStr.toByteArray(Charset.forName("EUC-KR"))
            mDataCnt = mByteBuf.size
            mIntBuf = IntArray(mDataCnt)
            // set data to integer buffer
            i = 0
            while (i < mDataCnt) {
                mIntBuf[i] = mByteBuf[i].toInt()
                i++
            }
            // call send data
            sendCommand(mDevice, mIntBuf, mDataCnt)

            // Bold Off
            mIntBuf[0] = CMD_ESC.toInt()
            mIntBuf[1] = 0x45
            mIntBuf[2] = 0x00
            mDataCnt = 3
            sendCommand(mDevice, mIntBuf, mDataCnt)
            mStr = "$card_num**********\n"
            mByteBuf = mStr.toByteArray(Charset.forName("EUC-KR"))
            mDataCnt = mByteBuf.size
            mIntBuf = IntArray(mDataCnt)
            // set data to integer buffer
            i = 0
            while (i < mDataCnt) {
                mIntBuf[i] = mByteBuf[i].toInt()
                i++
            }
            // call send data
            sendCommand(mDevice, mIntBuf, mDataCnt)
            mIntBuf[0] = CMD_ESC.toInt()
            mIntBuf[1] = 0x45
            mIntBuf[2] = 0x01
            mDataCnt = 3
            sendCommand(mDevice, mIntBuf, mDataCnt)
            mIntBuf[0] = CMD_ESC.toInt()
            mIntBuf[1] = 0x45
            mIntBuf[2] = 0x01
            mDataCnt = 3
            sendCommand(mDevice, mIntBuf, mDataCnt)
            mStr = "매입사명 : "
            mByteBuf = mStr.toByteArray(Charset.forName("EUC-KR"))
            mDataCnt = mByteBuf.size
            mIntBuf = IntArray(mDataCnt)
            // set data to integer buffer
            i = 0
            while (i < mDataCnt) {
                mIntBuf[i] = mByteBuf[i].toInt()
                i++
            }
            // call send data
            sendCommand(mDevice, mIntBuf, mDataCnt)

            // Bold Off
            mIntBuf[0] = CMD_ESC.toInt()
            mIntBuf[1] = 0x45
            mIntBuf[2] = 0x00
            mDataCnt = 3
            sendCommand(mDevice, mIntBuf, mDataCnt)
            mStr = """
                $acc_name
                
                """.trimIndent()
            mByteBuf = mStr.toByteArray(Charset.forName("EUC-KR"))
            mDataCnt = mByteBuf.size
            mIntBuf = IntArray(mDataCnt)
            // set data to integer buffer
            i = 0
            while (i < mDataCnt) {
                mIntBuf[i] = mByteBuf[i].toInt()
                i++
            }
            // call send data
            sendCommand(mDevice, mIntBuf, mDataCnt)
            mIntBuf[0] = CMD_ESC.toInt()
            mIntBuf[1] = 0x45
            mIntBuf[2] = 0x01
            mDataCnt = 3
            sendCommand(mDevice, mIntBuf, mDataCnt)
            mStr = "\n사 업 자 : "
            mByteBuf = mStr.toByteArray(Charset.forName("EUC-KR"))
            mDataCnt = mByteBuf.size
            mIntBuf = IntArray(mDataCnt)
            // set data to integer buffer
            i = 0
            while (i < mDataCnt) {
                mIntBuf[i] = mByteBuf[i].toInt()
                i++
            }
            // call send data
            sendCommand(mDevice, mIntBuf, mDataCnt)

            // Bold Off
            mIntBuf[0] = CMD_ESC.toInt()
            mIntBuf[1] = 0x45
            mIntBuf[2] = 0x00
            mDataCnt = 3
            sendCommand(mDevice, mIntBuf, mDataCnt)
            mStr = "691-85-00176 엄문호\n"
            mByteBuf = mStr.toByteArray(Charset.forName("EUC-KR"))
            mDataCnt = mByteBuf.size
            mIntBuf = IntArray(mDataCnt)
            // set data to integer buffer
            i = 0
            while (i < mDataCnt) {
                mIntBuf[i] = mByteBuf[i].toInt()
                i++
            }
            // call send data
            sendCommand(mDevice, mIntBuf, mDataCnt) // Bold On
            mIntBuf[0] = CMD_ESC.toInt()
            mIntBuf[1] = 0x45
            mIntBuf[2] = 0x01
            mDataCnt = 3
            sendCommand(mDevice, mIntBuf, mDataCnt)
            mStr = "가맹점명 : "
            mByteBuf = mStr.toByteArray(Charset.forName("EUC-KR"))
            mDataCnt = mByteBuf.size
            mIntBuf = IntArray(mDataCnt)
            // set data to integer buffer
            i = 0
            while (i < mDataCnt) {
                mIntBuf[i] = mByteBuf[i].toInt()
                i++
            }
            // call send data
            sendCommand(mDevice, mIntBuf, mDataCnt)

            // Bold Off
            mIntBuf[0] = CMD_ESC.toInt()
            mIntBuf[1] = 0x45
            mIntBuf[2] = 0x00
            mDataCnt = 3
            sendCommand(mDevice, mIntBuf, mDataCnt)
            mStr = "11호관 커피숍\n"
            mByteBuf = mStr.toByteArray(Charset.forName("EUC-KR"))
            mDataCnt = mByteBuf.size
            mIntBuf = IntArray(mDataCnt)
            // set data to integer buffer
            i = 0
            while (i < mDataCnt) {
                mIntBuf[i] = mByteBuf[i].toInt()
                i++
            }
            // call send data
            sendCommand(mDevice, mIntBuf, mDataCnt)
            mIntBuf[0] = CMD_ESC.toInt()
            mIntBuf[1] = 0x45
            mIntBuf[2] = 0x01
            mDataCnt = 3
            sendCommand(mDevice, mIntBuf, mDataCnt)
            mStr = "가맹번호 : "
            mByteBuf = mStr.toByteArray(Charset.forName("EUC-KR"))
            mDataCnt = mByteBuf.size
            mIntBuf = IntArray(mDataCnt)
            // set data to integer buffer
            i = 0
            while (i < mDataCnt) {
                mIntBuf[i] = mByteBuf[i].toInt()
                i++
            }
            // call send data
            sendCommand(mDevice, mIntBuf, mDataCnt)

            // Bold Off
            mIntBuf[0] = CMD_ESC.toInt()
            mIntBuf[1] = 0x45
            mIntBuf[2] = 0x00
            mDataCnt = 3
            sendCommand(mDevice, mIntBuf, mDataCnt)
            mStr = "157431024\n"
            mByteBuf = mStr.toByteArray(Charset.forName("EUC-KR"))
            mDataCnt = mByteBuf.size
            mIntBuf = IntArray(mDataCnt)
            // set data to integer buffer
            i = 0
            while (i < mDataCnt) {
                mIntBuf[i] = mByteBuf[i].toInt()
                i++
            }
            // call send data
            sendCommand(mDevice, mIntBuf, mDataCnt)
            mIntBuf[0] = CMD_ESC.toInt()
            mIntBuf[1] = 0x45
            mIntBuf[2] = 0x01
            mDataCnt = 3
            sendCommand(mDevice, mIntBuf, mDataCnt)
            mStr = "승인일시 : "
            mByteBuf = mStr.toByteArray(Charset.forName("EUC-KR"))
            mDataCnt = mByteBuf.size
            mIntBuf = IntArray(mDataCnt)
            // set data to integer buffer
            i = 0
            while (i < mDataCnt) {
                mIntBuf[i] = mByteBuf[i].toInt()
                i++
            }
            // call send data
            sendCommand(mDevice, mIntBuf, mDataCnt)

            // Bold Off
            mIntBuf[0] = CMD_ESC.toInt()
            mIntBuf[1] = 0x45
            mIntBuf[2] = 0x00
            mDataCnt = 3
            sendCommand(mDevice, mIntBuf, mDataCnt)

            // Todo 승인일자 고치기
            mStr = """
                $card_approval_date
                
                """.trimIndent()
            mByteBuf = mStr.toByteArray(Charset.forName("EUC-KR"))
            mDataCnt = mByteBuf.size
            mIntBuf = IntArray(mDataCnt)
            // set data to integer buffer
            i = 0
            while (i < mDataCnt) {
                mIntBuf[i] = mByteBuf[i].toInt()
                i++
            }
            // call send data
            sendCommand(mDevice, mIntBuf, mDataCnt)
            mIntBuf[0] = CMD_ESC.toInt()
            mIntBuf[1] = 0x45
            mIntBuf[2] = 0x01
            mDataCnt = 3
            sendCommand(mDevice, mIntBuf, mDataCnt)
            mStr = "승인번호 : "
            mByteBuf = mStr.toByteArray(Charset.forName("EUC-KR"))
            mDataCnt = mByteBuf.size
            mIntBuf = IntArray(mDataCnt)
            // set data to integer buffer
            i = 0
            while (i < mDataCnt) {
                mIntBuf[i] = mByteBuf[i].toInt()
                i++
            }
            // call send data
            sendCommand(mDevice, mIntBuf, mDataCnt)

            // Bold Off
            mIntBuf[0] = CMD_ESC.toInt()
            mIntBuf[1] = 0x45
            mIntBuf[2] = 0x00
            mDataCnt = 3
            sendCommand(mDevice, mIntBuf, mDataCnt)
            mStr = """
                $card_approval_num
                
                """.trimIndent()
            mByteBuf = mStr.toByteArray(Charset.forName("EUC-KR"))
            mDataCnt = mByteBuf.size
            mIntBuf = IntArray(mDataCnt)
            // set data to integer buffer
            i = 0
            while (i < mDataCnt) {
                mIntBuf[i] = mByteBuf[i].toInt()
                i++
            }
            // call send data
            sendCommand(mDevice, mIntBuf, mDataCnt)
            mIntBuf[0] = CMD_ESC.toInt()
            mIntBuf[1] = 0x45
            mIntBuf[2] = 0x01
            mDataCnt = 3
            sendCommand(mDevice, mIntBuf, mDataCnt)
            mStr = "승인금 : "
            mByteBuf = mStr.toByteArray(Charset.forName("EUC-KR"))
            mDataCnt = mByteBuf.size
            mIntBuf = IntArray(mDataCnt)
            // set data to integer buffer
            i = 0
            while (i < mDataCnt) {
                mIntBuf[i] = mByteBuf[i].toInt()
                i++
            }
            // call send data
            sendCommand(mDevice, mIntBuf, mDataCnt)

            // Bold Off
            mIntBuf[0] = CMD_ESC.toInt()
            mIntBuf[1] = 0x45
            mIntBuf[2] = 0x00
            mDataCnt = 3
            sendCommand(mDevice, mIntBuf, mDataCnt)

            // 총금액 다시 생각해보기.
            mStr = """
                $totalPrice
                
                """.trimIndent()
            mByteBuf = mStr.toByteArray(Charset.forName("EUC-KR"))
            mDataCnt = mByteBuf.size
            mIntBuf = IntArray(mDataCnt)
            // set data to integer buffer
            i = 0
            while (i < mDataCnt) {
                mIntBuf[i] = mByteBuf[i].toInt()
                i++
            }
            // call send data
            sendCommand(mDevice, mIntBuf, mDataCnt)

            // clear centering
            mIntBuf[0] = CMD_ESC.toInt()
            mIntBuf[1] = 0x61
            mIntBuf[2] = 0x00
            mDataCnt = 3
            sendCommand(mDevice, mIntBuf, mDataCnt)

            // Size Width 2x
            mIntBuf[0] = CMD_GS.toInt()
            mIntBuf[1] = '!'.code
            mIntBuf[2] = 0x10
            mDataCnt = 3
            sendCommand(mDevice, mIntBuf, mDataCnt)

            // Todo mBar
            mByteBuf = mBar.toByteArray(Charset.forName("EUC-KR"))
            mDataCnt = mByteBuf.size
            mIntBuf = IntArray(mDataCnt)
            // set data to integer buffer
            i = 0
            while (i < mDataCnt) {
                mIntBuf[i] = mByteBuf[i].toInt()
                i++
            }
            sendCommand(mDevice, mIntBuf, mDataCnt)

            // Size Width 2x
            mIntBuf[0] = CMD_GS.toInt()
            mIntBuf[1] = '!'.code
            mIntBuf[2] = 0x00
            mDataCnt = 3
            sendCommand(mDevice, mIntBuf, mDataCnt)

            // centering
            mIntBuf[0] = CMD_ESC.toInt()
            mIntBuf[1] = 0x61
            mIntBuf[2] = 0x01
            mDataCnt = 3
            sendCommand(mDevice, mIntBuf, mDataCnt)

            // mEndMsg
            mEndMsg = "저희 11호관 커피를 이용해 주셔서 감사합니다!\n"
            mByteBuf = mEndMsg.toByteArray(Charset.forName("EUC-KR"))
            mDataCnt = mByteBuf.size
            mIntBuf = IntArray(mDataCnt)
            // set data to integer buffer
            i = 0
            while (i < mDataCnt) {
                mIntBuf[i] = mByteBuf[i].toInt()
                i++
            }
            sendCommand(mDevice, mIntBuf, mDataCnt)

            // clear centering
            mIntBuf[0] = CMD_ESC.toInt()
            mIntBuf[1] = 0x61
            mIntBuf[2] = 0x00
            mDataCnt = 3
            sendCommand(mDevice, mIntBuf, mDataCnt)

            // feeding
            mIntBuf[0] = 0x0a
            mIntBuf[1] = 0x0a
            mIntBuf[2] = 0x0a
            mIntBuf[3] = 0x0a
            mIntBuf[4] = 0x0a
            mDataCnt = 5
            sendCommand(mDevice, mIntBuf, mDataCnt)

            // full cutting
            mIntBuf[0] = CMD_ESC.toInt()
            mIntBuf[1] = 0x69
            mDataCnt = 2
            sendCommand(mDevice, mIntBuf, mDataCnt)

            //releaseDevice(device_cnt);
            card_approval_num = null
            card_approval_date = null
        }
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
                //				delayhandler.postDelayed(
//						new Runnable() {
//							// 1 초 후에 실행
//							@Override
//							public void run() {
//								setTranData(tran_type,"");
//							}
//						}, 5000);
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
        readStatus()
    }

    companion object {
        //Printer ------------------------------------------
        private const val TAG = "UsbMenuActivity"
        @JvmField
        var statusloop = true

        // Intent
        private const val ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION"

        // hwasung vendor id string
        private const val HWASUNG_VENDOR_ID = "6"
        private const val MAX_IMAGE_LINE = 24 // NOTE! do not edit!

        // printer commands
        private const val CMD_DLE: Byte = 0x10
        private const val CMD_ESC: Byte = 0x1b
        private const val CMD_GS: Byte = 0x1d
        private const val CMD_FS: Byte = 0x1c
        fun printInent(i: Intent?) {
            try {
                //Log.e("KTC","-------------------------------------------------------");
                //util.saveLog("-------------------------------------------------------");
                if (i != null) {
                    val extras = i.extras
                    if (extras != null) {
                        val keys: Set<*> = extras.keySet()
                        for (_key in extras.keySet()) {
                            Log.e("KTC", "key=" + _key + " : " + extras[_key])
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
            }
        }
    }
}