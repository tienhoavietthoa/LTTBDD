package com.example.tllttbdd.data.model;

import com.google.gson.annotations.SerializedName; // Nếu bạn đang dùng Gson

public class ApiResponse {

    @SerializedName("success") // Tùy thuộc vào tên trường JSON trả về từ API của bạn
    private boolean success;

    @SerializedName("message") // Tùy thuộc vào tên trường JSON trả về từ API của bạn
    private String message;

    // Constructor mặc định (nếu cần)
    public ApiResponse() {
        // Khởi tạo giá trị mặc định nếu cần
    }

    // Getter cho 'success'
    public boolean isSuccess() {
        return success;
    }

    // Setter cho 'success' - ĐÂY LÀ PHẦN BẠN CẦN THÊM VÀO!
    public void setSuccess(boolean success) {
        this.success = success;
    }

    // Getter cho 'message'
    public String getMessage() {
        return message;
    }

    // Setter cho 'message' (nếu cần thiết để gán lỗi thủ công)
    public void setMessage(String message) {
        this.message = message;
    }

    // Bạn có thể có các thuộc tính và getter/setter khác tùy vào API của bạn
}