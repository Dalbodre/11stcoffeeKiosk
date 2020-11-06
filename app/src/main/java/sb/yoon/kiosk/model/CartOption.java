package sb.yoon.kiosk.model;

import com.google.gson.annotations.Expose;

public class CartOption {
    @Expose
    private String name;
    @Expose
    private String quantity;
    @Expose
    private int price;

    public CartOption(String name, String quantity, int price) {
        this.name = name;
        this.quantity = quantity;
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }
}
