package sb.yoon.kiosk;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.ListFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import sb.yoon.kiosk.controller.CartListAdapter;
import sb.yoon.kiosk.model.CartMenu;


public class CartFragment extends ListFragment {

    private List<CartMenu> cartMenuList;
    private CartListAdapter adapter;

    public CartFragment() { cartMenuList = new ArrayList<>(); cartMenuList.add(new CartMenu(null, "test")); }

    public CartFragment(List<CartMenu> menuList) {
        this.cartMenuList = menuList;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 인덱스 표시 어댑터 설정
        adapter = new CartListAdapter(cartMenuList);
        // 어댑터를 설정
        setListAdapter(adapter);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //View view = super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_cart, container, false);
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
}