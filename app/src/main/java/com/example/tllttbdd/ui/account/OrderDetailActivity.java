package com.example.tllttbdd.ui.account;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.tllttbdd.R;
import com.example.tllttbdd.data.model.OrderDetailResponse;
import com.example.tllttbdd.data.network.ApiClient;
import com.example.tllttbdd.data.network.OrderApi;
import java.util.ArrayList;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderDetailActivity extends AppCompatActivity {
    private TextView tvOrderInfo;
    private RecyclerView recyclerView;
    private OrderDetailAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);

        tvOrderInfo = findViewById(R.id.tvOrderInfo);
        recyclerView = findViewById(R.id.recyclerOrderDetail);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        int orderId = getIntent().getIntExtra("ORDER_ID", -1);
        if (orderId != -1) {
            fetchOrderDetail(orderId);
        }
    }

    private void fetchOrderDetail(int orderId) {
        OrderApi api = ApiClient.getClient().create(OrderApi.class);
        api.getOrderDetail(orderId).enqueue(new Callback<OrderDetailResponse>() {
            @Override
            public void onResponse(Call<OrderDetailResponse> call, Response<OrderDetailResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    OrderDetailResponse detail = response.body();
                    tvOrderInfo.setText("Mã đơn: " + detail.order.id_order + "\nTên: " + detail.order.name_order + "\nTổng: " + detail.order.total);
                    // Luôn gán adapter, kể cả khi detail.products rỗng
                    adapter = new OrderDetailAdapter(detail.products != null ? detail.products : new ArrayList<>());
                    recyclerView.setAdapter(adapter);
                } else {
                    tvOrderInfo.setText("Không lấy được chi tiết đơn hàng");
                }
            }
            @Override
            public void onFailure(Call<OrderDetailResponse> call, Throwable t) {
                tvOrderInfo.setText("Lỗi kết nối");
            }
        });
    }
}