package sb.yoon.kiosk;

import android.graphics.Color;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class KioskMain extends AppCompatActivity {
    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;
    private ItemList itemList;
    private ArrayList<String> categories = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kiosk_main);

        // 카테고리 더미
        String[] strings = {"소설", "교양", "수필", "IT", "TEST", "AAAAA", "XXXX", "YYYYYY", "ZZZZZZZZ"};
        categories.addAll(Arrays.asList(strings));

        // 카테고리 버튼들 생성
        LinearLayout categoryButtonsGroup = findViewById(R.id.categories_buttons_group);
        for (String category: categories) {
            Button button = new Button(this);
            button.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 33);
            button.setText(category);
            button.setTextColor(Color.parseColor("#544842"));
            //button.setBackgroundColor(Color.parseColor("#544842"));
            button.setOnClickListener(new ButtonClickListener());
            categoryButtonsGroup.addView(button);
        }

        // 기본으로 보여줄 플래그먼트 표시
        fragmentManager = getSupportFragmentManager();
        itemList = new ItemList();
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
                itemList = new ItemList();
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.list_fragment, itemList).commitAllowingStateLoss();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
