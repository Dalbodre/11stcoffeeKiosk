package sb.yoon.kiosk.model;

import android.graphics.drawable.Drawable;

import java.util.ArrayList;

// 데이터 클래스
public class Menu {
    private Drawable icon;
    private String text;
    private ArrayList<Ingredient> ingredients;
    private String[] prices;

    public Menu(String text, Drawable icon, String[] prices, ArrayList<Ingredient> ingredients) {
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

    public void setPrices(String[] prices) {
        this.prices = prices;
    }

    public void setIngredients(ArrayList<Ingredient> ingredients) {
        this.ingredients = ingredients;
    }

    public Drawable getIcon() {
        return this.icon;
    }

    public String getText() {
        return this.text;
    }

    public ArrayList<Ingredient> getIngredients() {
        return ingredients;
    }

    public String[] getPrices() {
        return prices;
    }
}
