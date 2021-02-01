package sb.yoon.kiosk;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

public class EnterMain extends AppCompatActivity {
    ProgressBar progressBar;
    ImageView SettingButton;

    private int easterCount=0;


    private final int W_STORAGE_PER_CODE = 333;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_main);

        progressBar = findViewById(R.id.progressBar);
        ImageView EASTER = findViewById(R.id.enter_easter);
        EASTER.setOnClickListener(new easterClickListener());

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                        W_STORAGE_PER_CODE);
        } else {
            // Toast.makeText(this.getApplicationContext(), "이미 허가되었습니다", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        progressBar.setVisibility(ProgressBar.GONE);
        Log.d("Status", "Resume");
    }

    public void buttonClicked(View view) {
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(ProgressBar.VISIBLE);
        Intent intent = new Intent(this, KioskListActivity.class);
        //Intent intent = new Intent(this, AdminActivity.class);
        startActivity(intent);
        //finish();
    }
    private class easterClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            easterCount ++;
            if(easterCount == 3 || easterCount == 4){
                Toast.makeText(EnterMain.this, easterCount+"만큼 입력하셨습니다.", Toast.LENGTH_SHORT).show();
            }
            if(easterCount == 5){
                startActivity(new Intent(this, AdminActivty.class));
                finish();
                easterCount = 0;
            }
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case W_STORAGE_PER_CODE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    Toast.makeText(this,"승인이 허가되어 있습니다.", Toast.LENGTH_LONG).show();

                } else {
                    Toast.makeText(this,"아직 승인받지 않았습니다.",Toast.LENGTH_LONG).show();
                }
                return;
            }

        }
    }
}
