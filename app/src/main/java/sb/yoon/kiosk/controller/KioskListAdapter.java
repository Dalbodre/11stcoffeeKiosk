package sb.yoon.kiosk.controller;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import androidx.core.content.ContextCompat;

import sb.yoon.kiosk.R;
import sb.yoon.kiosk.layout.ItemElement;
import sb.yoon.kiosk.model.Ingredient;
import sb.yoon.kiosk.model.Menu;

import java.util.ArrayList;
import java.util.List;

// 어뎁터 클래스
public class KioskListAdapter extends BaseAdapter {
    private List<Menu> menuList;

    public KioskListAdapter(List<Menu> menuList){
        this.menuList = menuList;
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
    public View getView(int position, View convertView, ViewGroup parent){
        final int pos = position;
        final Context context = parent.getContext();
        // 특정 행의 데이터 구함
        Menu menu = (Menu)getItem(position);

        // View는 재사용되기 때문에 처음에만 리스트 아이템 표시용 레이아웃을 읽어와서 생성함
        if(convertView == null){
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.list_item, parent, false);
        }

        // View의 각 Widget에 데이터 저장
        ItemElement view = convertView.findViewById(R.id.menu_element);
        Drawable drawable = ContextCompat.getDrawable(context, context.getResources()
                .getIdentifier(menu.getIconPath(), "drawable", context.getPackageName()));
        view.setImageDrawable(drawable);
        view.setText(menu.getName());

        TextView price1 = (TextView)convertView.findViewById(R.id.price1);
        price1.setText(Integer.toString(menu.getPrice()));
        TextView price2 = (TextView)convertView.findViewById(R.id.price2);
        price2.setText(Integer.toString(menu.getPrice()));

        // 재료들 추가
        List<Ingredient> ingredients = menu.getIngredientList();
        LinearLayout holder = null;
        for (Ingredient ingredient : ingredients) {
            holder = convertView.findViewById(R.id.ingredientsHolder);

            Drawable ingredientDrawable = ContextCompat.getDrawable(context, context.getResources().getIdentifier(ingredient.getIconPath(), "drawable", context.getPackageName()));

            // 메뉴 아이템 (이미지 + 텍스트) 삽입
            ItemElement element = new ItemElement(context, ingredientDrawable, ingredient.getName());

            // DP 단위로 변환
            final int width = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 100, context.getResources().getDisplayMetrics());
            final int height = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 100, context.getResources().getDisplayMetrics());
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width,height);
            holder.addView(element, params);
        }

        return convertView;
    }
}