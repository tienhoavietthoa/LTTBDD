package com.example.tllttbdd.ui.home;

import android.content.Intent; // Import Intent
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.tllttbdd.R;
import com.example.tllttbdd.data.model.Product;
import com.example.tllttbdd.data.model.ProductSearchResponse;
import com.example.tllttbdd.data.network.ApiClient;
import com.example.tllttbdd.data.network.ProductApi;

import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

// ProductSearchActivity sẽ implement OnItemClickListener của ProductAdapter
public class ProductSearchActivity extends AppCompatActivity implements ProductAdapter.OnItemClickListener {
    private RecyclerView recyclerView;
    private ProductAdapter productAdapter;
    private TextView tvSearchResult;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_search);

        // Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Kết quả tìm kiếm");
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        // Ánh xạ View
        recyclerView = findViewById(R.id.recyclerProductSearch);
        tvSearchResult = findViewById(R.id.tvSearchResult);

        // Khởi tạo Adapter và RecyclerView một lần duy nhất
        setupRecyclerView();

        // Nhận từ khóa tìm kiếm
        String searchQuery = getIntent().getStringExtra("SEARCH_QUERY");

        Log.d("SearchDebug", "Từ khóa nhận được: " + searchQuery);

        if (searchQuery != null && !searchQuery.isEmpty()) {
            tvSearchResult.setText("Kết quả tìm kiếm cho: '" + searchQuery + "'");
            searchProducts(searchQuery);
        } else {
            tvSearchResult.setText("Vui lòng nhập từ khóa để tìm kiếm.");
            // Log khi từ khóa rỗng
            Log.d("SearchDebug", "Search query is null or empty.");
        }
    }

    private void setupRecyclerView() {
        productAdapter = new ProductAdapter(new ArrayList<>());
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerView.setAdapter(productAdapter);
        // Gán listener cho adapter
        productAdapter.setOnItemClickListener(this);
    }

    private void searchProducts(String query) {
        Log.d("SearchDebug", "Calling searchProducts API for query: " + query); // <-- THÊM LOG TRƯỚC KHI GỌI API
        ProductApi api = ApiClient.getClient().create(ProductApi.class);
        api.searchProducts(query).enqueue(new Callback<ProductSearchResponse>() {
            @Override
            public void onResponse(@NonNull Call<ProductSearchResponse> call, @NonNull Response<ProductSearchResponse> response) {
                Log.d("SearchDebug", "onResponse received for query: " + query + ". HTTP Code: " + response.code()); // <-- THÊM LOG
                if (response.isSuccessful() && response.body() != null) {
                    List<Product> products = response.body().products;
                    if (products == null || products.isEmpty()) {
                        Log.d("SearchDebug", "API returned no products for query: " + query); // <-- THÊM LOG
                        Toast.makeText(ProductSearchActivity.this, "Không tìm thấy sản phẩm nào", Toast.LENGTH_LONG).show();
                        productAdapter.setProducts(new ArrayList<>()); // Xóa kết quả cũ
                    } else {
                        Log.d("SearchDebug", "API returned " + products.size() + " products for query: " + query); // <-- THÊM LOG
                        productAdapter.setProducts(products);
                    }
                } else {
                    String errorBody = "";
                    try {
                        if (response.errorBody() != null) {
                            errorBody = response.errorBody().string();
                        }
                    } catch (Exception e) {
                        Log.e("SearchApiError", "Error reading errorBody: " + e.getMessage());
                    }
                    Log.e("SearchApiError", "onResponse: API call not successful. Code: " + response.code() + ", Message: " + response.message() + ", Error Body: " + errorBody); // <-- THÊM LOG CHI TIẾT
                    Toast.makeText(ProductSearchActivity.this, "Không tìm thấy sản phẩm. Lỗi: " + response.code(), Toast.LENGTH_SHORT).show();
                    productAdapter.setProducts(new ArrayList<>()); // Xóa kết quả cũ
                }
            }

            @Override
            public void onFailure(@NonNull Call<ProductSearchResponse> call, @NonNull Throwable t) {
                Toast.makeText(ProductSearchActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("SearchApiError", "onFailure: " + t.getMessage(), t); // <-- THÊM LOG STACK TRACE
                productAdapter.setProducts(new ArrayList<>()); // Xóa kết quả cũ
            }
        });
    }

    // --- Triển khai phương thức onItemClick từ ProductAdapter.OnItemClickListener ---
    @Override
    public void onItemClick(Product product) {
        // Khi một sản phẩm được click, mở ProductDetailActivity
        Intent intent = new Intent(ProductSearchActivity.this, ProductDetailActivity.class);
        // Truyền đối tượng Product qua Intent
        intent.putExtra("product_detail", product); // "product_detail" là một key bạn tự định nghĩa
        startActivity(intent);
    }
}
