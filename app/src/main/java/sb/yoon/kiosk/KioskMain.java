package sb.yoon.kiosk;

import android.graphics.Color;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import sb.yoon.kiosk.controller.FirebaseController;
import sb.yoon.kiosk.model.Ingredient;
import sb.yoon.kiosk.model.Menu;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class KioskMain extends AppCompatActivity {
    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;
    private ItemList itemList;
    private HashMap<String, String> categories;

    // 파이어베이스 등록 후 cafe 쿼리
    FirebaseController firebaseController = new FirebaseController();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kiosk_main);

        // 카테고리 이름들 받아오기
        categories = firebaseController.fetchCategoryNames();

        // 카테고리 버튼들 생성
        LinearLayout categoryButtonsGroup = findViewById(R.id.categories_buttons_group);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(10, 0, 10, 0);
        for (String category: categories.values()) {
            Button button = new Button(this);
            button.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 33);
            button.setText(category);
            button.setTextColor(Color.parseColor("#fef1d1"));
            button.setBackgroundColor(Color.parseColor("#544842"));
            button.setOnClickListener(new ButtonClickListener());
            button.setPadding(20,20,20,20);

            categoryButtonsGroup.addView(button, params);
        }

        // 기본으로 보여줄 플래그먼트 (첫번째 카테고리)
        fragmentManager = getSupportFragmentManager();
        itemList = new ItemList(firebaseController.getMenuList("coffee"));
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.list_fragment, itemList).commitAllowingStateLoss();
    }

    // 버튼 누르면 버튼의 텍스트를 확인하고 플래그먼트에 삽입
    class ButtonClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            String text = ((Button)view).getText().toString();
            String key_text;
            switch (text) {
                case "에이드": key_text = "ade"; break;
                case "주스": key_text = "juices"; break;
                case "아이스티": key_text = "ice_tea"; break;
                default: key_text = "coffee";
            }
            try {
                itemList = new ItemList(firebaseController.getMenuList(key_text));
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.list_fragment, itemList).commitAllowingStateLoss();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
