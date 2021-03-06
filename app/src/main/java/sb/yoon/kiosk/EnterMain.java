package sb.yoon.kiosk;

import android.Manifest;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.usb.UsbConstants;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.regex.Pattern;

import sb.yoon.kiosk.controller.HttpNetworkController;


public class EnterMain extends AppCompatActivity {
    ProgressBar progressBar;
    private View employee_easter;
    ImageView SettingButton;
    TextView t;
    Button b;

    private int easterCount = 0;
    private int employeeCount = 0;

    private final int W_STORAGE_PER_CODE = 333;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_main);

        t = findViewById(R.id.splash_title);
        b = findViewById(R.id.enter_main_button);
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
            // Toast.makeText(this.getApplicationContext(), "?????? ?????????????????????", Toast.LENGTH_LONG).show();
        }
        employee_easter = findViewById(R.id.employee_easter);
        employee_easter.setOnClickListener(new employeeClickListener());

        savebmp("background_kicc.png",R.drawable.background_kicc);
        savebmp("close_kicc.png",R.drawable.close_kicc);
        savebmp("card_kicc.png",R.drawable.card_kicc);


    }


    public void savebmp(String filename, int drawable_id)
    {
        Bitmap bm = BitmapFactory.decodeResource(getApplicationContext().getResources(), drawable_id);
        File dir = new File(Environment.getExternalStorageDirectory() + File.separator + "kicc");

        boolean doSave = true;
        if (!dir.exists()) {
            doSave = dir.mkdirs();
        }

        if (doSave) {
            saveBitmapToFile(dir,filename,bm,Bitmap.CompressFormat.PNG,100);
        }
        else {
            Log.e("app","Couldn't create target directory.");
        }
    }

    public boolean saveBitmapToFile(File dir, String fileName, Bitmap bm,
                                    Bitmap.CompressFormat format, int quality) {

        File imageFile = new File(dir,fileName);

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(imageFile);

            bm.compress(format,quality,fos);

            fos.close();

            return true;
        }
        catch (IOException e) {
            Log.e("app",e.getMessage());
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
        return false;
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

        (new HttpCheckThread()).start();
    }

    class HttpCheckThread extends Thread {
        @Override
        public void run() {
            try {
                SocketAddress sockaddr = new InetSocketAddress("192.168.0.15", 8080);
                // Create an unbound socket
                Socket sock = new Socket();

                // This method will block no more than timeoutMs.
                // If the timeout occurs, SocketTimeoutException is thrown.
                int timeoutMs = 2000;   // 2 seconds
                sock.connect(sockaddr, timeoutMs);
                enterListActivity();
            } catch(IOException e) {
                connectionFailed();
            }
        }
    }

    public void enterListActivity() {
        Intent intent = new Intent(this, KioskListActivity.class);
        //Intent intent = new Intent(this, AdminActivity.class);
        startActivity(intent);
        //finish();
    }

    public void connectionFailed() {
        Handler mHandler = new Handler(Looper.getMainLooper());
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(EnterMain.this.getApplicationContext(), "????????? ???????????? ?????? ????????????. ???????????? ???????????????.",Toast.LENGTH_SHORT).show();
            }
        }, 0);
        progressBar.setVisibility(ProgressBar.INVISIBLE);
    }

    private class easterClickListener extends Thread implements View.OnClickListener {
        @Override
        public void run() {
            easterCount = 0;
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onClick(View v) {
            run();

            if (easterCount == 3) {
                if(!b.getText().toString().equals( "?????????")) {
                    Toast.makeText(EnterMain.this.getApplicationContext(), "??????????????? ???????????????.", Toast.LENGTH_SHORT).show();
                    t.setText("???????????? ??????????????????.\n??????????????? ?????? ????????????????????????.");
                    b.setText("?????????");
                }
                else{
                    Toast.makeText(EnterMain.this.getApplicationContext(), "??????????????? ???????????????.", Toast.LENGTH_SHORT).show();
                    t.setText("??????????????????\n?????? ???????????????.");
                    b.setText("????????????");
                }
            }
            if (easterCount == 4) {
                Intent intent = new Intent(EnterMain.this, AdminActivity.class);
                startActivity(intent);
                // finish();
                easterCount = 0;
            }
        }
    }

    private class employeeClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            employeeCount++;
            System.out.println("??????????????? : " + employeeCount);
            if (employeeCount == 3) {
                View dialogView = getLayoutInflater().inflate(R.layout.employee, null);

                AlertDialog.Builder builder = new AlertDialog.Builder(EnterMain.this);
                builder.setView(dialogView);

                final AlertDialog alertDialog = builder.create();
                alertDialog.show();

                Button employee_ok = dialogView.findViewById(R.id.employee_ok);
                employee_ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                });
                employeeCount = 0;
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

                    Toast.makeText(this, "????????? ???????????? ????????????.", Toast.LENGTH_LONG).show();

                } else {
                    Toast.makeText(this, "?????? ???????????? ???????????????.", Toast.LENGTH_LONG).show();
                }
                return;
            }

        }
    }

}
