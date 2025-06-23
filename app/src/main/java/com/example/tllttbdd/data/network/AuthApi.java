package com.example.tllttbdd.data.network;

import com.example.tllttbdd.data.model.LoginResponse;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface AuthApi {
    @POST("/auth/register")
    @FormUrlEncoded
    Call<LoginResponse> register(
            @Field("name_login") String username,
            @Field("pass") String password,
            @Field("phone_information") String phone
    );

    @POST("/auth/login")
    @FormUrlEncoded
    Call<LoginResponse> login(
            @Field("name_login") String username,
            @Field("pass") String password
    );
}