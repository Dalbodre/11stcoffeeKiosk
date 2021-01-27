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

public class AdminItemElement extends LinearLayout {
    private Drawable img;
    private String name;
    private View view;

    public Drawable getImg(){return img;}

    public void setImgDrawable(Drawable img){
        replaceImgDrawable(img);
        this.img = img;
    }

    public String getName() {return name;}

    public void setName(String name){
        replaceName(name);
        this.name = name;
    }

    public AdminItemElement(Context ctx, AttributeSet attrs){
        super(ctx, attrs);

        LayoutInflater inflater = (LayoutInflater)ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.admin_item_layout, this, true);
    }

    private void replaceImgDrawable(Drawable img){
        ImageView elementImg = view.findViewById(R.id.adminImg);
        elementImg.setImageDrawable(img);
    }

    private void replaceName(String txt){
        TextView elementName = view.findViewById(R.id.adminText);
        elementName.setText(txt);
    }

    public ImageView getImageView() {return view.findViewById(R.id.adminImg);}
}
