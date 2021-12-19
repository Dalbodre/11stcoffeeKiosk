package sb.yoon.kiosk.layout;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.widget.TextViewCompat;

import sb.yoon.kiosk.R;

public class IngredientItem extends RelativeLayout {
    private Drawable image;
    private String name;
    private View view;

    public Drawable getImage() {
        return image;
    }

    public void setImageDrawable(Drawable image) {
        replaceImageDrawable(image);
        this.image = image;
    }

    public void setName(String name) {
        replaceName(name);
        this.name = name;
    }

    private void replaceName(String text) {
        TextView elementText = view.findViewById(R.id.s_name);
        elementText.setText(text);
    }


    public IngredientItem(Context context, AttributeSet attrs) {
        super(context, attrs);

        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.for_ingredient_item,this,true);
    }

    private void replaceImageDrawable(Drawable image) {
        ImageView elementImage = view.findViewById(R.id.s_image);
        elementImage.setImageDrawable(image);
    }

    public ImageView getImageView() {
        return view.findViewById(R.id.s_image);
    }

}
