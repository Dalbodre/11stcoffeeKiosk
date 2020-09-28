package sb.yoon.kiosk;

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

        String[] strings = {"소설", "교양", "수필", "IT", "TEST", "AAAAA"};
        categories.addAll(Arrays.asList(strings));

        LinearLayout categoryButtonsGroup = findViewById(R.id.categories_buttons_group);

        for (String category: categories) {
            Button button = new Button(this);
            button.setText(category);
            button.setOnClickListener(new ButtonClickListener());
            categoryButtonsGroup.addView(button);
        }

        fragmentManager = getSupportFragmentManager();

        itemList = new ItemList();

        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.list_fragment, itemList).commitAllowingStateLoss();
    }

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
