package sb.yoon.kiosk;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.res.ResourcesCompat;
import sb.yoon.kiosk.layout.ItemElement;
import sb.yoon.kiosk.model.Menu;

import java.util.List;

// 어뎁터 클래스
public class CustomAdapter extends ArrayAdapter<Menu> {
    private final LayoutInflater layoutInflater;

    public CustomAdapter(Context context, int textViewResourceId, List<Menu> objects){
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
        view.setImage(getContext().getDrawable(R.drawable.apollo1));
        view.setText("TTTTT");

        TextView textView;
        textView = (TextView)convertView.findViewById(R.id.text);
        textView.setText(menu.getText());

        // 재료들 추가
        LinearLayout holder = convertView.findViewById(R.id.ingredientsHolder);
        for (int i = 0; i< menu.getIngredients().length; i++) {
            // 메뉴 아이템 (이미지 + 텍스트) 삽입
            ItemElement element = new ItemElement(getContext(), menu.getIngredients()[i].getIcon(), "Test");
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(150, 150);
            holder.addView(element, params);
        }

        return convertView;
    }
}
