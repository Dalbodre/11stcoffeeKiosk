package sb.yoon.kiosk;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
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

        TextView text3 = findViewById(R.id.text3);

        if (result_code.equals("0000")) {
            Log.d("결제완료", card + "로 " + total_amount + "만큼 " + "결제되었습니다.");

            TextView orderNumTextView = findViewById(R.id.text1);
            Intent intent = getIntent();
            int orderNumber = intent.getIntExtra("orderNumber", 0);
            orderNumTextView.setText("주문번호\n\n" + orderNumber);

            text3 = findViewById(R.id.text3);
            text3.setText("매장에서 드실 경우");
            text3.setTextColor(Color.parseColor("#ff0000"));
            TextView text4 = findViewById(R.id.text4);
            text4.setText("반드시 2차접종증명(2주경과)을 하셔야합니다.");
        } else {
            TextView text2 = findViewById(R.id.text2);
            TextView banner = findViewById(R.id.result_banner);
            TextView text1 = findViewById(R.id.text1);
            banner.setText("결제 오류");
            text2.setText("결제 오류가 발생했습니다.\n에러코드:" + result_code);
            text3.setText("에러로 인해 카드 결제가 불가능합니다. \n 카운터에서 결제 안내해드리겠습니다.");
            text1.setText(getResources().getIdentifier("card_err_" + result_code,"string",getPackageName()));
            text1.setTextSize(30f);
            text3.setTextColor(ContextCompat.getColor(this, R.color.hotRed));
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