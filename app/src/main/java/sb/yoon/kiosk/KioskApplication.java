package sb.yoon.kiosk;

import android.app.Application;

import org.greenrobot.greendao.database.Database;

import sb.yoon.kiosk.controller.DbQueryController;
import sb.yoon.kiosk.model.DaoMaster;
import sb.yoon.kiosk.model.DaoSession;

public class KioskApplication extends Application {
    private DaoSession daoSession;
    private DbQueryController dbQueryController;
    private Database db;
    private KioskMain kioskMain;

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

    public KioskMain getKioskMain() {
        return kioskMain;
    }

    public void setKioskMain(KioskMain kioskMain) {
        this.kioskMain = kioskMain;
    }
}
