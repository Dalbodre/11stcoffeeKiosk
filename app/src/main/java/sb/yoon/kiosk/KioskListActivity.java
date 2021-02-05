package sb.yoon.kiosk;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.CountDownTimer;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
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
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

//배경색 #081832 == 11호관 마크 색상으로 추정
public class KioskListActivity extends AppCompatActivity {
    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;
    private ItemListFragment itemListFragment;
    private List<Category> categories;

    private List<CartMenu> cartMenuList = new ArrayList<>();
    private CartListAdapter cartListAdapter;

    // 프론트에서 쓰기 편하게 DB 관련 메서드들 제공
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

        // DB 컨트롤러 (프론트에서 쓸 메서드들 모음)
        dbQueryController = kioskApplication.getDbQueryController();

        // DB 초기화
        // dbQueryController.initDB();

        // 카테고리 리스트 불러오기
        categories = dbQueryController.getCategoriesList();

        //버튼 객체 미리 생성
        tagNum = 0;

        categorySize = categories.size();

        // 카테고리 버튼들 생성
        //todo
        categoryTab = findViewById(R.id.categoryTab);
        categoryTab.addOnTabSelectedListener(new categoryTabClickListener());

        categoryTab.setSelectedTabIndicatorColor(Color.parseColor("#FFFFFF")); // 언더바 나오길래 안보이도록 만들었습니다.

        leftButton = findViewById(R.id.left_button);
        rightButton = findViewById(R.id.right_button);

        maxCategoryPage = categorySize / eleSize;


