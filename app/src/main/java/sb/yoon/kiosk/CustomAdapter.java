package sb.yoon.kiosk;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import java.util.List;

// 데이터 클래스
class Menu {
    private Drawable icon;
    private String text;
    private Ingredient[] ingredients;
    private int[] prices;

    Menu(String text, Drawable icon, int[] prices, Ingredient[] ingredients){
        this.icon = icon;
        this.text = text;
        this.prices = prices;
        this.ingredients = ingredients;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }
    public void setText(String text) {
        this.text = text;
    }
    public void setPrices(int[] prices) {
        this.prices = prices;
    }
    public void setIngredients(Ingredient[] ingredients) {
        this.ingredients = ingredients;
    }

    public Drawable getIcon() {
        return this.icon;
    }
    public String getText() {
        return this.text;
    }
    public Ingredient[] getIngredients() {
        return ingredients;
    }
    public int[] getPrices() {
        return prices;
    }
}

class Ingredient {
    private Drawable icon;
    private String text;

    public Ingredient(String text, Drawable icon) {
        this.icon = icon;
        this.text = text;
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}

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
            ingredIcon.setMaxWidth(100);
            ingredIcon.setMaxHeight(100);
            holder.addView(ingredIcon);
        }

        return convertView;
    }
}
