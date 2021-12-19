package sb.yoon.kiosk.libs;

import sb.yoon.kiosk.EnterMain;
import android.app.Activity;
import android.os.CountDownTimer;

import sb.yoon.kiosk.EnterMain;
import sb.yoon.kiosk.KioskListActivity;

public class IdleTimer extends CountDownTimer
{
    private final Activity activity;

    public IdleTimer(Activity activity, long millisInFuture, long countDownInterval)
    {
        super(millisInFuture, countDownInterval);
        this.activity = activity;
    }

    public IdleTimer(Activity activity) {
        super(120000, 1000);
        this.activity = activity;
    }

    @Override
    public void onTick(long millisUntilFinished) {

    }
    @Override
    public void onFinish() {
        activity.finish();
        EnterMain.takeoutVal = 0;
    }
}
