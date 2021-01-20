package sb.yoon.kiosk.layout;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import sb.yoon.kiosk.R;

public class for_element_item extends LinearLayout {
    private Drawable image;
    private View view;

    public Drawable getImage() {
        return image;
    }

    public void setImageDrawable(Drawable image) {
        replaceImageDrawable(image);
        this.image = image;
    }

    public for_element_item(Context context, AttributeSet attrs) {
        super(context, attrs);

        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.for_elements_items,this,true);
    }

    private void replaceImageDrawable(Drawable image) {
        ImageView elementImage = view.findViewById(R.id.for_element_image);
        elementImage.setImageDrawable(image);
    }

    public ImageView getImageView() {
        return view.findViewById(R.id.for_element_image);
    }

}
