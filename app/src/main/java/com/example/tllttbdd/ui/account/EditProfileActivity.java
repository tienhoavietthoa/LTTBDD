package com.example.tllttbdd.ui.account;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.tllttbdd.R;

public class EditProfileActivity extends AppCompatActivity {

    private AccountViewModel viewModel;
    // Thêm edtEmail
    private EditText edtName, edtPhone, edtEmail, edtDob;
    private Button btnSave, btnCancel;
    private int userId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Đổi lại tên layout cho đúng (nếu bạn dùng layout activity thay vì dialog)
        setContentView(R.layout.dialog_edit_profile);

        userId = getIntent().getIntExtra("USER_ID", -1);
        if (userId == -1) {
            Toast.makeText(this, "Lỗi: Không tìm thấy người dùng.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        viewModel = new ViewModelProvider(this).get(AccountViewModel.class);

        // Ánh xạ View, thêm edtEmail
        edtName = findViewById(R.id.edtName);
        edtPhone = findViewById(R.id.edtPhone);
        edtEmail = findViewById(R.id.edtEmail); // Thêm dòng này
        edtDob = findViewById(R.id.edtDob);
        btnSave = findViewById(R.id.btnSave);
        btnCancel = findViewById(R.id.btnCancel);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Chỉnh sửa thông tin");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        loadCurrentUserData();

        btnSave.setOnClickListener(v -> saveChanges());
        btnCancel.setOnClickListener(v -> finish());
    }

    private void loadCurrentUserData() {
        viewModel.getUser().observe(this, user -> {
            if (user != null) {
                edtName.setText(user.name_information);
                edtPhone.setText(user.phone_information);
                edtEmail.setText(user.email); // Hiển thị email
                edtDob.setText(user.date_of_birth);
            }
        });
        viewModel.fetchProfile(userId);
    }

    private void saveChanges() {
        String name = edtName.getText().toString().trim();
        String phone = edtPhone.getText().toString().trim();
        String email = edtEmail.getText().toString().trim(); // Lấy email từ ô nhập
        String dob = edtDob.getText().toString().trim();

        if (name.isEmpty() || email.isEmpty()) {
            Toast.makeText(this, "Tên và Email không được để trống", Toast.LENGTH_SHORT).show();
            return;
        }

        // Gọi hàm update trong ViewModel với đủ 5 tham số
        viewModel.updateProfile(userId, name, phone, email, dob);

        Toast.makeText(this, "Đã gửi yêu cầu cập nhật!", Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}