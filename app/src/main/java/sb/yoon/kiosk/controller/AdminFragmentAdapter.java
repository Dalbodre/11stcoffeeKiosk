package sb.yoon.kiosk.controller;

import android.os.Bundle;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.ArrayList;
import java.util.List;

import sb.yoon.kiosk.admin_tab;
import sb.yoon.kiosk.model.Menu;

public class AdminFragmentAdapter extends FragmentStateAdapter {
    private int size = 0;
    private List<Menu> menuList;
    public AdminFragmentAdapter(FragmentActivity fragmentActivity){
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        Fragment fragment = admin_tab.newInstance(position);
        return fragment;
    }

    @Override
    public int getItemCount() {
        return size;
    }

    public void setItemCount(int size) { this.size = size;}

    public void setMenuList(List<Menu> menuList){this.menuList = menuList;}
    public List<Menu> getMenuList(){return menuList;}
}
