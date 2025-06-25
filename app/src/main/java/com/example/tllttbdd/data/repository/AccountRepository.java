package com.example.tllttbdd.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.tllttbdd.data.model.LoginResponse;
import com.example.tllttbdd.data.model.User;
import com.example.tllttbdd.data.network.ApiClient;
import com.example.tllttbdd.data.network.AccountApi;
import java.util.Map;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AccountRepository {
    private final AccountApi api;
    private final MutableLiveData<User> user = new MutableLiveData<>();
    private final MutableLiveData<Map<String, Object>> updateResult = new MutableLiveData<>();
    private final MutableLiveData<Map<String, Object>> passwordResult = new MutableLiveData<>();
    private final MutableLiveData<Map<String, Object>> deleteResult = new MutableLiveData<>();

    public AccountRepository() {
        api = ApiClient.getClient().create(AccountApi.class);
    }

    public LiveData<User> getUser() { return user; }
    public LiveData<Map<String, Object>> getUpdateResult() { return updateResult; }
    public LiveData<Map<String, Object>> getPasswordResult() { return passwordResult; }
    public LiveData<Map<String, Object>> getDeleteResult() { return deleteResult; }

    public void fetchProfile(int idLogin) {
        api.getProfile(idLogin).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().user != null) {
                    user.postValue(response.body().user);
                }
            }
            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) { }
        });
    }

    public void updateProfile(int idLogin, String name, String phone, String email, String dob) {
        api.updateProfile(idLogin, name, phone, email, dob).enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                updateResult.postValue(response.body());
                // Fetch lại profile mới nhất sau khi update
                fetchProfile(idLogin);
            }
            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) { }
        });
    }

    public void changePassword(int idLogin, String oldPassword, String newPassword) {
        api.changePassword(idLogin, oldPassword, newPassword).enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                passwordResult.postValue(response.body());
                // Fetch lại profile mới nhất sau khi đổi mật khẩu
                fetchProfile(idLogin);
            }
            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) { }
        });
    }

    public void deleteAccount(int idLogin) {
        api.deleteAccount(idLogin).enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                deleteResult.postValue(response.body());
            }
            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) { }
        });
    }
}