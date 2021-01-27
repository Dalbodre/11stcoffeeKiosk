package sb.yoon.kiosk.layout;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import java.util.List;

import sb.yoon.kiosk.R;

public class CategoryButton extends androidx.appcompat.widget.AppCompatToggleButton {
    private String categoryName;
    private int tagNum;

    public CategoryButton(Context context, String categoryName, int tagNum) {
        super(context);
        this.tagNum = tagNum;
        this.categoryName = categoryName;
        styling();
    }

    public CategoryButton(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        styling();
    }

    private void styling() {
        this.setTextColor(Color.parseColor("#081832"));
        this.setPadding(20, 0, 20, 0);
        this.setBackgroundResource(R.drawable.togglebutton_off);

        this.getMaxHeight();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            this.setStateListAnimator(null); //버전 문제때문에 그러는데, Lollipop 이후 버전으로 해야한다고 합니다.
        }
        //어차피 우린 롤리팝 이후 버전 아닌가?
        //사용목적: 버튼 음영(그림자) 제거 위함 (기존 설계 따라가고 있음)
    }

    public int getTagNum() {
        return this.tagNum;
    }

    public String getCategoryName() {
        return this.categoryName;
    }
}
