package sb.yoon.kiosk.model;

import android.graphics.drawable.Drawable;

public class AdminMenu {
    private String menuName;
    private Drawable drawable;
    private int price;
    private Long menuId;
    private Long optionId;
    private Long categoryId;
    private boolean isHot;
    private boolean isCold;

    private String categoryName;

    public AdminMenu(Drawable img, String menuName, int price, boolean isHot, boolean isCold, Long categoryId, Long menuId) {
        this.drawable = img;
        this.menuName = menuName;
        this.price = price;
        this.isHot = isHot;
        this.isCold = isCold;
        this.menuId = menuId;
        this.categoryId = categoryId;
    }

    public String getMenuName() {
        return menuName;
    }

    public void setMenuName(String menuName) {
        this.menuName = menuName;
    }

    public Drawable getDrawable() {
        return drawable;
    }

    public void setDrawable(Drawable drawable) {
        this.drawable = drawable;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public Long getMenuId() {
        return menuId;
    }

    public void setMenuId(Long menuId) {
        this.menuId = menuId;
    }

    public Long getOptionId() {
        return optionId;
    }

    public void setOptionId(Long optionId) {
        this.optionId = optionId;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public boolean isHot() {
        return isHot;
    }

    public void setHot(boolean hot) {
        isHot = hot;
    }

    public boolean isCold() {
        return isCold;
    }

    public void setCold(boolean cold) {
        isCold = cold;
    }
}