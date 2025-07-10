package com.example.tllttbdd.data.repository;

import com.example.tllttbdd.data.model.LoginResponse;
import com.example.tllttbdd.data.network.ApiClient;
import com.example.tllttbdd.data.network.AuthApi;
import retrofit2.Call;
import retrofit2.Callback;

public class AuthRepository {
    private AuthApi authApi;
    //Lấy instance của Retrofit client,Tạo implementation cho interface AuthApi
    public AuthRepository() {
        authApi = ApiClient.getClient().create(AuthApi.class);
    }

    public void register(String username, String password, String phone, Callback<LoginResponse> callback) {
        Call<LoginResponse> call = authApi.register(username, password, phone);
        call.enqueue(callback);
    }

    public void login(String username, String password, Callback<LoginResponse> callback) {
        Call<LoginResponse> call = authApi.login(username, password);
        call.enqueue(callback);
    }
}