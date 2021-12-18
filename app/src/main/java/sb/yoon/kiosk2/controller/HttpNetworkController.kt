package sb.yoon.kiosk2.controller

import androidx.appcompat.app.AppCompatActivity
import com.android.volley.RequestQueue
import org.json.JSONObject
import com.android.volley.toolbox.JsonObjectRequest
import androidx.annotation.RequiresApi
import android.os.Build
import android.util.Log
import com.android.volley.Request
import sb.yoon.kiosk2.KioskListActivity
import org.json.JSONException
import com.android.volley.toolbox.Volley

class HttpNetworkController(private val activity: AppCompatActivity, private var url: String) {
    private val requestQueue: RequestQueue = Volley.newRequestQueue(activity)

    @RequiresApi(Build.VERSION_CODES.N)
    fun postJsonCartData(jsonObject: JSONObject?) {
        val jsonObjectRequest = JsonObjectRequest(Request.Method.POST,
                url,
                jsonObject,
                { response ->
                    Log.d("답신", response.toString())
                    val activity = activity as KioskListActivity
                    try {
                        activity.popUpOrderNumberAndQuit(response.getInt("orderNumber"), true)
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                }
        ) { error -> error.printStackTrace() }
        requestQueue.add(jsonObjectRequest)
    }
}