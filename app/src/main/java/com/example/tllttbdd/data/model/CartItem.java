package com.example.tllttbdd.data.model;

import android.os.Parcel;
import android.os.Parcelable;

public class CartItem implements Parcelable {
    public int productId;
    public String name;
    public int price;
    public String image;
    public int quantity;
    public boolean selected = false;

    public CartItem() {}

    protected CartItem(Parcel in) {
        productId = in.readInt();
        name = in.readString();
        price = in.readInt();
        image = in.readString();
        quantity = in.readInt();
        selected = in.readByte() != 0;
    }

    public static final Creator<CartItem> CREATOR = new Creator<CartItem>() {
        @Override
        public CartItem createFromParcel(Parcel in) {
            return new CartItem(in);
        }

        @Override
        public CartItem[] newArray(int size) {
            return new CartItem[size];
        }
    };

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(productId);
        dest.writeString(name);
        dest.writeInt(price);
        dest.writeString(image);
        dest.writeInt(quantity);
        dest.writeByte((byte) (selected ? 1 : 0));
    }
}