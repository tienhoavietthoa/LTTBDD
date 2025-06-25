package com.example.tllttbdd.data.network;

import com.example.tllttbdd.data.model.User;
import com.example.tllttbdd.data.model.LoginResponse;
import java.util.Map;
import retrofit2.Call;
import retrofit2.http.*;

public interface AccountApi {
    @Headers("Accept: application/json")
    @GET("/api/profile")
    Call<LoginResponse> getProfile(@Query("id_login") int idLogin);

    @FormUrlEncoded
    @POST("/api/profile/edit")
    Call<Map<String, Object>> updateProfile(
            @Field("id_login") int idLogin,
            @Field("name_information") String name,
            @Field("phone_information") String phone,
            @Field("email") String email,
            @Field("date_of_birth") String dob
    );

    @FormUrlEncoded
    @POST("/api/profile/change-password")
    Call<Map<String, Object>> changePassword(
            @Field("id_login") int idLogin,
            @Field("oldPassword") String oldPassword,
            @Field("newPassword") String newPassword
    );

    @FormUrlEncoded
    @POST("/api/profile/delete")
    Call<Map<String, Object>> deleteAccount(@Field("id_login") int idLogin);
}