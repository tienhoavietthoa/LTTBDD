package com.example.tllttbdd.ui.account;

import android.content.Context;
import android.content.Intent; // <-- Thêm import này
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tllttbdd.R;
import com.example.tllttbdd.data.model.Order;
import com.example.tllttbdd.ui.account.OrderHistoryAdapter;
import com.example.tllttbdd.ui.account.OrderDetailActivity; // <-- Thêm import này cho OrderDetailActivity

import java.util.ArrayList;
import java.util.List;

public class OrderHistoryFragment extends Fragment implements OrderHistoryAdapter.OnOrderClickListener {

    private AccountViewModel viewModel;
    private RecyclerView rvOrders;
    private TextView tvEmptyOrders;
    private OrderHistoryAdapter adapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(AccountViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_tab_placeholder, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rvOrders = view.findViewById(R.id.rvOrders);
        tvEmptyOrders = view.findViewById(R.id.tvEmptyOrders);

        rvOrders.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new OrderHistoryAdapter(new ArrayList<>(), this);
        rvOrders.setAdapter(adapter);

        observeViewModel();

        SharedPreferences prefs = requireActivity().getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        int userId = prefs.getInt("id_login", -1);

        if (userId != -1) {
            Log.d("OrderHistoryFragment", "Fetching order history for userId: " + userId);
            viewModel.fetchOrderHistory(userId);
        } else {
            Log.e("OrderHistoryFragment", "User ID not found in SharedPreferences, cannot fetch order history.");
            tvEmptyOrders.setText("Vui lòng đăng nhập để xem lịch sử đơn hàng.");
            tvEmptyOrders.setVisibility(View.VISIBLE);
            rvOrders.setVisibility(View.GONE);
        }
    }

    private void observeViewModel() {
        viewModel.getOrders().observe(getViewLifecycleOwner(), orders -> {
            if (orders != null && !orders.isEmpty()) {
                Log.d("OrderHistoryFragment", "Orders received: " + orders.size() + ". Updating adapter.");
                rvOrders.setVisibility(View.VISIBLE);
                tvEmptyOrders.setVisibility(View.GONE);
                adapter.setOrders(orders);
            } else {
                Log.d("OrderHistoryFragment", "No orders or null orders. Hiding RecyclerView, showing empty message.");
                rvOrders.setVisibility(View.GONE);
                tvEmptyOrders.setVisibility(View.VISIBLE);
                tvEmptyOrders.setText("Bạn chưa có đơn hàng nào.");
                adapter.setOrders(new ArrayList<>());
            }
        });

        viewModel.getErrorMessage().observe(getViewLifecycleOwner(), errorMessage -> {
            if (errorMessage != null && !errorMessage.isEmpty()) {
                Log.e("OrderHistoryFragment", "Error fetching orders: " + errorMessage);
                Toast.makeText(getContext(), "Lỗi tải lịch sử đơn hàng: " + errorMessage, Toast.LENGTH_LONG).show();
                tvEmptyOrders.setText("Lỗi tải dữ liệu: " + errorMessage);
                tvEmptyOrders.setVisibility(View.VISIBLE);
                rvOrders.setVisibility(View.GONE);
                viewModel.clearErrorMessage();
            }
        });
    }

    @Override
    public void onOrderClick(Order order) {
        Intent intent = new Intent(getContext(), OrderDetailActivity.class);
        intent.putExtra("ORDER_ID", order.id_order); // Truyền ID đơn hàng sang OrderDetailActivity
        startActivity(intent);
    }
}