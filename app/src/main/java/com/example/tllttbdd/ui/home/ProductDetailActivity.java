package com.example.tllttbdd.ui.home;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductDetailActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "user_prefs";
    private static final String KEY_USER_ID = "id_login";

    private ImageView imgProduct;
    private TextView tvName, tvPrice, tvAuthor, tvDescription, tvOriginalPrice, tvPublisher, tvPublisherYear, tvDimension, tvManufacturer, tvPage;
    private LinearLayout layoutBtnAddToCart, btnChatNow;
    private Button btnBuyNow;
    private ImageButton btnBack;
    private Product currentProduct;

    private RecyclerView recyclerSimilarProducts;
    private SimilarProductAdapter similarProductsAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        initViews();
        setupClickListeners();
        handleIncomingIntent();
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
        recyclerSimilarProducts = findViewById(R.id.recyclerSimilarProducts);

        setupSimilarProductsRecyclerView();
    }

    private void setupSimilarProductsRecyclerView() {
        similarProductsAdapter = new SimilarProductAdapter(new ArrayList<>());
        recyclerSimilarProducts.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerSimilarProducts.setAdapter(similarProductsAdapter);

        similarProductsAdapter.setOnItemClickListener(product -> {
            Intent intent = new Intent(this, ProductDetailActivity.class);
            intent.putExtra("PRODUCT_ID", product.id_product);
            startActivity(intent);
            finish();
        });
    }

    private void setupClickListeners() {
        btnBack.setOnClickListener(v -> finish());
        btnChatNow.setOnClickListener(v -> {
            Intent resultIntent = new Intent();
            resultIntent.putExtra("NAVIGATE_TO", "CONTACTS");
            setResult(RESULT_OK, resultIntent);
            finish();
        });
        btnBuyNow.setOnClickListener(v -> {
            if (currentProduct != null) {
                Intent intent = new Intent(ProductDetailActivity.this, OrderActivity.class);
                intent.putExtra("PRODUCT_OBJECT", currentProduct);
                startActivity(intent);
            } else {
                Toast.makeText(this, "Vui lòng chờ tải dữ liệu", Toast.LENGTH_SHORT).show();
            }
        });
        layoutBtnAddToCart.setOnClickListener(v -> {
            if (currentProduct == null) {
                Toast.makeText(this, "Vui lòng chờ tải dữ liệu", Toast.LENGTH_SHORT).show();
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

    private void handleIncomingIntent() {
        int productId = getIntent().getIntExtra("PRODUCT_ID", -1);
        if (productId != -1) {
            fetchProductDetail(productId);
        } else {
            Toast.makeText(this, "Lỗi: Không tìm thấy ID sản phẩm.", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void fetchProductDetail(int id) {
        ProductApi api = ApiClient.getClient().create(ProductApi.class);
        api.getProductDetail(id).enqueue(new Callback<ProductResponse>() {
            @Override
            public void onResponse(@NonNull Call<ProductResponse> call, @NonNull Response<ProductResponse> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().products.isEmpty()) {
                    currentProduct = response.body().products.get(0);
                    bindDataToView(currentProduct);
                    // SỬA 1: Dùng ID DANH MỤC để tìm sản phẩm khác
                    if (currentProduct.id_category > 0) {
                        fetchSimilarProducts(currentProduct.id_category);
                    }
                } else {
                    Toast.makeText(ProductDetailActivity.this, "Không thể tải chi tiết sản phẩm", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(@NonNull Call<ProductResponse> call, @NonNull Throwable t) {
                Toast.makeText(ProductDetailActivity.this, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // SỬA 2: VIẾT LẠI HOÀN TOÀN HÀM NÀY
    private void fetchSimilarProducts(int categoryId) {
        if (categoryId <= 0) {
            findViewById(R.id.similar_products_block).setVisibility(View.GONE);
            return;
        }
        ProductApi api = ApiClient.getClient().create(ProductApi.class);
        // Gọi đúng API lấy sản phẩm theo danh mục
        api.getProductsByCategory(categoryId).enqueue(new Callback<ProductResponse>() {
            @Override
            public void onResponse(@NonNull Call<ProductResponse> call, @NonNull Response<ProductResponse> response) {
                View similarBlock = findViewById(R.id.similar_products_block);
                if (response.isSuccessful() && response.body() != null && response.body().products != null) {
                    // Lọc sản phẩm hiện tại ra khỏi danh sách tương tự
                    List<Product> similarProducts = response.body().products.stream()
                            .filter(p -> p.id_product != currentProduct.id_product)
                            .collect(Collectors.toList());

                    if (!similarProducts.isEmpty()) {
                        similarBlock.setVisibility(View.VISIBLE);
                        similarProductsAdapter.setProducts(similarProducts);
                    } else {
                        similarBlock.setVisibility(View.GONE);
                    }
                } else {
                    similarBlock.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(@NonNull Call<ProductResponse> call, @NonNull Throwable t) {
                findViewById(R.id.similar_products_block).setVisibility(View.GONE);
            }
        });
    }

    private void bindDataToView(Product p) {
        tvName.setText(p.name_product);
        Glide.with(this).load("http://10.0.2.2:3000" + p.image_product).into(imgProduct);

        tvAuthor.setText(p.author);
        tvPublisher.setText(p.publisher);
        tvPublisherYear.setText(String.valueOf(p.publisher_year));
        tvDimension.setText(p.dimension);
        tvManufacturer.setText(p.manufacturer);
        tvPage.setText(String.valueOf(p.page));
        tvDescription.setText(p.text_product);

        DecimalFormat formatter = new DecimalFormat("#,###");
        try {
            double salePrice = Double.parseDouble(p.price);
            tvPrice.setText("đ" + formatter.format(salePrice));
            btnBuyNow.setText("Mua ngay\n" + "đ" + formatter.format(salePrice));

            // SỬA 3: SỬA LỖI LOGIC GIÁ GỐC
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
        } catch (Exception e) {
            tvPrice.setText(p.price != null ? p.price + "đ" : "N/A");
            tvOriginalPrice.setVisibility(View.GONE);
        }
    }
}