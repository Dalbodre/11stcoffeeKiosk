package sb.yoon.kiosk;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

public class noPaperDlg {
    private Context context;

    public noPaperDlg(Context ctx) {
        this.context = ctx;
    }

    public void callFunction() {

        // 커스텀 다이얼로그를 정의하기위해 Dialog클래스를 생성한다.
        final Dialog dlg = new Dialog(context);

        // 액티비티의 타이틀바를 숨긴다.
        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE);

        // 커스텀 다이얼로그의 레이아웃을 설정한다.
        dlg.setContentView(R.layout.nopaper);

        // 커스텀 다이얼로그를 노출한다.
        dlg.show();
        final Button noPaper = (Button)dlg.findViewById(R.id.noPaperButton);
        noPaper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                KioskListActivity.statusloop = true;
                dlg.dismiss();
            }
        });
    }
}
