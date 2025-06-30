package com.example.tllttbdd.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.recyclerview.widget.LinearLayoutManager;


import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.example.tllttbdd.R;
import com.example.tllttbdd.data.model.Category;
import com.example.tllttbdd.data.model.CategoryResponse;
import com.example.tllttbdd.data.model.Product;
import com.example.tllttbdd.data.network.ApiClient;
import com.example.tllttbdd.data.network.HomeApi;
import com.example.tllttbdd.databinding.FragmentHomeBinding;
import com.example.tllttbdd.ui.home.ProductAdapter;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {
    private FragmentHomeBinding binding;
    private ProductAdapter productAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Banner quảng cáo
        ViewPager2 bannerPager = binding.bannerPager;
        List<Integer> banners = Arrays.asList(
                R.drawable.banner1, R.drawable.banner2, R.drawable.banner3
        );
        BannerPagerAdapter adapter = new BannerPagerAdapter(banners);
        bannerPager.setAdapter(adapter);

        // Khởi tạo RecyclerView
        binding.productRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        productAdapter = new ProductAdapter(null);
        binding.productRecycler.setAdapter(productAdapter);

        // Gọi API lấy categories
        HomeApi api = ApiClient.getClient().create(HomeApi.class);
        api.getAllCategories().enqueue(new Callback<CategoryResponse>() {
            // ...existing code...
            @Override
            public void onResponse(Call<CategoryResponse> call, Response<CategoryResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Category> categories = response.body().categories;
                    if (categories != null && !categories.isEmpty()) {
                        // Gộp tất cả sản phẩm của mọi category
                        List<Product> allProducts = new ArrayList<>();
                        for (Category cat : categories) {
                            if (cat.products != null) allProducts.addAll(cat.products);
                        }
                        binding.textHome.setText("Tất cả sản phẩm");
                        productAdapter.setProducts(allProducts);
                    } else {
                        binding.textHome.setText("Không có dữ liệu");
                        productAdapter.setProducts(null);
                    }
                } else {
                    Toast.makeText(getContext(), "Không có dữ liệu", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<CategoryResponse> call, Throwable t) {
                Toast.makeText(getContext(), "Lỗi mạng: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}