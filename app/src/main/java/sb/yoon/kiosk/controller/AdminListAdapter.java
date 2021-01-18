package sb.yoon.kiosk.controller;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import sb.yoon.kiosk.model.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import androidx.core.content.ContextCompat;

import java.util.List;

import sb.yoon.kiosk.AdminActivity;
import sb.yoon.kiosk.R;
import sb.yoon.kiosk.layout.admin_ItemElement;

public class AdminListAdapter extends BaseAdapter implements View.OnClickListener {
    //메뉴 리스트
    private List<Menu> menuList;

    private AdminActivity context;

    public AdminListAdapter(List<Menu> menuList, AdminActivity adminActivity){
        this.menuList = menuList;
        this.context = adminActivity;
    }

    @Override
    public int getCount() {
        return menuList.size();
    }

    @Override
    public Object getItem(int i) {
        return menuList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public int getViewTypeCount() {
        if(getCount() > 0){
            return getCount();
        }
        else{
            return super.getViewTypeCount();
        }
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public View getView(int pos, View view, ViewGroup parent) {
        context = (AdminActivity) parent.getContext();

        Menu menu = (Menu)getItem(pos);

        if(view == null){
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = layoutInflater.inflate(R.layout.admin_item_layout, parent, false);
        }

        admin_ItemElement menuItem = view.findViewById(R.id.admin_menu_element);
        Drawable drawable = ContextCompat.getDrawable(context,
                context.getResources().getIdentifier(menu.getIconPath(), "drawable", context.getPackageName()));
        menuItem.setImgDrawable(drawable);
        menuItem.setName(menu.getName());
        menuItem.setTag(pos);

        menuItem.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View view) {
        int position = (int) view.getTag();

        //여기는 누르면 시작되는리스너들.
    }
}
