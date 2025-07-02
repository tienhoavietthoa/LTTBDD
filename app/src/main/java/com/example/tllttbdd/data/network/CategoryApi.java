package com.example.tllttbdd.data.network;

import com.example.tllttbdd.data.model.CategoryResponse;
import com.example.tllttbdd.data.model.ProductResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
public interface CategoryApi {

    @GET("api/products")
    Call<CategoryResponse> getCategories();
    // Lấy sản phẩm theo category id
    @GET("/api/categories/{id}/products")
    Call<ProductResponse> getProductsByCategory(@Path("id") int categoryId);
}
