package com.example.tllttbdd;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.tllttbdd.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private LinearLayout topBar;
    private EditText searchBox;
    private ImageButton btnSearch;
    private ImageView logo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        BottomNavigationView navView = findViewById(R.id.nav_view);

        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications,
                R.id.navigation_account, R.id.navigation_cart)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupWithNavController(binding.navView, navController);

        // Lấy các view từ layout mới (chỉ topBar, logo, searchBox, btnSearch)
        topBar = findViewById(R.id.top_bar);
        logo = findViewById(R.id.logo);
        searchBox = findViewById(R.id.search_box);
        btnSearch = findViewById(R.id.btn_search);

        // LUÔN hiện thanh tìm kiếm & các view liên quan
        if (topBar != null) topBar.setVisibility(android.view.View.VISIBLE);
        if (searchBox != null) searchBox.setVisibility(android.view.View.VISIBLE);

        // Xử lý sự kiện tìm kiếm khi bấm nút tìm kiếm
        btnSearch.setOnClickListener(v -> {
            String query = searchBox.getText().toString().trim();
            // TODO: Xử lý tìm kiếm với giá trị query
        });
    }
}