package sb.yoon.kiosk;

import android.content.Intent;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;

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
    private class easterClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            easterCount ++;
            if(easterCount == 5){
                //startActivity(new Intent(this, AdminActivty.class));
            }
        }
    }
}
