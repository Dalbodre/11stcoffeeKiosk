package sb.yoon.kiosk;

import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import sb.yoon.kiosk.libs.Util;
import sb.yoon.kiosk.model.CartMenu;
import sb.yoon.kiosk.model.CartOption;
import sb.yoon.kiosk.model.Category;
import sb.yoon.kiosk.model.Menu;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

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
    private int categoryPage;
    private List<CategoryButton> buttons = new ArrayList<>();
    private CategoryButton button;
    int categorySize;

    // 카드 모듈 관련
    byte[] mRequestTelegram;

    String mDeviceNo = "";

    String mTotAmt = "";
    String mVat = "";
    String mSupAmt = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kiosk_main);

        KioskApplication kioskApplication = (KioskApplication)getApplication();
        kioskApplication.setKioskListActivity(this);

        // DB 컨트롤러 (프론트에서 쓸 메서드들 모음)
        dbQueryController = kioskApplication.getDbQueryController();

        // DB 초기화
        // dbQueryController.initDB();

        // 카테고리 리스트 불러오기
        categories = dbQueryController.getCategoriesList();
        categoryPage = 0;

        //버튼 객체 미리 생성
        tagNum = 0;

        for (Category category: categories) {
            button = new CategoryButton(this, category.getName(), tagNum);
            Log.d("button", String.valueOf(tagNum));
            Log.d("button", category.getName());
            buttons.add(button);
            tagNum += 1;
        }
        categorySize = buttons.size();
        // 카테고리 버튼들 생성
        this.createCategoryButtons();
        this.updateCategory(categoryPage);

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
    }

    private void createCategoryButtons() {
        GridLayout categoryButtonsGroup = findViewById(R.id.category_list);
        GridLayout.LayoutParams params = new GridLayout.LayoutParams();
        params.setGravity(GridLayout.TEXT_ALIGNMENT_CENTER);
        //params.setMargins(20, 0, 20, 0);

        /*
        검색은 차후에 구현
        // 검색버튼 삽입
        CategoryButton searchButton = new CategoryButton(this);
        Drawable searchIcon = ContextCompat.getDrawable(this, R.drawable.search_icon);
        // 검색 아이콘 삽입 (Drawable Left)
        searchIcon.setBounds(0, 0, 90, 90);
        //searchIcon.setColorFilter(Color.parseColor("#ffffff"), PorterDuff.Mode.MULTIPLY);
        searchButton.setCompoundDrawables(searchIcon, null, null, null);
        searchButton.setText("검색");
        searchButton.setTextSize(60f);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(KioskListActivity.this, SearchActivity.class);
                startActivityForResult(intent, 2);
            }
        });
        categoryButtonsGroup.addView(searchButton, params);
*/

        // 버튼 순서 태그로 지정
        // 4개씩 자름.

        /*for(int i = categoryPage*4 ; i < (categoryPage+1)*4; i++) {
            //카테고리 사이즈보다 i가 클 때 반복문 나감.
            if(i > categorySize) break;

        }*/
        for(int i=0; i<categorySize; i++){
            buttons.get(i).setText(buttons.get(i).getCategoryName());
            buttons.get(i).setTextSize(60f);
            buttons.get(i).setOnClickListener(new categoryButtonClickListener());
            buttons.get(i).setTag(buttons.get(i).getTagNum());
            buttons.get(i).setVisibility(View.GONE);
            categoryButtonsGroup.addView(buttons.get(i));
        }
    }

    private void updateCategory(int categoryPage){
        Button leftBtn = (Button)findViewById(R.id.left_button);
        Button rightBtn = (Button)findViewById(R.id.right_button);
        for(int j = 0; j<categorySize;j++){
            buttons.get(j).setVisibility(View.GONE);
        }
        for(int i = categoryPage*6; i<(categoryPage+1)*6; i++){
            if(i>categorySize)break;
            Log.d("index", String.valueOf(i));
            buttons.get(i).setVisibility(View.VISIBLE);
        }
        if(categoryPage == 0){
            leftBtn.setEnabled(false);
            rightBtn.setEnabled(true);
        }
        else{
            leftBtn.setEnabled(true);
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
        Intent intent = new Intent(KioskListActivity.this, SearchActivity.class);
        startActivityForResult(intent, 2);
    }

    private void setAdapter() {
        // 인덱스 표시 어댑터 설정
        cartListAdapter = new CartListAdapter(cartMenuList);
    }

    public void category_select(View view) {
        switch(view.getId()){
            case R.id.left_button:
                categoryPage--;
                break;
            case R.id.right_button:
                //오른쪽
                categoryPage++;
                break;
        }
        updateCategory(categoryPage);
    }

    // 버튼 누르면 버튼의 순서를 확인하고 플래그먼트에 삽입
    class categoryButtonClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            int tagNo = (int) view.getTag();
            try {
                itemListFragment = new ItemListFragment(dbQueryController.getMenuList(categories.get(tagNo)));
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.list_fragment, itemListFragment).commitAllowingStateLoss();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    class purchaseButtonClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            if (cartMenuList.isEmpty()) {
                return;
            }

            mDeviceNo = "DPT0TEST03"; // 단말기 번호
            makeTelegramIC("1");
            ComponentName componentName = new ComponentName("com.ksnet.kscat_a","com.ksnet.kscat_a.PaymentIntentActivity");
            Intent mIntent = new Intent(Intent.ACTION_MAIN);
            mIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            mIntent.addCategory(Intent.CATEGORY_LAUNCHER);
            mIntent.setComponent(componentName);
            mIntent.putExtra("Telegram", mRequestTelegram);
            mIntent.putExtra("TelegramLength", mRequestTelegram.length);
            mIntent.putExtra("PlayType", "D");           // Daemon Type
            mIntent.putExtra("ApprType", 1);             // 1 : 승인, 0 : 취소
            mIntent.putExtra("TradeCode", "IC");         // IC거래
            startActivityForResult(mIntent, 0);

            String str = Util.HexDump.dumpHexString(mRequestTelegram);
            Log.d("요청전문", str);
        }
    }

    public void popUpOrderNumberAndQuit(int orderNumber) {
        Intent intent = new Intent(KioskListActivity.this, OrderNumberPopupActivity.class);
        intent.putExtra("orderNumber", orderNumber);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //데이터 넘겨줄 때 씀
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {
            if(resultCode == RESULT_OK && data != null){

                Toast.makeText(this, "성공", Toast.LENGTH_LONG).show();
                byte[] recvByte = data.getByteArrayExtra("responseTelegram");
                // Log.e("KSCAT_INTENT_RESULT", HexDump.dumpHexString(recvByte));
                // Util.byteTo20ByteLog(recvByte, "");
                Log.e("Recv Telegram \n", Util.HexDump.dumpHexString(recvByte));

                String str = Util.HexDump.dumpHexString(recvByte);
                Log.d("응답전문", str);

                // 라즈베리파이 서버에 전송
                final Gson gson = new GsonBuilder()
                        .excludeFieldsWithoutExposeAnnotation()
                        .create();
                final TextView totalPriceView = KioskListActivity.this.findViewById(R.id.total_price);
                JSONObject jsonObject = new JSONObject();
                try {
                    //                                       총 가격 숫자만
                    jsonObject.put("totalPrice", totalPriceView.getTag());
                    jsonObject.put("menus", new JSONArray(gson.toJson(cartMenuList,
                            new TypeToken<List<CartMenu>>(){}.getType())));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.d("결제", jsonObject.toString());
                // Toast.makeText(KioskListActivity.this, jsonObject.toString(), Toast.LENGTH_LONG).show();
                HttpNetworkController httpController = new HttpNetworkController(
                        KioskListActivity.this, getResources().getString(R.string.server_ip));
                httpController.postJsonCartData(jsonObject);

                // 승인번호 승인일자 가져오기
//                byte[] apprNo = new byte[12];
//                System.arraycopy(recvByte, 94, apprNo, 0, 12);
//                byte[] apprDate = new byte[6];
//                System.arraycopy(recvByte, 49, apprDate, 0, 6);

//                EditText et = findViewById(R.id.cardet3);
//                et.setText(new String(apprNo));
//
//                et = findViewById(R.id.cardet2);
//                et.setText(new String(apprDate));

                Intent intent = new Intent(this, ResultActivity.class);
                intent.putExtra("resData", recvByte);
                intent.putExtra("totAmt", mTotAmt);
                intent.putExtra("VAT", mVat);
                intent.putExtra("supplyAmt", mSupAmt);
                startActivity(intent);
            }
            else if(resultCode == RESULT_CANCELED)
            {
                if(data != null){
                    Log.e("result", String.valueOf(data.getIntExtra("result",1)));

                    try {
                        String message1 = "";
                        message1 = data.getStringExtra("message1");
                        String message2 = "";
                        message2 = data.getStringExtra("message2");
                        String notice1 = "";
                        notice1 = data.getStringExtra("notice1");
                        if (notice1 == null)
                            notice1 = "";
                        String notice2 = "";
                        notice2 = data.getStringExtra("notice2");
                        if (notice2 == null)
                            notice2 = "";

                        String msg = message1 + "\n" + message2 + "\n" + notice1 + "\n" + notice2;
                        Log.d("결제오류", msg);

                        new android.app.AlertDialog.Builder(this)
                                .setTitle("KSCAT_TEST")
                                .setMessage(msg)
                                .setCancelable(false)
                                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                    }
                                }).show();

                        byte[] recvByte = data.getByteArrayExtra("responseTelegram");
                        // Log.e("KSCAT_INTENT_RESULT", HexDump.dumpHexString(recvByte));
                        Log.e("Recv Telegram \n", Util.HexDump.dumpHexString(recvByte));

                        String str = Util.HexDump.dumpHexString(recvByte);
                        Log.d("응답전문", str);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
                else
                    Toast.makeText(this, "앱 호출 실패", Toast.LENGTH_LONG).show();
            }
        }
        else if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                //데이터 받기
                String result = data.getStringExtra("result");
                if (result != null) {
                    Log.d("데이터", result);
                }
            }
        } else if (requestCode == 2) {
            if (resultCode == RESULT_OK) {
                ArrayList<Integer> searchedMenuIdList = data.getIntegerArrayListExtra("searchedMenuIdList");
                Log.d("얻은값", searchedMenuIdList != null ? searchedMenuIdList.toString() : "값 없음");

                List<Menu> queryResult = dbQueryController.getMenuListByIdArray(searchedMenuIdList);
                Log.d("쿼리결과", queryResult.toString());
                try {
                    itemListFragment = new ItemListFragment(queryResult);
                    fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.list_fragment, itemListFragment).commitAllowingStateLoss();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void makeTelegramIC(String ApprCode) {
        ByteBuffer bb = ByteBuffer.allocate(4096);

        bb.put((byte)0x02);                                                 // STX
        bb.put("IC".getBytes());                                            // 거래구분
        // bb.put("MS".getBytes());                                         // 거래구분
        bb.put("01".getBytes());                                            // 업무구분

        if(ApprCode.equals("1"))
            bb.put("0200".getBytes());                                      // 전문구분
        else if(ApprCode.equals("0"))
            bb.put("0420".getBytes());                                      // 전문구분
        bb.put("N".getBytes());                                             // 거래형태

        mDeviceNo = "DPT0TEST03";
        bb.put(mDeviceNo.getBytes());                                        // 단말기번호
        for(int i=0; i< 4; i++) bb.put(" ".getBytes());                     // 업체정보
        for(int i=0; i<12; i++) bb.put(" ".getBytes());                     // 전문일련번호
        // bb.put("K".getBytes());                                          // POS Entry Mode   // MS
        bb.put("S".getBytes());                                             // POS Entry Mode   // IC
        for(int i=0; i<20; i++) bb.put(" ".getBytes());                     // 거래 고유 번호
        for(int i=0; i<20; i++) bb.put(" ".getBytes());                     // 암호화하지 않은 카드 번호
        bb.put("1".getBytes());                                             // 암호화여부
        bb.put("################".getBytes());
        bb.put("################".getBytes());
        for(int i=0; i<40; i++) bb.put(" ".getBytes());                     // 암호화 정보
        // bb.put("4330280486944821=17072011025834200000".getBytes());      // Track II - MS
        for(int i=0; i<37; i++) bb.put(" ".getBytes());                     // Track II - IC
        bb.put(Util.FS);                                                    // FS

        String et = "00";
        bb.put(et.getBytes());                         // 할부개월

        String totAmt = "1004";                          // 금액인듯
        Util.CalcTax tax = new Util.CalcTax();
        tax.setConfig(Long.parseLong(totAmt), 10, 0);

        bb.put(Util.stringToAmount(totAmt, 12).getBytes());           // 세금 + 금액 = 총금액일거임
        mTotAmt = Util.stringToAmount(totAmt, 12);

        bb.put(Util.stringToAmount(String.valueOf(tax.getServiceAmt()), 12).getBytes());    // 봉사료
        bb.put(Util.stringToAmount(String.valueOf(tax.getVAT()), 12).getBytes());           // 세금
        mVat = Util.stringToAmount(String.valueOf(tax.getVAT()), 12);
        bb.put(Util.stringToAmount(String.valueOf(tax.getSupplyAmt()), 12).getBytes());     // 공급금액
        mSupAmt = Util.stringToAmount(String.valueOf(tax.getSupplyAmt()), 12);
        bb.put("000000000000".getBytes());                                  // 면세금액
        bb.put("AA".getBytes());                                            // Working Key Index
        for(int i=0; i<16; i++) bb.put("0".getBytes());                     // 비밀번호

        if(ApprCode.equals("1"))
        {
            bb.put("            ".getBytes());                              // 원거래승인번호
            bb.put("      ".getBytes());                                    // 원거래승인일자
        }
        else{
            String orgDate = "201020";                                      // 원거래승인일자
            String orgApprNo = String.format("%-12s","123456789012");       // 원거래승인번호

            bb.put(orgApprNo.getBytes());                                   // 원거래승인번호
            bb.put(orgDate.getBytes());                                     // 원거래승인일자
        }
        for(int i=0; i<163; i++) bb.put(" ".getBytes());                    // 사용자정보 ~ DCC 환율조회 Data
        // EMV Data Length(4 bytes)
        // EMV Data
        bb.put("N".getBytes());                                             // 전자서명 유무
        //bb.put("S".getBytes());                                           // 전자서명 유무
        //bb.put("83".getBytes());                                          // 전자서명 암호화 Key Index
        //for(int i=0; i<16; i++) bb.put("0".getBytes());                   // 제품코드 및 버전
        //bb.put(String.format("%04d",  encBmpData.length()).getBytes());   // 전자서명 길이
        //bb.put(encBmpData.getBytes());                                    // 전자서명

        bb.put((byte)0x03);                                                 // ETX
        bb.put((byte)0x0D);                                                 // CR

        byte[] telegram = new byte[ bb.position() ];
        bb.rewind();
        bb.get( telegram );

        mRequestTelegram = new byte[telegram.length + 4];
        String telegramLength = String.format("%04d", telegram.length);
        System.arraycopy(telegramLength.getBytes(), 0, mRequestTelegram, 0, 4              );
        System.arraycopy(telegram                 , 0, mRequestTelegram, 4, telegram.length      );
    }
}
