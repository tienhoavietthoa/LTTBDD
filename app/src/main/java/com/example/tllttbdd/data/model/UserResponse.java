package com.example.tllttbdd.data.model;

import com.google.gson.annotations.SerializedName;

/**
 * Lớp này dùng để bọc đối tượng User khi nhận dữ liệu từ API.
 * Cấu trúc này rất phổ biến khi API trả về một đối tượng JSON có chứa đối tượng con.
 * Ví dụ: { "success": true, "user": { ... } }
 */
public class UserResponse {

    @SerializedName("success")
    private boolean success;

    @SerializedName("user")
    private User user;

    // Getter cho trạng thái success
    public boolean isSuccess() {
        return success;
    }

    // Getter cho đối tượng User
    public User getUser() {
        return user;
    }
}
