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
import android.widget.Toast;
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
    private ToggleButton takeout;
    private ToggleButton no_takeout;
    private ToggleButton tumbler;

    private Button confirm;
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
        takeout = findViewById(R.id.option_takeout_toggle);
        no_takeout = findViewById(R.id.option_store_toggle);
        confirm = findViewById(R.id.option_confirm_button);
        tumbler = findViewById(R.id.option_tumblr_toggle);

        hotButton.setOnClickListener(new OnTempToggleChanged());
        iceButton.setOnClickListener(new OnTempToggleChanged());
        takeout.setOnClickListener(new OnTakeoutToggleChanged());
        no_takeout.setOnClickListener(new OnTakeoutToggleChanged());
        tumbler.setOnClickListener(new OnTumblerToggleChanged());

        init();
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

    public void init(){
        iceButton.setChecked(false);
        hotButton.setChecked(true);

        iceButton.setBackgroundDrawable(ContextCompat.getDrawable(PopupActivity.this, R.drawable.togglebutton_off));
        hotButton.setBackgroundDrawable(ContextCompat.getDrawable(PopupActivity.this, R.drawable.togglebutton_on));
        takeout.setBackgroundDrawable(ContextCompat.getDrawable(PopupActivity.this, R.drawable.togglebutton_off));
        no_takeout.setBackgroundDrawable(ContextCompat.getDrawable(PopupActivity.this, R.drawable.togglebutton_off));

        if (cartMenu.isCold()) {
            iceButton.setVisibility(ToggleButton.VISIBLE);
        }
        if (cartMenu.isHot()) {
            hotButton.setVisibility(ToggleButton.VISIBLE);
        }

        if (!cartMenu.isHot() && cartMenu.isCold()) {
            iceButton.setChecked(true);
            hotButton.setChecked(false);
            iceButton.setBackgroundDrawable(ContextCompat.getDrawable(PopupActivity.this, R.drawable.togglebutton_on));
            hotButton.setBackgroundDrawable(ContextCompat.getDrawable(PopupActivity.this, R.drawable.togglebutton_off));
        }

        tumbler.setChecked(false);
        tumbler.setBackgroundDrawable(ContextCompat.getDrawable(PopupActivity.this, R.drawable.togglebutton_off));
    }

    private class OnTempToggleChanged implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            ToggleButton tButton = (ToggleButton) view;
            if(tButton.equals(iceButton)){
                iceButton.setChecked(true);
                iceButton.setBackgroundDrawable(ContextCompat.getDrawable(PopupActivity.this, R.drawable.togglebutton_on));
                hotButton.setChecked(false);
                hotButton.setBackgroundDrawable(ContextCompat.getDrawable(PopupActivity.this, R.drawable.togglebutton_off));
            }
            else if(tButton.equals(hotButton)){
                iceButton.setChecked(false);
                iceButton.setBackgroundDrawable(ContextCompat.getDrawable(PopupActivity.this, R.drawable.togglebutton_off));
                hotButton.setChecked(true);
                hotButton.setBackgroundDrawable(ContextCompat.getDrawable(PopupActivity.this, R.drawable.togglebutton_on));
            }
        }
    }

    private class OnTumblerToggleChanged implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            ToggleButton tButton = (ToggleButton) view;
            if(!tumbler.isChecked()){
                tumbler.setChecked(false);
                tumbler.setBackgroundDrawable(ContextCompat.getDrawable(PopupActivity.this, R.drawable.togglebutton_off));
            }
            else{
                tumbler.setChecked(true);
                tumbler.setBackgroundDrawable(ContextCompat.getDrawable(PopupActivity.this, R.drawable.togglebutton_on));
            }
        }
    }

    private class OnTakeoutToggleChanged implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            ToggleButton tButton = (ToggleButton) view;
            if(tButton.equals(takeout)){
                takeout.setChecked(true);
                takeout.setBackgroundDrawable(ContextCompat.getDrawable(PopupActivity.this, R.drawable.togglebutton_on));
                no_takeout.setChecked(false);
                no_takeout.setBackgroundDrawable(ContextCompat.getDrawable(PopupActivity.this, R.drawable.togglebutton_off));
            }
            else if(tButton.equals(no_takeout)){
                takeout.setChecked(false);
                takeout.setBackgroundDrawable(ContextCompat.getDrawable(PopupActivity.this, R.drawable.togglebutton_off));
                no_takeout.setChecked(true);
                no_takeout.setBackgroundDrawable(ContextCompat.getDrawable(PopupActivity.this, R.drawable.togglebutton_on));
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

        if (hotButton.isChecked()) {
            cartMenu.setTemp("뜨거움");
        } else if (iceButton.isChecked()) {
            cartMenu.setTemp("차가움");
        }

        if (tumbler.isChecked()) {
            cartMenu.setTumblr(true);
            cartMenu.setTotalPrice(cartMenu.getTotalPrice() -200);
        }

        if(!takeout.isChecked() && !no_takeout.isChecked()){
            Toast.makeText(this, "포장 혹은 매장 버튼을 선택해주세요.", Toast.LENGTH_LONG).show();
        }
        else{
            cartMenu.setTakeOut(takeout.isChecked());
            kioskListActivity.addCartMenuList(cartMenu);
            finish();
        }
    }
}