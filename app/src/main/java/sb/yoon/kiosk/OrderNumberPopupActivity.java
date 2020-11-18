package sb.yoon.kiosk;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.widget.TextView;

public class OrderNumberPopupActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_order_number_popup);

        TextView orderNumTextView = findViewById(R.id.text1);
        Intent intent = new Intent();
        int orderNumber = intent.getIntExtra("orderNumber", 10101);
        orderNumTextView.setText("주문번호 : " + orderNumber);

        (new Handler()).postDelayed(this::finish, 5000);
    }
}