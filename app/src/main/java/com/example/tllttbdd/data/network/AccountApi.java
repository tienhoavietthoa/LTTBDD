package com.example.tllttbdd.data.network;

import com.example.tllttbdd.data.model.User;
import com.example.tllttbdd.data.model.LoginResponse;
import com.example.tllttbdd.data.model.Order; // Import Order model
import java.util.List; // Import List
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

    // --- THÊM MỚI: API để lấy danh sách đơn hàng ---
    // Giả sử API này sẽ trả về một List<Order>
    // Thay đổi endpoint này cho phù hợp với API của bạn (ví dụ: /api/orders)
    @Headers("Accept: application/json")
    @GET("/api/orders") // Đã điều chỉnh endpoint theo cấu trúc /api/ của bạn
    Call<List<Order>> getOrders(@Query("user_id") int userId); // Đã điều chỉnh tên tham số query theo chuẩn API (thường là snake_case)
}
