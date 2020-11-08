package sb.yoon.kiosk.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;

public class CartOption implements Parcelable {
    @Expose
    private String name;
    @Expose
    private int quantity;
    @Expose
    private int price;

    private boolean isInteger;

    public CartOption(String name, int quantity, int price, boolean isInteger) {
        this.name = name;
        this.quantity = quantity;
        this.price = price;
        this.isInteger = isInteger;
    }

    protected CartOption(Parcel in) {
        name = in.readString();
        quantity = in.readInt();
        price = in.readInt();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public boolean isInteger() {
        return isInteger;
    }

    public void setInteger(boolean integer) {
        isInteger = integer;
    }

    // Parcelable 객체가 file descriptor를 포함하고 있다면 CONTENTS_FILE_DESCRIPTOR를 리턴하고 그 외는 0을 리턴하라고 함
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeInt(quantity);
        parcel.writeInt(price);
    }

    public static final Creator<CartOption> CREATOR = new Creator<CartOption>() {
        @Override
        public CartOption createFromParcel(Parcel in) {
            return new CartOption(in);
        }

        @Override
        public CartOption[] newArray(int size) {
            return new CartOption[size];
        }
    };
}
