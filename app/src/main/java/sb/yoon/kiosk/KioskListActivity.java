package sb.yoon.kiosk;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Context;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.hardware.usb.UsbConstants;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import sb.yoon.kiosk.controller.CartListAdapter;
import sb.yoon.kiosk.controller.DbQueryController;
import sb.yoon.kiosk.controller.HttpNetworkController;
import sb.yoon.kiosk.layout.CategoryButton;
import sb.yoon.kiosk.libs.IdleTimer;
import sb.yoon.kiosk.libs.Util;
import sb.yoon.kiosk.model.CartMenu;
import sb.yoon.kiosk.model.CartOption;
import sb.yoon.kiosk.model.Category;
import sb.yoon.kiosk.model.Menu;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

//????????? #081832 == 11?????? ?????? ???????????? ??????
public class KioskListActivity extends AppCompatActivity {
    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;
    private ItemListFragment itemListFragment;
    private List<Category> categories;

    private List<CartMenu> cartMenuList = new ArrayList<>();
    private CartListAdapter cartListAdapter;

    // ??????????????? ?????? ????????? DB ?????? ???????????? ??????
    private DbQueryController dbQueryController;

    private int tagNum;
    private int categoryPage = 0;
    int categorySize;
    float maxCategoryPage;

    boolean searchButtonClicked = false;
    public boolean menuOptionPopupButtonClicked = false;

    private TabLayout categoryTab;
    private ArrayList<ToggleButton> toggleButtons = new ArrayList<>();


    private Button leftButton;
    private Button rightButton;

    final int eleSize = 4;

    private IdleTimer idleTimer;

    private TextView totalPriceView;
    final private int CARD_INTENT_NUM = 811;
    private Intent i2;

    //Printer ------------------------------------------
    private static final String TAG = "UsbMenuActivity";
    Thread statusThread;
    TextView status,count;
    static boolean statusloop = true;
    byte[] readBuffer;
    int readBufferPosition;
    int prt_count;
    PendingIntent permissionIntent;

    private UsbManager mUsbManager;
    private UsbDevice mDevice;
    private UsbDeviceConnection mConnection;
    private UsbEndpoint mEndpointBulk;
    // Intent
    private static final String ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION";
    // hwasung vendor id string
    private static final String HWASUNG_VENDOR_ID = "6";
    private static final int MAX_IMAGE_LINE = 24;	// NOTE! do not edit!

    // printer commands
    private static final byte CMD_DLE = 0x10;
    private static final byte CMD_ESC = 0x1b;
    private static final byte CMD_GS = 0x1d;
    private static final byte CMD_FS = 0x1c;
    //------------------------------------------------------

    private String card_approval_num;
    private String card_approval_date;

    private HttpNetworkController httpController;
    private Button purchaseButton;
    private int totalPrice = 0;

    private String card_name;
    private String acc_name;
    private String card_num;


    @Override
    public boolean onCreatePanelMenu(int featureId, @NonNull android.view.Menu menu) {
        return super.onCreatePanelMenu(featureId, menu);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kiosk_main);

        KioskApplication kioskApplication = (KioskApplication) getApplication();
        kioskApplication.setKioskListActivity(this);

        httpController = new HttpNetworkController(
                KioskListActivity.this, getResources().getString(R.string.server_ip));

        // DB ???????????? (??????????????? ??? ???????????? ??????)
        dbQueryController = kioskApplication.getDbQueryController();

        // DB ?????????
        // dbQueryController.initDB();

        // ???????????? ????????? ????????????
        categories = dbQueryController.getCategoriesList();

        //?????? ?????? ?????? ??????
        tagNum = 0;

        categorySize = categories.size();

        // ???????????? ????????? ??????
        //todo
        categoryTab = findViewById(R.id.categoryTab);
        categoryTab.addOnTabSelectedListener(new categoryTabClickListener());

        categoryTab.setSelectedTabIndicatorColor(Color.parseColor("#FFFFFF")); // ????????? ???????????? ??????????????? ??????????????????.

        leftButton = findViewById(R.id.left_button);
        rightButton = findViewById(R.id.right_button);

        maxCategoryPage = categorySize / eleSize;


