package com.example.tllttbdd.ui.dashboard;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.tllttbdd.R;
import com.example.tllttbdd.data.model.Product;
import com.example.tllttbdd.data.model.ProductResponse;
import com.example.tllttbdd.data.network.ApiClient;
import com.example.tllttbdd.data.network.ProductApi;
import com.example.tllttbdd.ui.home.ProductAdapter;
import com.example.tllttbdd.ui.home.ProductDetailActivity;

import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductByCategoryActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ProductAdapter adapter;
    private List<Product> productList = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products_by_category);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String categoryName = getIntent().getStringExtra("CATEGORY_NAME");
        getSupportActionBar().setTitle(categoryName);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        // 🔧 KHỞI TẠO RECYCLERVIEW VÀ ADAPTER
        recyclerView = findViewById(R.id.recyclerProductsByCategory);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        adapter = new ProductAdapter(productList);
        adapter.setOnItemClickListener(product -> {
            Intent intent = new Intent(ProductByCategoryActivity.this, ProductDetailActivity.class);
            intent.putExtra("PRODUCT_ID", product.id_product);
            startActivity(intent);
        });
        recyclerView.setAdapter(adapter);

        int categoryId = getIntent().getIntExtra("CATEGORY_ID", -1);
        if (categoryId != -1) {
            fetchProductsByCategory(categoryId);
        }
    }


    private void fetchProductsByCategory(int categoryId) {
        ProductApi api = ApiClient.getClient().create(ProductApi.class);
        api.getProductsByCategory(categoryId).enqueue(new Callback<ProductResponse>() {
            @Override
            public void onResponse(Call<ProductResponse> call, Response<ProductResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    productList.clear();
                    productList.addAll(response.body().products);
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(ProductByCategoryActivity.this, "Không có sản phẩm", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<ProductResponse> call, Throwable t) {
                Toast.makeText(ProductByCategoryActivity.this, "Lỗi mạng", Toast.LENGTH_SHORT).show();
            }
        });
    }
}