package sb.yoon.kiosk2.layout;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import sb.yoon.kiosk2.R;

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
        TextView elementText = view.findViewById(R.id.search_name);
        elementText.setText(text);
    }


    public IngredientItem(Context context, AttributeSet attrs) {
        super(context, attrs);

        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.for_search_item,this,true);
    }

    private void replaceImageDrawable(Drawable image) {
        ImageView elementImage = view.findViewById(R.id.search_image);
        elementImage.setImageDrawable(image);
    }

    public ImageView getImageView() {
        return view.findViewById(R.id.search_image);
    }

}
