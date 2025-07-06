package com.example.tllttbdd.ui.account; // Giữ lại package của bạn

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.tllttbdd.R;
import com.example.tllttbdd.data.model.OrderDetailResponse;
import com.example.tllttbdd.data.network.ApiClient;
import com.example.tllttbdd.data.network.OrderApi;
import java.text.DecimalFormat;
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

        initViews();
        setupListeners();

        int orderId = getIntent().getIntExtra("ORDER_ID", -1);
        if (orderId != -1) {
            fetchOrderDetail(orderId);
        } else {
            Toast.makeText(this, "Lỗi: Không tìm thấy ID đơn hàng.", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        tvOrderId = findViewById(R.id.tvOrderId);
        tvOrderName = findViewById(R.id.tvOrderName);
        tvOrderPhone = findViewById(R.id.tvOrderPhone);
        tvOrderAddress = findViewById(R.id.tvOrderAddress);
        tvOrderTotalInfo = findViewById(R.id.tvOrderTotalInfo);
        tvOrderTotalFooter = findViewById(R.id.tvOrderTotalFooter);
        recyclerView = findViewById(R.id.recyclerOrderDetail);

        // Khởi tạo Adapter và gán cho RecyclerView ngay từ đầu
        adapter = new OrderDetailAdapter(new ArrayList<>());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());
    }

    private void fetchOrderDetail(int orderId) {
        OrderApi api = ApiClient.getClient().create(OrderApi.class);
        api.getOrderDetail(orderId).enqueue(new Callback<OrderDetailResponse>() {
            @Override
            public void onResponse(@NonNull Call<OrderDetailResponse> call, @NonNull Response<OrderDetailResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Gọi hàm riêng để hiển thị dữ liệu
                    bindData(response.body());
                } else {
                    Toast.makeText(OrderDetailActivity.this, "Không lấy được chi tiết đơn hàng", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(@NonNull Call<OrderDetailResponse> call, @NonNull Throwable t) {
                Toast.makeText(OrderDetailActivity.this, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Hàm hiển thị toàn bộ thông tin đơn hàng lên giao diện
     */
    private void bindData(OrderDetailResponse detail) {
        if (detail.order == null) return;

        tvOrderId.setText("Mã đơn: " + detail.order.id_order);
        tvOrderName.setText("Tên: " + detail.order.name_order);
        tvOrderPhone.setText("SĐT: " + detail.order.phone_order);
        tvOrderAddress.setText("Địa chỉ: " + detail.order.address_order);

        // SỬA: Gọi hàm định dạng tiền tệ
        displayFormattedTotal(detail.order.total);

        // Cập nhật danh sách sản phẩm cho adapter
        if (detail.products != null) {
            adapter.updateProducts(detail.products);
        }
    }

    /**
     * Hàm nhận vào một số và hiển thị lên các TextView tổng tiền
     * với định dạng tiền tệ Việt Nam.
     * @param amount Số tiền cần định dạng (kiểu int hoặc double)
     */
    private void displayFormattedTotal(double amount) {
        DecimalFormat formatter = new DecimalFormat("#,###");
        String formattedAmount = formatter.format(amount);
        String finalPriceString = formattedAmount + "đ";

        tvOrderTotalInfo.setText("Tổng: " + finalPriceString);
        tvOrderTotalFooter.setText("Tổng: " + finalPriceString);
    }
}
