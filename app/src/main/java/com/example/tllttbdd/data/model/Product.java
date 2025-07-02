package com.example.tllttbdd.data.model;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class Product implements Serializable {

    @SerializedName("id_product")
    public int id_product;

    @SerializedName("name_product")
    public String name_product;

    @SerializedName("price")
    public String price;

    // <-- THÊM 3: Bổ sung trường giá gốc còn thiếu
    @SerializedName("original_price")
    public String original_price;

    @SerializedName("image_product")
    public String image_product;

    @SerializedName("quantity")
    public int quantity;

    @SerializedName("dimension")
    public String dimension;

    @SerializedName("manufacturer")
    public String manufacturer;

    @SerializedName("page")
    public int page;

    @SerializedName("author")
    public String author;

    @SerializedName("publisher")
    public String publisher;

    @SerializedName("publisher_year")
    public int publisher_year;

    @SerializedName("text_product")
    public String text_product;

    @SerializedName("id_category")
    public int id_category;
}