        // 기본으로 보여줄 플래그먼트 (첫번째 카테고리)
        fragmentManager = getSupportFragmentManager();
        itemListFragment = new ItemListFragment(dbQueryController.getMenuList(categories.get(0)));
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.list_fragment, itemListFragment).commitAllowingStateLoss();

        // 카트 리사이클러뷰
        RecyclerView cartRecyclerView = this.findViewById(R.id.cart_recycler_list);
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(this);
        mLinearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        cartRecyclerView.setLayoutManager(mLinearLayoutManager);

        cartListAdapter = new CartListAdapter(cartMenuList);
        cartRecyclerView.setAdapter(cartListAdapter);

        // 계산버튼 리스너
        Button purchaseButton = findViewById(R.id.purchase_button);
        purchaseButton.setOnClickListener(new purchaseButtonClickListener());

        updateCategoryTab(categoryPage);

        totalPriceView = this.findViewById(R.id.total_price);

        idleTimer = new IdleTimer(this, 115000, 1000);//잠깐만 115초로 변경좀 해놓겠습니다. -jojo
        idleTimer.start();
    }

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
        int totalPrice = 0;
        TextView totalPriceView = this.findViewById(R.id.total_price);
        for (CartMenu cartMenu :
                this.cartMenuList) {
            totalPrice += cartMenu.getTotalPrice();
        }
        totalPriceView.setTag(totalPrice);
        String text = Integer.toString(totalPrice) + " 원";
        totalPriceView.setText(text);
        totalPriceView.setTextSize(70f);
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
        Log.d("카트에는: ", this.cartMenuList.toString());

        this.updateCartTotalPrice();
    }

    public void clickSearchIcon(View view) {
        if (this.searchButtonClicked)
            return;
        this.searchButtonClicked = true;
        Intent intent = new Intent(KioskListActivity.this, SearchActivity.class);
        startActivityForResult(intent, 2);
    }

    private void setAdapter() {
        // 인덱스 표시 어댑터 설정
        cartListAdapter = new CartListAdapter(cartMenuList);
    }

    // 버튼 누르면 버튼의 순서를 확인하고 플래그먼트에 삽입
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

            System.out.println("결제버튼: 클릭함");


            //custom dialog
            View dialogView = getLayoutInflater().inflate(R.layout.dialog_custom, null);
            TextView price_tv = dialogView.findViewById(R.id.price_text);
            TextView totalPriceView = KioskListActivity.this.findViewById(R.id.total_price);
            price_tv.setText(totalPriceView.getText().toString());
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

            setTranData("D1", "");
        }
    }

    public void setTranData(String tran_types, String ADD_FIELD){

        ComponentName compName = new ComponentName("kr.co.kicc.easycarda","kr.co.kicc.easycarda.CallPopup");

        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.putExtra("TRAN_NO", "1"); 		// 거래 구분을 위한 용도
        intent.putExtra("TRAN_TYPE", tran_types);							// 거래 타입. 'D1' 승인. 'D4' 거래 취소
        if("M8".equals(tran_types)) {												// 포인트 조회 용도. 필요 없음
            intent.putExtra("TERMINAL_TYPE", "CE");
            intent.putExtra("TEXT_DECLINE", "포인트 조회가 거절되었습니다");
        }
        else {
            intent.putExtra("TERMINAL_TYPE", "40");					// '40' = 일반 거래
        }
        intent.putExtra("TOTAL_AMOUNT", totalPriceView.getTag().toString());	// 총 금액
        intent.putExtra("TAX", "0");				// 세금 금액
        intent.putExtra("TAX_OPTION","A");								// M = manual. 입력값으로 처리.  A = auto. 10% 자동으로 계산
        intent.putExtra("TIP", "0");				// 팁 금액
        //intent.putExtra("TIP_OPTION","N");
//        if("D4".equals(tran_types)||"B2".equals(tran_types) || "B4".equals(tran_types)) {	// 필요 없는 부분
//            intent.putExtra("APPROVAL_NUM", this.appr_num.getText().toString());
//            intent.putExtra("APPROVAL_DATE", this.appr_date.getText().toString());
//            intent.putExtra("TRAN_SERIALNO", this.tran_serialno.getText().toString());
//        }
        if("B1".equals(tran_types) || "B2".equals(tran_types) || "B3".equals(tran_types) || "B4".equals(tran_types)) { // 필요 없는 부분
            intent.putExtra("CASHAMOUNT", "00");
        }else {
            intent.putExtra("INSTALLMENT", "0"); // 할부여부. 0 = 일시불
        }
//        if("PT".equals(tran_types)){ 															// 프린트 관련. 현재 필요 없음
//            String printmsg = printMessage.receiptPrint("160516120000", "IC신용구매", false, false, 1004, 91, 0, 0, "1234-56**-****-1234", "테스트점", "1234567890", "홍길동", "02-1234-5678", "1234567", "서울 테스트구 테스트동", "발급사", "00001111", "", "", "12345678", "매입사", "", "", true);
//            try {
//                intent.putExtra("PRINT_DATA", printmsg.getBytes("EUC-KR"));
//            } catch (UnsupportedEncodingException e) {
//                e.printStackTrace();
//            }
//        }
        intent.putExtra("ADD_FIELD", ADD_FIELD);							// 모르겠음
        intent.putExtra("TIMEOUT","30");							// 아마 결제 시 타임아웃 지정

        intent.putExtra("TEXT_PROCESS","결제 진행중입니다");		// 결제 진행중 문구
        intent.putExtra("TEXT_COMPLETE", "결제가 완료되었습니다");	// 결제 완료시 문구
//		intent.putExtra("TEXT_FALLBACK", "카드의 마그네틱부분으로\n읽어주세요");
//
//		intent.putExtra("TEXT_MAIN_SIZE", 60);
//		intent.putExtra("IMG_BG_WIDTH", 1000);
//		intent.putExtra("IMG_CARD_WIDTH", 600);
//		intent.putExtra("IMG_CLOSE_WIDTH", 100);

//		intent.putExtra("FALLBACK_FLAG","N");

		// 테마 커스터마이징
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

    public void popUpOrderNumberAndQuit(int orderNumber) {
        i2.putExtra("orderNumber", orderNumber);
        startActivity(i2);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //데이터 넘겨줄 때 씀
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == CARD_INTENT_NUM) {
                Bundle extras = data.getExtras();

                if ("9977".equals(data.getStringExtra("RESULT_CODE"))) // 승인 처리 시간 종료
                    Toast.makeText(this, "승인처리 시간 종료", Toast.LENGTH_SHORT).show();

                i2 = new Intent(this, OrderNumberPopupActivity.class);

                if (extras != null) {
                    Set keys = extras.keySet();

                    for (String _key : extras.keySet()) {
                        if (extras.get(_key) == null) {
                            i2.putExtra(_key, "null");
                        } else
                            i2.putExtra(_key, extras.get(_key).toString());
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
                    Log.d("할부", data.getStringExtra("INSTALLMENT"));
                    Log.d("승인날짜", data.getStringExtra("APPROVAL_DATE"));
                    Log.d("승인넘버", data.getStringExtra("APPROVAL_NUM"));
                    Log.d("TRAN_SERIALNO", data.getStringExtra("TRAN_SERIALNO"));
                } catch (Exception e) {
                    e.printStackTrace();
                }

                printInent(data);

                if (extras.get("RESULT_CODE").equals("0000")) {
                    // 라즈베리파이 서버에 전송
                    final Gson gson = new GsonBuilder()
                            .excludeFieldsWithoutExposeAnnotation()
                            .create();
                    JSONObject jsonObject = new JSONObject();
                    try {
                        //                                       총 가격 숫자만
                        jsonObject.put("totalPrice", totalPriceView.getTag());
                        jsonObject.put("menus", new JSONArray(gson.toJson(cartMenuList,
                                new TypeToken<List<CartMenu>>() {
                                }.getType())));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    //Log.d("결제", jsonObject.toString());
                    // Toast.makeText(KioskListActivity.this, jsonObject.toString(), Toast.LENGTH_LONG).show();
                    HttpNetworkController httpController = new HttpNetworkController(
                            KioskListActivity.this, getResources().getString(R.string.server_ip));
                    httpController.postJsonCartData(jsonObject);
                } else {
                    Toast.makeText(this.getApplicationContext(), "에러 :: " + extras.get("RESULT_CODE").toString(), Toast.LENGTH_SHORT).show();
                    popUpOrderNumberAndQuit(extras.getInt("RESULT_CODE"));
                }
            }
        } else if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                //데이터 받기
                String result = data.getStringExtra("result");
                if (result != null) {
                    Log.d("데이터", result);
                }
            }
            menuOptionPopupButtonClicked = false;
        } else if (requestCode == 2) {
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
    }
}
