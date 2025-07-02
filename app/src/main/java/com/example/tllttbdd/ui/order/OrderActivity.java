package com.example.tllttbdd.ui.order;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;
import android.widget.ImageButton;
import android.widget.AdapterView;

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
import com.example.tllttbdd.data.model.Province;
import com.example.tllttbdd.data.model.District;
import com.example.tllttbdd.data.model.Ward;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderActivity extends AppCompatActivity {
    private EditText editName, editPhone, editDetailAddress;
    private RadioGroup radioPayment;
    private ImageView imgQR;
    private TextView orderTotal;
    private Button btnOrder;
    private RecyclerView orderProductRecycler;
    private ArrayList<CartItem> cartItemsToOrder;
    private Spinner spinnerCity, spinnerDistrict, spinnerWard;
    private ImageButton btnBack;

    private List<Province> provinceList = new ArrayList<>();
    private ArrayAdapter<String> cityAdapter, districtAdapter, wardAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.order_activity);

        initViews();
        setupAddressSpinners();
        handleIncomingIntent();
        setupListeners();
    }

    private void initViews() {
        editName = findViewById(R.id.editName);
        editPhone = findViewById(R.id.editPhone);
        spinnerCity = findViewById(R.id.spinnerCity);
        spinnerDistrict = findViewById(R.id.spinnerDistrict);
        spinnerWard = findViewById(R.id.spinnerWard);
        editDetailAddress = findViewById(R.id.editDetailAddress);
        radioPayment = findViewById(R.id.radioPayment);
        imgQR = findViewById(R.id.imgQR);
        orderTotal = findViewById(R.id.orderTotal);
        btnOrder = findViewById(R.id.btnOrder);
        orderProductRecycler = findViewById(R.id.orderProductRecycler);
        btnBack = findViewById(R.id.btnBack);

        btnBack.setOnClickListener(v -> finish());
    }

    private void setupAddressSpinners() {
        // Đọc file JSON địa chỉ từ assets
        String json = loadJSONFromAsset("provinces.json");
        if (json != null) {
            provinceList = new Gson().fromJson(json, new TypeToken<List<Province>>(){}.getType());
        }

        // Fill city spinner
        List<String> cityNames = new ArrayList<>();
        for (Province p : provinceList) cityNames.add(p.name);
        cityAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, cityNames);
        cityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCity.setAdapter(cityAdapter);

        // Khi chọn city, fill district
        spinnerCity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Province selectedProvince = provinceList.get(position);
                List<String> districtNames = new ArrayList<>();
                for (District d : selectedProvince.districts) districtNames.add(d.name);
                districtAdapter = new ArrayAdapter<>(OrderActivity.this, android.R.layout.simple_spinner_item, districtNames);
                districtAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerDistrict.setAdapter(districtAdapter);
                // Reset ward spinner
                spinnerWard.setAdapter(null);
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });

        // Khi chọn district, fill ward
        spinnerDistrict.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int cityPos = spinnerCity.getSelectedItemPosition();
                if (cityPos < 0) return;
                Province selectedProvince = provinceList.get(cityPos);
                if (position < 0 || position >= selectedProvince.districts.size()) return;
                District selectedDistrict = selectedProvince.districts.get(position);
                List<String> wardNames = new ArrayList<>();
                for (Ward w : selectedDistrict.wards) wardNames.add(w.name);
                wardAdapter = new ArrayAdapter<>(OrderActivity.this, android.R.layout.simple_spinner_item, wardNames);
                wardAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerWard.setAdapter(wardAdapter);
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });
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
        String detail = editDetailAddress.getText().toString().trim();

        String city = spinnerCity.getSelectedItem() != null ? spinnerCity.getSelectedItem().toString() : "";
        String district = spinnerDistrict.getSelectedItem() != null ? spinnerDistrict.getSelectedItem().toString() : "";
        String ward = spinnerWard.getSelectedItem() != null ? spinnerWard.getSelectedItem().toString() : "";

        String address = detail + ", " + ward + ", " + district + ", " + city;

        if (name.isEmpty() || phone.isEmpty() || detail.isEmpty() || city.isEmpty() || district.isEmpty() || ward.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập/chọn đủ thông tin giao hàng!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Kiểm tra số điện thoại đủ 10 số
        if (!phone.matches("^0\\d{9}$")) {
            Toast.makeText(this, "Số điện thoại phải đủ 10 số và bắt đầu bằng số 0!", Toast.LENGTH_SHORT).show();
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

    // Đọc file JSON từ assets
    private String loadJSONFromAsset(String filename) {
        try {
            InputStream is = getAssets().open(filename);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            return new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    // Các class mẫu cho địa chỉ
    public static class Province {
        public String name;
        public List<District> districts;
    }
    public static class District {
        public String name;
        public List<Ward> wards;
    }
    public static class Ward {
        public String name;
    }
}