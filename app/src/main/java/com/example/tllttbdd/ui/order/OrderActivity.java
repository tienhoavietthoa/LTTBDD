package com.example.tllttbdd.ui.order;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tllttbdd.R;
import com.example.tllttbdd.data.model.ApiResponse;
import com.example.tllttbdd.data.model.CartItem;
import com.example.tllttbdd.data.model.Product;
import com.example.tllttbdd.data.network.ApiClient;
import com.example.tllttbdd.data.network.OrderApi;
import com.google.gson.Gson;

import java.text.DecimalFormat;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderActivity extends AppCompatActivity {
    private EditText editName, editPhone, editAddress;
    private RadioGroup radioPayment;
    private ImageView imgQR;
    private TextView orderTotal;
    private Button btnOrder;
    private RecyclerView orderProductRecycler;
    private ArrayList<CartItem> cartItemsToOrder;
    private ImageButton btnBack;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.order_activity);

        initViews();
        handleIncomingIntent();
        setupListeners();
    }

    private void initViews() {
        editName = findViewById(R.id.editName);
        editPhone = findViewById(R.id.editPhone);
        editAddress = findViewById(R.id.editAddress);
        radioPayment = findViewById(R.id.radioPayment);
        imgQR = findViewById(R.id.imgQR);
        orderTotal = findViewById(R.id.orderTotal);
        btnOrder = findViewById(R.id.btnOrder);
        orderProductRecycler = findViewById(R.id.orderProductRecycler);
        btnBack = findViewById(R.id.btnBack);

        // Nút back - quay về trang trước
        btnBack.setOnClickListener(v -> finish());
    }

    private void handleIncomingIntent() {
        cartItemsToOrder = new ArrayList<>();
        Intent intent = getIntent();

        // Nhận danh sách sản phẩm từ giỏ hàng
        if (intent.hasExtra("selectedItems")) {
            ArrayList<CartItem> itemsFromCart = intent.getParcelableArrayListExtra("selectedItems");
            if (itemsFromCart != null && !itemsFromCart.isEmpty()) {
                cartItemsToOrder.addAll(itemsFromCart);
            }
        }
        // Nhận một sản phẩm từ nút "Mua ngay"
        else if (intent.hasExtra("PRODUCT_OBJECT")) {
            Product productFromDetail = (Product) intent.getSerializableExtra("PRODUCT_OBJECT");
            if (productFromDetail != null) {
                CartItem singleItem = new CartItem(
                        productFromDetail.id_product,
                        productFromDetail.name_product,
                        (int) Double.parseDouble(productFromDetail.price),
                        productFromDetail.image_product,
                        1
                );
                cartItemsToOrder.add(singleItem);
            }
        }

        // Nếu không có sản phẩm nào, báo lỗi và thoát
        if (cartItemsToOrder.isEmpty()) {
            Toast.makeText(this, "Không có sản phẩm để thanh toán", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Hiển thị dữ liệu lên giao diện
        displayOrderData();
    }

    private void displayOrderData() {
        // Hiển thị danh sách sản phẩm
        orderProductRecycler.setLayoutManager(new LinearLayoutManager(this));
        orderProductRecycler.setAdapter(new OrderProductAdapter(cartItemsToOrder));

        // Tính và hiển thị tổng tiền
        double total = 0;
        for (CartItem item : cartItemsToOrder) {
            total += (double) item.price * item.quantity;
        }
        DecimalFormat formatter = new DecimalFormat("#,###");
        orderTotal.setText("Tổng: " + formatter.format(total) + " đ");
    }

    private void setupListeners() {
        radioPayment.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.radioQR) {
                imgQR.setVisibility(View.VISIBLE);
            } else {
                imgQR.setVisibility(View.GONE);
            }
        });

        btnOrder.setOnClickListener(v -> placeOrder());
    }

    private void placeOrder() {
        String name = editName.getText().toString().trim();
        String phone = editPhone.getText().toString().trim();
        String address = editAddress.getText().toString().trim();

        if (name.isEmpty() || phone.isEmpty() || address.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đủ thông tin giao hàng!", Toast.LENGTH_SHORT).show();
            return;
        }

        RadioButton selectedPayment = findViewById(radioPayment.getCheckedRadioButtonId());
        String payment = selectedPayment.getText().toString();

        double total = 0;
        for (CartItem item : cartItemsToOrder) {
            total += (double) item.price * item.quantity;
        }

        int idLogin = getSharedPreferences("user_prefs", MODE_PRIVATE).getInt("id_login", -1);
        if (idLogin <= 0) {
            Toast.makeText(this, "Bạn chưa đăng nhập!", Toast.LENGTH_SHORT).show();
            return;
        }

        String productsJson = new Gson().toJson(cartItemsToOrder);

        OrderApi orderApi = ApiClient.getClient().create(OrderApi.class);
        orderApi.createOrder(name, phone, address, payment, (int) total, idLogin, productsJson)
                .enqueue(new Callback<ApiResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<ApiResponse> call, @NonNull Response<ApiResponse> response) {
                        if (response.isSuccessful() && response.body() != null && response.body().success) {
                            Toast.makeText(OrderActivity.this, "Đặt hàng thành công!", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(OrderActivity.this, "Đặt hàng thất bại!", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<ApiResponse> call, @NonNull Throwable t) {
                        Toast.makeText(OrderActivity.this, "Lỗi kết nối!", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}