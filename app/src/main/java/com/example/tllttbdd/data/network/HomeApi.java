package com.example.tllttbdd.data.network;

import com.example.tllttbdd.data.model.CategoryResponse;

import retrofit2.Call;
import retrofit2.http.GET;

public interface HomeApi {
    @GET("/")
    Call<CategoryResponse> getAllCategories();
}