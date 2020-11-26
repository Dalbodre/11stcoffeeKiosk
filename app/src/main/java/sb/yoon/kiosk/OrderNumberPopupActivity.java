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
        Intent intent = getIntent();
        int orderNumber = intent.getIntExtra("orderNumber", 0);
        orderNumTextView.setText("주문번호\n\n" + orderNumber);

        TextView text3 = findViewById(R.id.text3);
        text3.setText("메뉴가 준비되면 모니터에서\n안내해드리겠습니다.");

        (new Handler()).postDelayed(this::finish, 8000);
    }
}