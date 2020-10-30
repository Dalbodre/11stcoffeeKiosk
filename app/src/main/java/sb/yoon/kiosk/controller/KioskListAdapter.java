package sb.yoon.kiosk.controller;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import sb.yoon.kiosk.R;
import sb.yoon.kiosk.layout.ItemElement;
import sb.yoon.kiosk.model.Ingredient;
import sb.yoon.kiosk.model.Menu;

import java.util.List;

// 어뎁터 클래스
public class KioskListAdapter extends ArrayAdapter<Menu> {
    private final LayoutInflater layoutInflater;

    public KioskListAdapter(Context context, int textViewResourceId, List<Menu> objects){
        super(context, textViewResourceId, objects);
        layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        // 특정 행의 데이터 구함
        Menu menu = (Menu)getItem(position);

        // View는 재사용되기 때문에 처음에만 리스트 아이템 표시용 레이아웃을 읽어와서 생성함
        if(convertView == null){
            convertView = layoutInflater.inflate(R.layout.list_item, null);
        }

        // View의 각 Widget에 데이터 저장
        ItemElement view = convertView.findViewById(R.id.menu_element);
        Drawable drawable = getResources().getDrawable(getResources()
                .getIdentifier($name_of_the_image, "drawable", getPackageName()));
        view.setImage(getContext().getDrawable(R.drawable.apollo1));
        view.setText(menu.getName());

        TextView price1 = (TextView)convertView.findViewById(R.id.price1);
        price1.setText(menu.getPrices().get(0));
        TextView price2 = (TextView)convertView.findViewById(R.id.price2);
        price2.setText(menu.getPrices().get(1));

        // 재료들 추가
        LinearLayout holder = convertView.findViewById(R.id.ingredientsHolder);
        for (Ingredient ingredient : menu.getIngredients()) {
            System.out.println(menu.getName() + ingredient.getText());
            // 메뉴 아이템 (이미지 + 텍스트) 삽입
            ItemElement element = new ItemElement(getContext(), ingredient.getIcon(), ingredient.getText());
            // DP 단위로 변환
            final int width = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 150, getContext().getResources().getDisplayMetrics());
            final int height = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 150, getContext().getResources().getDisplayMetrics());
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width,height);
            holder.addView(element, params);
        }

        return convertView;
    }
}