package com.example.tllttbdd.ui.cart;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.tllttbdd.data.model.CartItem;
import com.example.tllttbdd.data.model.CartResponse;
import com.example.tllttbdd.data.model.ApiResponse;
import com.example.tllttbdd.data.repository.CartRepository;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CartViewModel extends ViewModel {
    private final CartRepository repository = new CartRepository();
    private final MutableLiveData<List<CartItem>> cartItems = new MutableLiveData<>();
    private final MutableLiveData<Integer> total = new MutableLiveData<>();
    private int idLogin = -1;
    private Context context;

    public LiveData<List<CartItem>> getCartItems() { return cartItems; }
    public LiveData<Integer> getTotal() { return total; }

    public void fetchCart(Context context) {
        this.context = context;
        SharedPreferences prefs = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        idLogin = prefs.getInt("id_login", -1);
        if (idLogin <= 0) {
            cartItems.setValue(null);
            total.setValue(0);
            return;
        }
        repository.getCart(idLogin).enqueue(new Callback<CartResponse>() {
            @Override
            public void onResponse(Call<CartResponse> call, Response<CartResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    cartItems.setValue(response.body().cart);
                    total.setValue(response.body().total);
                } else {
                    cartItems.setValue(null);
                    total.setValue(0);
                }
            }
            @Override
            public void onFailure(Call<CartResponse> call, Throwable t) {
                cartItems.setValue(null);
                total.setValue(0);
            }
        });
    }

    public void removeFromCart(int productId) {
        if (idLogin <= 0) return;
        repository.removeFromCart(productId, idLogin).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                fetchCart(context);
            }
            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) { }
        });
    }

    public void updateCart(int productId, int quantity) {
        if (idLogin <= 0) return;
        repository.updateCart(productId, quantity, idLogin).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                fetchCart(context);
            }
            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) { }
        });
    }
}