package com.example.tllttbdd.ui.account;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.tllttbdd.R;

import java.text.DecimalFormat;

public class AccountActionsFragment extends Fragment {

    // Đổi tên biến cho nhất quán
    private AccountViewModel accountViewModel;
    private TextView tvTotalOrders, tvTotalSpent;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Khởi tạo AccountViewModel
        accountViewModel = new ViewModelProvider(requireActivity()).get(AccountViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_tab_actions, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvTotalOrders = view.findViewById(R.id.tvTotalOrders);
        tvTotalSpent = view.findViewById(R.id.tvTotalSpent);

        observeViewModel();
    }

    private void observeViewModel() {
        // SỬA LỖI TẠI ĐÂY: Đổi từ getOrderList() thành getOrders()
        accountViewModel.getOrders().observe(getViewLifecycleOwner(), orders -> {
            if (orders != null) {
                tvTotalOrders.setText(String.valueOf(orders.size()));
            }
        });

        // Lắng nghe tổng tiền
        accountViewModel.getTotalSpent().observe(getViewLifecycleOwner(), total -> {
            if (total != null) {
                DecimalFormat formatter = new DecimalFormat("#,###");
                String formattedTotal = formatter.format(total) + "đ";
                tvTotalSpent.setText(formattedTotal);
            }
        });

        // Lắng nghe lỗi (nếu có)
        // Giả sử AccountRepository của bạn có LiveData cho lỗi
        /*
        accountViewModel.getErrorMessage().observe(getViewLifecycleOwner(), error -> {
            if (error != null && !error.isEmpty()) {
                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
            }
        });
        */
    }
}
