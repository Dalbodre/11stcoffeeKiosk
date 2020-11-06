package sb.yoon.kiosk;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import sb.yoon.kiosk.controller.DbQueryController;
import sb.yoon.kiosk.layout.CategoryButton;
import sb.yoon.kiosk.model.Category;

import java.util.List;

public class KioskMain extends AppCompatActivity {
    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;
    private ItemListFragment itemListFragment;
    private List<Category> categories;

    // 프론트에서 쓰기 편하게 DB 관련 메서드들 제공
    private DbQueryController dbQueryController;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kiosk_main);

        // DB 컨트롤러 (프론트에서 쓸 메서드들 모음)
        dbQueryController = ((KioskApplication)getApplication()).getDbQueryController();

        // DB 초기화
        dbQueryController.initDB();

        // 카테고리 리스트 불러오기
        categories = dbQueryController.getCategoriesList();

        // 카테고리 버튼들 생성
        this.createCategoryButtons();

        // 기본으로 보여줄 플래그먼트 (첫번째 카테고리)
        fragmentManager = getSupportFragmentManager();
        itemListFragment = new ItemListFragment(dbQueryController.getMenuList(categories.get(0)));
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.list_fragment, itemListFragment).commitAllowingStateLoss();

    }

    private void createCategoryButtons() {
        LinearLayout categoryButtonsGroup = findViewById(R.id.categories_buttons_group);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(10, 0, 10, 0);

        // 검색버튼 삽입
        CategoryButton searchButton = new CategoryButton(this);
        Drawable searchIcon = ContextCompat.getDrawable(this, R.drawable.search_icon);
        // 검색 아이콘 삽입 (Drawable Left)
        searchIcon.setBounds(0, 0, 80, 80);
        //searchIcon.setColorFilter(Color.parseColor("#ffffff"), PorterDuff.Mode.MULTIPLY);
        searchButton.setCompoundDrawables(searchIcon, null, null, null);
        searchButton.setText("검색");
        categoryButtonsGroup.addView(searchButton, params);

        // 버튼 순서 태그로 지정
        int tagNum = 0;
        for (Category category: categories) {
            CategoryButton button = new CategoryButton(this);
            button.setText(category.getName());
            button.setOnClickListener(new ButtonClickListener());
            button.setTag(tagNum);
            tagNum += 1;

            categoryButtonsGroup.addView(button, params);
        }
    }

    // 버튼 누르면 버튼의 순서를 확인하고 플래그먼트에 삽입
    class ButtonClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            int tagNum = (int) view.getTag();
            try {
                itemListFragment = new ItemListFragment(dbQueryController.getMenuList(categories.get(tagNum)));
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.list_fragment, itemListFragment).commitAllowingStateLoss();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void mOnPopupClick(View v) {
        //어케보면 액티비티 이동시키는 거랑 같은 방식이라고 보면 됩니다.
        Intent intent = new Intent(this, PopupActivity.class); //액티비티 이동입니다.
        intent.putExtra("data", "test popup");                     //시험칠 때 봤겠지만 데이터 넘길 때 쓰는 애 입니다.
        startActivityForResult(intent, 1);
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
        }
    }
}
