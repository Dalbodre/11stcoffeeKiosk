package sb.yoon.kiosk2

import android.app.Activity
import android.widget.TextView
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import java.lang.Exception

class ResultActivity : Activity() {
    private var result: TextView? = null
    var msg: String? = ""
    private var extras: Bundle? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)
        result = findViewById<View>(R.id.TextView1) as TextView
        extras = intent.extras
        val card = extras!!["ACQUIRER_NAME"] as String?
        val resultCode = extras!!["RESULT_CODE"] as String?
        val totalAmount = extras!!["TOTAL_AMOUNT"] as String?
        printInent(intent)
        if (resultCode == "0000") {
            Log.d("결제완료", card + "로 " + totalAmount + "만큼 " + "결제되었습니다.")
        }
    }

    fun printInent(i: Intent?) {
        try {
            if (i != null) {
                if (extras != null) {
                    val keys: Set<*> = extras!!.keySet()
                    for (_key in extras!!.keySet()) {
                        Log.e("RESULTACT", "key=" + _key + " : " + extras!![_key])
                        msg += "\n$_key="
                        msg += extras!![_key]
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onBackPressed() {
        finishAffinity()
    }
}