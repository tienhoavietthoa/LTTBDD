package com.example.tllttbdd.ui.auth;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.tllttbdd.data.model.LoginResponse;
import com.example.tllttbdd.data.repository.AuthRepository;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuthViewModel extends ViewModel {
    private AuthRepository authRepository = new AuthRepository();
    private MutableLiveData<LoginResponse> loginResult = new MutableLiveData<>();
    private MutableLiveData<LoginResponse> registerResult = new MutableLiveData<>();

    public LiveData<LoginResponse> getLoginResult() { return loginResult; }
    public LiveData<LoginResponse> getRegisterResult() { return registerResult; }

    public void login(String username, String password) {
        authRepository.login(username, password, new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                loginResult.postValue(response.body());
            }
            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                loginResult.postValue(null);
            }
        });
    }

    public void register(String username, String password, String phone) {
        authRepository.register(username, password, phone, new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                registerResult.postValue(response.body());
            }
            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                registerResult.postValue(null);
            }
        });
    }
}