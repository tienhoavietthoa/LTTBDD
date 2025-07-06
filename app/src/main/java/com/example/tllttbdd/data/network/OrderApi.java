package com.example.tllttbdd.data.network;

import com.example.tllttbdd.data.model.ApiResponse;
import com.example.tllttbdd.data.model.OrderDetailResponse;
import com.example.tllttbdd.data.model.OrderHistoryResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.Path;
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
            @Field("id_login") int idLogin, // <-- ĐÃ SỬA: Tên tham số là "id_login"
            @Field("products") String productsJson
    );
    // Thêm API lấy lịch sử đơn hàng
    @GET("/order/api/history")
    Call<OrderHistoryResponse> getOrderHistory(@Query("id_login") int idLogin); // <-- ĐÃ SỬA: Tên tham số là "id_login"

    // Thêm API lấy chi tiết đơn hàng
    @GET("/order/api/detail/{id}")
    Call<OrderDetailResponse> getOrderDetail(@Path("id") int orderId);
}