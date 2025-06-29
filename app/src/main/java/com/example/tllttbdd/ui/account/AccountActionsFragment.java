package com.example.tllttbdd.ui.account;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.tllttbdd.R;

public class AccountActionsFragment extends Fragment {

    private AccountViewModel viewModel;
    private TextView tvTotalOrders;
    private TextView tvTotalSpent;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Sử dụng layout thống kê
        return inflater.inflate(R.layout.fragment_tab_actions, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(AccountViewModel.class);

        // Chỉ tìm các TextView có trong layout
        tvTotalOrders = view.findViewById(R.id.tvTotalOrders);
        tvTotalSpent = view.findViewById(R.id.tvTotalSpent);

        // Lắng nghe dữ liệu để cập nhật
        setupObservers();
    }

    private void setupObservers() {
        viewModel.getUser().observe(getViewLifecycleOwner(), user -> {
            if (user != null) {
                // TODO: Cập nhật dữ liệu thật từ user object
                tvTotalOrders.setText("15");
                tvTotalSpent.setText("5,430,000đ");
            }
        });
    }
}