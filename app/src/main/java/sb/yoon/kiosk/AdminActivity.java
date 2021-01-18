package sb.yoon.kiosk;


import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import java.util.List;

import sb.yoon.kiosk.controller.DbQueryController;
import sb.yoon.kiosk.controller.KioskListAdapter;
import sb.yoon.kiosk.layout.CategoryButton;
import sb.yoon.kiosk.model.Category;

public class AdminActivity extends AppCompatActivity {
    private FrameLayout mFrameLayout;
    private FragmentTransaction mFragmentTransaction;
    private DbQueryController dbQueryController;

    private List<Category> categories;
    private Button categoryButton;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        KioskApplication kioskApplication = (KioskApplication)getApplication();
        dbQueryController = kioskApplication.getDbQueryController();

        categories = dbQueryController.getCategoriesList();

        //카테고리 버튼 생성
        this.createCategoryButtons();
    }

    private void createCategoryButtons(){
        HorizontalScrollView horizontalScrollView = findViewById(R.id.adminCategory);
        int i = 0;
        for(Category category : categories){
            categoryButton = new Button(this);
            categoryButton.setText(category.getName());
            categoryButton.setTextSize(60f);
            categoryButton.setOnClickListener(new categoryButtonListener());
            categoryButton.setTag(i);
            horizontalScrollView.addView(categoryButton);
            i++;
        }
    }
    class categoryButtonListener implements View.OnClickListener{
        int tagNo;
        @Override
        public void onClick(View view) {
            tagNo = (int)view.getTag();
            try{

            }
        }
    }
}
