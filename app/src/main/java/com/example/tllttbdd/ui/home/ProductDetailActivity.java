package com.example.tllttbdd.ui.home;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.RelativeSizeSpan;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.tllttbdd.R;
import com.example.tllttbdd.data.model.ApiResponse;
import com.example.tllttbdd.data.model.Product;
import com.example.tllttbdd.data.model.ProductResponse;
import com.example.tllttbdd.data.network.ApiClient;
import com.example.tllttbdd.data.network.ProductApi;
import com.example.tllttbdd.data.repository.CartRepository;
import com.example.tllttbdd.ui.order.OrderActivity;

import java.text.DecimalFormat;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductDetailActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "user_prefs";
    private static final String KEY_USER_ID = "id_login";

    private ImageView imgProduct;
    private TextView tvName, tvPrice, tvAuthor, tvDescription, tvOriginalPrice,tvPublisher,tvPublisherYear,tvDimension,
            tvManufacturer,tvPage;
    private LinearLayout layoutBtnAddToCart, btnChatNow;
    private Button btnBuyNow;
    private ImageButton btnBack;
    private Product currentProduct; // Lưu trữ sản phẩm hiện tại để các listener có thể dùng
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        initViews();
        setupClickListeners();

        int productId = getIntent().getIntExtra("PRODUCT_ID", -1);
        if (productId != -1) {
            fetchProductDetail(productId);
        } else {
            Toast.makeText(this, "Không tìm thấy sản phẩm", Toast.LENGTH_SHORT).show();
            finish();
        }
    }
    private void initViews() {
        imgProduct = findViewById(R.id.imgProductDetail);
        tvName = findViewById(R.id.tvProductNameDetail);
        tvPrice = findViewById(R.id.tvProductPriceDetail);
        tvAuthor = findViewById(R.id.tvProductAuthorDetail);
        tvDescription = findViewById(R.id.tvProductDescDetail);
        tvOriginalPrice = findViewById(R.id.tvOriginalPrice);
        tvPublisher = findViewById(R.id.tvProductPublisher);
        tvPublisherYear = findViewById(R.id.tvProductPublisherYear);
        tvDimension = findViewById(R.id.tvProductDimension);
        tvManufacturer = findViewById(R.id.tvProductManufacturer);
        tvPage = findViewById(R.id.tvProductPage);
        layoutBtnAddToCart = findViewById(R.id.layoutBtnAddToCart);
        btnBuyNow = findViewById(R.id.btnBuyNow);
        btnBack = findViewById(R.id.btnBack);
        btnChatNow = findViewById(R.id.btnChatNow);
    }

    private void setupClickListeners() {
        btnBack.setOnClickListener(v -> finish());
        btnChatNow.setOnClickListener(v -> Toast.makeText(this, "Mở màn hình Chat", Toast.LENGTH_SHORT).show());

        // Chuyển sang trang OrderActivity khi bấm Mua ngay
        btnBuyNow.setOnClickListener(v -> {
            if (currentProduct != null) {
                Intent intent = new Intent(ProductDetailActivity.this, OrderActivity.class);
                // Truyền cả đối tượng product đi, yêu cầu lớp Product phải implement Serializable
                intent.putExtra("PRODUCT_OBJECT", currentProduct);
                startActivity(intent);
            } else {
                Toast.makeText(this, "Vui lòng chờ tải xong dữ liệu sản phẩm", Toast.LENGTH_SHORT).show();
            }
        });

        // Xử lý Thêm vào giỏ hàng
        layoutBtnAddToCart.setOnClickListener(v -> {
            if (currentProduct == null) {
                Toast.makeText(this, "Vui lòng chờ tải xong dữ liệu sản phẩm", Toast.LENGTH_SHORT).show();
                return;
            }

            int quantity = 1;
            SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
            int idLogin = prefs.getInt(KEY_USER_ID, -1);
            if (idLogin <= 0) {
                Toast.makeText(this, "Bạn cần đăng nhập!", Toast.LENGTH_SHORT).show();
                return;
            }

            CartRepository repo = new CartRepository();
            repo.addToCart(currentProduct.id_product, quantity, idLogin).enqueue(new Callback<ApiResponse>() {
                @Override
                public void onResponse(@NonNull Call<ApiResponse> call, @NonNull Response<ApiResponse> response) {
                    if (response.isSuccessful() && response.body() != null && response.body().success) {
                        Toast.makeText(ProductDetailActivity.this, "Đã thêm vào giỏ hàng!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(ProductDetailActivity.this, "Thêm vào giỏ hàng thất bại!", Toast.LENGTH_SHORT).show();
                    }
                }
                @Override
                public void onFailure(@NonNull Call<ApiResponse> call, @NonNull Throwable t) {
                    Toast.makeText(ProductDetailActivity.this, "Lỗi mạng!", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    private void fetchProductDetail(int id) {
        ProductApi api = ApiClient.getClient().create(ProductApi.class);
        api.getProductDetail(id).enqueue(new Callback<ProductResponse>() {
            @Override
            public void onResponse(@NonNull Call<ProductResponse> call, @NonNull Response<ProductResponse> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().products.isEmpty()) {
                    currentProduct = response.body().products.get(0); // Lưu sản phẩm vào biến toàn cục
                    bindDataToView(currentProduct);
                } else {
                    Toast.makeText(ProductDetailActivity.this, "Không tìm thấy chi tiết sản phẩm", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
            @Override
            public void onFailure(@NonNull Call<ProductResponse> call, @NonNull Throwable t) {
                Toast.makeText(ProductDetailActivity.this, "Lỗi kết nối mạng", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void bindDataToView(Product p) {
        tvName.setText(p.name_product);
        Glide.with(this).load("http://10.0.2.2:3000" + p.image_product)
                .placeholder(R.drawable.ic_launcher_background).into(imgProduct);

        if (p.author != null && !p.author.isEmpty()) {
            tvAuthor.setText("Tác giả: " + p.author);
            tvAuthor.setVisibility(View.VISIBLE);
        } else {
            tvAuthor.setVisibility(View.GONE);
        }

        // Nhà xuất bản
        if (p.publisher != null && !p.publisher.isEmpty()) {
            tvPublisher.setText("Nhà xuất bản: " + p.publisher);
            tvPublisher.setVisibility(View.VISIBLE);
        } else {
            tvPublisher.setVisibility(View.GONE);
        }

        // Năm xuất bản
        if (p.publisher_year > 0) {
            tvPublisherYear.setText("Năm XB: " + p.publisher_year);
            tvPublisherYear.setVisibility(View.VISIBLE);
        } else {
            tvPublisherYear.setVisibility(View.GONE);
        }

        // Kích thước
        if (p.dimension != null && !p.dimension.isEmpty()) {
            tvDimension.setText("Kích thước: " + p.dimension);
            tvDimension.setVisibility(View.VISIBLE);
        } else {
            tvDimension.setVisibility(View.GONE);
        }

        // Nhà sản xuất
        if (p.manufacturer != null && !p.manufacturer.isEmpty()) {
            tvManufacturer.setText("Nhà SX: " + p.manufacturer);
            tvManufacturer.setVisibility(View.VISIBLE);
        } else {
            tvManufacturer.setVisibility(View.GONE);
        }

        // Số trang
        if (p.page > 0) {
            tvPage.setText("Số trang: " + p.page);
            tvPage.setVisibility(View.VISIBLE);
        } else {
            tvPage.setVisibility(View.GONE);
        }

        // Mô tả
        View descriptionBlock = findViewById(R.id.product_description_block);
        if (p.text_product != null && !p.text_product.isEmpty()) {
            tvDescription.setText(p.text_product);
            descriptionBlock.setVisibility(View.VISIBLE);
        } else {
            descriptionBlock.setVisibility(View.GONE);
        }

        // Giá
        DecimalFormat formatter = new DecimalFormat("#,###");
        try {
            double salePrice = Double.parseDouble(p.price);
            String formattedSalePrice = "đ" + formatter.format(salePrice);
            SpannableString spannableSalePrice = new SpannableString(formattedSalePrice);
            spannableSalePrice.setSpan(new RelativeSizeSpan(0.7f), 0, 1, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            tvPrice.setText(spannableSalePrice);
            btnBuyNow.setText("Mua ngay\n" + formattedSalePrice);

            // Giá gốc (nếu có)
            if (p.price != null && !p.price.isEmpty()) {
                double originalPrice = Double.parseDouble(p.price);
                if (originalPrice > salePrice) {
                    tvOriginalPrice.setText("đ" + formatter.format(originalPrice));
                    tvOriginalPrice.setPaintFlags(tvOriginalPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    tvOriginalPrice.setVisibility(View.VISIBLE);
                } else {
                    tvOriginalPrice.setVisibility(View.GONE);
                }
            } else {
                tvOriginalPrice.setVisibility(View.GONE);
            }
        } catch (NumberFormatException e) {
            tvPrice.setText(p.price);
            tvOriginalPrice.setVisibility(View.GONE);
        }
    }
}