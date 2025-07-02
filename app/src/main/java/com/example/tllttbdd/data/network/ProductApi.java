package com.example.tllttbdd.data.network;

import com.example.tllttbdd.data.model.ProductResponse;
import com.example.tllttbdd.data.model.ProductSearchResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.Path;

public interface ProductApi {
    @GET("/api/search")
    Call<ProductSearchResponse> searchProducts(@Query("q") String query);
    @GET("/api/categories/{id}/products")
    Call<ProductResponse> getProductsByCategory(@Path("id") int categoryId);
    @GET("/api/products/{id}")
    Call<ProductResponse> getProductDetail(@Path("id") int id);
}