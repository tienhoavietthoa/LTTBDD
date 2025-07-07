package com.example.tllttbdd.data.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Category {
    @SerializedName("id_category")
    public int id_category;

    @SerializedName("name_category")
    public String name_category;

    @SerializedName("image")
    public String image;

    @SerializedName("products")
    public List<Product> products;
}