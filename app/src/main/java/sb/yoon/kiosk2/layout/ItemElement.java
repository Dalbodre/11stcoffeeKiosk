package sb.yoon.kiosk2.layout;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import sb.yoon.kiosk2.R;

public class ItemElement extends LinearLayout{
    private Drawable image;
    private String name;
    private int price;
    private View view;

    public Drawable getImage() {
        return image;
    }

    public void setImageDrawable(Drawable image) {
        replaceImageDrawable(image);
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        replaceName(name);
        this.name = name;
    }

    public void setPrice(int price) {
        replacePrice(price);
        this.price = price;
    }

    public ItemElement(Context context, AttributeSet attrs) {
        super(context, attrs);

        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.element_item_layout,this,true);
    }

    private void replaceImageDrawable(Drawable image) {
        ImageView elementImage = view.findViewById(R.id.element_image);
        elementImage.setImageDrawable(image);
    }

    private void replaceName(String text) {
        TextView elementText = view.findViewById(R.id.element_name);
        elementText.setText(text);
    }

    private void replacePrice(int price) {
        TextView elementText = view.findViewById(R.id.element_price);
        elementText.setVisibility(View.VISIBLE);
        elementText.setText(Integer.toString(price));
    }

    public ImageView getImageView() {
        return view.findViewById(R.id.element_image);
    }
}
