package sb.yoon.kiosk;

import android.content.Intent;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

public class EnterMain extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_main);
    }

    public void buttonClicked(View view) {
        Intent intent = new Intent(this, KioskListActivity.class);
        startActivity(intent);

        //finish();
    }
}
