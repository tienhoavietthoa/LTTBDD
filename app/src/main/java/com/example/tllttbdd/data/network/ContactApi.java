package com.example.tllttbdd.data.network;

import com.example.tllttbdd.data.model.ApiResponse;
import com.example.tllttbdd.data.model.ContactMessage;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.*;

public interface ContactApi {
    @GET("/contact/api")
    Call<Map<String, Object>> getContactInfo(@Query("id_login") int idLogin);

    @GET("/contact/api/list")
    Call<Map<String, List<ContactMessage>>> getMessages(@Query("id_login") int idLogin);

    @FormUrlEncoded
    @POST("/contact/api/create")
    Call<ApiResponse> sendMessage(
            @Field("id_login") int idLogin,
            @Field("phone_contact") String phoneContact,
            @Field("text_contact") String textContact
    );
}