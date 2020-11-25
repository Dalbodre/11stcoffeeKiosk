package sb.yoon.kiosk.layout;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.TypedValue;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

public class CategoryButton extends androidx.appcompat.widget.AppCompatButton {
    public CategoryButton(Context context) {
        super(context);
        styling();
    }

    public CategoryButton(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        styling();
    }

    private void styling() {
        this.setTextSize(TypedValue.COMPLEX_UNIT_SP, 33);
        this.setTextColor(Color.parseColor("#4b3621"));
        this.setBackgroundColor(Color.parseColor("#fbe49e"));
        this.setPadding(20,0,20,0);
        this.getMaxHeight();
        this.setStateListAnimator(null); //버전 문제때문에 그러는데, Lollipop 이후 버전으로 해야한다고 합니다.
                                        //어차피 우린 롤리팝 이후 버전 아닌가?
                                        //사용목적: 버튼 음영(그림자) 제거 위함 (기존 설계 따라가고 있음)
    }
}
