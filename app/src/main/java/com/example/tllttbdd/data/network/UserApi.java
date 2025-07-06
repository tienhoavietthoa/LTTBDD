package com.example.tllttbdd.data.network;

import com.example.tllttbdd.data.model.ApiResponse;
import com.example.tllttbdd.data.model.UserResponse;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Interface định nghĩa các lời gọi API liên quan đến User.
 */
public interface UserApi {

    // 1. Lời gọi để lấy thông tin chi tiết của người dùng
    // Backend: router.get('/api/profile', homeController.profile);
    // Backend mong đợi: req.query.id_login
    @GET("/api/profile")
    Call<UserResponse> getUserProfile(@Query("id_login") int idLogin); // <-- ĐÃ SỬA: Tên tham số là "id_login"

    // 2. Lời gọi để cập nhật thông tin người dùng
    // Backend: router.post('/api/profile/edit', homeController.updateProfile);
    // Backend mong đợi: req.body.id_login
    @POST("/api/profile/edit")
    @FormUrlEncoded
    Call<ApiResponse> updateProfile(
            @Field("id_login") int idLogin, // <-- ĐÃ SỬA: Tên tham số là "id_login"
            @Field("name_information") String name, // <-- ĐÃ SỬA: Tên tham số là "name_information"
            @Field("phone_information") String phone, // <-- ĐÃ SỬA: Tên tham số là "phone_information"
            @Field("email") String email,
            @Field("date_of_birth") String dob // <-- ĐÃ SỬA: Tên tham số là "date_of_birth"
    );

    // 3. Lời gọi để đổi mật khẩu
    // Backend: router.post('/api/profile/change-password', homeController.changePassword);
    // Backend mong đợi: req.body.id_login, req.body.oldPassword, req.body.newPassword
    @POST("/api/profile/change-password")
    @FormUrlEncoded // <-- THÊM CÁI NÀY VÌ BACKEND DÙNG req.body
    Call<ApiResponse> changePassword(
            @Field("id_login") int idLogin, // <-- ĐÃ SỬA: Tên tham số là "id_login"
            @Field("oldPassword") String oldPassword, // <-- ĐÃ SỬA: Dùng @Field vì backend dùng req.body
            @Field("newPassword") String newPassword // <-- ĐÃ SỬA: Dùng @Field vì backend dùng req.body
    );

    // 4. Lời gọi để xóa tài khoản
    // Backend: router.post('/api/profile/delete', homeController.deleteAccount);
    // Backend mong đợi: req.body.id_login
    @POST("/api/profile/delete")
    @FormUrlEncoded // <-- THÊM CÁI NÀY VÌ BACKEND DÙNG req.body
    Call<ApiResponse> deleteAccount(@Field("id_login") int idLogin); // <-- ĐÃ SỬA: Tên tham số là "id_login" và dùng @Field
}