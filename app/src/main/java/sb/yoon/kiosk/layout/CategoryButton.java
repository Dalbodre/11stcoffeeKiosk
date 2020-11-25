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
        this.setTextColor(ContextCompat.getColor(getContext(), android.R.color.white));
        this.setBackgroundColor(Color.parseColor("#544842"));
        this.setPadding(20,0,20,0);
        this.getMaxHeight();
    }
}
