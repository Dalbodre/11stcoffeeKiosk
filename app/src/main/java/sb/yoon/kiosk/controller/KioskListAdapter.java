package sb.yoon.kiosk.controller;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import androidx.core.content.ContextCompat;

import sb.yoon.kiosk.CartFragment;
import sb.yoon.kiosk.R;
import sb.yoon.kiosk.layout.ItemElement;
import sb.yoon.kiosk.model.Ingredient;
import sb.yoon.kiosk.model.Menu;

import java.util.ArrayList;
import java.util.List;

// 어뎁터 클래스
public class KioskListAdapter extends BaseAdapter implements View.OnClickListener {
    private List<Menu> menuList;
    private Context context;
    private CartFragment cartFragment;

    public KioskListAdapter(List<Menu> menuList, CartFragment cartFragment){
        this.menuList = menuList;
        this.cartFragment = cartFragment;
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
        //return getCount();

        if(getCount() > 0) {
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
    public View getView(int position, View convertView, ViewGroup parent){
        context = parent.getContext();

        // 특정 행의 데이터 구함
        Menu menu = (Menu)getItem(position);

        // View는 재사용되기 때문에 처음에만 리스트 아이템 표시용 레이아웃을 읽어와서 생성함
        if(convertView == null){
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.list_item, parent, false);
        }

        // View의 각 Widget에 데이터 저장
        ItemElement menuItem = convertView.findViewById(R.id.menu_element);
        Drawable drawable = ContextCompat.getDrawable(context, context.getResources()
                .getIdentifier(menu.getIconPath(), "drawable", context.getPackageName()));
        menuItem.setImageDrawable(drawable);
        menuItem.setText(menu.getName());
        menuItem.setTag(position);

        // ItemElement에 OnClickListener 달아주기
        menuItem.setOnClickListener(this);

        TextView price1 = (TextView)convertView.findViewById(R.id.price1);
        price1.setText(Integer.toString(menu.getPrice()));
        TextView price2 = (TextView)convertView.findViewById(R.id.price2);
        price2.setText(Integer.toString(menu.getPrice()));

        // 재료들 추가
        List<Ingredient> ingredients = menu.getIngredientList();
        LinearLayout holder = null;
        int index = 0;
        for (Ingredient ingredient : ingredients) {
            holder = (LinearLayout) convertView.findViewById(R.id.ingredientsHolder);

            Drawable ingredientDrawable = ContextCompat.getDrawable(context, context.getResources().getIdentifier(ingredient.getIconPath(), "drawable", context.getPackageName()));

            // 메뉴 아이템 (이미지 + 텍스트) 삽입
            // 수 지정 안해주면 (아마도) 다중스레드 때문에 랜덤으로 추가되어버리는 버그 있음
            // @todo 버그 제대로 픽스
            ItemElement ingredientElement = null;
            switch(index) {
                case 0: ingredientElement = holder.findViewById(R.id.ingredient1); break;
                case 1: ingredientElement = holder.findViewById(R.id.ingredient2); break;
                case 2: ingredientElement = holder.findViewById(R.id.ingredient3); break;
                case 3: ingredientElement = holder.findViewById(R.id.ingredient4); break;
                default:
                    throw new IllegalStateException("재료가 너무 많습니다: " + index);
            }
            if (ingredientElement != null) {
                ingredientElement.setImageDrawable(ingredientDrawable);
                ingredientElement.setText(ingredient.getName());
                ingredientElement.setVisibility(View.VISIBLE);
            }

            // DP 단위로 변환
            //final int width = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 100, context.getResources().getDisplayMetrics());
            //final int height = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 100, context.getResources().getDisplayMetrics());
            //LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width,height);
            //holder.addView(element, params);

            index += 1;
        }

        return convertView;
    }

    // 메뉴 아이콘 눌렸을 때
    @Override
    public void onClick(View view) {
        int position = (int) view.getTag();
        cartFragment.addCartMenuList(menuList.get(position));
        cartFragment.setListWrapperVisibility(true);
    }
}