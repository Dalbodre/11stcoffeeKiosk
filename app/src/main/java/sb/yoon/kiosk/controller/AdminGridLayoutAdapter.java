package sb.yoon.kiosk.controller;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.LayoutInflater;

import sb.yoon.kiosk.AdminAddActivity;
import sb.yoon.kiosk.KioskApplication;
import sb.yoon.kiosk.model.IngredientsAndMenuJoinerDao;
import sb.yoon.kiosk.model.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import java.io.File;
import java.util.List;

import sb.yoon.kiosk.AdminActivity;
import sb.yoon.kiosk.R;
import sb.yoon.kiosk.model.MenuDao;

// 플래그먼트 내부의 그리드레이아웃을 담당하는 어댑터
public class AdminGridLayoutAdapter extends BaseAdapter {
    //메뉴 리스트
    private List<Menu> menuList;
    private FragmentActivity context;
    public DbQueryController dbQueryController;
    //private List<IngredientsAndMenuJoiner> ingredientList;

    public AdminGridLayoutAdapter(List<Menu> menuList, FragmentActivity context){
        this.menuList = menuList;
        this.context = context;
        KioskApplication kapp = (KioskApplication) context.getApplication();
        dbQueryController = kapp.getDbQueryController();
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
        /*Drawable drawable = ContextCompat.getDrawable(context,
                context.getResources().getIdentifier(menu.getIconPath(), "drawable", context.getPackageName()));*/
        try{
            File img = new File(menu.getIconPath());
            if(img.exists() == true){
                Uri uri = Uri.parse(menu.getIconPath());
                menuItem.setImageURI(uri);
            } else {
                Drawable drawable = ContextCompat.getDrawable(context,
                        context.getResources().getIdentifier(menu.getIconPath(), "drawable", context.getPackageName()));
                menuItem.setImageDrawable(drawable);

            }
        } catch (Exception e){
            menuItem.setImageResource(R.drawable.empty_img);
        }
        /*menuItem.setImageDrawable(drawable);*/

        TextView textView = view.findViewById(R.id.adminText);
        textView.setText(menu.getName());

        menuItem.setTag(pos);
        menuItem.setOnClickListener(new MenuOnClickListener());

        Button delete_button = view.findViewById(R.id.admin_delete_button);
        delete_button.setTag(pos);
        delete_button.setOnClickListener(new ButtonOnClickListener());

        return view;
    }

    // 그리드뷰 안 이미지 클릭 리스너
    class MenuOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            int position = (int) v.getTag();
            AdminAddActivity.isAdd = false;
            //여기는 누르면 시작되는 리스너들.
            Intent intent = new Intent(context, AdminAddActivity.class);
            Menu menu = menuList.get(position);

            Long menuId = menu.getId();
            intent.putExtra("menuID", menuId);
            AdminAddActivity.isAdd = false;
            context.startActivity(intent);
            context.finish();
            //Toast.makeText(context.getApplicationContext(), Integer.toString(position), Toast.LENGTH_SHORT).show();

            //Drawable drawable = ContextCompat.getDrawable(context, context.getResources().getIdentifier(menu.getIconPath(),
            //       "drawable", context.getPackageName()));
            //List<Option> options = menu.getOptionList();

        }
    }

    // 수정, 삭제 버튼
    class ButtonOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            int position = (int) v.getTag();
            if (v.getId() == R.id.admin_delete_button) {
                dbQueryController.ingredientsAndMenuJoinerDao.queryBuilder().where(IngredientsAndMenuJoinerDao.Properties.MenuId.eq(menuList.get(position).getId())).buildDelete()
                        .executeDeleteWithoutDetachingEntities();
                menuList.get(position).delete();
                if(dbQueryController.menuDao.queryBuilder().where(MenuDao.Properties.CategoryId.eq(menuList.get(position).getCategoryId())).count() == 0L) {
                    //dbQueryController.refreshCategory(menuList.get(position).getCategoryId());
                    dbQueryController.categoryDao.deleteByKey(menuList.get(position).getCategoryId());
                }
                menuList.remove(position);

                AdminGridLayoutAdapter.this.notifyDataSetChanged();
            }
        }
    }
}
