package com.example.tllttbdd.ui.account;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.material.button.MaterialButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.tllttbdd.R;
import com.example.tllttbdd.data.model.User;
import com.example.tllttbdd.ui.auth.LoginActivity;

public class UserDetailsActivity extends AppCompatActivity {

    private AccountViewModel viewModel;
    private TextView tvName, tvPhone, tvEmail, tvDob;

    // THAY ĐỔI Ở ĐÂY: Sửa lại kiểu dữ liệu cho đúng với layout mới
    private View btnEdit, btnChangePassword;
    private MaterialButton btnLogout, btnDelete;

    private int idLogin = -1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_details);

        viewModel = new ViewModelProvider(this).get(AccountViewModel.class);

        // Ánh xạ View
        tvName = findViewById(R.id.tvName);
        tvEmail = findViewById(R.id.tvEmail);
        tvPhone = findViewById(R.id.tvPhone);
        tvDob = findViewById(R.id.tvDob);

        // Các nút hành động giờ là View (vì chúng là RelativeLayout)
        btnEdit = findViewById(R.id.btnEdit);
        btnChangePassword = findViewById(R.id.btnChangePassword);
        ImageButton btnReturn = findViewById(R.id.btnReturn);
        btnReturn.setOnClickListener(v -> finish());

        // Các nút ở dưới là MaterialButton
        btnLogout = findViewById(R.id.btnLogout);
        btnDelete = findViewById(R.id.btnDelete);

        // Lấy idLogin từ SharedPreferences
        idLogin = getSharedPreferences("user_prefs", MODE_PRIVATE).getInt("id_login", -1);
        if (idLogin == -1) {
            Log.e("UserDetailsActivity", "Invalid idLogin. Closing activity.");
            Toast.makeText(this, "Lỗi: Không tìm thấy thông tin đăng nhập.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        // Quan sát LiveData<User> từ ViewModel để cập nhật UI
        viewModel.getUser().observe(this, user -> {
            if (user != null) {
                // Đổi cách setText để không bị trùng lặp "Tên: Tên: Nghia"
                tvName.setText(user.name_information);
                tvEmail.setText(user.email);
                tvPhone.setText("Số điện thoại: " + (user.phone_information != null ? user.phone_information : "Chưa cập nhật"));
                tvDob.setText("Ngày sinh: " + (user.date_of_birth != null ? user.date_of_birth : "Chưa cập nhật"));
            } else {
                tvName.setText("Không có dữ liệu");
                tvEmail.setText("Không có dữ liệu");
                tvPhone.setText("Số điện thoại: Chưa cập nhật");
                tvDob.setText("Ngày sinh: Chưa cập nhật");
            }
        });

        // Thiết lập sự kiện click
        btnEdit.setOnClickListener(v -> handleEditProfile(idLogin));
        btnChangePassword.setOnClickListener(v -> handleChangePassword(idLogin));
        btnDelete.setOnClickListener(v -> showDeleteConfirmationDialog());
        btnLogout.setOnClickListener(v -> showLogoutConfirmationDialog());
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (idLogin != -1) {
            Log.d("UserDetailsActivity", "Resuming and fetching profile.");
            viewModel.fetchProfile(idLogin);
        }
    }

    private void handleEditProfile(int userId) {
        Intent intent = new Intent(this, EditProfileActivity.class);
        intent.putExtra("USER_ID", userId);
        startActivity(intent);
    }

    private void handleChangePassword(int userId) {
        Intent intent = new Intent(this, ChangePasswordActivity.class);
        intent.putExtra("USER_ID", userId);
        startActivity(intent);
    }

    private void handleLogout() {
        getSharedPreferences("user_prefs", MODE_PRIVATE).edit().clear().apply();
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void showDeleteConfirmationDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Xóa tài khoản")
                .setMessage("Bạn có chắc chắn muốn xóa tài khoản không? Hành động này không thể hoàn tác.")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton("Xác nhận", (dialog, which) -> {
                    viewModel.deleteAccount(idLogin);
                    Toast.makeText(this, "Đã gửi yêu cầu xóa tài khoản.", Toast.LENGTH_SHORT).show();
                    handleLogout();
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void showLogoutConfirmationDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Đăng xuất")
                .setMessage("Bạn có chắc chắn muốn đăng xuất không?")
                .setPositiveButton("Đồng ý", (dialog, which) -> {
                    handleLogout();
                })
                .setNegativeButton("Hủy", null)
                .show();
    }
}