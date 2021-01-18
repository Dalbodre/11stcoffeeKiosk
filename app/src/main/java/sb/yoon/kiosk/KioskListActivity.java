package sb.yoon.kiosk;

import android.content.Intent;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
import sb.yoon.kiosk.model.CartMenu;
import sb.yoon.kiosk.model.CartOption;
import sb.yoon.kiosk.model.Category;
import sb.yoon.kiosk.model.Menu;

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
}
