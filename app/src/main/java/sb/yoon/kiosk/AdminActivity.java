package sb.yoon.kiosk;


import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.IntentCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.List;

import sb.yoon.kiosk.controller.AdminGridLayoutAdapter;
import sb.yoon.kiosk.controller.AdminFragmentAdapter;
import sb.yoon.kiosk.controller.DbQueryController;
import sb.yoon.kiosk.controller.KioskListAdapter;
import sb.yoon.kiosk.layout.CategoryButton;
import sb.yoon.kiosk.model.Category;

public class AdminActivity extends AppCompatActivity {
    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    //메뉴마다 어댑터 달기
    private DbQueryController dbQueryController;

    private List<Category> categories;
    private AdminFragmentAdapter adminFragmentAdapter;
    private AdminTabFragment adminTab;
    //private admin_ItemListFragment admin_itemListFragment;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        KioskApplication kioskApplication = (KioskApplication)getApplication();
        dbQueryController = kioskApplication.getDbQueryController();

        categories = dbQueryController.getCategoriesList();
        for(Category category : categories){
            dbQueryController.refreshCategory((Long)category.getId());
        }
        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.pager);

        // 어댑터 지옥의 시작...
        // 아이템 카운트에 맞춰서 플래그먼트 생성
        // 카테고리 리스트를 주고 거기서 메뉴리스트를 동적으로 받아서 생성

        adminFragmentAdapter = new AdminFragmentAdapter(categories, this);
        adminFragmentAdapter.setItemCount(categories.size());
        viewPager.setAdapter(adminFragmentAdapter);

        new TabLayoutMediator(tabLayout, viewPager, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                tab.setText(categories.get(position).getName());
                if (categories.get(position).getMenuList().size() > 0)
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
                AdminAddActivity.isAdd = true;
                Intent intent = new Intent(view.getContext(), AdminAddActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                break;

            case R.id.exit:
                System.exit(0);
                Intent startActivity = new Intent(this, EnterMain.class);
                startActivity(startActivity);
                break;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        categories = dbQueryController.getCategoriesList();
        Log.d("status", "onresume");
        for (int i=0; i<categories.size(); i++) {
            categories.get(i).refresh();
            System.out.println(categories.get(i).getName());
            categories.get(i).resetMenuList();
        }

        adminFragmentAdapter.notifyDataSetChanged();
    }
}

