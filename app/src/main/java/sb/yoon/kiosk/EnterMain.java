package sb.yoon.kiosk;

import android.content.Intent;
import android.view.View;
import android.widget.LinearLayout;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.res.ResourcesCompat;
import sb.yoon.kiosk.layout.ItemElement;

public class EnterMain extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_main);
    }

    public void buttonClicked(View view) {
        Intent intent = new Intent(this, KioskMain.class);
        startActivity(intent);

        finish();
    }
}