        // ???????????? ????????? ??????????????? (????????? ????????????)
        fragmentManager = getSupportFragmentManager();
        itemListFragment = new ItemListFragment(dbQueryController.getMenuList(categories.get(0)));
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.list_fragment, itemListFragment).commitAllowingStateLoss();

        // ?????? ??????????????????
        RecyclerView cartRecyclerView = this.findViewById(R.id.cart_recycler_list);
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(this);
        mLinearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        cartRecyclerView.setLayoutManager(mLinearLayoutManager);

        cartListAdapter = new CartListAdapter(cartMenuList);
        cartRecyclerView.setAdapter(cartListAdapter);

        // ???????????? ?????????
        Button purchaseButton = findViewById(R.id.purchase_button);
        purchaseButton.setOnClickListener(new purchaseButtonClickListener());

        updateCategoryTab(categoryPage);

        totalPriceView = this.findViewById(R.id.total_price);

        idleTimer = new IdleTimer(this, 150000, 1000);//????????? 115?????? ????????? ??????????????????. -jojo
        idleTimer.start();

        try {
            //Printer-------------------------------------------
            mUsbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
            permissionIntent = PendingIntent.getBroadcast(this, 0, new Intent(ACTION_USB_PERMISSION), 0);
            IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
            registerReceiver(usbReceiver, filter);
            setDevice();
            mUsbManager.requestPermission(mDevice, permissionIntent);
            purchaseButton = findViewById(R.id.purchase_button);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //Printer ---------------------------------------------------
    private final BroadcastReceiver usbReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (ACTION_USB_PERMISSION.equals(action)){
                synchronized (this){
                    UsbDevice device = (UsbDevice)intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);

                    if(intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)){
                        if(device != null){
                            //status_thread();
                        }
                        else{
                            Log.d(TAG, "permission denied for device"+device);
                        }
                    }
                }
            }
            if(UsbManager.ACTION_USB_ACCESSORY_DETACHED.equals(action)){
                UsbDevice device = (UsbDevice)intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                if(device != null){

                }
            }
        }
    };

    public void status_thread(){
        final Handler handler = new Handler();
        Log.d("printer status", "?????????");
        statusloop = true;

        statusThread = new Thread(new Runnable(){
            @Override
            public void run() {
                while(statusloop){
                    final int[] mIntBuf, mIntBuf3;
                    int mDataCnt;
                    final char device_cnt;

                    device_cnt = setDevice();
                    try{
                        if(device_cnt != 0){
                            mIntBuf = new int[6];
                            mIntBuf3 = new int[1];

                            mIntBuf[0] = (byte) 0x10;
                            mIntBuf[1] = (byte) 0xAA;
                            mIntBuf[2] = (byte) 0x55;
                            mIntBuf[3] = (byte) 0x80;
                            mIntBuf[4] = (byte) 0x54;
                            mIntBuf[5] = (byte) 0xAB;
                            mDataCnt = 6;
                            // call send data
                            sendCommand(mDevice, mIntBuf, mDataCnt);
                            Thread.sleep(1);
                            receiveCommand(mDevice, mIntBuf3);
                            handler.post(new Runnable()
                            {
                                public void run()
                                {
                                    /*Log.d("printer status", String.valueOf(mIntBuf3[0]));*/
                                    if(mIntBuf3[0] == 0x09){
                                        Toast.makeText(KioskListActivity.this, "mIntBuf3[0]:"+String.valueOf(mIntBuf3[0]), Toast.LENGTH_SHORT).show();
                                        statusloop = false;
                                        noPaperDlg noPaperDlg = new noPaperDlg(KioskListActivity.this);
                                        noPaperDlg.callFunction();
                                    }
                                    /*status.setText("Status(Dec) : "+ mIntBuf3[0]);*/
                                }

                            });
                            //Log.d("status",""+Integer.toHexString(mIntBuf3[0]));
                        }else{
                            statusloop = false;
                        }

                        Thread.sleep(500);

                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();

                    }
                }
            }
        });
        statusThread.start();
    }

    //???????????? ??????
    private char setDevice() {

        String Title;
        String Message;
        String mDeviceNameStr;
        UsbDevice mDeviceName;
        char device_cnt = 0;	// hwasung device detect counter

        mUsbManager = (UsbManager)getSystemService(Context.USB_SERVICE);


        HashMap<String, UsbDevice> deviceList = mUsbManager.getDeviceList();

        Iterator<UsbDevice> deviceIterator = deviceList.values().iterator();


        while(deviceIterator.hasNext()){
            UsbDevice device = deviceIterator.next();
            String mVendorId = String.valueOf(device.getVendorId());

            boolean b;
            b = Pattern.matches(HWASUNG_VENDOR_ID,mVendorId);
            if( b == true ){
                mDevice = device;
                device_cnt++;
                break;
            }
            // no hwasung device or disconnected
            else{
                device_cnt = 0;
            }

        }

        if(device_cnt == 0) {
//        	Title = "USB Connection";
//     		Message = "Can not find Hwasung USB Device!";
//            showDialog(Title, Message);

            return 0;
        }
        UsbInterface intf = mDevice.getInterface(0);
        // endpoint type BULK
        UsbEndpoint ep = intf.getEndpoint(0);

        if (ep.getType() != UsbConstants.USB_ENDPOINT_XFER_BULK) {
            Log.e(TAG, "endpoint is not BULK type");
            return 0;
        }

        mEndpointBulk = ep;

        if (mDevice != null) {
            UsbDeviceConnection connection = mUsbManager.openDevice(mDevice);
            if (connection != null && connection.claimInterface(intf, true)) {
                mConnection = connection;	// USB OPEN SUCCESS
            }
        }
        return device_cnt;

    }
    //????????? ?????????
    private void sendCommand(UsbDevice device, int mIntBuf[], int mDataCnt) {

        int mRet;

        if (mConnection != null) {
            UsbInterface intf = device.getInterface(0);
            UsbEndpoint ep = intf.getEndpoint(0);

            byte[] mByteBuf = new byte[mDataCnt];
            int i;

            // convert integer to byte
            mByteBuf = new byte[mDataCnt];

            for( i=0; i < mDataCnt ; i++) {
                mByteBuf[i] = (byte) mIntBuf[i];
            }
            mRet = mConnection.bulkTransfer(ep, mByteBuf, mDataCnt, 5000);
            if (mRet == -1)Log.d("send command fail", ""+mRet);
        }

    }
    //????????? ????????? ???
    private void receiveCommand(UsbDevice device,int mIntBuf[]) {
        String Title;
        String Message;

        int mRet,i;

        if (mConnection != null) {

            UsbInterface intf = device.getInterface(0);
            // receive BULK-IN endpoint6(mAddress:0x86)
            UsbEndpoint ep = intf.getEndpoint(2);  //?????? ?????? ???????????? ?????????

            byte[] mByteBuf = new byte[1];

            // 1byte read, time out 1second
            mRet = mConnection.bulkTransfer( ep, mByteBuf, 1, 5000);
            if(mRet == -1) {
                Log.d("status fail", "?????? ??????");
                Log.d("receive command ", ""+mRet);
            }
            // set 1byte status data to integer buffer and return
            mIntBuf[0] = (int) mByteBuf[0];
        }

    }
    public void readStatus() {
        String Title;
        String Message;

        char device_cnt;
        int[] mIntBuf ;
        int[] mIntBuf3 = new int[12];
        int mDataCnt,i,mRet;

        device_cnt = setDevice();

        if(device_cnt != 0 ) {

            mDataCnt = 6;
            mIntBuf = new int[mDataCnt];

            mIntBuf[0] = 0x10;
            mIntBuf[1] = 0xAA;
            mIntBuf[2] = 0x55;
            mIntBuf[3] = 0x80;
            mIntBuf[4] = 0x54;
            mIntBuf[5] = 0xAB;

            sendCommand(mDevice, mIntBuf,mDataCnt);

            receiveCommand(mDevice, mIntBuf3);

            // display status value of printer

            Log.d(TAG, "status : "+mIntBuf3[0]);
            if(mIntBuf3[0] == 0x09){
                noPaperDlg noPaperDlg = new noPaperDlg(KioskListActivity.this);
                noPaperDlg.callFunction();
            }
        }
    }
    //--------------------------------------------------------------printer

    @Override
    public void onUserInteraction() {
        idleTimer.cancel();
        idleTimer.start();
        super.onUserInteraction();
    }

    private void updateCategoryTab(int page) {
        Log.d("page", String.valueOf(page));
        categoryTab.removeAllTabs();
        int limit = page * eleSize + eleSize;
        toggleButtons.clear();
        for (int i = page * eleSize; i < limit; i++) {
            if (i >= categorySize) {
                break;
            }
            TabLayout.Tab tab = categoryTab.newTab();
            tab.setCustomView(createTabView(categories.get(i).getName(), i));
            categoryTab.addTab(tab.setText(categories.get(i).getName()));
        }
        leftButton.setEnabled(page != 0);
        rightButton.setEnabled(!(maxCategoryPage <= page));
    }

    public void updateCategoryTab(View view) {
        if (view.getId() == R.id.left_button) {
            categoryPage -= 1;
            updateCategoryTab(categoryPage);
        } else if (view.getId() == R.id.right_button) {
            categoryPage += 1;
            updateCategoryTab(categoryPage);
        }
        itemListFragment = new ItemListFragment(dbQueryController.getMenuList(categories.get(categoryPage * eleSize)));
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.list_fragment, itemListFragment).commitAllowingStateLoss();

        toggleButtons.get(0).setText(categories.get(categoryPage * eleSize).getName());
    }

    private View createTabView(String tabName, int tag) {
        View tabView = LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
        ToggleButton toggleButton = tabView.findViewById(R.id.txt_name);
        toggleButton.setChecked(false);
        toggleButton.setBackgroundColor(Color.parseColor("#ffffff"));
        toggleButton.setTextColor(Color.parseColor("#081832"));
        toggleButton.setText(tabName);
        toggleButton.setTextOff(tabName);
        toggleButton.setTextOn(tabName);
        toggleButton.setTag(tag);
        toggleButton.setOnClickListener(new onClickToggleButton());
        toggleButtons.add(toggleButton);
        return tabView;
    }

    public void clickReturnButton(View view) {
        finish();
    }

    class onClickToggleButton implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            int tagNo = (int) view.getTag();
            categoryTab.getTabAt(tagNo % eleSize).select();
        }
    }

    private void updateCartTotalPrice() {
        int uTotalPrice = 0;
        TextView totalPriceView = this.findViewById(R.id.total_price);
        for (CartMenu cartMenu :
                this.cartMenuList) {
            uTotalPrice += cartMenu.getTotalPrice();
        }
        totalPriceView.setTag(uTotalPrice);
        String text = Integer.toString(uTotalPrice) + " ???";
        totalPriceView.setText(text);
        totalPriceView.setTextSize(70f);
        totalPrice = uTotalPrice;
    }

    public void delCartMenuList(int position) {
        this.cartMenuList.remove(position);
        cartListAdapter.notifyDataSetChanged();

        this.updateCartTotalPrice();
    }

    public void addCartMenuList(CartMenu cartMenu) {
        this.cartMenuList.add(cartMenu);
        cartListAdapter.notifyDataSetChanged();

        this.updateCartTotalPrice();
    }

    public void setCartMenuList(List<CartMenu> cartMenuList) {
        this.cartMenuList = cartMenuList;
        cartListAdapter.notifyDataSetChanged();
        Log.d("????????????: ", this.cartMenuList.toString());

        this.updateCartTotalPrice();
    }

    public void clickSearchIcon(View view) {
        Log.d("????????????", Boolean.toString(searchButtonClicked));
        if (this.searchButtonClicked)
            return;
        this.searchButtonClicked = true;
        Intent intent = new Intent(KioskListActivity.this, SearchActivity.class);
        startActivityForResult(intent, 2);
    }

    private void setAdapter() {
        // ????????? ?????? ????????? ??????
        cartListAdapter = new CartListAdapter(cartMenuList);
    }

    // ?????? ????????? ????????? ????????? ???????????? ?????????????????? ??????
    class categoryTabClickListener implements TabLayout.OnTabSelectedListener {
        @Override
        public void onTabSelected(TabLayout.Tab tab) {
            int tagNo = tab.getPosition();
            itemListFragment = new ItemListFragment(dbQueryController.getMenuList(categories.get(tagNo + eleSize * categoryPage)));
            fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.list_fragment, itemListFragment).commitAllowingStateLoss();

            for (int i = 0; i < toggleButtons.size(); i++) {
                int index = i + categoryPage * eleSize;
                toggleButtons.get(i).setChecked(false);
                toggleButtons.get(i).setBackgroundColor(Color.parseColor("#ffffff"));
                toggleButtons.get(i).setTextColor(Color.parseColor("#081832"));
                toggleButtons.get(i).setText(categories.get(index).getName());
            }

            KioskListActivity.this.toggleButtons.get(tagNo).setChecked(true);
            KioskListActivity.this.toggleButtons.get(tagNo).setBackgroundResource(R.drawable.togglebutton_on);
            KioskListActivity.this.toggleButtons.get(tagNo).setTextColor(Color.parseColor("#ffffff"));
            KioskListActivity.this.toggleButtons.get(tagNo).setText(categories.get(tagNo + eleSize * categoryPage).getName());
        }

        @Override
        public void onTabUnselected(TabLayout.Tab tab) {

        }

        @Override
        public void onTabReselected(TabLayout.Tab tab) {

        }
    }

    class purchaseButtonClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            if (cartMenuList.isEmpty())
                return;

            System.out.println("????????????: ?????????");

            (new HttpCheckThread()).start();
            view.setEnabled(false);
        }
    }

    class HttpCheckThread extends Thread {
        @Override
        public void run() {
            try {
                SocketAddress sockaddr = new InetSocketAddress("192.168.0.15", 8080);
                // Create an unbound socket
                Socket sock = new Socket();

                // This method will block no more than timeoutMs.
                // If the timeout occurs, SocketTimeoutException is thrown.
                int timeoutMs = 2000;   // 2 seconds
                sock.connect(sockaddr, timeoutMs);
                doPurchase();
            } catch(IOException e) {
                connectionFailed();
            }
        }
    }

    public void connectionFailed() {
        Toast.makeText(this.getApplicationContext(), "????????? ????????? ??????????????????. ???????????? ???????????????/", Toast.LENGTH_SHORT).show();
        finish();
    }

    public void doPurchase() {
        //custom dialog
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_custom, null);
        TextView price_tv = dialogView.findViewById(R.id.price_text);
        TextView totalPriceView = KioskListActivity.this.findViewById(R.id.total_price);
        price_tv.setText(totalPriceView.getText().toString());
