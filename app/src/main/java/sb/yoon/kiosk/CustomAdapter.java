package sb.yoon.kiosk;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import sb.yoon.kiosk.model.Menu;

import java.util.List;

// 어뎁터 클래스
public class CustomAdapter extends ArrayAdapter<Menu> {
    private LayoutInflater layoutInflater;

    public CustomAdapter(Context context, int textViewResourceId, List<Menu> objects){
        super(context, textViewResourceId, objects);
        layoutInflater = (LayoutInflater)context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
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
        ImageView imageView;
        imageView = (ImageView)convertView.findViewById(R.id.img);
        imageView.setImageDrawable(menu.getIcon());

        TextView textView;
        textView = (TextView)convertView.findViewById(R.id.text);
        textView.setText(menu.getText());

        LinearLayout holder = convertView.findViewById(R.id.ingredientsHolder);
        for (int i = 0; i< menu.getIngredients().length; i++) {
            ImageView ingredIcon = new ImageView(getContext());
            ingredIcon.setImageDrawable(menu.getIngredients()[i].getIcon());
            ingredIcon.setMaxWidth(150);
            ingredIcon.setMaxHeight(150);
            holder.addView(ingredIcon);
        }

        return convertView;
    }
}
