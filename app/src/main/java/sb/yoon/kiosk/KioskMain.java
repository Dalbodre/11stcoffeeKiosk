package sb.yoon.kiosk;

import android.graphics.Color;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class KioskMain extends AppCompatActivity {
    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;
    private ItemList itemList;
    private final ArrayList<String> categories = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kiosk_main);

        // 카테고리 더미
        String[] strings = {"커피", "차", "빵", "케이크", "쿠키", "아이스크림"};
        categories.addAll(Arrays.asList(strings));

        // 카테고리 버튼들 생성
        LinearLayout categoryButtonsGroup = findViewById(R.id.categories_buttons_group);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(10, 0, 10, 0);
        for (String category: categories) {
            Button button = new Button(this);
            button.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 33);
            button.setText(category);
            button.setTextColor(Color.parseColor("#fef1d1"));
            button.setBackgroundColor(Color.parseColor("#544842"));
            button.setOnClickListener(new ButtonClickListener());
            button.setPadding(20,20,20,20);

            categoryButtonsGroup.addView(button, params);
        }

        // 기본으로 보여줄 플래그먼트 표시
        fragmentManager = getSupportFragmentManager();
        itemList = new ItemList(getMenuList(strings[0]));
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.list_fragment, itemList).commitAllowingStateLoss();
    }

    // 버튼 누르면 버튼의 텍스트를 확인하고 플래그먼트에 삽입
    //
    class ButtonClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            String text = ((Button)view).getText().toString();
            try {
                itemList = new ItemList(getMenuList(text));
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.list_fragment, itemList).commitAllowingStateLoss();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    Menu[] getMenuList(String categoryName) {
        Menu[] INDEX_MENU = new Menu[0];

        // 카테고리 이름에 따라 메뉴 인덱스 구성
        if (categoryName.equals("커피")) {
            INDEX_MENU = new Menu[]{
                    new Menu("아메리카노",
                            ResourcesCompat.getDrawable(getResources(), R.drawable.apollo1, null),
                            new int[]{1000, 2000},
                            new Ingredient[]{
                                    new Ingredient("커피", ResourcesCompat.getDrawable(getResources(), R.drawable.apollo1, null)),
                                    new Ingredient("물", ResourcesCompat.getDrawable(getResources(), R.drawable.apollo1, null)),
                                    new Ingredient("물", ResourcesCompat.getDrawable(getResources(), R.drawable.apollo1, null)),
                                    new Ingredient("물", ResourcesCompat.getDrawable(getResources(), R.drawable.apollo1, null)),
                                    new Ingredient("물", ResourcesCompat.getDrawable(getResources(), R.drawable.apollo1, null)),
                                    new Ingredient("물", ResourcesCompat.getDrawable(getResources(), R.drawable.apollo1, null)),
                            }),
                    new Menu("라떼",
                            ResourcesCompat.getDrawable(getResources(), R.drawable.apollo1, null),
                            new int[]{1000, 2000},
                            new Ingredient[]{
                                    new Ingredient("커피", ResourcesCompat.getDrawable(getResources(), R.drawable.apollo1, null)),
                                    new Ingredient("물", ResourcesCompat.getDrawable(getResources(), R.drawable.apollo1, null)),
                                    new Ingredient("물", ResourcesCompat.getDrawable(getResources(), R.drawable.apollo1, null)),
                                    new Ingredient("sss", ResourcesCompat.getDrawable(getResources(), R.drawable.apollo1, null)),
                                    new Ingredient("zzz", ResourcesCompat.getDrawable(getResources(), R.drawable.apollo1, null)),
                                    new Ingredient("물", ResourcesCompat.getDrawable(getResources(), R.drawable.apollo1, null)),
                            }),
                    new Menu("카푸치노",
                            ResourcesCompat.getDrawable(getResources(), R.drawable.apollo1, null),
                            new int[]{1000, 2000},
                            new Ingredient[]{
                                    new Ingredient("커피", ResourcesCompat.getDrawable(getResources(), R.drawable.apollo1, null)),
                                    new Ingredient("우유", ResourcesCompat.getDrawable(getResources(), R.drawable.apollo1, null))
                            }),
            };
        } else if(categoryName.equals("차")) {
            INDEX_MENU = new Menu[]{
                    new Menu("우롱차",
                            ResourcesCompat.getDrawable(getResources(), R.drawable.apollo1, null),
                            new int[]{1000, 2000},
                            new Ingredient[]{
                                    new Ingredient("커피", ResourcesCompat.getDrawable(getResources(), R.drawable.apollo1, null)),
                                    new Ingredient("물", ResourcesCompat.getDrawable(getResources(), R.drawable.apollo1, null)),
                                    new Ingredient("물", ResourcesCompat.getDrawable(getResources(), R.drawable.apollo1, null)),
                                    new Ingredient("물", ResourcesCompat.getDrawable(getResources(), R.drawable.apollo1, null)),
                                    new Ingredient("물", ResourcesCompat.getDrawable(getResources(), R.drawable.apollo1, null)),
                                    new Ingredient("물", ResourcesCompat.getDrawable(getResources(), R.drawable.apollo1, null)),
                            }),
                    new Menu("녹차",
                            ResourcesCompat.getDrawable(getResources(), R.drawable.apollo1, null),
                            new int[]{1000, 2000},
                            new Ingredient[]{
                                    new Ingredient("커피", ResourcesCompat.getDrawable(getResources(), R.drawable.apollo1, null)),
                                    new Ingredient("물", ResourcesCompat.getDrawable(getResources(), R.drawable.apollo1, null)),
                                    new Ingredient("물", ResourcesCompat.getDrawable(getResources(), R.drawable.apollo1, null)),
                                    new Ingredient("sss", ResourcesCompat.getDrawable(getResources(), R.drawable.apollo1, null)),
                                    new Ingredient("zzz", ResourcesCompat.getDrawable(getResources(), R.drawable.apollo1, null)),
                                    new Ingredient("물", ResourcesCompat.getDrawable(getResources(), R.drawable.apollo1, null)),
                            }),
                    new Menu("둥글레차",
                            ResourcesCompat.getDrawable(getResources(), R.drawable.apollo1, null),
                            new int[]{1000, 2000},
                            new Ingredient[]{
                                    new Ingredient("커피", ResourcesCompat.getDrawable(getResources(), R.drawable.apollo1, null)),
                                    new Ingredient("우유", ResourcesCompat.getDrawable(getResources(), R.drawable.apollo1, null))
                            }),
            };
        }
        return INDEX_MENU;
    }
}
