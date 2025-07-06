package com.example.tllttbdd.ui.account;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.EditText;
import android.widget.ProgressBar; // Import ProgressBar
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.tllttbdd.R;

public class ChangePasswordActivity extends AppCompatActivity {

    private AccountViewModel viewModel;
    private ImageButton btnBack;
    private EditText edtOldPassword, edtNewPassword, edtConfirmPassword;
    private Button btnSave, btnCancel;
    private ProgressBar progressBar;
    private int userId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_change_password);

        userId = getIntent().getIntExtra("USER_ID", -1);
        if (userId == -1) {
            Toast.makeText(this, "Lỗi: Không tìm thấy người dùng.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        viewModel = new ViewModelProvider(this).get(AccountViewModel.class);
        initViews();
        setupObservers();
        setupListeners();
    }

    private void initViews() {
        edtOldPassword = findViewById(R.id.edtOldPassword);
        edtNewPassword = findViewById(R.id.edtNewPassword);
        edtConfirmPassword = findViewById(R.id.edtConfirmPassword); // Ánh xạ view mới
        btnSave = findViewById(R.id.btnSave);
        btnBack = findViewById(R.id.btnBack);
        btnCancel = findViewById(R.id.btnCancel);
        progressBar = findViewById(R.id.progressBar);
    }

    private void setupListeners() {
        btnSave.setOnClickListener(v -> performChangePassword());
        btnCancel.setOnClickListener(v -> finish());
        btnBack.setOnClickListener(v -> finish());

        // Thiết lập cho nút back trên Action Bar (nếu có)
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void setupObservers() {
        viewModel.getPasswordResult().observe(this, result -> {
            showLoading(false);
            if (result == null) {
                Toast.makeText(this, "Có lỗi xảy ra, vui lòng thử lại.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Lấy cả trạng thái và thông báo từ kết quả
            Boolean success = (Boolean) result.get("success");
            String message = (String) result.get("message");

            // Kiểm tra dựa trên trạng thái boolean
            if (success != null && success) {
                // Hiển thị thông báo mà server trả về
                Toast.makeText(this, message, Toast.LENGTH_LONG).show();
                finish();
            } else {
                Toast.makeText(this, "Lỗi: " + message, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void performChangePassword() {
        String oldPass = edtOldPassword.getText().toString().trim();
        String newPass = edtNewPassword.getText().toString().trim();
        String confirmPass = edtConfirmPassword.getText().toString().trim();

        if (oldPass.isEmpty() || newPass.isEmpty() || confirmPass.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        if (newPass.length() < 6) {
            Toast.makeText(this, "Mật khẩu mới phải có ít nhất 6 ký tự", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!newPass.equals(confirmPass)) {
            Toast.makeText(this, "Mật khẩu mới và xác nhận không trùng khớp", Toast.LENGTH_SHORT).show();
            return;
        }

        if (newPass.equals(oldPass)) {
            Toast.makeText(this, "Mật khẩu mới phải khác mật khẩu cũ", Toast.LENGTH_SHORT).show();
            return;
        }

        showLoading(true); // Hiển thị ProgressBar khi bắt đầu gọi API
        viewModel.changePassword(userId, oldPass, newPass);
    }

    private void showLoading(boolean isLoading) {
        if (isLoading) {
            progressBar.setVisibility(View.VISIBLE);
            btnSave.setEnabled(false);
            btnCancel.setEnabled(false);
        } else {
            progressBar.setVisibility(View.GONE);
            btnSave.setEnabled(true);
            btnCancel.setEnabled(true);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}