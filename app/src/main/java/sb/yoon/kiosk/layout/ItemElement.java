package sb.yoon.kiosk.layout;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import sb.yoon.kiosk.R;

public class ItemElement extends LinearLayout {
    private Drawable image;
    private String text;
    private View view;

    public Drawable getImage() {
        return image;
    }

    public void setImageDrawable(Drawable image) {
        replaceImageDrawable(image);
        this.image = image;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        replaceText(text);
        this.text = text;
    }

    public ItemElement(Context context, AttributeSet attrs) {
        super(context, attrs);

        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.element_item_layout,this,true);
    }

    public ItemElement(Context context, Drawable image, String text) {
        super(context);
        this.image = image;
        this.text = text;

        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.element_item_layout,this,true);
        replaceImageDrawable(this.image);
        replaceText(this.text);
    }

    private void replaceImageDrawable(Drawable image) {
        ImageView elementImage = view.findViewById(R.id.element_image);
        elementImage.setImageDrawable(image);
    }

    private void replaceText(String text) {
        TextView elementText = view.findViewById(R.id.element_text);
        elementText.setText(text);
    }
}
