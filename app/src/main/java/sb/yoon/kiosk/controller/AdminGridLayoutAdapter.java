package sb.yoon.kiosk.controller;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;

import sb.yoon.kiosk.AdminAddActivity;
import sb.yoon.kiosk.model.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import java.util.List;

import sb.yoon.kiosk.AdminActivity;
import sb.yoon.kiosk.R;
import sb.yoon.kiosk.layout.AdminItemElement;
import sb.yoon.kiosk.model.Option;

// 플래그먼트 내부의 그리드레이아웃을 담당하는 어댑터
public class AdminGridLayoutAdapter extends BaseAdapter implements View.OnClickListener {
    //메뉴 리스트
    private List<Menu> menuList;

    private Context context;

    public AdminGridLayoutAdapter(List<Menu> menuList, Context context){
        this.menuList = menuList;
        this.context = context;
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

        ImageView menuItem = view.findViewById(R.id.adminImg);
        Drawable drawable = ContextCompat.getDrawable(context,
                context.getResources().getIdentifier(menu.getIconPath(), "drawable", context.getPackageName()));
        menuItem.setImageDrawable(drawable);

        TextView textView = view.findViewById(R.id.adminText);
        textView.setText(menu.getName());

        menuItem.setTag(pos);
        menuItem.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View view) {
        int position = (int) view.getTag();

        //여기는 누르면 시작되는 리스너들.
        Intent intent = new Intent(context, AdminAddActivity.class);
        Menu menu = menuList.get(position);

        Toast.makeText(context.getApplicationContext(), position, Toast.LENGTH_LONG).show();

        Drawable drawable = ContextCompat.getDrawable(context, context.getResources().getIdentifier(menu.getIconPath(),
                "drawable", context.getPackageName()));
        List<Option> options = menu.getOptionList();
        Long menuId = menu.getId();

        //카트메뉴로 되있지만 여기는 수정메뉴
        /*
        AdminAddActivity.cartMenu =
                new CartMenu(drawable, menu.getName(), menu.getPrice(), 0, cartOptions, menu.getId(), menu.getCategoryId(), menu.getIsHot(), menu.getIsCold(), null);
                */
        // context.startActivityForResult(intent, 1);
    }
}
