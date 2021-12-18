package sb.yoon.kiosk2.controller;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.ArrayList;
import java.util.List;

import sb.yoon.kiosk2.AdminActivity;
import sb.yoon.kiosk2.AdminTabFragment;
import sb.yoon.kiosk2.model.Category;
import sb.yoon.kiosk2.model.Menu;

public class AdminFragmentAdapter extends FragmentStateAdapter {
    private int size = 0;
    private List<Category> categories;
    private FragmentActivity context;
    private ArrayList<Long> ids = new ArrayList<Long>();

    public AdminFragmentAdapter(List<Category> categories, FragmentActivity fragmentActivity){
        super(fragmentActivity);
        this.categories = categories;
        this.context = fragmentActivity;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        // 각 카테고리의 플레그먼트 생성
        List<Menu> menuList = categories.get(position).getMenuList();
        Fragment fragment = new AdminTabFragment(menuList, context);
        return fragment;
    }


    @Override
    public int getItemCount() {
        return size;
    }

    public void setItemCount(int size) { this.size = size;}

    @Override
    public long getItemId(int position) {
        long id = (long)(categories.get(position).getMenuList().hashCode());
        int index = ids.indexOf(id);
        if (index >= 0) {
            ids.set(index, id);
        } else {
            ids.add(id);
        }

        return id;
    }

    @Override
    public boolean containsItem(long itemId) {
        return ids.contains(itemId);
    }
}
