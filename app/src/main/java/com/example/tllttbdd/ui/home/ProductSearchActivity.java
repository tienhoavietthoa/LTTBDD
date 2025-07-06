package com.example.tllttbdd.ui.home;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
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

public class ProductSearchActivity extends AppCompatActivity implements ProductAdapter.OnItemClickListener {

    private RecyclerView recyclerView;
    private ProductAdapter productAdapter;
    private TextView tvSearchResult;
    private EditText searchBox;
    private ImageButton btnSearch, btnBack;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_search);

        initViews();
        setupListeners();

        String searchQuery = getIntent().getStringExtra("SEARCH_QUERY");
        if (searchQuery != null && !searchQuery.isEmpty()) {
            searchBox.setText(searchQuery);
            tvSearchResult.setText("Kết quả liên quan");
            searchProducts(searchQuery);
        } else {
            tvSearchResult.setText("Vui lòng nhập từ khóa để tìm kiếm.");
        }
    }

    private void initViews() {
        recyclerView = findViewById(R.id.recyclerProductSearch);
        tvSearchResult = findViewById(R.id.tvSearchResult);
        searchBox = findViewById(R.id.search_box);
        btnSearch = findViewById(R.id.btn_search);
        btnBack = findViewById(R.id.btnBack);

        setupRecyclerView();
    }

    private void setupRecyclerView() {
        productAdapter = new ProductAdapter(new ArrayList<>());
        recyclerView.setAdapter(productAdapter);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        productAdapter.setOnItemClickListener(this);
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());
        btnSearch.setOnClickListener(v -> performSearch());
        searchBox.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                performSearch();
                return true;
            }
            return false;
        });
    }

    private void performSearch() {
        String query = searchBox.getText().toString().trim();
        if (!query.isEmpty()) {
            hideKeyboard();
            tvSearchResult.setText("Kết quả liên quan");
            searchProducts(query);
        } else {
            Toast.makeText(this, "Vui lòng nhập từ khóa", Toast.LENGTH_SHORT).show();
        }
    }

    private void searchProducts(String query) {
        ProductApi api = ApiClient.getClient().create(ProductApi.class);
        api.searchProducts(query).enqueue(new Callback<ProductSearchResponse>() {
            @Override
            public void onResponse(@NonNull Call<ProductSearchResponse> call, @NonNull Response<ProductSearchResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Product> products = response.body().products;
                    if (products == null || products.isEmpty()) {
                        Toast.makeText(ProductSearchActivity.this, "Không tìm thấy sản phẩm nào", Toast.LENGTH_LONG).show();
                        productAdapter.setProducts(new ArrayList<>());
                    } else {
                        productAdapter.setProducts(products);
                    }
                } else {
                    Toast.makeText(ProductSearchActivity.this, "Tìm kiếm thất bại", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ProductSearchResponse> call, @NonNull Throwable t) {
                Toast.makeText(ProductSearchActivity.this, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null && getCurrentFocus() != null) {
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }

    @Override
    public void onItemClick(Product product) {
        Intent intent = new Intent(this, ProductDetailActivity.class);
        intent.putExtra("PRODUCT_ID", product.id_product);
        startActivity(intent);
    }
}