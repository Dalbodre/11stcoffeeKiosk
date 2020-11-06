package sb.yoon.kiosk.model;

import android.graphics.drawable.Drawable;

import com.google.gson.annotations.Expose;

import java.util.List;

public class CartMenu {
    private Drawable icon;

    // Gson에서 긁을 멤버변수들 지정
    @Expose
    private String name;
    @Expose
    private int price;

    // 옵션들 + 메뉴 가격
    @Expose
    private int totalPrice;
    @Expose
    private List<CartOption> options;

    public CartMenu(Drawable icon, String name, int price, int totalPrice, List<CartOption> options) {
        this.icon = icon;
        this.name = name;
        this.price = price;
        this.totalPrice = totalPrice;
        this.options = options;
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(int totalPrice) {
        this.totalPrice = totalPrice;
    }

    public List<CartOption> getOptions() {
        return options;
    }

    public void setOptions(List<CartOption> options) {
        this.options = options;
    }
}
