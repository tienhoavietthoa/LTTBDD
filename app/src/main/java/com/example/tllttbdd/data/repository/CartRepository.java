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

    public Call<ApiResponse> addToCart(int productId, int quantity) {
        return cartApi.addToCart(productId, quantity);
    }

    public Call<ApiResponse> updateCart(int productId, int quantity) {
        return cartApi.updateCart(productId, quantity);
    }

    public Call<ApiResponse> removeFromCart(int productId) {
        return cartApi.removeFromCart(productId);
    }

    public Call<ApiResponse> placeOrder(String name, String phone, String address) {
        return cartApi.placeOrder(name, phone, address);
    }
}