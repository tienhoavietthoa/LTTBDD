package com.example.tllttbdd.data.network;

import com.example.tllttbdd.data.model.CartResponse;
import com.example.tllttbdd.data.model.ApiResponse;

import retrofit2.Call;
import retrofit2.http.*;

public interface CartApi {
    @GET("/api/cart")
    Call<CartResponse> getCart(@Query("id_login") int idLogin);

    @FormUrlEncoded
    @POST("/api/cart/add")
    Call<ApiResponse> addToCart(@Field("productId") int productId, @Field("quantity") int quantity, @Field("id_login") int idLogin);

    @FormUrlEncoded
    @POST("/api/cart/update")
    Call<ApiResponse> updateCart(@Field("productId") int productId, @Field("quantity") int quantity, @Field("id_login") int idLogin);

    @FormUrlEncoded
    @POST("/api/cart/remove")
    Call<ApiResponse> removeFromCart(@Field("productId") int productId, @Field("id_login") int idLogin);
}