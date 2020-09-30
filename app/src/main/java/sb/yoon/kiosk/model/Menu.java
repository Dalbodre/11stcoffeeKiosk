package sb.yoon.kiosk.model;

import android.graphics.drawable.Drawable;

// 데이터 클래스
public class Menu {
    private Drawable icon;
    private String text;
    private Ingredient[] ingredients;
    private int[] prices;

    public Menu(String text, Drawable icon, int[] prices, Ingredient[] ingredients) {
        this.icon = icon;
        this.text = text;
        this.prices = prices;
        this.ingredients = ingredients;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setPrices(int[] prices) {
        this.prices = prices;
    }

    public void setIngredients(Ingredient[] ingredients) {
        this.ingredients = ingredients;
    }

    public Drawable getIcon() {
        return this.icon;
    }

    public String getText() {
        return this.text;
    }

    public Ingredient[] getIngredients() {
        return ingredients;
    }

    public int[] getPrices() {
        return prices;
    }
}
