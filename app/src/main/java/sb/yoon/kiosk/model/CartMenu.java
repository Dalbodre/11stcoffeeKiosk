package sb.yoon.kiosk.model;

import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;

import java.util.List;

public class CartMenu implements Parcelable {
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

    private String temp;

    private Long menuId;
    private Long categoryId;

    private boolean isHot;
    private boolean isCold;

    private boolean isTakeOut;

    public CartMenu(Drawable icon, String name, int price, int totalPrice, List<CartOption> options, Long menuId, Long categoryId, boolean isHot, boolean isCold, String temp) {
        this.icon = icon;
        this.name = name;
        this.price = price;
        this.totalPrice = totalPrice;
        this.options = options;
        this.menuId = menuId;
        this.categoryId = categoryId;
        this.isHot = isHot;
        this.isCold = isCold;
        this.temp = temp;
    }

    protected CartMenu(Parcel in) {
        name = in.readString();
        price = in.readInt();
        totalPrice = in.readInt();
        menuId = in.readLong();
        categoryId = in.readLong();
    }

    public String getTemp() {
        return temp;
    }

    public void setTemp(String temp) {
        this.temp = temp;
    }

    public Drawable getIcon() {
        return icon;
    }

    public boolean isTakeOut() {
        return isTakeOut;
    }

    public void setTakeOut(boolean takeOut) {
        isTakeOut = takeOut;
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

    public Long getMenuId() {
        return menuId;
    }

    public void setMenuId(Long menuId) {
        this.menuId = menuId;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryid) {
        this.categoryId = categoryid;
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

    // Parcelable 객체가 file descriptor를 포함하고 있다면 CONTENTS_FILE_DESCRIPTOR를 리턴하고 그 외는 0을 리턴하라고 함
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeInt(price);
        parcel.writeInt(totalPrice);
        parcel.writeLong(menuId);
        parcel.writeLong(categoryId);
    }

    public static final Creator<CartMenu> CREATOR = new Creator<CartMenu>() {
        @Override
        public CartMenu createFromParcel(Parcel in) {
            return new CartMenu(in);
        }

        @Override
        public CartMenu[] newArray(int size) {
            return new CartMenu[size];
        }
    };
}
