package sb.yoon.kiosk.controller;

import android.content.Context;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import sb.yoon.kiosk.KioskListActivity;

public class HttpNetworkController {
    private RequestQueue requestQueue;
    private String url;
    private AppCompatActivity activity;

    public HttpNetworkController(AppCompatActivity activity) {
        this.requestQueue = Volley.newRequestQueue(activity);
    }

    public HttpNetworkController(AppCompatActivity activity, String url) {
        this.activity = activity;
        this.requestQueue = Volley.newRequestQueue(activity);
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void postJsonCartData(JSONObject jsonObject) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                url,
                jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("답신", response.toString());
                        KioskListActivity activity = (KioskListActivity) HttpNetworkController.this.activity;
                        try {
                            activity.popUpOrderNumberAndQuit(response.getInt("orderNumber"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                });
        requestQueue.add(jsonObjectRequest);
    }
}
