package sb.yoon.kiosk.controller;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import androidx.core.content.ContextCompat;

import sb.yoon.kiosk.CartFragment;
import sb.yoon.kiosk.KioskMain;
import sb.yoon.kiosk.PopupActivity;
import sb.yoon.kiosk.R;
import sb.yoon.kiosk.layout.ItemElement;
import sb.yoon.kiosk.model.Ingredient;
import sb.yoon.kiosk.model.Menu;

import java.util.List;

// 어뎁터 클래스
public class KioskListAdapter extends BaseAdapter implements View.OnClickListener {
    private List<Menu> menuList;
    // private Context context;
    private KioskMain context;

    public KioskListAdapter(List<Menu> menuList, KioskMain mainActivity){
        this.menuList = menuList;
        this.context = mainActivity;
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
        context = (KioskMain) parent.getContext();

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

            index += 1;
        }

        return convertView;
    }

    // 메뉴 아이콘 눌렸을 때
    @Override
    public void onClick(View view) {
        // 리사이클뷰 (쇼핑카트)에 아이템 넣는거
        int position = (int) view.getTag();
        context.addCartMenuList(menuList.get(position));

        // 팝업 띄우는거
        Intent intent = new Intent(context, PopupActivity.class); //액티비티 이동입니다.
        intent.putExtra("data", menuList.get(position).getCategoryId());                     //시험칠 때 봤겠지만 데이터 넘길 때 쓰는 애 입니다.
        context.startActivityForResult(intent, 1);
    }
}