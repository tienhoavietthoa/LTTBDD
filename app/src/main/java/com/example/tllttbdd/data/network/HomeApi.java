package com.example.tllttbdd.data.network;

import com.example.tllttbdd.data.model.Category;
import com.example.tllttbdd.data.model.CategoryResponse;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.GET;

public interface HomeApi {

    // Phương thức lấy danh sách thể loại (nếu trả về kiểu mảng Category
    @GET("/")
    Call<CategoryResponse> getAllCategories();
}