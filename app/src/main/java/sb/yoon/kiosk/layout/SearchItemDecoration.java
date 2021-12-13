package sb.yoon.kiosk2.layout;

import android.app.Activity;
import android.graphics.Rect;
import android.util.TypedValue;
import android.view.View;
import androidx.recyclerview.widget.RecyclerView;
import org.jetbrains.annotations.NotNull;

public class SearchItemDecoration extends RecyclerView.ItemDecoration {
    private int spanCount;
    private int spacing;
    private int outerMargin;


    public SearchItemDecoration(Activity mActivity, int count) {
        spanCount = count;
        spacing = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                12, mActivity.getResources().getDisplayMetrics());
        outerMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                50, mActivity.getResources().getDisplayMetrics());
    }


    @Override
    public void getItemOffsets(Rect outRect, @NotNull View view, RecyclerView parent, @NotNull RecyclerView.State state) {
        int maxCount = parent.getAdapter().getItemCount();
        int position = parent.getChildAdapterPosition(view);
        int column = position % spanCount;
        int row = position / spanCount;
        int lastRow = (maxCount - 1) / spanCount;

        //outRect.left = column * spacing / spanCount;
        //outRect.right = spacing - (column + 1) * spacing / spanCount;
        //outRect.top = spacing * 2;

        if (row == lastRow) {
            outRect.bottom = outerMargin;
        }
    }
}
