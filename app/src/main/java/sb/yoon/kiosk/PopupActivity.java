package sb.yoon.kiosk;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import sb.yoon.kiosk.controller.CartListAdapter;
import sb.yoon.kiosk.controller.OptionListAdapter;
import sb.yoon.kiosk.layout.SearchItemDecoration;
import sb.yoon.kiosk.model.CartMenu;
import sb.yoon.kiosk.model.CartOption;

import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import java.util.List;

public class PopupActivity extends Activity{
    private RecyclerView optionRecyclerView;
    private OptionListAdapter optionListAdapter;
    public static CartMenu cartMenu;
    private List<CartOption> cartOptionList;

    private ToggleButton hotButton;
    private ToggleButton iceButton;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); //타이틀 없는 거 설정
        setContentView(R.layout.activity_popup);

        if (cartMenu != null) {
            Log.d("팝업인풋", cartMenu.toString());
        }

        if (cartMenu != null) {
            cartOptionList = cartMenu.getOptions();
        }

        Log.d("카트옵션", cartOptionList.toString());

        hotButton = findViewById(R.id.option_hot_toggle);
        iceButton = findViewById(R.id.option_ice_toggle);
        hotButton.setOnClickListener(new OnToggleChanged());
        iceButton.setOnClickListener(new OnToggleChanged());

        ImageView imageView = findViewById(R.id.pop_up_option_pic);
        imageView.setImageDrawable(cartMenu.getIcon());

        TextView menuText = findViewById(R.id.pop_up_option_name);
        TextView menuPriceText = findViewById(R.id.pop_up_option_price);
        menuText.setText(cartMenu.getName());
        menuPriceText.setText(cartMenu.getPrice() + "원");

        optionRecyclerView = this.findViewById(R.id.option_recycler);
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(this);
        mLinearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        optionRecyclerView.setLayoutManager(mLinearLayoutManager);

        optionListAdapter = new OptionListAdapter(cartOptionList);
        optionRecyclerView.setAdapter(optionListAdapter);
    }

    private class OnToggleChanged implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            ToggleButton toggleButton = (ToggleButton) view;
            if (toggleButton.equals(hotButton)) {
                if (toggleButton.isChecked()) {
                    //toggleButton.setChecked(false);
                    iceButton.setChecked(false);
                } else {
                    //toggleButton.setChecked(true);
                    iceButton.setChecked(true);
                }
            } else if (toggleButton.equals(iceButton)) {
                if (toggleButton.isChecked()) {
                    //toggleButton.setChecked(false);
                    hotButton.setChecked(false);
                } else {
                    //toggleButton.setChecked(true);
                    hotButton.setChecked(true);
                }
            }
        }
    }

    public void onClickPopUpCloseButtons(View view) {
        finish();
    }

    public void onClickPopUpConfirmedButtons(View view) {
        KioskListActivity kioskListActivity = ((KioskApplication) getApplication()).getKioskListActivity();
        cartMenu.setOptions(cartOptionList);
        int optionsTotalPrice = 0;
        for (CartOption cartOption : cartOptionList) {
            optionsTotalPrice += cartOption.getPrice() * cartOption.getQuantity();
        }
        cartMenu.setTotalPrice(cartMenu.getPrice() + optionsTotalPrice);
        kioskListActivity.addCartMenuList(cartMenu);
        finish();
    }
}