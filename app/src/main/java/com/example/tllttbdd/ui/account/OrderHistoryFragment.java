package com.example.tllttbdd.ui.account;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.example.tllttbdd.R;
import com.example.tllttbdd.data.model.Order;
import com.example.tllttbdd.data.repository.OrderRepository;

import java.util.List;

public class OrderHistoryFragment extends Fragment implements OrderHistoryAdapter.OnOrderClickListener {
    private RecyclerView recyclerView;
    private OrderHistoryAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_order_history, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = view.findViewById(R.id.recyclerOrderHistory);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Lấy id_login từ SharedPreferences
        SharedPreferences prefs = requireActivity().getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        int idLogin = prefs.getInt("id_login", -1);

        if (idLogin != -1) {
            // Gọi API lấy danh sách đơn hàng (tạo OrderRepository và model Order tương ứng)
            OrderRepository.getInstance().fetchOrderHistory(idLogin, new OrderRepository.OrderHistoryCallback() {
                @Override
                public void onSuccess(List<Order> orders) {
                    adapter = new OrderHistoryAdapter(orders, OrderHistoryFragment.this);
                    recyclerView.setAdapter(adapter);
                }
                @Override
                public void onError(String error) {
                    Toast.makeText(getContext(), "Lỗi: " + error, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    // Trong OrderHistoryFragment
    @Override
    public void onOrderClick(Order order) {
        Intent intent = new Intent(getActivity(), OrderDetailActivity.class);
        intent.putExtra("ORDER_ID", order.id_order);
        startActivity(intent);
    }
}