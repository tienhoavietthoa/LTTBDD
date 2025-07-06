package com.example.tllttbdd.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.annotation.NonNull;

import com.example.tllttbdd.data.model.ApiResponse;
import com.example.tllttbdd.data.model.Order;
import com.example.tllttbdd.data.model.OrderHistoryResponse;
import com.example.tllttbdd.data.model.User;
import com.example.tllttbdd.data.model.UserResponse;
import com.example.tllttbdd.data.network.ApiClient;
import com.example.tllttbdd.data.network.OrderApi;
import com.example.tllttbdd.data.network.UserApi;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AccountRepository {

    // --- LiveData cho User và các hành động liên quan ---
    private final MutableLiveData<User> user = new MutableLiveData<>();
    private final MutableLiveData<ApiResponse> profileUpdateResult = new MutableLiveData<>();
    private final MutableLiveData<ApiResponse> passwordResult = new MutableLiveData<>();
    private final MutableLiveData<ApiResponse> deleteResult = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>(); // LiveData chung cho lỗi

    // --- LiveData cho Lịch sử đơn hàng ---
    private final MutableLiveData<List<Order>> orders = new MutableLiveData<>();


    // --- Getters cho LiveData ---
    public LiveData<User> getUser() { return user; }
    public LiveData<ApiResponse> getProfileUpdateResult() { return profileUpdateResult; }
    public LiveData<ApiResponse> getPasswordResult() { return passwordResult; }
    public LiveData<ApiResponse> getDeleteResult() { return deleteResult; }
    public LiveData<String> getErrorMessage() { return errorMessage; }
    public LiveData<List<Order>> getOrders() { return orders; }

    // Phương thức để xóa thông báo lỗi
    public void clearErrorMessage() {
        errorMessage.setValue(null);
    }

    // --- CÁC PHƯƠNG THỨC XỬ LÝ API VÀ DỮ LIỆU ---

    public void fetchProfile(int userId) {
        UserApi api = ApiClient.getClient().create(UserApi.class);
        // Đã sửa: getUserProfile giờ nhận @Query("id")
        api.getUserProfile(userId).enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(@NonNull Call<UserResponse> call, @NonNull Response<UserResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    User fetchedUser = response.body().getUser();
                    if (fetchedUser != null) {
                        user.setValue(fetchedUser);
                    } else {
                        errorMessage.setValue("Không tìm thấy thông tin người dùng trong phản hồi.");
                    }
                } else {
                    try {
                        String errorBody = response.errorBody() != null ? response.errorBody().string() : "No error body";
                        errorMessage.setValue("Lỗi tải thông tin người dùng: " + response.code() + " - " + errorBody);
                    } catch (IOException e) {
                        errorMessage.setValue("Lỗi tải thông tin người dùng: " + response.code() + " - Lỗi đọc phản hồi.");
                    }
                }
            }
            @Override
            public void onFailure(@NonNull Call<UserResponse> call, @NonNull Throwable t) {
                errorMessage.setValue("Lỗi kết nối mạng khi tải thông tin người dùng: " + t.getMessage());
            }
        });
    }

    public void changePassword(int userId, String oldPassword, String newPassword) {
        UserApi api = ApiClient.getClient().create(UserApi.class);
        // Đã sửa: changePassword giờ nhận @Query("userId")
        api.changePassword(userId, oldPassword, newPassword).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse> call, @NonNull Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    passwordResult.setValue(response.body());
                } else {
                    ApiResponse errorApi = new ApiResponse();
                    errorApi.setSuccess(false);
                    errorApi.setMessage(response.message().isEmpty() ? "Đổi mật khẩu thất bại" : response.message());
                    passwordResult.setValue(errorApi);
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse> call, @NonNull Throwable t) {
                ApiResponse errorApi = new ApiResponse();
                errorApi.setSuccess(false);
                errorApi.setMessage("Lỗi kết nối mạng khi đổi mật khẩu: " + t.getMessage());
                passwordResult.setValue(errorApi);
            }
        });
    }

    public void deleteAccount(int userId) {
        UserApi api = ApiClient.getClient().create(UserApi.class);
        // Đã sửa: deleteAccount giờ nhận @Query("id")
        api.deleteAccount(userId).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse> call, @NonNull Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    deleteResult.setValue(response.body());
                } else {
                    ApiResponse errorApi = new ApiResponse();
                    errorApi.setSuccess(false);
                    errorApi.setMessage("Xóa tài khoản thất bại: " + response.message());
                    deleteResult.setValue(errorApi);
                }
            }
            @Override
            public void onFailure(@NonNull Call<ApiResponse> call, @NonNull Throwable t) {
                ApiResponse errorApi = new ApiResponse();
                errorApi.setSuccess(false);
                errorApi.setMessage("Lỗi kết nối mạng khi xóa tài khoản: " + t.getMessage());
                deleteResult.setValue(errorApi);
            }
        });
    }

    public void updateProfile(int userId, String name, String phone, String email, String dob) {
        UserApi api = ApiClient.getClient().create(UserApi.class);
        // Đã sửa: updateProfile giờ nhận @Field("id")
        api.updateProfile(userId, name, phone, email, dob).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse> call, @NonNull Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    profileUpdateResult.setValue(response.body());
                } else {
                    ApiResponse errorApi = new ApiResponse();
                    errorApi.setSuccess(false);
                    errorApi.setMessage("Cập nhật profile thất bại: " + response.message());
                    profileUpdateResult.setValue(errorApi);
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse> call, @NonNull Throwable t) {
                ApiResponse errorApi = new ApiResponse();
                errorApi.setSuccess(false);
                errorApi.setMessage("Lỗi kết nối mạng khi cập nhật profile: " + t.getMessage());
                profileUpdateResult.setValue(errorApi);
            }
        });
    }

    public void fetchOrders(int userId) {
        OrderApi api = ApiClient.getClient().create(OrderApi.class);
        api.getOrderHistory(userId).enqueue(new Callback<OrderHistoryResponse>() {
            @Override
            public void onResponse(@NonNull Call<OrderHistoryResponse> call, @NonNull Response<OrderHistoryResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().orders != null) {
                    orders.setValue(response.body().orders);
                } else {
                    errorMessage.setValue("Không thể tải lịch sử đơn hàng: " + response.code());
                }
            }
            @Override
            public void onFailure(@NonNull Call<OrderHistoryResponse> call, @NonNull Throwable t) {
                errorMessage.setValue("Lỗi kết nối mạng khi tải đơn hàng: " + t.getMessage());
            }
        });
    }
}