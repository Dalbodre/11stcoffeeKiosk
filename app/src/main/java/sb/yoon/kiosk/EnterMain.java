package sb.yoon.kiosk;

import android.content.Intent;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

public class EnterMain extends AppCompatActivity {
    ProgressBar progressBar;
    ImageView SettingButton;

    private int easterCount=0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_main);

        progressBar = findViewById(R.id.progressBar);
        View EASTER = findViewById(R.id.enter_easter);
        EASTER.setOnClickListener(new easterClickListener());
    }

    @Override
    protected void onResume() {
        super.onResume();
        progressBar.setVisibility(ProgressBar.GONE);
    }

    public void buttonClicked(View view) {
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(ProgressBar.VISIBLE);
        Intent intent = new Intent(this, KioskListActivity.class);
        startActivity(intent);
        //finish();
    }

    public void openListActivity() {
        Intent intent = new Intent(this, KioskListActivity.class);
        startActivity(intent);
        //finish();
    }
    private class easterClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            easterCount ++;
            if(easterCount == 5)
                finish();
        }
    }

    public void checkServerOn() {
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        String url = getResources().getString(R.string.server_ip);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        System.out.println(response);
                        openListActivity();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //System.out.println(error.toString());
                TextView tv = EnterMain.this.findViewById(R.id.alertTextView);
                tv.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
                //showToast(getString(R.string.server_not_reachable));
            }
        });
        int socketTimeout = 6000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(policy);
        requestQueue.add(stringRequest);
    };
}
