package sb.yoon.kiosk;

import android.app.Application;
import android.content.SharedPreferences;
import android.widget.Toast;

import org.greenrobot.greendao.database.Database;

import sb.yoon.kiosk.controller.DbQueryController;
import sb.yoon.kiosk.model.DaoMaster;
import sb.yoon.kiosk.model.DaoSession;

public class KioskApplication extends Application {
    private DaoSession daoSession;
    private DbQueryController dbQueryController;
    private Database db;
    private KioskListActivity kioskListActivity;

    @Override
    public void onCreate() {
        super.onCreate();

        // regular SQLite database
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "kiosk-db");
        db = helper.getWritableDb();

        // encrypted SQLCipher database
        // note: you need to add SQLCipher to your dependencies, check the build.gradle file
        // DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "notes-db-encrypted");
        // Database db = helper.getEncryptedWritableDb("encryption-key");

        daoSession = new DaoMaster(db).newSession();
        dbQueryController = new DbQueryController(daoSession);

        //Toast.makeText(this.getApplicationContext(), "앱이 실행되었습니다", Toast.LENGTH_SHORT).show();

        // DB 초기화
        SharedPreferences prefs = getSharedPreferences("sb.yoon.kiosk", MODE_PRIVATE);
        if (prefs.getBoolean("firstrun", true)) {
            dbQueryController.initDB();
            Toast.makeText(this.getApplicationContext(), "DB 초기화가 실행되었습니다", Toast.LENGTH_SHORT).show();
            prefs.edit().putBoolean("firstrun", false).apply();
        }
    }

    public DaoSession getDaoSession() {
        return daoSession;
    }

    public void resetDaoSession() {
        daoSession.clear();
        daoSession = new DaoMaster(db).newSession();
    }

    public DbQueryController getDbQueryController() {
        return dbQueryController;
    }

    public sb.yoon.kiosk.KioskListActivity getKioskListActivity() {
        return kioskListActivity;
    }

    public void setKioskListActivity(KioskListActivity kioskListActivity) {
        this.kioskListActivity = kioskListActivity;
    }
}
