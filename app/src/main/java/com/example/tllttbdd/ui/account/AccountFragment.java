package com.example.tllttbdd.ui.account;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;
import com.example.tllttbdd.R;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class AccountFragment extends Fragment {

    private AccountViewModel viewModel;
    private TextView tvWelcomeName;
    private ImageView ivAvatar;
    private TabLayout tabLayout;
    private ViewPager2 viewPager;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate layout chính
        return inflater.inflate(R.layout.fragment_account, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Ánh xạ các view
        ivAvatar = view.findViewById(R.id.ivAvatar);
        tvWelcomeName = view.findViewById(R.id.tvWelcomeName);
        tabLayout = view.findViewById(R.id.tab_layout);
        viewPager = view.findViewById(R.id.view_pager);

        viewModel = new ViewModelProvider(this).get(AccountViewModel.class);

        // Lấy thông tin user từ SharedPreferences
        SharedPreferences prefs = requireActivity().getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        int idLogin = prefs.getInt("id_login", -1);
        // Lấy name_login đã lưu lúc đăng nhập để làm phương án dự phòng
        String nameLogin = prefs.getString("name_login", "Khách");

        if (idLogin != -1) {
            viewModel.fetchProfile(idLogin);
        }

        // Quan sát dữ liệu user từ ViewModel
        viewModel.getUser().observe(getViewLifecycleOwner(), user -> {
            // Ưu tiên hiển thị name_information, nếu không có thì hiển thị name_login
            if (user != null && user.name_information != null && !user.name_information.isEmpty()) {
                tvWelcomeName.setText(user.name_information);
            } else {
                tvWelcomeName.setText(nameLogin); // Hiển thị tên đăng nhập nếu tên đầy đủ rỗng
            }
        });

        // Thiết lập Adapter và các Tab
        AccountTabsAdapter adapter = new AccountTabsAdapter(getChildFragmentManager(), getLifecycle());
        viewPager.setAdapter(adapter);

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            if (position == 1) {
                tab.setText("Lịch sử");
            } else {
                tab.setText("Thống kê");
            }
        }).attach();

        tvWelcomeName.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), UserDetailsActivity.class);
            startActivity(intent);
        });

        TextView tvWelcomeName = view.findViewById(R.id.tvWelcomeName);
        ImageView ivArrow = view.findViewById(R.id.ivArrow); // Tìm ImageView mới

// Tạo một OnClickListener chung để tái sử dụng
        View.OnClickListener showDetailsListener = v -> {
            // Đặt logic mở màn hình UserDetailsActivity ở đây
            Intent intent = new Intent(getActivity(), UserDetailsActivity.class);
            // Bạn có thể cần gửi ID người dùng qua intent nếu cần
            // intent.putExtra("USER_ID", userId);
            startActivity(intent);
        };

// Gán cùng một hành động cho cả hai view
        tvWelcomeName.setOnClickListener(showDetailsListener);
        ivArrow.setOnClickListener(showDetailsListener);
    }
}
