package sb.yoon.kiosk;


import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.tabs.TabLayout;

import java.util.List;

import sb.yoon.kiosk.controller.AdminButtonAdapter;
import sb.yoon.kiosk.controller.DbQueryController;
import sb.yoon.kiosk.controller.KioskListAdapter;
import sb.yoon.kiosk.layout.CategoryButton;
import sb.yoon.kiosk.model.Category;
import sb.yoon.kiosk.model.Menu;

public class AdminActivity extends AppCompatActivity {
    private TabLayout tabLayout;
    //메뉴마다 어댑터 달기
    private AdminButtonAdapter adapter;
    private DbQueryController dbQueryController;

    private List<Category> categories;

    //private admin_ItemListFragment admin_itemListFragment;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        KioskApplication kioskApplication = (KioskApplication)getApplication();
        dbQueryController = kioskApplication.getDbQueryController();
        categories = dbQueryController.getCategoriesList();

        //카테고리 버튼 생성
        this.createTabs();
    }

    private void createTabs(){
        tabLayout = findViewById(R.id.tabLayout);
        int i = 0;
        for(Category category : categories){
            TabLayout.Tab tab = tabLayout.newTab();
            tab.setText(category.getName());
            //탭에 내용 추가
            //this.createMenu(dbQueryController.getMenuList(categories.get(i)));
            tabLayout.addTab(tab);
        }
    }
    private void createMenuGrid(List<Menu> menu){

    }
}
