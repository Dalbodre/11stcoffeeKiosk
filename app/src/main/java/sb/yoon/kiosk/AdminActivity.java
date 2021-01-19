package sb.yoon.kiosk;


import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.List;

import sb.yoon.kiosk.controller.AdminButtonAdapter;
import sb.yoon.kiosk.controller.AdminFragmentAdapter;
import sb.yoon.kiosk.controller.DbQueryController;
import sb.yoon.kiosk.controller.KioskListAdapter;
import sb.yoon.kiosk.layout.CategoryButton;
import sb.yoon.kiosk.model.Category;
import sb.yoon.kiosk.model.Menu;

public class AdminActivity extends AppCompatActivity {
    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    //메뉴마다 어댑터 달기
    private AdminButtonAdapter adapter;
    private DbQueryController dbQueryController;

    private List<Category> categories;
    private admin_tab adminTab;
    //private admin_ItemListFragment admin_itemListFragment;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        KioskApplication kioskApplication = (KioskApplication)getApplication();
        dbQueryController = kioskApplication.getDbQueryController();
        categories = dbQueryController.getCategoriesList();

        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.pager);

        AdminFragmentAdapter adminFragmentAdapter = new AdminFragmentAdapter(this);
        adminFragmentAdapter.setItemCount(categories.size());
        viewPager.setAdapter(adminFragmentAdapter);

        new TabLayoutMediator(tabLayout, viewPager, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                tab.setText(categories.get(position).getName());
                Log.d("menu", categories.get(position).getMenuList().get(0).getName());
               /* Bundle args = new Bundle();
                args.putParcelableArrayList("list", (ArrayList<? extends Parcelable>) categories.get(position).getMenuList());*/
            }

        }).attach();
        //카테고리 버튼 생성
        //this.createTabs();
    }
    public void adminOnClick(View view){
        switch(view.getId()){
            case R.id.addmenu:
                Intent intent = new Intent(view.getContext(), AdminAddActivity.class);
                startActivity(intent);
                break;
            case R.id.delmenu:
                //Todo
                break;
            case R.id.exit:
                finish();
                break;
        }
    }
}
