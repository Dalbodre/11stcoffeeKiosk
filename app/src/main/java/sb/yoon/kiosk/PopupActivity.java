package sb.yoon.kiosk;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;

import sb.yoon.kiosk.controller.OptionListAdapter;
import sb.yoon.kiosk.model.CartMenu;
import sb.yoon.kiosk.model.CartOption;

import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class PopupActivity extends Activity {
    private RecyclerView optionRecyclerView;
    private OptionListAdapter optionListAdapter;
    public static CartMenu cartMenu;
    private List<CartOption> cartOptionList; //tests

    private ToggleButton hotButton;
    private ToggleButton iceButton;
    private ToggleButton takeout;
    private ToggleButton no_takeout;

    private ToggleButton tumbler;

    private String com_blue = "#081832";
    private String com_white = "#ffffff";

    private Button confirm;

    private Drawable packageIcon;
    private Drawable tableIcon;
    private Drawable thumblerIcon;

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

        //to. JoRim: 나중에 시연해보고 나서 아이콘 크기 별로면 여기를 건드려서 해결하길 바람
        Drawable iceIcon = ContextCompat.getDrawable(this, R.drawable.ice_cubes);
        iceIcon.setBounds(0,0,130,130);
        iceButton.setCompoundDrawables(iceIcon,null,null,null);

        Drawable hotIcon = ContextCompat.getDrawable(this, R.drawable.burn);
        hotIcon.setBounds(0,0,130,130);
        hotButton.setCompoundDrawables(hotIcon,null,null,null);

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

        icons(); // 아이콘 너무 많아서 아이콘 별로 따로 메서드 팠습니다
        //왜 커밋이 오류가 날까요?
        tumbler.setChecked(false);
        tumbler.setBackgroundDrawable(ContextCompat.getDrawable(PopupActivity.this, R.drawable.togglebutton_off));
        if (cartMenu.getCategoryId() == (Long)9L) {tumbler.setText("선택불가"); tumbler.setEnabled(false);}
    }

    private void icons() {
        // 크기 조절은 여기서
        tableIcon = ContextCompat.getDrawable(this, R.drawable.cup_xxxhdpi);
        tableIcon.setBounds(0,0,100,100);
        DrawableCompat.setTint(tableIcon, Color.BLACK);
        no_takeout.setCompoundDrawables(null, tableIcon,null,null);

        packageIcon = ContextCompat.getDrawable(this, R.drawable.takeout_xxxhdpi);
        packageIcon.setBounds(0,0,80,100);
        DrawableCompat.setTint(packageIcon, Color.BLACK);
        takeout.setCompoundDrawables(null, packageIcon,null,null);

        thumblerIcon = ContextCompat.getDrawable(this, R.drawable.thermos_xxxhdpi);
        thumblerIcon.setBounds(0,0,110,110);
        DrawableCompat.setTint(thumblerIcon, Color.BLACK);
        tumbler.setCompoundDrawables(null, thumblerIcon,null,null);

    }

    private class OnTempToggleChanged implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            ToggleButton tButton = (ToggleButton) view;
            if(tButton.equals(iceButton)){
                iceButton.setChecked(true);
                iceButton.setBackgroundDrawable(ContextCompat.getDrawable(PopupActivity.this, R.drawable.togglebutton_on));
                iceButton.setTextColor(Color.parseColor(com_white));
                hotButton.setChecked(false);
                hotButton.setBackgroundDrawable(ContextCompat.getDrawable(PopupActivity.this, R.drawable.togglebutton_off));
                hotButton.setTextColor(Color.parseColor(com_blue));
            }
            else if(tButton.equals(hotButton)){
                iceButton.setChecked(false);
                iceButton.setBackgroundDrawable(ContextCompat.getDrawable(PopupActivity.this, R.drawable.togglebutton_off));
                iceButton.setTextColor(Color.parseColor("#081832"));
                hotButton.setChecked(true);
                hotButton.setBackgroundDrawable(ContextCompat.getDrawable(PopupActivity.this, R.drawable.togglebutton_on));
                hotButton.setTextColor(Color.parseColor("#ffffff"));
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
                tumbler.setTextColor(Color.parseColor(com_blue));

                DrawableCompat.setTint(thumblerIcon, Color.BLACK);
                tumbler.setCompoundDrawables(null, thumblerIcon,null,null);
            }
            else{
                tumbler.setChecked(true);
                tumbler.setBackgroundDrawable(ContextCompat.getDrawable(PopupActivity.this, R.drawable.togglebutton_on));
                no_takeout.setCompoundDrawables(null, tableIcon,null,null);
                tumbler.setTextColor(Color.parseColor(com_white));

                DrawableCompat.setTint(thumblerIcon, Color.WHITE);
                tumbler.setCompoundDrawables(null, thumblerIcon,null,null);
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
                takeout.setTextColor(Color.parseColor(com_white));
                no_takeout.setChecked(false);
                no_takeout.setBackgroundDrawable(ContextCompat.getDrawable(PopupActivity.this, R.drawable.togglebutton_off));
                no_takeout.setTextColor(Color.parseColor(com_blue));

                DrawableCompat.setTint(packageIcon, Color.WHITE);
                takeout.setCompoundDrawables(null, packageIcon,null,null);
                DrawableCompat.setTint(tableIcon, Color.BLACK);
                no_takeout.setCompoundDrawables(null, tableIcon,null,null);
            }
            else if(tButton.equals(no_takeout)){
                takeout.setChecked(false);
                takeout.setBackgroundDrawable(ContextCompat.getDrawable(PopupActivity.this, R.drawable.togglebutton_off));
                takeout.setTextColor(Color.parseColor(com_blue));
                no_takeout.setChecked(true);
                no_takeout.setBackgroundDrawable(ContextCompat.getDrawable(PopupActivity.this, R.drawable.togglebutton_on));
                no_takeout.setTextColor(Color.parseColor(com_white));

                DrawableCompat.setTint(packageIcon, Color.BLACK);
                takeout.setCompoundDrawables(null, packageIcon,null,null);
                DrawableCompat.setTint(tableIcon, Color.WHITE);
                no_takeout.setCompoundDrawables(null, tableIcon,null,null);
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
            cartMenu.setTemp("핫");
        } else if (iceButton.isChecked()) {
            cartMenu.setTemp("아이스");
        }

        if (tumbler.isChecked()) {
            cartMenu.setTumbler(true);
            cartMenu.setTotalPrice(cartMenu.getTotalPrice() -200);
        }

        if(!takeout.isChecked() && !no_takeout.isChecked()){
            // https://hydok.tistory.com/24 (토스트 메세지 글씨크기)
            Toast toast = Toast.makeText(this, "포장 혹은 매장 버튼을 선택해주세요.", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER_VERTICAL,0,0);
            toast.show();
        }
        else{
            cartMenu.setTakeOut(takeout.isChecked());
            kioskListActivity.addCartMenuList(cartMenu);
            finish();
        }
    }
}

/*
팝업 32인치 기준으로 모두 해결. 21.01.12
*/
