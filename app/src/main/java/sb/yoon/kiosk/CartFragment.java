package sb.yoon.kiosk;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.ListFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

import sb.yoon.kiosk.controller.CartListAdapter;
import sb.yoon.kiosk.model.CartMenu;
import sb.yoon.kiosk.model.Menu;


public class CartFragment extends ListFragment {

    private List<CartMenu> cartMenuList = new ArrayList<>();
    private CartListAdapter adapter;

    public CartFragment() {
        this.setAdapter();
    }

    public CartFragment(List<CartMenu> menuList) {
        this.setAdapter();
        this.setCartMenuList(menuList);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //View view = super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_cart, container, false);

        Button button = view.findViewById(R.id.hideshow_button);
        final LinearLayout listWrapper = view.findViewById(R.id.list_wrapper);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listWrapper.getVisibility() == View.GONE)
                    listWrapper.setVisibility(View.VISIBLE);
                else
                    listWrapper.setVisibility(View.GONE);
            }
        });

        return view;
    }

    public void delCartMenuList(int position) {
        this.cartMenuList.remove(position);
        adapter.notifyDataSetChanged();
    }

    public void addCartMenuList(CartMenu cartMenu) {
        this.cartMenuList.add(cartMenu);
        adapter.notifyDataSetChanged();
    }

    public void addCartMenuList(Menu menu) {
        Context context = getContext();
        Drawable drawable = ContextCompat.getDrawable(context, context.getResources()
                .getIdentifier(menu.getIconPath(), "drawable", context.getPackageName()));

        CartMenu cartMenu = new CartMenu(drawable, menu.getName());
        this.cartMenuList.add(cartMenu);
        adapter.notifyDataSetChanged();
    }

    public void setCartMenuList(List<CartMenu> cartMenuList) {
        this.cartMenuList = cartMenuList;
        adapter.notifyDataSetChanged();
    }

    private void setAdapter() {
        // 인덱스 표시 어댑터 설정
        adapter = new CartListAdapter(cartMenuList);
        // 어댑터를 설정
        this.setListAdapter(adapter);
    }
}