//            View dialogView = getLayoutInflater().inflate(R.layout.dialog_custom, null);
//            TextView price_tv = dialogView.findViewById(R.id.price_text);
//            TextView totalPriceView = KioskListActivity.this.findViewById(R.id.total_price);
//            // Log.d("???????????????", (String) totalPriceView.getText());
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
//                    // ?????????????????? ????????? ??????
//                    final Gson gson = new GsonBuilder()
//                            .excludeFieldsWithoutExposeAnnotation()
//                            .create();
//                    JSONObject jsonObject = new JSONObject();
//                    try {
//                        //                                       ??? ?????? ?????????
//                        jsonObject.put("totalPrice", totalPriceView.getTag());
//                        jsonObject.put("menus", new JSONArray(gson.toJson(cartMenuList,
//                                new TypeToken<List<CartMenu>>() {
//                                }.getType())));
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                    //Log.d("??????", jsonObject.toString());
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
//                    Toast.makeText(KioskListActivity.this, "????????? ?????????????????????.", Toast.LENGTH_SHORT).show();
//                    alertDialog.dismiss();
//                }
//            });
        //todo master??? merge ?????? intent result?????? ????????? ?????? ???
        //purchaseButtonClicked = false;

        setTranData("D1", "");
    }


    // ?????? ?????? ??????????????????
    public void setTranData(String tran_types, String ADD_FIELD){

        ComponentName compName = new ComponentName("kr.co.kicc.easycarda","kr.co.kicc.easycarda.CallPopup");

        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.putExtra("TRAN_NO", "1"); 		// ?????? ????????? ?????? ??????
        intent.putExtra("TRAN_TYPE", tran_types);							// ?????? ??????. 'D1' ??????. 'D4' ?????? ??????
        if("M8".equals(tran_types)) {												// ????????? ?????? ??????. ?????? ??????
            intent.putExtra("TERMINAL_TYPE", "CE");
            intent.putExtra("TEXT_DECLINE", "????????? ????????? ?????????????????????");
        }
        else {
            intent.putExtra("TERMINAL_TYPE", "40");					// '40' = ?????? ??????
        }
        intent.putExtra("TOTAL_AMOUNT", totalPriceView.getTag().toString());	// ??? ??????
        intent.putExtra("TAX", "0");				// ?????? ??????
        intent.putExtra("TAX_OPTION","A");								// M = manual. ??????????????? ??????.  A = auto. 10% ???????????? ??????
        intent.putExtra("TIP", "0");				// ??? ??????
        //intent.putExtra("TIP_OPTION","N");
//        if("D4".equals(tran_types)||"B2".equals(tran_types) || "B4".equals(tran_types)) {	// ?????? ?????? ??????
//            intent.putExtra("APPROVAL_NUM", this.appr_num.getText().toString());
//            intent.putExtra("APPROVAL_DATE", this.appr_date.getText().toString());
//            intent.putExtra("TRAN_SERIALNO", this.tran_serialno.getText().toString());
//        }
        if("B1".equals(tran_types) || "B2".equals(tran_types) || "B3".equals(tran_types) || "B4".equals(tran_types)) { // ?????? ?????? ??????
            intent.putExtra("CASHAMOUNT", "00");
        }else {
            intent.putExtra("INSTALLMENT", "0"); // ????????????. 0 = ?????????
        }
//        if("PT".equals(tran_types)){ 															// ????????? ??????. ?????? ?????? ??????
//            String printmsg = printMessage.receiptPrint("160516120000", "IC????????????", false, false, 1004, 91, 0, 0, "1234-56**-****-1234", "????????????", "1234567890", "?????????", "02-1234-5678", "1234567", "?????? ???????????? ????????????", "?????????", "00001111", "", "", "12345678", "?????????", "", "", true);
//            try {
//                intent.putExtra("PRINT_DATA", printmsg.getBytes("EUC-KR"));
//            } catch (UnsupportedEncodingException e) {
//                e.printStackTrace();
//            }
//        }
        intent.putExtra("ADD_FIELD", ADD_FIELD);							// ????????????
        intent.putExtra("TIMEOUT","80");							// ?????? ?????? ??? ???????????? ??????

        intent.putExtra("TEXT_PROCESS","?????? ??????????????????");		// ?????? ????????? ??????
        intent.putExtra("TEXT_COMPLETE", "????????? ?????????????????????");	// ?????? ????????? ??????
//		intent.putExtra("TEXT_FALLBACK", "????????? ????????????????????????\n???????????????");
//
//		intent.putExtra("TEXT_MAIN_SIZE", 60);
//		intent.putExtra("IMG_BG_WIDTH", 1000);
//		intent.putExtra("IMG_CARD_WIDTH", 600);
//		intent.putExtra("IMG_CLOSE_WIDTH", 100);

//		intent.putExtra("FALLBACK_FLAG","N");

		// ?????? ??????????????????
        intent.putExtra("IMG_BG_PATH", "/sdcard/kicc/background_kicc.png");
        intent.putExtra("IMG_CARD_PATH", "/sdcard/kicc/card_kicc.png");
        intent.putExtra("IMG_CLOSE_PATH", "/sdcard/kicc/close_kicc.png");
        intent.putExtra("TEXT_MAIN_SIZE", 18);
        intent.putExtra("TEXT_MAIN_COLOR", "#303030");
        intent.putExtra("TEXT_SUB1_SIZE", 12);
        intent.putExtra("TEXT_SUB1_COLOR", "#ff752a");
        intent.putExtra("TEXT_SUB2_SIZE", 10);
        intent.putExtra("TEXT_SUB2_COLOR", "#ff752a");
        intent.putExtra("TEXT_SUB3_SIZE", 16);
        intent.putExtra("TEXT_SUB3_COLOR", "#909090");


//		if(barcode.getText().toString().isEmpty()==false)
//		{
//			intent.putExtra("DONGLE_FLAG",this.dongleflag.getText().toString());
//			intent.putExtra("BARCODE",this.barcode.getText().toString());
//
//		}

        intent.setComponent(compName);
        printInent(intent);
        startActivityForResult(intent, CARD_INTENT_NUM);
    }

    public static void printInent(Intent i) {
        try {
            //Log.e("KTC","-------------------------------------------------------");
            //util.saveLog("-------------------------------------------------------");
            if (i != null) {
                Bundle extras = i.getExtras();
                if (extras != null) {
                    Set keys = extras.keySet();

                    for (String _key : extras.keySet()) {
                        Log.e("KTC","key=" + _key + " : " + extras.get(_key));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void popUpOrderNumberAndQuit(int orderNumber, boolean orderSucceess) {
        i2.putExtra("orderNumber", orderNumber);
        startActivity(i2);
        //Todo
        if (orderSucceess) {
            onClick_usb_text2(orderNumber);
        }
        finish();
    }

    //????????? ??????-------------------------
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void onClick_usb_text2(int orderNumber) {
        char device_cnt;
        String orderNum;
        String mCafeName;
        String mCafeInfo;
        String mBar;
        String mPriceInfo;
        String mPrice;
        String mTotalPrice;
        String mEndMsg;
        String mStr;

        String mCardInfo;

        StringBuilder mStrBodyBuilder;
        int mIntBuf[] ;
        int mDataCnt,i;
        byte mByteBuf[];

        orderNum = "???????????? : " + Integer.toString(orderNumber) + "\n";
        mCafeName = "\n11?????? ?????????\n";
        mCafeInfo = "????????? ?????? ????????? 93 11?????? 305???\n"+
                "TEL.052-220-5757\n";

        mBar = " ------------------------ \n";
        mPriceInfo = "   ??????\t\t\t\t??????\t    ??????\n";;



        HashMap<String, Integer> quantityTable = new HashMap<String, Integer>();
        HashMap<String, Integer> priceTable = new HashMap<>();
        for (CartMenu cartMenu : cartMenuList) {
            String name = cartMenu.getName();
            quantityTable.put(name, quantityTable.getOrDefault(name, 0) + 1);
            priceTable.put(name, priceTable.getOrDefault(name, 0) + cartMenu.getTotalPrice());
        }

        mStrBodyBuilder = new StringBuilder();
        for (String name : quantityTable.keySet()) {
            if (name.length() > 6) {
                mStrBodyBuilder.append(String.format("  %s\t\t%d\t    \\%d\n", name, quantityTable.get(name), priceTable.get(name)));
            } else {
                mStrBodyBuilder.append(String.format("  %s\t\t\t%d\t    \\%d\n", name, quantityTable.get(name), priceTable.get(name)));
            }
        }

        mPrice = mStrBodyBuilder.toString();

        // set Hwasung Syatem usb device
        device_cnt = setDevice();

        if(device_cnt != 0 ) {
            //Todo ???????????? ??????
            //centering print position
            mIntBuf = new int[3];
            mIntBuf[0] = CMD_ESC;
            mIntBuf[1] = 0x61;
            mIntBuf[2] = 0x01;
            mDataCnt = 3;
            // call send data
            sendCommand(mDevice,mIntBuf,mDataCnt);

            // ???????????? 2??? ????????? ??????
            mIntBuf = new int[8];
            mIntBuf[0] = CMD_GS;
            mIntBuf[1] = '!';
            mIntBuf[2] = 0x11;
            mDataCnt = 3;

            // ????????? ?????? ??????
            sendCommand(mDevice, mIntBuf, mDataCnt);

            //???????????? ??????
            mByteBuf = orderNum.getBytes(Charset.forName("EUC-KR"));
            mDataCnt = mByteBuf.length;
            mIntBuf = new int[mDataCnt];
            // ?????? ????????? ????????? ??????
            for(i=0;i<mDataCnt;i++){
                mIntBuf[i] = (int) mByteBuf[i];
            }
            // ????????? ?????? ??????
            sendCommand(mDevice, mIntBuf, mDataCnt);

            // ?????? 2??????????????? ??????
            mIntBuf[0] = CMD_GS;
            mIntBuf[1] = '!';
            mIntBuf[2] = 0x01;
            mDataCnt = 3;

            // ????????? ?????? ??????
            sendCommand(mDevice,mIntBuf,mDataCnt);

            //Todo mCafeName ??????
            //mCafeName ??????
            mByteBuf = mCafeName.getBytes(Charset.forName("EUC-KR"));
            mDataCnt = mByteBuf.length;
            mIntBuf = new int[mDataCnt];

            // set data to integer buffer
            for(i=0;i<mDataCnt;i++) {
                mIntBuf[i] = (int) mByteBuf[i];
            }
            // call send data
            sendCommand(mDevice,mIntBuf,mDataCnt);

            //2??? ????????? ???
            mIntBuf[0] = CMD_GS;
            mIntBuf[1] = '!';
            mIntBuf[2] = 0x00;
            mDataCnt = 3;

            // ????????? ?????? ??????
            sendCommand(mDevice,mIntBuf,mDataCnt);

            //Todo mCafeInfo ??????
            //mCafeInfo ??????
            mByteBuf = mCafeInfo.getBytes(Charset.forName("EUC-KR"));
            mDataCnt = mByteBuf.length;
            mIntBuf = new int[mDataCnt];

            for(i=0;i<mDataCnt;i++) {
                mIntBuf[i] = (int) mByteBuf[i];
            }
            sendCommand(mDevice,mIntBuf,mDataCnt);

            // ???????????? ???
            mIntBuf[0] = CMD_ESC;
            mIntBuf[1] = 0x61;
            mIntBuf[2] = 0x00;
            mDataCnt = 3;
            sendCommand(mDevice, mIntBuf, mDataCnt);

            //Todo mBar??????
            //2??? ????????? ???
            mIntBuf[0] = CMD_GS;
            mIntBuf[1] = '!';
            mIntBuf[2] = 0x11;
            mDataCnt = 3;

            // ????????? ?????? ??????
            sendCommand(mDevice,mIntBuf,mDataCnt);

            //call mBar
            mByteBuf = mBar.getBytes(Charset.forName("EUC-KR"));
            mDataCnt = mByteBuf.length;
            mIntBuf = new int[mDataCnt];

            // set data to integer buffer
            for(i=0;i<mDataCnt;i++) {
                mIntBuf[i] = (int) mByteBuf[i];
            }
            // call send data
            sendCommand(mDevice,mIntBuf,mDataCnt);

            //2??? ????????? ???
            mIntBuf[0] = CMD_GS;
            mIntBuf[1] = '!';
            mIntBuf[2] = 0x00;
            mDataCnt = 3;

            // ????????? ?????? ??????
            sendCommand(mDevice,mIntBuf,mDataCnt);
            //Todo mPriceInfo ??????

            mByteBuf = mPriceInfo.getBytes(Charset.forName("EUC-KR"));
            mDataCnt = mByteBuf.length;
            mIntBuf = new int[mDataCnt];

            for(i=0;i<mDataCnt;i++){
                mIntBuf[i] = (int) mByteBuf[i];
            }

            sendCommand(mDevice, mIntBuf, mDataCnt);
            //Todo mBar ??????

            // ?????? 2???
            mIntBuf[0] = CMD_GS;
            mIntBuf[1] = '!';
            mIntBuf[2] = 0x11;
            mDataCnt = 3;
            // call send data
            sendCommand(mDevice,mIntBuf,mDataCnt);

            // mBar ??????
            mByteBuf = mBar.getBytes(Charset.forName("EUC-KR"));
            mDataCnt = mByteBuf.length;
            mIntBuf = new int[mDataCnt];
            // set data to integer buffer
            for(i=0;i<mDataCnt;i++) {
                mIntBuf[i] = (int) mByteBuf[i];
            }
            // call send data
            sendCommand(mDevice,mIntBuf,mDataCnt);

            mIntBuf[0] = CMD_GS;
            mIntBuf[1] = '!';
            mIntBuf[2] = 0x00;
            mDataCnt = 3;
            // call send data
            sendCommand(mDevice,mIntBuf,mDataCnt);

            // Todo mPrice ??????
            // Bold On
            mIntBuf[0] = CMD_ESC;
            mIntBuf[1] = 0x45;
            mIntBuf[2] = 0x01;
            mDataCnt = 3;
            sendCommand(mDevice,mIntBuf,mDataCnt);

            mByteBuf = mPrice.getBytes(Charset.forName("EUC-KR"));
            mDataCnt = mByteBuf.length;
            mIntBuf = new int[mDataCnt];
            // set data to integer buffer
            for(i=0;i<mDataCnt;i++) {
                mIntBuf[i] = (int) mByteBuf[i];
            }
            // call send data
            sendCommand(mDevice,mIntBuf,mDataCnt);

            //Bold Off
            mIntBuf[0] = CMD_ESC;
            mIntBuf[1] = 0x45;
            mIntBuf[2] = 0x00;
            mDataCnt = 3;
            sendCommand(mDevice,mIntBuf,mDataCnt);

            // Todo mBar
            mIntBuf[0] = CMD_GS;
            mIntBuf[1] = '!';
            mIntBuf[2] = 0x10;
            mDataCnt = 3;
            // call send data
            sendCommand(mDevice,mIntBuf,mDataCnt);

            // mBar ??????
            mByteBuf = mBar.getBytes(Charset.forName("EUC-KR"));
            mDataCnt = mByteBuf.length;
            mIntBuf = new int[mDataCnt];
            // set data to integer buffer
            for(i=0;i<mDataCnt;i++) {
                mIntBuf[i] = (int) mByteBuf[i];
            }
            // call send data
            sendCommand(mDevice,mIntBuf,mDataCnt);



            // Todo mTotalPrice
            int p = Math.toIntExact(Math.round(totalPrice / 1.1));

            mIntBuf[0] = CMD_ESC;
            mIntBuf[1] = 0x61;
            mIntBuf[2] = 0x02;
            mDataCnt = 3;
            sendCommand(mDevice, mIntBuf, mDataCnt);

            mIntBuf[0] = CMD_GS;
            mIntBuf[1] = '!';
            mIntBuf[2] = 0x11;
            mDataCnt = 3;
            sendCommand(mDevice, mIntBuf, mDataCnt);


            mTotalPrice = "  ???????????? : ";
            mTotalPrice += totalPrice + " ??? \n";
            mByteBuf = mTotalPrice.getBytes(Charset.forName("EUC-KR"));
            mDataCnt = mByteBuf.length;
            mIntBuf = new int[mDataCnt];
            // set data to integer buffer
            for(i=0;i<mDataCnt;i++) {
                mIntBuf[i] = (int) mByteBuf[i];
            }
            // call send data
            sendCommand(mDevice,mIntBuf,mDataCnt);


            mTotalPrice = "  ???????????? : ";

            mTotalPrice += p + " ??? \n";

            mByteBuf = mTotalPrice.getBytes(Charset.forName("EUC-KR"));
            mDataCnt = mByteBuf.length;
            mIntBuf = new int[mDataCnt];
            // set data to integer buffer
            for(i=0;i<mDataCnt;i++) {
                mIntBuf[i] = (int) mByteBuf[i];
            }
            // call send data
            sendCommand(mDevice,mIntBuf,mDataCnt);


            mTotalPrice = "   ????????? : ";

            mTotalPrice += totalPrice-p + " ??? \n";
            mByteBuf = mTotalPrice.getBytes(Charset.forName("EUC-KR"));
            mDataCnt = mByteBuf.length;
            mIntBuf = new int[mDataCnt];
            // set data to integer buffer
            for(i=0;i<mDataCnt;i++) {
                mIntBuf[i] = (int) mByteBuf[i];
            }
            // call send data
            sendCommand(mDevice,mIntBuf,mDataCnt);


            // Size width 2x
            mIntBuf[0] = CMD_GS;
            mIntBuf[1] = '!';
            mIntBuf[2] = 0x10;
            mDataCnt = 3;
            // call send data
            sendCommand(mDevice,mIntBuf,mDataCnt);

            mIntBuf[0] = CMD_ESC;
            mIntBuf[1] = 0x61;
            mIntBuf[2] = 0x00;
            mDataCnt = 3;
            sendCommand(mDevice, mIntBuf, mDataCnt);


            // Todo mBar
            mByteBuf = mBar.getBytes(Charset.forName("EUC-KR"));
            mDataCnt = mByteBuf.length;
            mIntBuf = new int[mDataCnt];
            // set data to integer buffer
            for(i=0;i<mDataCnt;i++) {
                mIntBuf[i] = (int) mByteBuf[i];
            }
            // call send data
            sendCommand(mDevice,mIntBuf,mDataCnt);

            // Todo ????????? ??????
            //centering print position
            mIntBuf[0] = CMD_ESC;
            mIntBuf[1] = 0x61;
            mIntBuf[2] = 0x01;
            mDataCnt = 3;
            // call send data
            sendCommand(mDevice,mIntBuf,mDataCnt);

            // Size height 2x
            mIntBuf[0] = CMD_GS;
            mIntBuf[1] = '!';
            mIntBuf[2] = 0x01;
            mDataCnt = 3;
            // call send data
            sendCommand(mDevice,mIntBuf,mDataCnt);

            mStr = "\n???????????? ?????? ??????\n";



            mByteBuf = mStr.getBytes(Charset.forName("EUC-KR"));
            mDataCnt = mByteBuf.length;
            mIntBuf = new int[mDataCnt];
            // set data to integer buffer
            for(i=0;i<mDataCnt;i++) {
                mIntBuf[i] = (int) mByteBuf[i];
            }
            // call send data
            sendCommand(mDevice,mIntBuf,mDataCnt);

            // Size 1x
            mIntBuf[0] = CMD_GS;
            mIntBuf[1] = '!';
            mIntBuf[2] = 0x00;
            mDataCnt = 3;
            // call send data
            sendCommand(mDevice,mIntBuf,mDataCnt);

            // Bold On
            mIntBuf[0] = CMD_ESC;
            mIntBuf[1] = 0x45;
            mIntBuf[2] = 0x01;
            mDataCnt = 3;
            sendCommand(mDevice,mIntBuf,mDataCnt);

            mStr = "?????????: ";
            mByteBuf = mStr.getBytes(Charset.forName("EUC-KR"));
            mDataCnt = mByteBuf.length;
            mIntBuf = new int[mDataCnt];
            // set data to integer buffer
            for(i=0;i<mDataCnt;i++) {
                mIntBuf[i] = (int) mByteBuf[i];
            }
            // call send data
            sendCommand(mDevice,mIntBuf,mDataCnt);

            // Bold Off
            mIntBuf[0] = CMD_ESC;
            mIntBuf[1] = 0x45;
            mIntBuf[2] = 0x00;
            mDataCnt = 3;
            sendCommand(mDevice,mIntBuf,mDataCnt);

            mStr = card_name+"\n";
            mByteBuf = mStr.getBytes(Charset.forName("EUC-KR"));
            mDataCnt = mByteBuf.length;
            mIntBuf = new int[mDataCnt];
            // set data to integer buffer
            for(i=0;i<mDataCnt;i++) {
                mIntBuf[i] = (int) mByteBuf[i];
            }
            // call send data
            sendCommand(mDevice,mIntBuf,mDataCnt);


            mIntBuf[0] = CMD_ESC;
            mIntBuf[1] = 0x45;
            mIntBuf[2] = 0x01;
            mDataCnt = 3;
            sendCommand(mDevice,mIntBuf,mDataCnt);

            mStr = "???????????? : ";
            mByteBuf = mStr.getBytes(Charset.forName("EUC-KR"));
            mDataCnt = mByteBuf.length;
            mIntBuf = new int[mDataCnt];
            // set data to integer buffer
            for(i=0;i<mDataCnt;i++) {
                mIntBuf[i] = (int) mByteBuf[i];
            }
            // call send data
            sendCommand(mDevice,mIntBuf,mDataCnt);

            // Bold Off
            mIntBuf[0] = CMD_ESC;
            mIntBuf[1] = 0x45;
            mIntBuf[2] = 0x00;
            mDataCnt = 3;
            sendCommand(mDevice,mIntBuf,mDataCnt);

            mStr = card_num+"**********\n";
            mByteBuf = mStr.getBytes(Charset.forName("EUC-KR"));
            mDataCnt = mByteBuf.length;
            mIntBuf = new int[mDataCnt];
            // set data to integer buffer
            for(i=0;i<mDataCnt;i++) {
                mIntBuf[i] = (int) mByteBuf[i];
            }
            // call send data
            sendCommand(mDevice,mIntBuf,mDataCnt);

            mIntBuf[0] = CMD_ESC;
            mIntBuf[1] = 0x45;
            mIntBuf[2] = 0x01;
            mDataCnt = 3;
            sendCommand(mDevice,mIntBuf,mDataCnt);

            mIntBuf[0] = CMD_ESC;
            mIntBuf[1] = 0x45;
            mIntBuf[2] = 0x01;
            mDataCnt = 3;
            sendCommand(mDevice,mIntBuf,mDataCnt);

            mStr = "???????????? : ";
            mByteBuf = mStr.getBytes(Charset.forName("EUC-KR"));
            mDataCnt = mByteBuf.length;
            mIntBuf = new int[mDataCnt];
            // set data to integer buffer
            for(i=0;i<mDataCnt;i++) {
                mIntBuf[i] = (int) mByteBuf[i];
            }
            // call send data
            sendCommand(mDevice,mIntBuf,mDataCnt);

            // Bold Off
            mIntBuf[0] = CMD_ESC;
            mIntBuf[1] = 0x45;
            mIntBuf[2] = 0x00;
            mDataCnt = 3;
            sendCommand(mDevice,mIntBuf,mDataCnt);

            mStr = acc_name+"\n";
            mByteBuf = mStr.getBytes(Charset.forName("EUC-KR"));
            mDataCnt = mByteBuf.length;
            mIntBuf = new int[mDataCnt];
            // set data to integer buffer
            for(i=0;i<mDataCnt;i++) {
                mIntBuf[i] = (int) mByteBuf[i];
            }
            // call send data
            sendCommand(mDevice,mIntBuf,mDataCnt);

            mIntBuf[0] = CMD_ESC;
            mIntBuf[1] = 0x45;
            mIntBuf[2] = 0x01;
            mDataCnt = 3;
            sendCommand(mDevice,mIntBuf,mDataCnt);

            mStr = "\n??? ??? ??? : ";
            mByteBuf = mStr.getBytes(Charset.forName("EUC-KR"));
            mDataCnt = mByteBuf.length;
            mIntBuf = new int[mDataCnt];
            // set data to integer buffer
            for(i=0;i<mDataCnt;i++) {
                mIntBuf[i] = (int) mByteBuf[i];
            }
            // call send data
            sendCommand(mDevice,mIntBuf,mDataCnt);

            // Bold Off
            mIntBuf[0] = CMD_ESC;
            mIntBuf[1] = 0x45;
            mIntBuf[2] = 0x00;
            mDataCnt = 3;
            sendCommand(mDevice,mIntBuf,mDataCnt);

            mStr = "691-85-00176 ?????????\n";
            mByteBuf = mStr.getBytes(Charset.forName("EUC-KR"));
            mDataCnt = mByteBuf.length;
            mIntBuf = new int[mDataCnt];
            // set data to integer buffer
            for(i=0;i<mDataCnt;i++) {
                mIntBuf[i] = (int) mByteBuf[i];
            }
            // call send data
            sendCommand(mDevice,mIntBuf,mDataCnt);// Bold On

            mIntBuf[0] = CMD_ESC;
            mIntBuf[1] = 0x45;
            mIntBuf[2] = 0x01;
            mDataCnt = 3;
            sendCommand(mDevice,mIntBuf,mDataCnt);

            mStr = "???????????? : ";
            mByteBuf = mStr.getBytes(Charset.forName("EUC-KR"));
            mDataCnt = mByteBuf.length;
            mIntBuf = new int[mDataCnt];
            // set data to integer buffer
            for(i=0;i<mDataCnt;i++) {
                mIntBuf[i] = (int) mByteBuf[i];
            }
            // call send data
            sendCommand(mDevice,mIntBuf,mDataCnt);

            // Bold Off
            mIntBuf[0] = CMD_ESC;
            mIntBuf[1] = 0x45;
            mIntBuf[2] = 0x00;
            mDataCnt = 3;
            sendCommand(mDevice,mIntBuf,mDataCnt);

            mStr = "11?????? ?????????\n";
            mByteBuf = mStr.getBytes(Charset.forName("EUC-KR"));
            mDataCnt = mByteBuf.length;
            mIntBuf = new int[mDataCnt];
            // set data to integer buffer
            for(i=0;i<mDataCnt;i++) {
                mIntBuf[i] = (int) mByteBuf[i];
            }
            // call send data
            sendCommand(mDevice,mIntBuf,mDataCnt);

            mIntBuf[0] = CMD_ESC;
            mIntBuf[1] = 0x45;
            mIntBuf[2] = 0x01;
            mDataCnt = 3;
            sendCommand(mDevice,mIntBuf,mDataCnt);

            mStr = "???????????? : ";
            mByteBuf = mStr.getBytes(Charset.forName("EUC-KR"));
            mDataCnt = mByteBuf.length;
            mIntBuf = new int[mDataCnt];
            // set data to integer buffer
            for(i=0;i<mDataCnt;i++) {
                mIntBuf[i] = (int) mByteBuf[i];
            }
            // call send data
            sendCommand(mDevice,mIntBuf,mDataCnt);

            // Bold Off
            mIntBuf[0] = CMD_ESC;
            mIntBuf[1] = 0x45;
            mIntBuf[2] = 0x00;
            mDataCnt = 3;
            sendCommand(mDevice,mIntBuf,mDataCnt);

            mStr = "157431024\n";
            mByteBuf = mStr.getBytes(Charset.forName("EUC-KR"));
            mDataCnt = mByteBuf.length;
            mIntBuf = new int[mDataCnt];
            // set data to integer buffer
            for(i=0;i<mDataCnt;i++) {
                mIntBuf[i] = (int) mByteBuf[i];
            }
            // call send data
            sendCommand(mDevice,mIntBuf,mDataCnt);

            mIntBuf[0] = CMD_ESC;
            mIntBuf[1] = 0x45;
            mIntBuf[2] = 0x01;
            mDataCnt = 3;
            sendCommand(mDevice,mIntBuf,mDataCnt);




            mStr = "???????????? : ";
            mByteBuf = mStr.getBytes(Charset.forName("EUC-KR"));
            mDataCnt = mByteBuf.length;
            mIntBuf = new int[mDataCnt];
            // set data to integer buffer
            for(i=0;i<mDataCnt;i++) {
                mIntBuf[i] = (int) mByteBuf[i];
            }
            // call send data
            sendCommand(mDevice,mIntBuf,mDataCnt);

            // Bold Off
            mIntBuf[0] = CMD_ESC;
            mIntBuf[1] = 0x45;
            mIntBuf[2] = 0x00;
            mDataCnt = 3;
            sendCommand(mDevice,mIntBuf,mDataCnt);

            // Todo ???????????? ?????????
            mStr = card_approval_date+"\n";
            mByteBuf = mStr.getBytes(Charset.forName("EUC-KR"));
            mDataCnt = mByteBuf.length;
            mIntBuf = new int[mDataCnt];
            // set data to integer buffer
            for(i=0;i<mDataCnt;i++) {
                mIntBuf[i] = (int) mByteBuf[i];
            }
            // call send data
            sendCommand(mDevice,mIntBuf,mDataCnt);

            mIntBuf[0] = CMD_ESC;
            mIntBuf[1] = 0x45;
            mIntBuf[2] = 0x01;
            mDataCnt = 3;
            sendCommand(mDevice,mIntBuf,mDataCnt);



            mStr = "???????????? : ";
            mByteBuf = mStr.getBytes(Charset.forName("EUC-KR"));
            mDataCnt = mByteBuf.length;
            mIntBuf = new int[mDataCnt];
            // set data to integer buffer
            for(i=0;i<mDataCnt;i++) {
                mIntBuf[i] = (int) mByteBuf[i];
            }
            // call send data
            sendCommand(mDevice,mIntBuf,mDataCnt);

            // Bold Off
            mIntBuf[0] = CMD_ESC;
            mIntBuf[1] = 0x45;
            mIntBuf[2] = 0x00;
            mDataCnt = 3;
            sendCommand(mDevice,mIntBuf,mDataCnt);

            mStr = card_approval_num+"\n";
            mByteBuf = mStr.getBytes(Charset.forName("EUC-KR"));
            mDataCnt = mByteBuf.length;
            mIntBuf = new int[mDataCnt];
            // set data to integer buffer
            for(i=0;i<mDataCnt;i++) {
                mIntBuf[i] = (int) mByteBuf[i];
            }
            // call send data
            sendCommand(mDevice,mIntBuf,mDataCnt);

            mIntBuf[0] = CMD_ESC;
            mIntBuf[1] = 0x45;
            mIntBuf[2] = 0x01;
            mDataCnt = 3;
            sendCommand(mDevice,mIntBuf,mDataCnt);

            mStr = "????????? : ";
            mByteBuf = mStr.getBytes(Charset.forName("EUC-KR"));
            mDataCnt = mByteBuf.length;
            mIntBuf = new int[mDataCnt];
            // set data to integer buffer
            for(i=0;i<mDataCnt;i++) {
                mIntBuf[i] = (int) mByteBuf[i];
            }
            // call send data
            sendCommand(mDevice,mIntBuf,mDataCnt);

            // Bold Off
            mIntBuf[0] = CMD_ESC;
            mIntBuf[1] = 0x45;
            mIntBuf[2] = 0x00;
            mDataCnt = 3;
            sendCommand(mDevice,mIntBuf,mDataCnt);

            // ????????? ?????? ???????????????.
            mStr = totalPrice+"\n";
            mByteBuf = mStr.getBytes(Charset.forName("EUC-KR"));
            mDataCnt = mByteBuf.length;
            mIntBuf = new int[mDataCnt];
            // set data to integer buffer
            for(i=0;i<mDataCnt;i++) {
                mIntBuf[i] = (int) mByteBuf[i];
            }
            // call send data
            sendCommand(mDevice,mIntBuf,mDataCnt);

            // clear centering
            mIntBuf[0] = CMD_ESC;
            mIntBuf[1] = 0x61;
            mIntBuf[2] = 0x00;
            mDataCnt = 3;
            sendCommand(mDevice,mIntBuf,mDataCnt);

            // Size Width 2x
            mIntBuf[0] = CMD_GS;
            mIntBuf[1] = '!';
            mIntBuf[2] = 0x10;
            mDataCnt = 3;
            sendCommand(mDevice,mIntBuf,mDataCnt);

            // Todo mBar
            mByteBuf = mBar.getBytes(Charset.forName("EUC-KR"));
            mDataCnt = mByteBuf.length;
            mIntBuf = new int[mDataCnt];
            // set data to integer buffer
            for(i=0;i<mDataCnt;i++) {
                mIntBuf[i] = (int) mByteBuf[i];
            }
            sendCommand(mDevice,mIntBuf,mDataCnt);

            // Size Width 2x
            mIntBuf[0] = CMD_GS;
            mIntBuf[1] = '!';
            mIntBuf[2] = 0x00;
            mDataCnt = 3;
            sendCommand(mDevice,mIntBuf,mDataCnt);

            // centering
            mIntBuf[0] = CMD_ESC;
            mIntBuf[1] = 0x61;
            mIntBuf[2] = 0x01;
            mDataCnt = 3;
            sendCommand(mDevice,mIntBuf,mDataCnt);

            // mEndMsg
            mEndMsg = "?????? 11?????? ????????? ????????? ????????? ???????????????!\n";

            mByteBuf = mEndMsg.getBytes(Charset.forName("EUC-KR"));
            mDataCnt = mByteBuf.length;
            mIntBuf = new int[mDataCnt];
            // set data to integer buffer
            for(i=0;i<mDataCnt;i++) {
                mIntBuf[i] = (int) mByteBuf[i];
            }
            sendCommand(mDevice,mIntBuf,mDataCnt);

            // clear centering
            mIntBuf[0] = CMD_ESC;
            mIntBuf[1] = 0x61;
            mIntBuf[2] = 0x00;
            mDataCnt = 3;
            sendCommand(mDevice,mIntBuf,mDataCnt);

            // feeding
            mIntBuf[0] = 0x0a;
            mIntBuf[1] = 0x0a;
            mIntBuf[2] = 0x0a;
            mIntBuf[3] = 0x0a;
            mIntBuf[4] = 0x0a;
            mDataCnt = 5;
            sendCommand(mDevice,mIntBuf,mDataCnt);

            // full cutting
            mIntBuf[0] = CMD_ESC;
            mIntBuf[1] = 0x69;
            mDataCnt = 2;
            sendCommand(mDevice,mIntBuf,mDataCnt);

            //releaseDevice(device_cnt);

            card_approval_num = null;
            card_approval_date = null;
        }

    }
    //????????? ??????-------------------------
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //????????? ????????? ??? ???
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == CARD_INTENT_NUM) {
                Bundle extras = data.getExtras();

                if ("9977".equals(data.getStringExtra("RESULT_CODE"))) // ?????? ?????? ?????? ??????
                    Toast.makeText(this, "???????????? ?????? ??????", Toast.LENGTH_SHORT).show();

                i2 = new Intent(this, OrderNumberPopupActivity.class);

                if (extras != null) {
                    Set keys = extras.keySet();

                    for (String _key : extras.keySet()) {
                        if (extras.get(_key) == null) {
                            i2.putExtra(_key, "null");
                        } else {
                            if (_key.equals("APPROVAL_NUM")) {
                                card_approval_num = extras.get(_key).toString();
                            } else if (_key.equals("APPROVAL_DATE")) {
                                card_approval_date = extras.get(_key).toString();
                            } else if (_key.equals("CARD_NAME")){
                                card_name = extras.get(_key).toString();
                            } else if (_key.equals("ACQUIRER_NAME")){
                                acc_name = extras.get(_key).toString();
                            } else if (_key.equals("CARD_NUM")){
                                card_num = extras.get(_key).toString();
                            }
                            i2.putExtra(_key, extras.get(_key).toString());
                        }
                    }
                }
//				delayhandler.postDelayed(
//						new Runnable() {
//							// 1 ??? ?????? ??????
//							@Override
//							public void run() {
//								setTranData(tran_type,"");
//							}
//						}, 5000);

                try {
                    Log.d("??????", data.getStringExtra("INSTALLMENT"));
                    Log.d("????????????", data.getStringExtra("APPROVAL_DATE"));
                    Log.d("????????????", data.getStringExtra("APPROVAL_NUM"));
                    Log.d("TRAN_SERIALNO", data.getStringExtra("TRAN_SERIALNO"));
                } catch (Exception e) {
                    e.printStackTrace();
                }

                printInent(data);

                if (extras.get("RESULT_CODE").equals("0000")) {
                    // ?????????????????? ????????? ??????
                    final Gson gson = new GsonBuilder()
                            .excludeFieldsWithoutExposeAnnotation()
                            .create();
                    JSONObject jsonObject = new JSONObject();
                    try {
                        //                                       ??? ?????? ?????????
                        jsonObject.put("totalPrice", totalPriceView.getTag());
                        jsonObject.put("approvalNumber", card_approval_num);
                        jsonObject.put("approvalDate", card_approval_date);
                        Collections.sort(cartMenuList);
                        jsonObject.put("menus", new JSONArray(gson.toJson(cartMenuList,
                                new TypeToken<List<CartMenu>>() {
                                }.getType())));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    //Log.d("??????", jsonObject.toString());
                    // Toast.makeText(KioskListActivity.this, jsonObject.toString(), Toast.LENGTH_LONG).show();
                    httpController.postJsonCartData(jsonObject);
                } else {
                    Toast.makeText(this.getApplicationContext(), "?????? :: " + extras.get("RESULT_CODE").toString(), Toast.LENGTH_SHORT).show();
                    popUpOrderNumberAndQuit(extras.getInt("RESULT_CODE"), false);
                }
            }
        }
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                //????????? ??????
                String result = data.getStringExtra("result");
                if (result != null) {
                    Log.d("?????????", result);
                }
            }
            menuOptionPopupButtonClicked = false;
        }
        if (requestCode == 2) {
            if (resultCode == RESULT_OK) {
                ArrayList<Integer> searchedMenuIdList = data.getIntegerArrayListExtra("searchedMenuIdList");
                List<Menu> queryResult = dbQueryController.getMenuListByIdArray(searchedMenuIdList);
                try {
                    itemListFragment = new ItemListFragment(queryResult);
                    fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.list_fragment, itemListFragment).commitAllowingStateLoss();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            this.searchButtonClicked = false;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        idleTimer.cancel();
    }

    @Override
    protected void onResume() {
        super.onResume();
        idleTimer.start();
        readStatus();
    }
}