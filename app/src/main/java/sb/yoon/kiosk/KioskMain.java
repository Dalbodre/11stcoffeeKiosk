package sb.yoon.kiosk;

import android.content.res.AssetManager;
import android.graphics.Color;
import android.os.Build;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import sb.yoon.kiosk.controller.Init;
import sb.yoon.kiosk.model.Category;
import sb.yoon.kiosk.model.CategoryDao;
import sb.yoon.kiosk.model.DaoSession;

import java.util.List;

public class KioskMain extends AppCompatActivity {
    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;
    private ItemListFragment itemListFragment;
    private List<Category> categories;

    private CategoryDao categoriesDao;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kiosk_main);

        DaoSession daoSession = ((KioskApplication) getApplication()).getDaoSession();

        AssetManager assetManager = getResources().getAssets();
        try {
            //InputStream fileInputStream = assetManager.open("menupan.zip");
            new Init(daoSession); //, fileInputStream, getFilesDir().toString() + "/");
        } catch (Exception e) {
            e.printStackTrace();
        }

        categoriesDao = daoSession.getCategoryDao();
        categories = categoriesDao.queryBuilder().orderAsc(CategoryDao.Properties.Id).list();

        // 카테고리 버튼들 생성
        LinearLayout categoryButtonsGroup = findViewById(R.id.categories_buttons_group);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(10, 0, 10, 0);

        int tagNum = 0;
        for (Category category: categories) {
            Button button = new Button(this);
            button.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 33);
            button.setText(category.getName());
            button.setTextColor(Color.parseColor("#fef1d1"));
            button.setBackgroundColor(Color.parseColor("#544842"));
            button.setOnClickListener(new ButtonClickListener());
            button.setPadding(20,20,20,20);
            button.setTag(tagNum);
            tagNum += 1;

            categoryButtonsGroup.addView(button, params);
        }

        // 기본으로 보여줄 플래그먼트 (첫번째 카테고리)
        fragmentManager = getSupportFragmentManager();
        itemListFragment = new ItemListFragment(categories.get(0).getMenuList());
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.list_fragment, itemListFragment).commitAllowingStateLoss();

    }

    // 버튼 누르면 버튼의 텍스트를 확인하고 플래그먼트에 삽입
    class ButtonClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            int tagNum = (int) view.getTag();
            try {
                itemListFragment = new ItemListFragment(categories.get(tagNum).getMenuList());
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.list_fragment, itemListFragment).commitAllowingStateLoss();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
