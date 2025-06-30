package com.example.tllttbdd.data.model;

import com.google.gson.annotations.SerializedName;

public class Product {
    @SerializedName("id_product")
    public int id_product;
    @SerializedName("name_product")
    public String name_product;
    @SerializedName("price")
    public String price;
    @SerializedName("image_product")
    public String image_product;
    // Thêm các trường khác nếu cần (quantity, author, ...)
}