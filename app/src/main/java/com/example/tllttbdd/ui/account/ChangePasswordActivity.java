package com.example.tllttbdd.ui.account;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.tllttbdd.R;

import java.util.Map;

public class ChangePasswordActivity extends AppCompatActivity {

    private AccountViewModel viewModel;
    // Bỏ biến edtConfirmPassword
    private EditText edtOldPassword, edtNewPassword;
    private Button btnSave, btnCancel;
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

        edtOldPassword = findViewById(R.id.edtOldPassword);
        edtNewPassword = findViewById(R.id.edtNewPassword);
        // Bỏ dòng findViewById cho edtConfirmPassword
        btnSave = findViewById(R.id.btnSave);
        btnCancel = findViewById(R.id.btnCancel);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Đổi mật khẩu");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        viewModel.getPasswordResult().observe(this, result -> {
            if (result == null) {
                Toast.makeText(this, "Có lỗi xảy ra, vui lòng thử lại.", Toast.LENGTH_SHORT).show();
                return;
            }

            String message = (String) result.get("message");

            if ("success".equalsIgnoreCase(message)) {
                Toast.makeText(this, "Đổi mật khẩu thành công!", Toast.LENGTH_LONG).show();
                finish();
            } else {
                Toast.makeText(this, "Lỗi: " + message, Toast.LENGTH_LONG).show();
            }
        });

        btnSave.setOnClickListener(v -> performChangePassword());
        btnCancel.setOnClickListener(v -> finish());
    }

    private void performChangePassword() {
        String oldPass = edtOldPassword.getText().toString();
        String newPass = edtNewPassword.getText().toString();

        // Bỏ kiểm tra confirmPass
        if (oldPass.isEmpty() || newPass.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        // Bỏ khối lệnh so sánh newPass và confirmPass

        viewModel.changePassword(userId, oldPass, newPass);
        Toast.makeText(this, "Đang gửi yêu cầu...", Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}