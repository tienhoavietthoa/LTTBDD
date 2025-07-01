package com.example.tllttbdd.ui.order;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tllttbdd.R;
import com.example.tllttbdd.data.model.CartItem;
import com.example.tllttbdd.data.model.ApiResponse;
import com.example.tllttbdd.data.network.OrderApi;
import com.example.tllttbdd.data.network.ApiClient;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderActivity extends AppCompatActivity {
    private EditText editName, editPhone, editAddress;
    private RadioGroup radioPayment;
    private RadioButton radioCOD, radioQR;
    private ImageView imgQR;
    private TextView orderTotal;
    private Button btnOrder;
    private RecyclerView orderProductRecycler;
    private ArrayList<CartItem> selectedItems;
    private int total = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.order_activity);

        editName = findViewById(R.id.editName);
        editPhone = findViewById(R.id.editPhone);
        editAddress = findViewById(R.id.editAddress);
        radioPayment = findViewById(R.id.radioPayment);
        radioCOD = findViewById(R.id.radioCOD);
        radioQR = findViewById(R.id.radioQR);
        imgQR = findViewById(R.id.imgQR);
        orderTotal = findViewById(R.id.orderTotal);
        btnOrder = findViewById(R.id.btnOrder);
        orderProductRecycler = findViewById(R.id.orderProductRecycler);

        // Nhận danh sách sản phẩm từ Intent
        selectedItems = getIntent().getParcelableArrayListExtra("selectedItems");
        if (selectedItems == null) selectedItems = new ArrayList<>();

        // Hiển thị danh sách sản phẩm
        orderProductRecycler.setLayoutManager(new LinearLayoutManager(this));
        orderProductRecycler.setAdapter(new OrderProductAdapter(selectedItems));

        // Tính tổng tiền
        total = 0;
        for (CartItem item : selectedItems) {
            total += item.price * item.quantity;
        }
        orderTotal.setText("Tổng: " + total + " đ");

        // Xử lý chọn phương thức thanh toán
        radioPayment.setOnCheckedChangeListener((group, checkedId) -> {
            imgQR.setVisibility(checkedId == R.id.radioQR ? View.VISIBLE : View.GONE);
        });

        btnOrder.setOnClickListener(v -> {
            String name = editName.getText().toString().trim();
            String phone = editPhone.getText().toString().trim();
            String address = editAddress.getText().toString().trim();
            String payment = radioCOD.isChecked() ? "Trả tiền khi nhận hàng" : "Thanh toán QR";
            if (name.isEmpty() || phone.isEmpty() || address.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đủ thông tin!", Toast.LENGTH_SHORT).show();
                return;
            }
            placeOrder(name, phone, address, payment, selectedItems, total);
        });
    }

    private void placeOrder(String name, String phone, String address, String payment, List<CartItem> items, int total) {
        int idLogin = getSharedPreferences("user_prefs", MODE_PRIVATE).getInt("id_login", -1);
        if (idLogin <= 0) {
            Toast.makeText(this, "Bạn chưa đăng nhập!", Toast.LENGTH_SHORT).show();
            return;
        }
        // Chuyển danh sách sản phẩm sang JSON string
        String productsJson = new Gson().toJson(items);

        OrderApi orderApi = ApiClient.getClient().create(OrderApi.class);
        orderApi.createOrder(name, phone, address, payment, total, idLogin, productsJson)
                .enqueue(new Callback<ApiResponse>() {
                    @Override
                    public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                        if (response.isSuccessful() && response.body() != null && response.body().success) {
                            Toast.makeText(OrderActivity.this, "Đặt hàng thành công!", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(OrderActivity.this, "Đặt hàng thất bại!", Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override
                    public void onFailure(Call<ApiResponse> call, Throwable t) {
                        Toast.makeText(OrderActivity.this, "Lỗi kết nối!", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}