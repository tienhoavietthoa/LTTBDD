package com.example.tllttbdd.ui.home;

import android.os.Bundle;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.tllttbdd.R;
import com.example.tllttbdd.data.model.Product;
import com.example.tllttbdd.data.model.ProductSearchResponse;
import com.example.tllttbdd.data.network.ApiClient;
import com.example.tllttbdd.data.network.ProductApi;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductSearchActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ProductAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_search);

        recyclerView = findViewById(R.id.recyclerProductSearch);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        String query = getIntent().getStringExtra("QUERY");
        if (query != null && !query.isEmpty()) {
            searchProducts(query);
        }
    }

    private void searchProducts(String query) {
        ProductApi api = ApiClient.getClient().create(ProductApi.class);
        api.searchProducts(query).enqueue(new Callback<ProductSearchResponse>() {
            @Override
            public void onResponse(Call<ProductSearchResponse> call, Response<ProductSearchResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Product> products = response.body().products;
                    adapter = new ProductAdapter(products);
                    recyclerView.setAdapter(adapter);
                } else {
                    Toast.makeText(ProductSearchActivity.this, "Không tìm thấy sản phẩm", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<ProductSearchResponse> call, Throwable t) {
                Toast.makeText(ProductSearchActivity.this, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
            }
        });
    }
}