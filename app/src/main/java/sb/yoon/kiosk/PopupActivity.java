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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); //타이틀 없는 거 설정
        setContentView(R.layout.activity_popup);

        final Intent intent = getIntent();
        CartMenu cartMenu = intent.getParcelableExtra("cartMenu");
        if (cartMenu != null) {
            Log.d("팝업인풋", cartMenu.toString());
        }
    }

    public void onClickPopUpBackButton(View view) {

    }
}