package sb.yoon.kiosk.model;

import android.graphics.drawable.Drawable;

public class Ingredient {
    private Drawable icon;
    private String text;

    public Ingredient(String text, Drawable icon) {
        this.icon = icon;
        this.text = text;
    }

    // @todo iconPath 받아서 스토리지에 있는 이미지의 Drawable 필드에 할당하기
    // @todo 혹은 다른 방법으로도 리팩토링 가능
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
