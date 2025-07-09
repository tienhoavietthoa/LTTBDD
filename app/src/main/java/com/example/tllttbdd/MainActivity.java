package com.example.tllttbdd;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.tllttbdd.databinding.ActivityMainBinding;
import com.example.tllttbdd.ui.account.AccountViewModel;
import com.example.tllttbdd.ui.chatbox.ChatboxActivity;
import com.example.tllttbdd.ui.home.ProductSearchActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private LinearLayout topBar;
    private EditText searchBox;
    private ImageButton btnSearch;
    private ImageView logo;
    private ImageView btnOpenChat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        BottomNavigationView navView = findViewById(R.id.nav_view);
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home,
                R.id.navigation_dashboard,
                R.id.navigation_notifications,
                R.id.navigation_account,
                R.id.navigation_cart
        ).build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupWithNavController(binding.navView, navController);

        topBar = findViewById(R.id.top_bar);
        logo = findViewById(R.id.logo);
        searchBox = findViewById(R.id.search_box);
        btnSearch = findViewById(R.id.btn_search);
        btnOpenChat = findViewById(R.id.btnOpenChat);

        setupSearchListeners();
        setupDraggableChatButton();
        setupChatButtonClick();
    }

    private void setupChatButtonClick() {
        btnOpenChat.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ChatboxActivity.class);
            startActivity(intent);
        });
    }

    private void setupDraggableChatButton() {
        btnOpenChat.setOnTouchListener(new View.OnTouchListener() {
            float dX = 0f, dY = 0f;

            @Override
            public boolean onTouch(View view, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        dX = view.getX() - event.getRawX();
                        dY = view.getY() - event.getRawY();
                        break;
                    case MotionEvent.ACTION_UP:
                        view.performClick(); // đảm bảo onClick hoạt động
                        break;
                    case MotionEvent.ACTION_MOVE:
                        view.animate()
                                .x(event.getRawX() + dX)
                                .y(event.getRawY() + dY)
                                .setDuration(0)
                                .start();
                        break;
                }
                return true;
            }
        });
    }

    private void setupSearchListeners() {
        btnSearch.setOnClickListener(v -> performSearch());

        searchBox.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                performSearch();
                return true;
            }
            return false;
        });
    }

    private void performSearch() {
        String query = searchBox.getText().toString().trim();
        if (!query.isEmpty()) {
            hideKeyboard();
            openSearchResultActivity(query);
        } else {
            Toast.makeText(this, "Vui lòng nhập từ khóa tìm kiếm", Toast.LENGTH_SHORT).show();
        }
    }

    private void openSearchResultActivity(String query) {
        Intent intent = new Intent(MainActivity.this, ProductSearchActivity.class);
        intent.putExtra("SEARCH_QUERY", query);
        startActivity(intent);
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        View view = getCurrentFocus();
        if (view == null) view = new View(this);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @Override
    protected void onResume() {
        super.onResume();
        AccountViewModel viewModel = new ViewModelProvider(this).get(AccountViewModel.class);
        int currentUserId = 1; // thay bằng ID thực tế
        viewModel.fetchOrderHistory(currentUserId);
    }
}
