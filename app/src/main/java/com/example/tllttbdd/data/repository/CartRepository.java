package com.example.tllttbdd.data.repository;

import com.example.tllttbdd.data.model.CartResponse;
import com.example.tllttbdd.data.model.ApiResponse;
import com.example.tllttbdd.data.network.ApiClient;
import com.example.tllttbdd.data.network.CartApi;

import retrofit2.Call;

public class CartRepository {
    private final CartApi cartApi = ApiClient.getClient().create(CartApi.class);

    public Call<CartResponse> getCart(int idLogin) {
        return cartApi.getCart(idLogin);
    }

    public Call<ApiResponse> addToCart(int productId, int quantity, int idLogin) {
        return cartApi.addToCart(productId, quantity, idLogin);
    }

    public Call<ApiResponse> updateCart(int productId, int quantity, int idLogin) {
        return cartApi.updateCart(productId, quantity, idLogin);
    }

    public Call<ApiResponse> removeFromCart(int productId, int idLogin) {
        return cartApi.removeFromCart(productId, idLogin);
    }
}