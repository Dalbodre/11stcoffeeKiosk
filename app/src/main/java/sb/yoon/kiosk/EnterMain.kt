package sb.yoon.kiosk

import android.Manifest
import android.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Bitmap.CompressFormat
import android.content.Intent
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.net.InetSocketAddress
import java.net.Socket
import java.net.SocketAddress

class EnterMain : AppCompatActivity() {
    val progressBar: ProgressBar by lazy {
        findViewById(R.id.progressBar)
    }
    val t: TextView by lazy {
        findViewById(R.id.splash_title)
    }
    val b: Button by lazy {
        findViewById(R.id.enter_main_button)
    }
    private var easterCount = 0
    private var employeeCount = 0
    private val W_STORAGE_PER_CODE = 333

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_enter_main)

        // 히든 버튼
        val EASTER = findViewById<ImageView>(R.id.enter_easter)
        EASTER.setOnClickListener(EasterClickListener())

        // 권한 체크
        if (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this,
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE),
                    W_STORAGE_PER_CODE)
        }

        val employee_easter: View = findViewById(R.id.employee_easter)
        employee_easter.setOnClickListener(employeeClickListener())
        savebmp("background_kicc.png", R.drawable.background_kicc)
        savebmp("close_kicc.png", R.drawable.close_kicc)
        savebmp("card_kicc.png", R.drawable.card_kicc)
    }

    fun savebmp(filename: String?, drawable_id: Int) {
        val bm = BitmapFactory.decodeResource(applicationContext.resources, drawable_id)
        val dir = File(Environment.getExternalStorageDirectory().toString() + File.separator + "kicc")
        var doSave = true
        if (!dir.exists()) {
            doSave = dir.mkdirs()
        }
        if (doSave) {
            saveBitmapToFile(dir, filename, bm, CompressFormat.PNG, 100)
        } else {
            Log.e("app", "Couldn't create target directory.")
        }
    }

    fun saveBitmapToFile(dir: File?, fileName: String?, bm: Bitmap,
                         format: CompressFormat?, quality: Int): Boolean {
        val imageFile = File(dir, fileName)
        var fos: FileOutputStream? = null
        try {
            fos = FileOutputStream(imageFile)
            bm.compress(format, quality, fos)
            fos.close()
            return true
        } catch (e: IOException) {
            Log.e("app", e.message!!)
            if (fos != null) {
                try {
                    fos.close()
                } catch (e1: IOException) {
                    e1.printStackTrace()
                }
            }
        }
        return false
    }

    override fun onResume() {
        super.onResume()
        progressBar.visibility = ProgressBar.GONE
        Log.d("Status", "Resume")
    }

    fun buttonClicked(view: View?) {
        progressBar.visibility = ProgressBar.VISIBLE
        HttpCheckThread().start()
    }

    internal inner class HttpCheckThread : Thread() {
        override fun run() {
            try {
                val sockaddr: SocketAddress = InetSocketAddress("192.168.0.15", 8080)
                // Create an unbound socket
                val sock = Socket()

                // This method will block no more than timeoutMs.
                // If the timeout occurs, SocketTimeoutException is thrown.
                val timeoutMs = 2000 // 2 seconds
                sock.connect(sockaddr, timeoutMs)
                enterListActivity()
            } catch (e: IOException) {
                connectionFailed()
            }
        }
    }

    fun enterListActivity() {
        val intent = Intent(this, KioskListActivity::class.java)
        //Intent intent = new Intent(this, AdminActivity.class);
        startActivity(intent)
        //finish();
    }

    fun connectionFailed() {
        val mHandler = Handler(Looper.getMainLooper())
        mHandler.postDelayed({ Toast.makeText(this@EnterMain.applicationContext, "서버와 연결되어 있지 않습니다. 직원에게 문의하세요.", Toast.LENGTH_SHORT).show() }, 0)
        progressBar!!.visibility = ProgressBar.INVISIBLE
    }

    private inner class EasterClickListener : Thread(), View.OnClickListener {
        override fun run() {
            easterCount = 0
            try {
                sleep(3000)
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
        }

        override fun onClick(v: View) {
            run()
            if (easterCount == 3) {
                if (b.text.toString() != "작업중") {
                    Toast.makeText(this@EnterMain.applicationContext, "작업모드로 전환합니다.", Toast.LENGTH_SHORT).show()
                    t.text = "키오스크 작업중입니다.\n카운터에서 주문 도와드리겠습니다."
                    b.text = "작업중"
                } else {
                    Toast.makeText(this@EnterMain.applicationContext, "주문모드로 전환합니다.", Toast.LENGTH_SHORT).show()
                    t.text = "카운터에서도\n주문 가능합니다."
                    b.text = "주문하기"
                }
            }
            if (easterCount == 4) {
                val intent = Intent(this@EnterMain, AdminActivity::class.java)
                startActivity(intent)
                // finish();
                easterCount = 0
            }
        }
    }

    private inner class employeeClickListener : View.OnClickListener {
        override fun onClick(v: View) {
            employeeCount++
            println("이스터에그 : $employeeCount")
            if (employeeCount == 3) {
                val dialogView = layoutInflater.inflate(R.layout.employee, null)
                val builder = AlertDialog.Builder(this@EnterMain)
                builder.setView(dialogView)
                val alertDialog = builder.create()
                alertDialog.show()
                val employee_ok = dialogView.findViewById<Button>(R.id.employee_ok)
                employee_ok.setOnClickListener { alertDialog.dismiss() }
                employeeCount = 0
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            W_STORAGE_PER_CODE -> {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty()
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "승인이 허가되어 있습니다.", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(this, "아직 승인받지 않았습니다.", Toast.LENGTH_LONG).show()
                }
                return
            }
        }
    }
}