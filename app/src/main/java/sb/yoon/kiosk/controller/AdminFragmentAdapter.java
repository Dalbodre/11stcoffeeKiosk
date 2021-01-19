package sb.yoon.kiosk.controller;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.List;

import sb.yoon.kiosk.AdminActivity;
import sb.yoon.kiosk.AdminTabFragment;
import sb.yoon.kiosk.model.Category;
import sb.yoon.kiosk.model.Menu;

public class AdminFragmentAdapter extends FragmentStateAdapter {
    private int size = 0;
    private List<Category> categories;
    private Context context;

    public AdminFragmentAdapter(List<Category> categories, FragmentActivity fragmentActivity){
        super(fragmentActivity);
        this.categories = categories;
        this.context = (Context) fragmentActivity;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        // 각 플레그먼트 생성
        // position에 따라서 메뉴리스트 다르게 줘야할듯

        List<Menu> menuList = categories.get(position).getMenuList();

        Fragment fragment = new AdminTabFragment(menuList, context);
        return fragment;
    }

    @Override
    public int getItemCount() {
        return size;
    }

    public void setItemCount(int size) { this.size = size;}
}
