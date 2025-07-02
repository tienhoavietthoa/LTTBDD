package com.example.tllttbdd.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.example.tllttbdd.R;

public class RegisterActivity extends AppCompatActivity {

    private EditText edtUsername, edtPassword, edtPhone;
    private Button btnRegister;
    private TextView tvResult;
    private AuthViewModel authViewModel;
    private TextView btnGoToLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        edtUsername = findViewById(R.id.edtUsername);
        edtPassword = findViewById(R.id.edtPassword);
        edtPhone = findViewById(R.id.edtPhone);
        btnRegister = findViewById(R.id.btnRegister);
        tvResult = findViewById(R.id.tvResult);

        // --- THÊM Ở ĐÂY ---
        // Ánh xạ view cho nút quay về trang Đăng nhập
        btnGoToLogin = findViewById(R.id.btnGoToLogin);

        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        authViewModel.getRegisterResult().observe(this, registerResponse -> {
            if (registerResponse != null && registerResponse.success) {
                Toast.makeText(this, "Đăng ký thành công!", Toast.LENGTH_SHORT).show();
                // Sau khi đăng ký thành công, quay về trang đăng nhập
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
                finish(); // Đóng màn hình đăng ký
            } else if (registerResponse != null) {
                tvResult.setText(registerResponse.error != null ? registerResponse.error : "Đăng ký thất bại!");
            } else {
                tvResult.setText("Lỗi kết nối hoặc dữ liệu không hợp lệ!");
            }
        });

        btnRegister.setOnClickListener(v -> {
            String username = edtUsername.getText().toString().trim();
            String password = edtPassword.getText().toString().trim();
            String phone = edtPhone.getText().toString().trim();

            if (username.isEmpty() || password.isEmpty() || phone.isEmpty()) {
                tvResult.setText("Vui lòng nhập đầy đủ thông tin!");
                return;
            }

            // Kiểm tra số điện thoại Việt Nam: 10 số, bắt đầu bằng 0
            if (!phone.matches("^0\\d{9}$")) {
                tvResult.setText("Số điện thoại phải đủ 10 số và bắt đầu bằng 0!");
                return;
            }

            // Kiểm tra mật khẩu: ít nhất 10 ký tự, có cả số và chữ
            if (password.length() < 10 || !password.matches(".*[a-zA-Z].*") || !password.matches(".*\\d.*")) {
                tvResult.setText("Mật khẩu phải có ít nhất 10 ký tự, bao gồm cả chữ và số!");
                return;
            }

            authViewModel.register(username, password, phone);
        });

        // --- THÊM Ở ĐÂY ---
        // Xử lý sự kiện khi người dùng nhấn vào link "Đăng nhập"
        btnGoToLogin.setOnClickListener(v -> {
            // Đóng màn hình đăng ký hiện tại để quay về màn hình đăng nhập đã có sẵn
            finish();
        });
    }
}