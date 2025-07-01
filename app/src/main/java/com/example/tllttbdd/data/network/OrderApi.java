package com.example.tllttbdd.data.network;

import com.example.tllttbdd.data.model.ApiResponse;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface OrderApi {
    @FormUrlEncoded
    @POST("/order/api/create")
    Call<ApiResponse> createOrder(
            @Field("name_order") String name,
            @Field("phone_order") String phone,
            @Field("address_order") String address,
            @Field("payment") String payment,
            @Field("total") int total,
            @Field("id_login") int idLogin,
            @Field("products") String productsJson // Thêm dòng này
    );
}