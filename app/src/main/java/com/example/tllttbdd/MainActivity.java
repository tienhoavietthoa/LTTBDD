package com.example.tllttbdd;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import com.example.tllttbdd.ui.account.AccountViewModel;
import androidx.lifecycle.ViewModelProvider;
import android.os.Bundle;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.tllttbdd.ui.home.ProductSearchActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import com.example.tllttbdd.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    // Khai báo đầy đủ các view có trong layout của bạn
    private LinearLayout topBar;
    private EditText searchBox;
    private ImageButton btnSearch;
    private ImageView logo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // --- Cài đặt cho Navigation Component và BottomNavigationView ---
        BottomNavigationView navView = findViewById(R.id.nav_view);
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications,
                R.id.navigation_account, R.id.navigation_cart) // Sử dụng ID fragment từ nav_graph của bạn
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupWithNavController(binding.navView, navController);

        // === Ánh xạ các View trên top bar ===
        topBar = findViewById(R.id.top_bar);
        logo = findViewById(R.id.logo);
        searchBox = findViewById(R.id.search_box);
        btnSearch = findViewById(R.id.btn_search);

        // === Gán sự kiện cho thanh tìm kiếm ===
        setupSearchListeners();
    }

    /**
     * Gán các sự kiện lắng nghe cho thanh tìm kiếm.
     */
    private void setupSearchListeners() {
        // Cách 1: Bấm vào icon kính lúp
        btnSearch.setOnClickListener(v -> performSearch());

        // Cách 2 (Khuyên dùng): Bấm nút "Tìm kiếm" trên bàn phím ảo
        searchBox.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                performSearch();
                return true; // Đã xử lý sự kiện
            }
            return false; // Chưa xử lý
        });
    }

    /**
     * Lấy từ khóa, kiểm tra và bắt đầu tìm kiếm.
     */
    private void performSearch() {
        String query = searchBox.getText().toString().trim();
        if (!query.isEmpty()) {
            // Ẩn bàn phím để có trải nghiệm tốt hơn
            hideKeyboard();
            // Mở màn hình kết quả
            openSearchResultActivity(query);
        } else {
            Toast.makeText(this, "Vui lòng nhập từ khóa tìm kiếm", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Mở Activity kết quả tìm kiếm và truyền từ khóa đi.
     */
    private void openSearchResultActivity(String query) {
        Intent intent = new Intent(MainActivity.this, ProductSearchActivity.class);
        // Sửa lại key cho nhất quán, ví dụ: SEARCH_QUERY
        intent.putExtra("SEARCH_QUERY", query);
        startActivity(intent);
    }

    /**
     * Hàm tiện ích để ẩn bàn phím.
     */
    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        View view = getCurrentFocus();
        if (view == null) {
            view = new View(this);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Ví dụ: tự động tải lại thống kê đơn hàng (nếu bạn dùng ViewModel scoped theo Activity)
        AccountViewModel viewModel = new ViewModelProvider(this).get(AccountViewModel.class);
        // Lấy idLogin của người dùng hiện tại ở đây.
        // Tạm thời dùng 1 làm ví dụ, bạn cần thay thế bằng ID người dùng thực tế.
        int currentUserId = 1; // <-- Thay thế bằng ID người dùng thực tế từ SharedPreferences hoặc Auth
        viewModel.loadOrdersFromRepository(currentUserId);  // Đã đổi tên phương thức và thêm tham số userId

        // Optional: clear search nếu bạn muốn mỗi lần quay về sẽ xóa nội dung cũ
        // searchBox.setText("");
    }
}
