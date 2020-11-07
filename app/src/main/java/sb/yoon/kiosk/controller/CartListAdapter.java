package sb.yoon.kiosk.controller;

import android.content.ClipData;
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

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import sb.yoon.kiosk.KioskMain;
import sb.yoon.kiosk.R;
import sb.yoon.kiosk.layout.ItemElement;
import sb.yoon.kiosk.model.CartMenu;
import sb.yoon.kiosk.model.Ingredient;
import sb.yoon.kiosk.model.Menu;

// 어뎁터 클래스
public class CartListAdapter extends RecyclerView.Adapter<CartListAdapter.CustomViewHolder> {
    private List<CartMenu> cartMenuList;

    // 뷰 홀더
    public class CustomViewHolder extends RecyclerView.ViewHolder {
        protected ItemElement itemElement;
        protected TextView price;
        protected Button deleteButton;
        protected KioskMain context;

        public CustomViewHolder(View v){
            super(v);
            this.itemElement = (ItemElement)v.findViewById(R.id.menu_element);
            this.price = (TextView)v.findViewById(R.id.cart_item_price_tag);
            this.deleteButton = v.findViewById(R.id.cart_item_delete_button);
            this.context = (KioskMain) v.getContext();
        }

        public KioskMain getContext() {
            return context;
        }
    }

    public CartListAdapter(List<CartMenu> menuList){
        this.cartMenuList = menuList;
    }

    @NotNull
    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType){
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.cart_item, viewGroup, false);

        return new CustomViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final CustomViewHolder viewHolder, final int position) {
        viewHolder.itemElement.setImageDrawable(cartMenuList.get(position).getIcon());
        viewHolder.itemElement.setText(cartMenuList.get(position).getName());
        viewHolder.price.setText(Integer.toString(cartMenuList.get(position).getTotalPrice()));

        viewHolder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewHolder.getContext().delCartMenuList(position);
            }
        });
    }

    @Override
    public int getItemCount(){
        return (null != cartMenuList ? cartMenuList.size() : 0);
    }
}