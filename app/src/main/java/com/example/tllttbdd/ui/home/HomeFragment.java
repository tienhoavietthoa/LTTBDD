package com.example.tllttbdd.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager2.widget.ViewPager2;

import com.example.tllttbdd.R;
import com.example.tllttbdd.data.model.Category;
import com.example.tllttbdd.data.model.CategoryResponse;
import com.example.tllttbdd.data.model.Product;
import com.example.tllttbdd.data.network.ApiClient;
import com.example.tllttbdd.data.network.HomeApi;
import com.example.tllttbdd.databinding.FragmentHomeBinding;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private ProductAdapter productAdapter;
    private BannerPagerAdapter bannerAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Cấu hình các thành phần của giao diện
        setupRecyclerView();
        connectBannerAndIndicator();
        setupSwipeToRefresh();

        // Tải dữ liệu lần đầu tiên
        fetchData();
    }

    private void setupRecyclerView() {
        // Khởi tạo Adapter với danh sách rỗng để tránh lỗi NullPointerException
        productAdapter = new ProductAdapter(new ArrayList<>());
        binding.productRecycler.setLayoutManager(new GridLayoutManager(getContext(), 2));
        binding.productRecycler.setAdapter(productAdapter);
    }

    private void connectBannerAndIndicator() {
        List<Integer> banners = Arrays.asList(
                R.drawable.banner1, R.drawable.banner2, R.drawable.banner3
        );
        bannerAdapter = new BannerPagerAdapter(banners);
        binding.bannerPager.setAdapter(bannerAdapter);

        // Sử dụng TabLayoutMediator để kết nối ViewPager2 với TabLayout (dấu chấm chỉ báo)
        new TabLayoutMediator(binding.tabIndicator, binding.bannerPager, (tab, position) -> {
            // Không cần làm gì ở đây vì chúng ta đã tạo kiểu bằng XML
        }).attach();
    }

    private void setupSwipeToRefresh() {
        // Gán sự kiện cho hành động vuốt xuống để làm mới
        binding.swipeRefreshLayout.setOnRefreshListener(() -> fetchData());
    }

    private void fetchData() {
        // Hiển thị vòng xoay loading của SwipeRefreshLayout
        binding.swipeRefreshLayout.setRefreshing(true);

        HomeApi api = ApiClient.getClient().create(HomeApi.class);
        api.getAllCategories().enqueue(new Callback<CategoryResponse>() {
            @Override
            public void onResponse(@NonNull Call<CategoryResponse> call, @NonNull Response<CategoryResponse> response) {
                // Ẩn vòng xoay loading khi có kết quả
                binding.swipeRefreshLayout.setRefreshing(false);

                if (response.isSuccessful() && response.body() != null) {
                    List<Category> categories = response.body().categories;
                    if (categories != null && !categories.isEmpty()) {
                        List<Product> allProducts = new ArrayList<>();
                        for (Category cat : categories) {
                            if (cat.products != null) {
                                allProducts.addAll(cat.products);
                            }
                        }
                        // Cập nhật dữ liệu cho adapter
                        productAdapter.setProducts(allProducts);
                    } else {
                        Toast.makeText(getContext(), "Không có dữ liệu sản phẩm", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getContext(), "Lấy dữ liệu thất bại", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<CategoryResponse> call, @NonNull Throwable t) {
                // Ẩn vòng xoay loading khi có lỗi
                binding.swipeRefreshLayout.setRefreshing(false);
                Toast.makeText(getContext(), "Lỗi mạng: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}