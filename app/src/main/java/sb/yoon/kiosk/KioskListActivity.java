package sb.yoon.kiosk;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
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
import sb.yoon.kiosk.libs.Util;
import sb.yoon.kiosk.model.CartMenu;
import sb.yoon.kiosk.model.CartOption;
import sb.yoon.kiosk.model.Category;
import sb.yoon.kiosk.model.Menu;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

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
    boolean purchaseButtonClicked = false;

    private TabLayout categoryTab;
    private ArrayList<ToggleButton> toggleButtons = new ArrayList<>();


    private Button leftButton;
    private Button rightButton;

    final int eleSize = 5;

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

        maxCategoryPage = categorySize / 8;


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
    }

    private void updateCategoryTab(int page) {
        Log.d("page", String.valueOf(page));
        categoryTab.removeAllTabs();
        int limit = page*eleSize+eleSize;
        toggleButtons.clear();
        for (int i=page*eleSize; i<limit; i++) {
            if (i>=categorySize) {
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
        itemListFragment = new ItemListFragment(dbQueryController.getMenuList(categories.get(categoryPage*eleSize)));
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.list_fragment, itemListFragment).commitAllowingStateLoss();

        toggleButtons.get(0).setText(categories.get(categoryPage*eleSize).getName());
    }

    private View createTabView(String tabName, int tag) {
        View tabView = LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
        ToggleButton toggleButton = tabView.findViewById(R.id.txt_name);
        toggleButton.setChecked(false);
        toggleButton.setBackgroundColor(Color.parseColor("#ffffff"));
        toggleButton.setTextColor(Color.parseColor("#081832"));
        toggleButton.setText(tabName);
        toggleButton.setTag(tag);
        toggleButton.setOnClickListener(new onClickToggleButton());
        toggleButtons.add(toggleButton);
        return tabView;
    }

    class onClickToggleButton implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            int tagNo = (int) view.getTag();
//            itemListFragment = new ItemListFragment(dbQueryController.getMenuList(categories.get(tagNo)));
//            fragmentTransaction = fragmentManager.beginTransaction();
//            fragmentTransaction.replace(R.id.list_fragment, itemListFragment).commitAllowingStateLoss();
//
//            for (int i=0; i<toggleButtons.size(); i++) {
//                int index = i + categoryPage*eleSize;
//                toggleButtons.get(i).setChecked(false);
//                toggleButtons.get(i).setBackgroundColor(Color.parseColor("#ffffff"));
//                toggleButtons.get(i).setTextColor(Color.parseColor("#081832"));
//                toggleButtons.get(i).setText(categories.get(index).getName());
//            }

            categoryTab.getTabAt(tagNo % eleSize).select();

//            ToggleButton toggleButton = (ToggleButton) view;
//            toggleButton.setChecked(true);
//            toggleButton.setBackgroundResource(R.drawable.togglebutton_on);
//            toggleButton.setTextColor(Color.parseColor("#ffffff"));
//            toggleButton.setText(categories.get((Integer) toggleButton.getTag()).getName());
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
            itemListFragment = new ItemListFragment(dbQueryController.getMenuList(categories.get(tagNo + eleSize*categoryPage)));
            fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.list_fragment, itemListFragment).commitAllowingStateLoss();

            for (int i=0; i<toggleButtons.size(); i++) {
                int index = i + categoryPage*eleSize;
                toggleButtons.get(i).setChecked(false);
                toggleButtons.get(i).setBackgroundColor(Color.parseColor("#ffffff"));
                toggleButtons.get(i).setTextColor(Color.parseColor("#081832"));
                toggleButtons.get(i).setText(categories.get(index).getName());
            }

            KioskListActivity.this.toggleButtons.get(tagNo).setChecked(true);
            KioskListActivity.this.toggleButtons.get(tagNo).setBackgroundResource(R.drawable.togglebutton_on);
            KioskListActivity.this.toggleButtons.get(tagNo).setTextColor(Color.parseColor("#ffffff"));
            KioskListActivity.this.toggleButtons.get(tagNo).setText(categories.get(tagNo).getName());
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
            if (cartMenuList.isEmpty()) {
                return;
            }/*
            if (purchaseButtonClicked) {
                return;
            }
            purchaseButtonClicked = true;*/
            System.out.println("결제버튼: 클릭함");

            //custom dialog
            View dialogView = getLayoutInflater().inflate(R.layout.dialog_custom, null);
            TextView price_tv = dialogView.findViewById(R.id.price_text);
            TextView totalPriceView = KioskListActivity.this.findViewById(R.id.total_price);
            // Log.d("가격텍스트", (String) totalPriceView.getText());
            price_tv.setText(totalPriceView.getText().toString());

            AlertDialog.Builder builder = new AlertDialog.Builder(KioskListActivity.this);
            builder.setView(dialogView);

            final AlertDialog alertDialog = builder.create();
            alertDialog.show();

            Button ok_btn = dialogView.findViewById(R.id.ok_btn);
            ok_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
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
                }
            });

            Button cancle_btn = dialogView.findViewById(R.id.cancle_btn);
            cancle_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(KioskListActivity.this, "결제를 취소하셨습니다.", Toast.LENGTH_SHORT).show();
                    alertDialog.dismiss();
                }
            });
            //todo master에 merge 이후 intent result받는 곳으로 옮길 것
            //purchaseButtonClicked = false;
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
        if (requestCode == 1) {
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
                //Log.d("얻은값", searchedMenuIdList != null ? searchedMenuIdList.toString() : "값 없음");

                List<Menu> queryResult = dbQueryController.getMenuListByIdArray(searchedMenuIdList);
                //Log.d("쿼리결과", queryResult.toString());
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
}
