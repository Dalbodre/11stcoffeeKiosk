package sb.yoon.kiosk;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import sb.yoon.kiosk.controller.DbQueryController;
import sb.yoon.kiosk.model.CartMenu;
import sb.yoon.kiosk.model.CartOption;
import sb.yoon.kiosk.model.Menu;
import sb.yoon.kiosk.model.Option;

import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;

import java.util.List;

public class PopupActivity extends Activity{
    CartMenu mCartMenu;
    Menu menu;
    DbQueryController dbQueryController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); //타이틀 없는 거 설정
        setContentView(R.layout.activity_popup);

        final Intent intent = getIntent();
        //Long id = intent.getLongExtra("menuId", 1L);

        KioskApplication app = (KioskApplication) getApplication();
        //Log.d("다오세션", app.getDaoSession().toString());
        dbQueryController = new DbQueryController(app.getDaoSession());

        menu = (Menu) intent.getParcelableExtra("menu");
        menu.__setDaoSession(app.getDaoSession());
        String iconPath = menu.getIconPath();
        String name = menu.getName();
        int price = menu.getPrice();
        Long categoryId = menu.getCategoryId();

        pop_TOP();          //위에 back/canel버튼
        pop_main_pic(iconPath, name, price);     //커피이미지 불러올 자리
        pop_coldhot();      //cold,hot버튼
        pop_addOptions(categoryId);   //추가옵션

        Button confirmedButton = findViewById(R.id.OptionSelectButton);
        confirmedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //CartOption cartOption = new CartOption();
                setResult(RESULT_OK, intent);

                finish();
            }
        });
    }

    private void pop_addOptions(Long cateId) {
        TextView textView = this.findViewById(R.id.addOptionText);
        Button shot = this.findViewById(R.id.pop_shot);
        Button hazel = this.findViewById(R.id.pop_hazel);
        Button sugar = this.findViewById(R.id.pop_sugar);
        Button nongdo = this.findViewById(R.id.pop_nongdo);

        if(cateId != 1L) { //카테고리가 커피가 아닌 경우 사라지게 만듬.
            textView.setVisibility(TextView.GONE);
            shot.setVisibility(Button.GONE);
            hazel.setVisibility(Button.GONE);
            sugar.setVisibility(Button.GONE);
            nongdo.setVisibility(Button.GONE);
        }
        else{ //카테고리가 커피인 경우 나타남.
            List<Option> options = dbQueryController.getOptionList(menu);
            Log.d("옵션들", options.toString());

            shot.setText(options.get(2).getName());
            hazel.setText(options.get(3).getName());
            sugar.setText(options.get(4).getName());
            nongdo.setText(options.get(5).getName());

            textView.setVisibility(TextView.VISIBLE);
            shot.setVisibility(Button.VISIBLE);
            sugar.setVisibility(Button.VISIBLE);
            hazel.setVisibility(Button.VISIBLE);
            nongdo.setVisibility(Button.VISIBLE);
        }
    }

    private void pop_coldhot() {
        Button ice = this.findViewById(R.id.pop_icemode);
        Button hot = this.findViewById(R.id.pop_hotmode);

        ice.setPressed(true);
    }

    private void pop_main_pic(String iconPath, String name, int price) {
        ImageView main = this.findViewById(R.id.pop_main_pic);

        Intent intent = getIntent();

        //Drawable 불러오기
        Drawable drawable = ContextCompat.getDrawable(this, this.getResources()
                .getIdentifier(iconPath, "drawable", this.getPackageName()));
        main.setImageDrawable(drawable);

        TextView main_coffee = this.findViewById(R.id.pop_main_pic_name);
        main_coffee.setText(name);                            //커피 이름 넣는 자리입니다. 여기 수정하면 커피 이름 수정됩니다.
        main_coffee.setTextSize(30);

        TextView main_coffee_price = this.findViewById(R.id.pop_main_pic_price);
        main_coffee_price.setText(Integer.toString(price)+"원");                        //커피 가격 넣는 자리입니다. 여기 수정하면 가격 수정됩니다.
        main_coffee_price.setTextSize(20);
    }

    private void pop_TOP() {
        //back버튼과 cancel 버튼입니다.
        //vector asset에서 들고 왔고, back버튼은 back, cancel버튼은 clear라고 입력하시면 바로 찾을 수 있습니다.
        ImageView back = this.findViewById(R.id.option_top_back);
        ImageView cancel = this.findViewById(R.id.option_top_cancel);

        Glide.with(this).load(R.drawable.ic_baseline_arrow_back_ios_24).into(back);
        Glide.with(this).load(R.drawable.ic_baseline_clear_24).into(cancel);
    }

    public void mOnClose(View v){
        //종료시키는 메서드 입니다.
        Intent intent = new Intent();
        intent.putExtra("result", "Close Popup");

        setResult(RESULT_OK, intent);

        finish();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // 팝업 바깥을 눌러도 팝업이 지워지지 않도록 return false를 둬서 아무 일 없도록 했습니다.
        if(event.getAction() == MotionEvent.ACTION_OUTSIDE)
            return false;
        return true;
    }

    public void mOptionSelect(View v){

    }

    @Override
    public void onBackPressed() {
        return;
    }
}