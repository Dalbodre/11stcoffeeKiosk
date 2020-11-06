package sb.yoon.kiosk.controller;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import java.util.List;

import sb.yoon.kiosk.R;
import sb.yoon.kiosk.layout.ItemElement;
import sb.yoon.kiosk.model.CartMenu;
import sb.yoon.kiosk.model.Ingredient;
import sb.yoon.kiosk.model.Menu;

// 어뎁터 클래스
public class CartListAdapter extends BaseAdapter {
    private List<CartMenu> cartMenuList;

    public CartListAdapter(List<CartMenu> menuList){
        this.cartMenuList = menuList;
    }

    @Override
    public int getCount() {
        return cartMenuList.size();
    }

    @Override
    public Object getItem(int i) {
        return cartMenuList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public int getViewTypeCount() { //List하나마다 카운트를 해야하는데 안적으면 랜덤으로 들어감...
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
    } //둘이 같이..

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        final Context context = parent.getContext();
        // 특정 행의 데이터 구함
        final CartMenu cartMenu = (CartMenu)getItem(position);

        // View는 재사용되기 때문에 처음에만 리스트 아이템 표시용 레이아웃을 읽어와서 생성함
        if(convertView == null){
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.cart_item, parent, false);
        }

        // View의 각 Widget에 데이터 저장
        ItemElement cartItemElement = convertView.findViewById(R.id.menu_element);
        Drawable drawable = cartMenu.getIcon();
        cartItemElement.setImageDrawable(drawable);
        cartItemElement.setText(cartMenu.getName());
        //cartItemElement.setText();


        // 없애기 버튼
        Button deleteButton = convertView.findViewById(R.id.delete);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cartMenuList.remove(cartMenu);
                CartListAdapter.this.notifyDataSetChanged();
            }
        });

        return convertView;
    }
}