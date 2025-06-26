// File 1: AccountFragment.java (File chính đã được chỉnh sửa)
// Nhiệm vụ của nó BÂY GIỜ chỉ là thiết lập UI chính (thông tin + các tab).
package com.example.tllttbdd.ui.account;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
    private TextView tvName, tvPhone, tvEmail, tvDob;
    private TabLayout tabLayout;
    private ViewPager2 viewPager;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate layout chính mới
        return inflater.inflate(R.layout.fragment_account, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Ánh xạ các view của layout chính
        tvName = view.findViewById(R.id.tvName);
        tvPhone = view.findViewById(R.id.tvPhone);
        tvEmail = view.findViewById(R.id.tvEmail);
        tvDob = view.findViewById(R.id.tvDob);
        tabLayout = view.findViewById(R.id.tab_layout);
        viewPager = view.findViewById(R.id.view_pager);

        // Khởi tạo ViewModel
        // ViewModel giờ sẽ được chia sẻ cho cả Fragment này và các Fragment con trong tab
        viewModel = new ViewModelProvider(this).get(AccountViewModel.class);

        // Lấy thông tin user và cập nhật UI
        SharedPreferences prefs = requireActivity().getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        int idLogin = prefs.getInt("id_login", -1);

        // Chỉ gọi fetchProfile nếu idLogin hợp lệ
        if (idLogin != -1) {
            viewModel.fetchProfile(idLogin);
        }

        viewModel.getUser().observe(getViewLifecycleOwner(), user -> {
            if (user != null) {
                // Fragment này giờ chịu trách nhiệm cập nhật phần thông tin
                tvName.setText("Tên: " + user.name_information);
                tvPhone.setText("Số điện thoại: " + user.phone_information);
                tvEmail.setText("Email: " + user.email);
                tvDob.setText("Ngày sinh: " + user.date_of_birth);
            }
        });

        // Thiết lập Adapter và các Tab
        AccountTabsAdapter adapter = new AccountTabsAdapter(getChildFragmentManager(), getLifecycle());
        viewPager.setAdapter(adapter);

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            if (position == 1) {
                tab.setText("Tab 2"); // Ví dụ tên cho Tab 2
            } else {
                tab.setText("Chức năng"); // Tên cho Tab 1
            }
        }).attach();
    }
}

