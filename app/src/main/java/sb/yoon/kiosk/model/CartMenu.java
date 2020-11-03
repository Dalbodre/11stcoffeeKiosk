package sb.yoon.kiosk.model;

import android.graphics.drawable.Drawable;

public class CartMenu {
    private Drawable icon;
    private String name;
    private int quantity = 1;

    // 따로 연산하거나 DB에 저장할 필요없이 서버에다 보내주면 되는 데이터이므로 통합하여 문자열로 처리
    // 예: 사이즈:라지/샷 추가:1/온도: 아이스/
    private String option;

    public CartMenu(Drawable icon, String name) {
        this.icon = icon;
        this.name = name;
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
