package com.example.tllttbdd.ui.account;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;
import com.example.tllttbdd.R;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class AccountDetailsActivity extends AppCompatActivity {

    private AccountViewModel viewModel; // Thêm biến này

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_details);

        // Hiển thị nút Back trên Toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Tài khoản & Cài đặt");
        }

        // --- THÊM LOGIC LẤY DỮ LIỆU VÀO ĐÂY ---
        // 1. Khởi tạo ViewModel cho Activity này
        viewModel = new ViewModelProvider(this).get(AccountViewModel.class);

        // 2. Lấy ID người dùng và ra lệnh cho ViewModel đi lấy thông tin
        SharedPreferences prefs = getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        int idLogin = prefs.getInt("id_login", -1);
        if (idLogin != -1) {
            viewModel.fetchProfile(idLogin);
        }
        // Giờ đây, ViewModel này sẽ chứa dữ liệu và chia sẻ cho các Fragment con

        // --- Phần thiết lập Tab giữ nguyên ---
        TabLayout tabLayout = findViewById(R.id.tab_layout);
        ViewPager2 viewPager = findViewById(R.id.view_pager);

        AccountTabsAdapter adapter = new AccountTabsAdapter(getSupportFragmentManager(), getLifecycle());
        viewPager.setAdapter(adapter);

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            if (position == 1) {
                tab.setText("Lịch sử");
            } else {
                tab.setText("Chức năng");
            }
        }).attach();
    }

    // Xử lý nút back trên Toolbar
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish(); // Đóng Activity hiện tại
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
