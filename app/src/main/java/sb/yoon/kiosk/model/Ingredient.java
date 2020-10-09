package sb.yoon.kiosk.model;

import android.graphics.drawable.Drawable;

public class Ingredient {
    private Drawable icon;
    private String text;

    public Ingredient(String text, Drawable icon) {
        this.icon = icon;
        this.text = text;
    }

    public Ingredient(String text, String iconPath) {
        this.text = text;


    }

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
