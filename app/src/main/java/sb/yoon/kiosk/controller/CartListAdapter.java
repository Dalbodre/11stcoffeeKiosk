package sb.yoon.kiosk.controller;

import android.icu.text.UFormat;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import sb.yoon.kiosk.KioskListActivity;
import sb.yoon.kiosk.R;
import sb.yoon.kiosk.layout.IngredientItem;
import sb.yoon.kiosk.model.CartMenu;
import sb.yoon.kiosk.model.CartOption;

// 어뎁터 클래스
public class CartListAdapter extends RecyclerView.Adapter<CartListAdapter.CustomViewHolder> {
    private List<CartMenu> cartMenuList;

    // 뷰 홀더
    public class CustomViewHolder extends RecyclerView.ViewHolder {
        protected IngredientItem itemElement;
        protected TextView price;
        protected KioskListActivity context;
        protected TextView tempText;
        protected TextView takeOutText;
        protected ImageButton cancel_button;

        public CustomViewHolder(View v){
            super(v);
            this.itemElement = (IngredientItem)v.findViewById(R.id.menu_element);
            this.cancel_button = (ImageButton)v.findViewById(R.id.cart_cancel_button);
            this.price = (TextView)v.findViewById(R.id.cart_item_price_tag);
            this.tempText = v.findViewById(R.id.temp);
            this.takeOutText = v.findViewById(R.id.cart_item_take_out);
            this.context = (KioskListActivity) v.getContext();
        }

        public KioskListActivity getContext() {
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
        TextView tv = v.findViewById(R.id.search_name);
        tv.setVisibility(View.GONE);
        return new CustomViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final CustomViewHolder viewHolder, final int position) {
        viewHolder.itemElement.setImageDrawable(cartMenuList.get(position).getIcon());
        //viewHolder.itemElement.setName(cartMenuList.get(position).getName());
        viewHolder.price.setText(Integer.toString(cartMenuList.get(position).getTotalPrice()));
        List<CartOption> OptionList = cartMenuList.get(position).getOptions();
        //String menuTemp = OptionList.get()

        if (cartMenuList.get(position).isTakeOut()) {
            viewHolder.takeOutText.setText("포장");
        } else {
            viewHolder.takeOutText.setText("매장");
        }
        viewHolder.tempText.setText(cartMenuList.get(position).getTemp());

        viewHolder.cancel_button.setOnClickListener(new View.OnClickListener() {
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