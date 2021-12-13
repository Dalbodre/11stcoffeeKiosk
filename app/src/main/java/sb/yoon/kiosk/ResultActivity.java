package sb.yoon.kiosk2;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.util.Set;

public class ResultActivity extends Activity {
	private TextView result;
	public String msg = "";
	public Intent i;
	private Bundle extras;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_result);
		result = (TextView)findViewById(R.id.TextView1);
		i = getIntent();
		extras = i.getExtras();
		String card = (String) extras.get("ACQUIRER_NAME");
		String result_code = (String) extras.get("RESULT_CODE");
		String total_amount = (String) extras.get("TOTAL_AMOUNT");
		printInent(i);

		if (result_code.equals("0000")) {
			Log.d("결제완료", card + "로 " + total_amount + "만큼 " + "결제되었습니다.");
		}
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
		finishAffinity();
	}

}
