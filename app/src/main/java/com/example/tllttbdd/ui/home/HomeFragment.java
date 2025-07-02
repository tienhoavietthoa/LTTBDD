package com.example.tllttbdd.ui.home;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
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
import com.google.android.material.bottomnavigation.BottomNavigationView;
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

    // BƯỚC 1: KHAI BÁO LAUNCHER ĐỂ MỞ MÀN HÌNH CHI TIẾT VÀ NHẬN KẾT QUẢ
    private final ActivityResultLauncher<Intent> productDetailLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    String navigateTo = result.getData().getStringExtra("NAVIGATE_TO");
                    if ("CONTACTS".equals(navigateTo)) {
                        // Yêu cầu MainActivity chuyển đến tab Liên hệ (NotificationsFragment)
                        BottomNavigationView navView = requireActivity().findViewById(R.id.nav_view);
                        if (navView != null) {
                            navView.setSelectedItemId(R.id.navigation_notifications);
                        }
                    }
                }
            }
    );

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupRecyclerView();
        connectBannerAndIndicator();
        setupSwipeToRefresh();
        fetchData();
    }

    private void setupRecyclerView() {
        productAdapter = new ProductAdapter(new ArrayList<>());
        binding.productRecycler.setLayoutManager(new GridLayoutManager(getContext(), 2));
        binding.productRecycler.setAdapter(productAdapter);

        // BƯỚC 2: GÁN SỰ KIỆN CLICK CHO ADAPTER ĐỂ MỞ MÀN HÌNH CHI TIẾT
        productAdapter.setOnItemClickListener(product -> {
            Intent intent = new Intent(requireContext(), ProductDetailActivity.class);
            intent.putExtra("PRODUCT_ID", product.id_product);
            // Dùng launcher để mở Activity
            productDetailLauncher.launch(intent);
        });
    }

    // ... các hàm còn lại (connectBannerAndIndicator, setupSwipeToRefresh, fetchData, onDestroyView) giữ nguyên ...

    private void connectBannerAndIndicator() {
        List<Integer> banners = Arrays.asList(R.drawable.banner1, R.drawable.banner2, R.drawable.banner3);
        bannerAdapter = new BannerPagerAdapter(banners);
        binding.bannerPager.setAdapter(bannerAdapter);
        new TabLayoutMediator(binding.tabIndicator, binding.bannerPager, (tab, position) -> {}).attach();
    }

    private void setupSwipeToRefresh() {
        binding.swipeRefreshLayout.setOnRefreshListener(this::fetchData);
    }

    private void fetchData() {
        binding.swipeRefreshLayout.setRefreshing(true);
        HomeApi api = ApiClient.getClient().create(HomeApi.class);
        api.getAllCategories().enqueue(new Callback<CategoryResponse>() {
            @Override
            public void onResponse(@NonNull Call<CategoryResponse> call, @NonNull Response<CategoryResponse> response) {
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