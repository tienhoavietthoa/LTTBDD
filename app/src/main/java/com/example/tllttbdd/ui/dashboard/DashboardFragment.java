package com.example.tllttbdd.ui.dashboard;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.tllttbdd.data.model.Category;
import com.example.tllttbdd.data.model.CategoryResponse;
import com.example.tllttbdd.data.network.ApiClient;
import com.example.tllttbdd.data.network.HomeApi;
import com.example.tllttbdd.databinding.FragmentDashboardBinding;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DashboardFragment extends Fragment {
    private FragmentDashboardBinding binding;
    private CategoryAdapter adapter;
    private List<Category> categoryList = new ArrayList<>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        binding.recyclerCategories.setLayoutManager(new GridLayoutManager(getContext(),2));
        adapter = new CategoryAdapter(categoryList, category -> {
            // Khi nhấn vào 1 loại, mở ProductByCategoryActivity
            Intent intent = new Intent(getContext(), ProductByCategoryActivity.class);
            intent.putExtra("CATEGORY_ID", category.id_category);
            intent.putExtra("CATEGORY_NAME", category.name_category);
            startActivity(intent);
        });
        binding.recyclerCategories.setAdapter(adapter);
        fetchCategories();
        return binding.getRoot();
    }

    private void fetchCategories() {
        HomeApi api = ApiClient.getClient().create(HomeApi.class);
        api.getAllCategories().enqueue(new Callback<CategoryResponse>() {
            @Override
            public void onResponse(Call<CategoryResponse> call, Response<CategoryResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    categoryList.clear();
                    categoryList.addAll(response.body().categories);
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(getContext(), "Không lấy được danh mục", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<CategoryResponse> call, Throwable t) {
                Toast.makeText(getContext(), "Lỗi mạng", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}