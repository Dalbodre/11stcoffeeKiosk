package sb.yoon.kiosk;

import android.content.Intent;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
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
import sb.yoon.kiosk.model.CartMenu;
import sb.yoon.kiosk.model.CartOption;
import sb.yoon.kiosk.model.Category;
import sb.yoon.kiosk.model.Menu;

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
    private int categoryPage;
    private List<CategoryButton> buttons = new ArrayList<>();
    private CategoryButton button;
    int categorySize;

    boolean searchButtonClicked = false;
    public boolean menuOptionPopupButtonClicked = false;
    boolean purchaseButtonClicked = false;

    @Override
    public boolean onCreatePanelMenu(int featureId, @NonNull android.view.Menu menu) {
        return super.onCreatePanelMenu(featureId, menu);
    }

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
            button.setWidth(220);
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

    private void checkCategoryButtons() {
        for(int i=0; i<categorySize; i++){
            if(buttons.get(i).isChecked()){
                buttons.get(i).setBackgroundResource(R.drawable.togglebutton_on);
                buttons.get(i).setTextColor(Color.parseColor("#ffffff"));
            }
        }
    }

    private void createCategoryButtons() {
        GridLayout categoryButtonsGroup = findViewById(R.id.category_list);
        GridLayout.LayoutParams params = new GridLayout.LayoutParams();
        params.setGravity(GridLayout.TEXT_ALIGNMENT_CENTER);

        //buttons.get(0).setChecked(true);
        for(int i=0; i<categorySize; i++){
            buttons.get(i).setText(buttons.get(i).getCategoryName());
            buttons.get(i).setTextSize(50f);
            buttons.get(i).setOnClickListener(new categoryButtonClickListener());
            buttons.get(i).setTag(buttons.get(i).getTagNum());
            categoryButtonsGroup.addView(buttons.get(i));
        }
        checkCategoryButtons();
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
            if (purchaseButtonClicked) {
                return;
            }
            purchaseButtonClicked = true;

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

            //todo master에 merge 이후 intent result받는 곳으로 옮길 것
            purchaseButtonClicked = false;
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
