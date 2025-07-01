package com.example.tllttbdd.data.repository;

import com.example.tllttbdd.data.model.Order;
import com.example.tllttbdd.data.model.OrderHistoryResponse;
import com.example.tllttbdd.data.network.ApiClient;
import com.example.tllttbdd.data.network.OrderApi;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderRepository {
    private static OrderRepository instance;
    private final OrderApi api;

    public interface OrderHistoryCallback {
        void onSuccess(List<Order> orders);
        void onError(String error);
    }

    private OrderRepository() {
        api = ApiClient.getClient().create(OrderApi.class);
    }

    public static OrderRepository getInstance() {
        if (instance == null) instance = new OrderRepository();
        return instance;
    }

    public void fetchOrderHistory(int idLogin, OrderHistoryCallback callback) {
        api.getOrderHistory(idLogin).enqueue(new Callback<OrderHistoryResponse>() {
            @Override
            public void onResponse(Call<OrderHistoryResponse> call, Response<OrderHistoryResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body().orders);
                } else {
                    callback.onError("Không lấy được lịch sử đơn hàng");
                }
            }
            @Override
            public void onFailure(Call<OrderHistoryResponse> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }
}