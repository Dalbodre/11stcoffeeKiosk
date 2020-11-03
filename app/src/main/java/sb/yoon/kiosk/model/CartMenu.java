package sb.yoon.kiosk.model;

import android.graphics.drawable.Drawable;

import com.google.gson.annotations.Expose;

public class CartMenu {
    private Drawable icon;

    // Gson에서 긁을 멤버변수들 지정
    @Expose
    private String name;
    @Expose
    private int quantity = 1;
    @Expose
    private int price;

    // 옵션등에 따른 추가요금
    @Expose
    private int extraPrice;

    // 따로 연산하거나 DB에 저장할 필요없이 서버에다 보내주면 되는 데이터이므로 통합하여 문자열로 처리
    // 예: 사이즈:라지/샷 추가:1/온도: 아이스/
    @Expose
    private String option;

    public CartMenu(Drawable icon, String name, int price, int extraPrice, String option) {
        this.icon = icon;
        this.name = name;
        this.price = price;
        this.extraPrice = extraPrice;
        this.option = option;
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

    public int plusQuantity() {
        this.quantity += 1;
        return this.quantity;
    }

    public int minusQuantity() {
        this.quantity -= 1;
        return this.quantity;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getOption() {
        return option;
    }

    public void setOption(String option) {
        this.option = option;
    }
}
