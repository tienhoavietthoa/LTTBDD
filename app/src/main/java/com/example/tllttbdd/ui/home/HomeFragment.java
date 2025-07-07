package com.example.tllttbdd.ui.home;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import com.example.tllttbdd.R;
import com.example.tllttbdd.data.model.Category;
import com.example.tllttbdd.data.model.CategoryResponse;
import com.example.tllttbdd.data.model.HomeResponse;
import com.example.tllttbdd.data.model.Product;
import com.example.tllttbdd.data.network.ApiClient;
import com.example.tllttbdd.data.network.HomeApi;
import com.example.tllttbdd.databinding.FragmentHomeBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private ProductAdapter productAdapter;
    private BannerPagerAdapter bannerAdapter;
    private List<Product> allProducts = new ArrayList<>();

    private final ActivityResultLauncher<Intent> productDetailLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    String navigateTo = result.getData().getStringExtra("NAVIGATE_TO");
                    if ("CONTACTS".equals(navigateTo)) {
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
        setupPriceFilterSpinner();
        fetchData();
    }

    private void setupRecyclerView() {
        productAdapter = new ProductAdapter(new ArrayList<>());
        binding.productRecycler.setLayoutManager(new GridLayoutManager(getContext(), 2));
        binding.productRecycler.setAdapter(productAdapter);
        productAdapter.setOnItemClickListener(product -> {
            Intent intent = new Intent(requireContext(), ProductDetailActivity.class);
            intent.putExtra("PRODUCT_ID", product.id_product);
            productDetailLauncher.launch(intent);
        });
    }

    private void setupPriceFilterSpinner() {
        List<String> priceRanges = new ArrayList<>(Arrays.asList("Tất cả giá", "Dưới 100,000đ", "100,000đ - 200,000đ", "Trên 200,000đ"));
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, priceRanges);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.priceFilterSpinner.setAdapter(adapter);

        binding.priceFilterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                filterProductList(position);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void filterProductList(int selection) {
        if (allProducts.isEmpty()) return;

        List<Product> filteredList;
        try {
            switch (selection) {
                case 1: // Dưới 100,000đ
                    filteredList = allProducts.stream()
                            .filter(p -> Double.parseDouble(p.price) < 100000)
                            .collect(Collectors.toList());
                    break;
                case 2: // 100,000đ - 200,000đ
                    filteredList = allProducts.stream()
                            .filter(p -> {
                                double price = Double.parseDouble(p.price);
                                return price >= 100000 && price <= 200000;
                            })
                            .collect(Collectors.toList());
                    break;
                case 3: // Trên 200,000đ
                    filteredList = allProducts.stream()
                            .filter(p -> Double.parseDouble(p.price) > 200000)
                            .collect(Collectors.toList());
                    break;
                default: // Case 0: Tất cả giá
                    filteredList = new ArrayList<>(allProducts);
                    break;
            }
            productAdapter.setProducts(filteredList);
        } catch (NumberFormatException e) {
            Toast.makeText(getContext(), "Lỗi định dạng giá sản phẩm", Toast.LENGTH_SHORT).show();
        }
    }

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
        api.getHomeData().enqueue(new Callback<HomeResponse>() {
            @Override
            public void onResponse(@NonNull Call<HomeResponse> call, @NonNull Response<HomeResponse> response) {
                binding.swipeRefreshLayout.setRefreshing(false);
                if (response.isSuccessful() && response.body() != null && response.body().products != null) {
                    allProducts.clear();
                    allProducts.addAll(response.body().products);
                    productAdapter.setProducts(allProducts);
                    binding.priceFilterSpinner.setSelection(0);
                } else {
                    Toast.makeText(getContext(), "Lấy dữ liệu thất bại", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(@NonNull Call<HomeResponse> call, @NonNull Throwable t) {
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