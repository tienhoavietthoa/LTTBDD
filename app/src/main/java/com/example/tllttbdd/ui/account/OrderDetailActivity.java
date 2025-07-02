package com.example.tllttbdd.ui.account;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ImageButton;
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
    private TextView tvOrderId, tvOrderName, tvOrderPhone, tvOrderAddress, tvOrderTotalInfo, tvOrderTotalFooter;
    private RecyclerView recyclerView;
    private OrderDetailAdapter adapter;
    private ImageButton btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);

        // Ánh xạ view đúng theo XML mới
        btnBack = findViewById(R.id.btnBack);
        tvOrderId = findViewById(R.id.tvOrderId);
        tvOrderName = findViewById(R.id.tvOrderName);
        tvOrderPhone = findViewById(R.id.tvOrderPhone);
        tvOrderAddress = findViewById(R.id.tvOrderAddress);
        tvOrderTotalInfo = findViewById(R.id.tvOrderTotalInfo);
        tvOrderTotalFooter = findViewById(R.id.tvOrderTotalFooter);
        recyclerView = findViewById(R.id.recyclerOrderDetail);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        btnBack.setOnClickListener(v -> finish());

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

                    // Hiển thị thông tin đơn hàng
                    tvOrderId.setText("Mã đơn: " + detail.order.id_order);
                    tvOrderName.setText("Tên: " + detail.order.name_order);
                    tvOrderPhone.setText("SĐT: " + detail.order.phone_order);
                    tvOrderAddress.setText("Địa chỉ: " + detail.order.address_order);

                    String tongTien = "Tổng: " + detail.order.total + " đ";
                    tvOrderTotalInfo.setText(tongTien);
                    tvOrderTotalFooter.setText(tongTien);

                    adapter = new OrderDetailAdapter(detail.products != null ? detail.products : new ArrayList<>());
                    recyclerView.setAdapter(adapter);
                } else {
                    tvOrderId.setText("Không lấy được chi tiết đơn hàng");
                    tvOrderName.setText("");
                    tvOrderPhone.setText("");
                    tvOrderAddress.setText("");
                    tvOrderTotalInfo.setText("");
                    tvOrderTotalFooter.setText("");
                }
            }
            @Override
            public void onFailure(Call<OrderDetailResponse> call, Throwable t) {
                tvOrderId.setText("Lỗi kết nối");
                tvOrderName.setText("");
                tvOrderPhone.setText("");
                tvOrderAddress.setText("");
                tvOrderTotalInfo.setText("");
                tvOrderTotalFooter.setText("");
            }
        });
    }
}