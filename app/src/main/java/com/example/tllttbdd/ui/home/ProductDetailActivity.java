package com.example.tllttbdd.ui.home;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
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

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductDetailActivity extends AppCompatActivity {
    private ImageView imgProduct;
    private TextView tvName, tvPrice, tvAuthor, tvDescription;
    private int productId;
    private Button btnAddToCart;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        imgProduct = findViewById(R.id.imgProductDetail);
        tvName = findViewById(R.id.tvProductNameDetail);
        tvPrice = findViewById(R.id.tvProductPriceDetail);
        tvAuthor = findViewById(R.id.tvProductAuthorDetail);
        tvDescription = findViewById(R.id.tvProductDescDetail);
        btnAddToCart = findViewById(R.id.btnAddToCart);

        productId = getIntent().getIntExtra("PRODUCT_ID", -1);
        if (productId != -1) {
            fetchProductDetail(productId);
        } else {
            Toast.makeText(this, "Không tìm thấy sản phẩm", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void fetchProductDetail(int id) {
        ProductApi api = ApiClient.getClient().create(ProductApi.class);
        api.getProductDetail(id).enqueue(new Callback<ProductResponse>() {
            @Override
            public void onResponse(Call<ProductResponse> call, Response<ProductResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().products != null && !response.body().products.isEmpty()) {
                    Product p = response.body().products.get(0);
                    tvName.setText(p.name_product);
                    tvPrice.setText(p.price + " đ");
                    tvAuthor.setText(p.author != null ? p.author : "");
                    tvDescription.setText(p.text_product != null ? p.text_product : "");
                    Glide.with(ProductDetailActivity.this)
                            .load("http://10.0.2.2:3000" + p.image_product)
                            .placeholder(R.drawable.ic_launcher_background)
                            .into(imgProduct);

                    // Đặt sự kiện click cho nút "Thêm vào giỏ hàng" tại đây
                    btnAddToCart.setOnClickListener(v -> {
                        int quantity = 1;
                        int idLogin = getSharedPreferences("user_prefs", MODE_PRIVATE).getInt("id_login", -1);
                        if (idLogin <= 0) {
                            Toast.makeText(ProductDetailActivity.this, "Bạn cần đăng nhập!", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        CartRepository repo = new CartRepository();
                        repo.addToCart(p.id_product, quantity, idLogin).enqueue(new retrofit2.Callback<ApiResponse>() {
                            @Override
                            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                                if (response.isSuccessful() && response.body() != null && response.body().success) {
                                    Toast.makeText(ProductDetailActivity.this, "Đã thêm vào giỏ hàng!", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(ProductDetailActivity.this, "Thêm vào giỏ hàng thất bại!", Toast.LENGTH_SHORT).show();
                                }
                            }
                            @Override
                            public void onFailure(Call<ApiResponse> call, Throwable t) {
                                Toast.makeText(ProductDetailActivity.this, "Lỗi mạng!", Toast.LENGTH_SHORT).show();
                            }
                        });
                    });

                } else {
                    Toast.makeText(ProductDetailActivity.this, "Không tìm thấy sản phẩm", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onFailure(Call<ProductResponse> call, Throwable t) {
                Toast.makeText(ProductDetailActivity.this, "Lỗi mạng", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }
}