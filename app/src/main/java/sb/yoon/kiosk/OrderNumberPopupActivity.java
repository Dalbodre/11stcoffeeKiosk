package sb.yoon.kiosk;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Window;
import android.widget.TextView;

import java.util.Set;

public class OrderNumberPopupActivity extends Activity {
    public String msg = "";
    public Intent i;
    private Bundle extras;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_order_number_popup);

        i = getIntent();
        extras = i.getExtras();
        String card = (String) extras.get("ACQUIRER_NAME");
        String result_code = (String) extras.get("RESULT_CODE");
        String total_amount = (String) extras.get("TOTAL_AMOUNT");
        printInent(i);

        if (result_code.equals("0000")) {
            Log.d("결제완료", card + "로 " + total_amount + "만큼 " + "결제되었습니다.");

            TextView orderNumTextView = findViewById(R.id.text1);
            Intent intent = getIntent();
            int orderNumber = intent.getIntExtra("orderNumber", 0);
            orderNumTextView.setText("주문번호\n\n" + orderNumber);

            TextView text3 = findViewById(R.id.text3);
            text3.setText("메뉴가 준비되면 모니터에서\n안내해드리겠습니다.");
        }

        (new Handler()).postDelayed(this::finish, 8000);
    }

    public void printInent(Intent i) {
        try {
            //Log.e("KTC","-------------------------------------------------------");
            //util.saveLog("-------------------------------------------------------");
            if (i != null) {
                if (extras != null) {
                    Set keys = extras.keySet();

                    for (String _key : extras.keySet()) {
                        Log.e("RESULTACT","key=" + _key + " : " + extras.get(_key));
                        msg += "\n" + _key + "=";
                        msg += extras.get(_key);
                    }
                }
                // result.setText(msg);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }
    }

    public void onBackPressed() {
        finish();
    }
}