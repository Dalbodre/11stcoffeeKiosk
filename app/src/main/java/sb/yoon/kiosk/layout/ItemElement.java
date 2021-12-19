package sb.yoon.kiosk.layout;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import sb.yoon.kiosk.R;

public class ItemElement extends LinearLayout{
    private Drawable image;
//    private String name;
    private int price;
    private View view;
    private int bgColor;

    public Drawable getImage() {
        return image;
    }

    public void setImageDrawable(Drawable image) {
        replaceImageDrawable(image);
        this.image = image;
    }

    public void setBgColor(int bgColor){
        replaceBgColor(bgColor);
        this.bgColor = bgColor;
    }

//    public String getName() {
//        return name;
//    }
//
//    public void setName(String name) {
//        replaceName(name);
//        this.name = name;
//    }

//    public void setPrice(int price) {
//        replacePrice(price);
//        this.price = price;
//    }

    public ItemElement(Context context, AttributeSet attrs) {
        super(context, attrs);

        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.menu_image_layout,this,true);
    }

    private void replaceImageDrawable(Drawable image) {
        ImageView elementImage = view.findViewById(R.id.menu_item_image);
        elementImage.setImageDrawable(image);
    }

//    private void replaceName(String text) {
//        TextView elementText = view.findViewById(R.id.element_name);
//        elementText.setText(text);
//    }


    private void replaceBgColor(int bgColor){
        Drawable drawable = ContextCompat.getDrawable(getContext(), R.drawable.list_fragment_corner);
        LayerDrawable bubble = (LayerDrawable) drawable;
        GradientDrawable bg = (GradientDrawable) bubble.findDrawableByLayerId(R.id.menu_bg);
        bg.setColor(bgColor);
    }

    public ImageView getImageView() {
        return view.findViewById(R.id.element_image);
    }
}
