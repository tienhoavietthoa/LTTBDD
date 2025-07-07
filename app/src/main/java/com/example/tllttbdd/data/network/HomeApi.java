package com.example.tllttbdd.data.network;

import com.example.tllttbdd.data.model.Category;
import com.example.tllttbdd.data.model.CategoryResponse;
import com.example.tllttbdd.data.model.HomeResponse;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.GET;

public interface HomeApi {

    @GET("/api/home")
    Call<HomeResponse> getHomeData();
    // Phương thức lấy danh sách thể loại (nếu trả về kiểu mảng Category
    @GET("/api/categories")
    Call<CategoryResponse> getAllCategories();

}