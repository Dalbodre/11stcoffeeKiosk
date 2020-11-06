package sb.yoon.kiosk;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.ListFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import sb.yoon.kiosk.controller.CartListAdapter;
import sb.yoon.kiosk.controller.HttpNetworkController;
import sb.yoon.kiosk.model.CartMenu;
import sb.yoon.kiosk.model.CartOption;
import sb.yoon.kiosk.model.Menu;
import sb.yoon.kiosk.model.Option;


public class CartFragment extends ListFragment {

    private List<CartMenu> cartMenuList = new ArrayList<>();
    private CartListAdapter adapter;
    private View view;

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
        view = inflater.inflate(R.layout.fragment_cart, container, false);

        // 숨김/보이기 버튼 리스너
        Button hideOrShowButton = view.findViewById(R.id.hideshow_button);
        hideOrShowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setListWrapperVisibility(!getListWrapperVisibility());
            }
        });

        // 결제 버튼 리스너
        final Gson gson = new GsonBuilder()
                .excludeFieldsWithoutExposeAnnotation()
                .create();
        final TextView totalPriceView = view.findViewById(R.id.total_price);
        Button purchaseButton = view.findViewById(R.id.purchase_button);
        purchaseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("결제가격", totalPriceView.getText());
                    jsonObject.put("포장", "N");
                    jsonObject.put("메뉴", new JSONArray(gson.toJson(cartMenuList,
                            new TypeToken<List<CartMenu>>(){}.getType())));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.d("결제 데이터", jsonObject.toString());
                HttpNetworkController httpController = new HttpNetworkController(getContext(), "https://reqres.in/api/users");
                httpController.postJson(jsonObject);
            }
        });

        return view;
    }

    public void setListWrapperVisibility(boolean visibility) {
        LinearLayout listWrapper = view.findViewById(R.id.list_wrapper);
        listWrapper.setVisibility(visibility ? View.VISIBLE : View.GONE);
    }

    public boolean getListWrapperVisibility() {
        LinearLayout listWrapper = view.findViewById(R.id.list_wrapper);
        return listWrapper.getVisibility() == View.VISIBLE;
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

        // @todo 옵션에 따른 추가요금 적용
        int totalPrice = 0;

        List<CartOption> cartOptions = new ArrayList<>();
        cartOptions.add(new CartOption("test option", "1", 500));

        totalPrice = totalPrice + menu.getPrice();

        CartMenu cartMenu = new CartMenu(drawable, menu.getName(), menu.getPrice(), totalPrice, cartOptions);
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