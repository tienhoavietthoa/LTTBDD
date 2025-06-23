package com.example.tllttbdd.ui.auth;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.example.tllttbdd.R;
import com.example.tllttbdd.data.model.LoginResponse;
import com.example.tllttbdd.MainActivity;

public class LoginActivity extends AppCompatActivity {
    private EditText edtUsername, edtPassword;
    private Button btnLogin, btnGoToRegister;
    private TextView tvResult;
    private AuthViewModel authViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Kiểm tra trạng thái đăng nhập
        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        boolean isLoggedIn = prefs.getBoolean("is_logged_in", false);
        if (isLoggedIn) {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        setContentView(R.layout.activity_login);

        edtUsername = findViewById(R.id.edtUsername);
        edtPassword = findViewById(R.id.edtPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvResult = findViewById(R.id.tvResult);
        btnGoToRegister = findViewById(R.id.btnGoToRegister); // Thêm dòng này

        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        authViewModel.getLoginResult().observe(this, loginResponse -> {
            if (loginResponse != null && loginResponse.success) {
                // Lưu trạng thái đăng nhập
                SharedPreferences.Editor editor = prefs.edit();
                editor.putBoolean("is_logged_in", true);
                editor.putInt("id_login", loginResponse.user.id_login);
                editor.putString("name_login", loginResponse.user.name_login);
                // ... lưu thêm các trường khác nếu cần
                editor.apply();

                Toast.makeText(this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            } else if (loginResponse != null) {
                tvResult.setText(loginResponse.error != null ? loginResponse.error : "Đăng nhập thất bại!");
            } else {
                tvResult.setText("Lỗi kết nối hoặc dữ liệu không hợp lệ!");
            }
        });

        btnLogin.setOnClickListener(v -> {
            String username = edtUsername.getText().toString().trim();
            String password = edtPassword.getText().toString().trim();

            if (username.isEmpty() || password.isEmpty()) {
                tvResult.setText("Vui lòng nhập đầy đủ thông tin!");
                return;
            }
            authViewModel.login(username, password);
        });

        // Xử lý chuyển sang màn hình đăng ký
        btnGoToRegister.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }
}