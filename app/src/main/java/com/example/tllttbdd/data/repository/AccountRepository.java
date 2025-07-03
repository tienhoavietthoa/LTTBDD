package com.example.tllttbdd.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.tllttbdd.data.model.LoginResponse;
import com.example.tllttbdd.data.model.Order; // Import Order model
import com.example.tllttbdd.data.model.User;
import com.example.tllttbdd.data.network.ApiClient;
import com.example.tllttbdd.data.network.AccountApi;

import java.util.ArrayList; // Import ArrayList
import java.util.List; // Import List
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import android.util.Log;

public class AccountRepository {
    private final AccountApi api;
    private final MutableLiveData<User> user = new MutableLiveData<>();
    private final MutableLiveData<Map<String, Object>> updateResult = new MutableLiveData<>();
    private final MutableLiveData<Map<String, Object>> passwordResult = new MutableLiveData<>();
    private final MutableLiveData<Map<String, Object>> deleteResult = new MutableLiveData<>();

    // --- THÊM MỚI: LiveData để chứa danh sách đơn hàng ---
    private final MutableLiveData<List<Order>> orders = new MutableLiveData<>();

    public AccountRepository() {
        api = ApiClient.getClient().create(AccountApi.class);
    }

    public LiveData<User> getUser () { return user; }
    public LiveData<Map<String, Object>> getUpdateResult() { return updateResult; }
    public LiveData<Map<String, Object>> getPasswordResult() { return passwordResult; }
    public LiveData<Map<String, Object>> getDeleteResult() { return deleteResult; }

    // --- THÊM MỚI: Getter cho LiveData đơn hàng ---
    public LiveData<List<Order>> getOrdersFromDatabase() {
        return orders;
    }

    public void fetchProfile(int idLogin) {
        Log.d("AccountRepository", "Fetching user profile for ID: " + idLogin);
        api.getProfile(idLogin).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null && response.body().user != null) {
                        user.postValue(response.body().user);
                        Log.d("AccountRepository", "User profile fetched successfully: " + response.body().user.name_information);
                    } else {
                        Log.e("AccountRepository", "Failed to fetch user profile: User object is null or response body is empty. Message: " + response.message());
                        user.postValue(null);
                    }
                } else {
                    Log.e("AccountRepository", "Failed to fetch user profile. HTTP Code: " + response.code() + ", Message: " + response.message());
                    user.postValue(null);
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Log.e("AccountRepository", "Error fetching user profile: " + t.getMessage());
                user.postValue(null);
            }
        });
    }

    public void updateProfile(int idLogin, String name, String phone, String email, String dob) {
        api.updateProfile(idLogin, name, phone, email, dob).enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                updateResult.postValue(response.body());
                fetchProfile(idLogin); // Fetch lại profile mới nhất sau khi update
            }

            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                Log.e("AccountRepository", "Error updating profile: " + t.getMessage());
            }
        });
    }

    public void changePassword(int idLogin, String oldPassword, String newPassword) {
        api.changePassword(idLogin, oldPassword, newPassword).enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                passwordResult.postValue(response.body());
                fetchProfile(idLogin); // Fetch lại profile mới nhất sau khi đổi mật khẩu
            }

            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                Log.e("AccountRepository", "Error changing password: " + t.getMessage());
            }
        });
    }

    public void deleteAccount(int idLogin) {
        api.deleteAccount(idLogin).enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                deleteResult.postValue(response.body());
            }

            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                Log.e("AccountRepository", "Error deleting account: " + t.getMessage());
            }
        });
    }

    // --- THÊM MỚI: Phương thức để lấy danh sách đơn hàng ---
    public void fetchOrders(int userId) { // Thêm userId nếu API yêu cầu
        Log.d("AccountRepository", "Fetching orders for user ID: " + userId);
        api.getOrders(userId).enqueue(new Callback<List<Order>>() {
            @Override
            public void onResponse(Call<List<Order>> call, Response<List<Order>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    orders.postValue(response.body());
                    Log.d("AccountRepository", "Orders fetched successfully. Count: " + response.body().size());
                } else {
                    Log.e("AccountRepository", "Failed to fetch orders. HTTP Code: " + response.code() + ", Message: " + response.message());
                    orders.postValue(new ArrayList<>()); // Trả về danh sách rỗng nếu lỗi
                }
            }

            @Override
            public void onFailure(Call<List<Order>> call, Throwable t) {
                Log.e("AccountRepository", "Error fetching orders: " + t.getMessage());
                orders.postValue(new ArrayList<>()); // Trả về danh sách rỗng nếu có lỗi mạng
            }
        });
    }
}
