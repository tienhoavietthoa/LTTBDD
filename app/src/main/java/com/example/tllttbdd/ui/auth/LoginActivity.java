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
import com.example.tllttbdd.ui.account.ForgotPasswordActivity;

public class LoginActivity extends AppCompatActivity {
    private EditText edtUsername, edtPassword;
    private Button btnLogin;
    private TextView tvResult;
    private AuthViewModel authViewModel;
    private TextView btnGoToRegister;
    private TextView tvForgotPassword; // Thêm TextView cho quên mật khẩu

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

        // Khởi tạo views
        edtUsername = findViewById(R.id.edtUsername);
        edtPassword = findViewById(R.id.edtPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvResult = findViewById(R.id.tvResult);
        btnGoToRegister = findViewById(R.id.btnGoToRegister);
        tvForgotPassword = findViewById(R.id.tvForgotPassword); // Khởi tạo TextView quên mật khẩu

        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        // Observer cho kết quả đăng nhập
        authViewModel.getLoginResult().observe(this, loginResponse -> {
            if (loginResponse != null && loginResponse.success) {
                // Lưu trạng thái đăng nhập
                SharedPreferences.Editor editor = prefs.edit();
                editor.putBoolean("is_logged_in", true);
                editor.putInt("id_login", loginResponse.user.id_login);
                editor.putString("name_login", loginResponse.user.name_login);
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

        // Xử lý nút đăng nhập
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

        // Xử lý chuyển sang màn hình quên mật khẩu
        tvForgotPassword.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
            startActivity(intent);
        });
    }
}