package sb.yoon.kiosk2

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.Window
import android.widget.TextView
import androidx.core.content.ContextCompat
import java.lang.Exception

class OrderNumberPopupActivity : Activity() {
    var msg: String? = ""
    private var extras: Bundle? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.activity_order_number_popup)
        extras = intent.extras
        val card = extras!!["ACQUIRER_NAME"] as String?
        val resultCode = extras!!["RESULT_CODE"] as String?
        val totalAmount = extras!!["TOTAL_AMOUNT"] as String?
        printInent(intent)
        var text3 = findViewById<TextView>(R.id.text3)
        if (resultCode == "0000") {
            Log.d("결제완료", card + "로 " + totalAmount + "만큼 " + "결제되었습니다.")
            val orderNumTextView = findViewById<TextView>(R.id.text1)
            val orderNumber = intent.getIntExtra("orderNumber", 0)
            orderNumTextView.text = "주문번호\n\n$orderNumber"
            text3 = findViewById(R.id.text3)
            text3.text = "메뉴가 준비되면 모니터에서\n안내해드리겠습니다."
        } else {
            val text2 = findViewById<TextView>(R.id.text2)
            val banner = findViewById<TextView>(R.id.result_banner)
            val text1 = findViewById<TextView>(R.id.text1)
            banner.text = "결제 오류"
            text2.text = "결제 오류가 발생했습니다.\n에러코드:$resultCode"
            text3.text = "에러로 인해 카드 결제가 불가능합니다. \n 카운터에서 결제 안내해드리겠습니다."
            text1.setText(resources.getIdentifier("card_err_$resultCode", "string", packageName))
            text1.textSize = 30f
            text3.setTextColor(ContextCompat.getColor(this, R.color.hotRed))
        }
        Handler().postDelayed({ finish() }, 8000)
    }

    private fun printInent(i: Intent?) {
        try {
            if (i != null) {
                if (extras != null) {
                    for (_key in extras!!.keySet()) {
                        Log.e("RESULTACT", "key=" + _key + " : " + extras!![_key])
                        msg += "\n$_key="
                        msg += extras!![_key]
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
        }
    }

    override fun onBackPressed() {
        finish()
    }
}