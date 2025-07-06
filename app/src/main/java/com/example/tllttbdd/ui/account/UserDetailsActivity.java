package com.example.tllttbdd.ui.account;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.example.tllttbdd.data.model.ApiResponse;
import com.example.tllttbdd.data.model.User;
import com.example.tllttbdd.ui.auth.LoginActivity;

public class UserDetailsActivity extends AppCompatActivity {

    private AccountViewModel viewModel;
    private TextView tvName, tvPhone, tvEmail, tvDob;

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

        btnEdit = findViewById(R.id.btnEdit);
        btnChangePassword = findViewById(R.id.btnChangePassword);
        ImageButton btnReturn = findViewById(R.id.btnReturn);
        btnReturn.setOnClickListener(v -> finish());

        btnLogout = findViewById(R.id.btnLogout);
        btnDelete = findViewById(R.id.btnDelete);

        // Lấy idLogin từ SharedPreferences
        idLogin = getSharedPreferences("user_prefs", MODE_PRIVATE).getInt("id_login", -1);
        Log.d("UserDetailsActivity", "onCreate: Retrieved idLogin from SharedPreferences: " + idLogin);

        if (idLogin == -1) {
            Log.e("UserDetailsActivity", "Invalid idLogin found. Displaying error and finishing activity.");
            Toast.makeText(this, "Lỗi: Không tìm thấy thông tin đăng nhập. Vui lòng đăng nhập lại.", Toast.LENGTH_LONG).show();
            displayNoUserData();
            disableActionButtons();
            // Không finish() ngay lập tức để người dùng có thể thấy thông báo
            return;
        }

        // Quan sát LiveData<User> từ ViewModel để cập nhật UI
        observeUserViewModel();

        // Quan sát kết quả của các hành động (update, change password, delete)
        observeActionResults();

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
            Log.d("UserDetailsActivity", "onResume: Fetching profile for idLogin: " + idLogin);
            viewModel.fetchProfile(idLogin);
        } else {
            Log.w("UserDetailsActivity", "onResume: idLogin is -1, not fetching profile.");
            displayNoUserData();
            disableActionButtons();
        }
    }

    private void observeUserViewModel() {
        viewModel.getUser().observe(this, user -> {
            if (user != null) {
                Log.d("UserDetailsActivity", "User data observed: " + user.name_information + ", Email: " + user.email);
                tvName.setText(user.name_information);
                tvEmail.setText(user.email);
                tvPhone.setText("Số điện thoại: " + (user.phone_information != null && !user.phone_information.isEmpty() ? user.phone_information : "Chưa cập nhật"));
                tvDob.setText("Ngày sinh: " + (user.date_of_birth != null && !user.date_of_birth.isEmpty() ? user.date_of_birth : "Chưa cập nhật"));
                enableActionButtons();
            } else {
                Log.w("UserDetailsActivity", "Observed user data is null. Displaying default values.");
                displayNoUserData();
                disableActionButtons();
            }
        });

        // Observe general error messages from repository
        viewModel.getErrorMessage().observe(this, error -> {
            if (error != null && !error.isEmpty()) {
                Toast.makeText(UserDetailsActivity.this, "Lỗi: " + error, Toast.LENGTH_LONG).show();
                Log.e("UserDetailsActivity", "General error from ViewModel: " + error);
                // <-- ĐÃ SỬA LỖI Ở ĐÂY: GỌI PHƯƠNG THỨC MỚI CỦA VIEWMODEL -->
                viewModel.clearErrorMessage();
            }
        });
    }

    private void observeActionResults() {
        // Observer cho kết quả cập nhật profile
        viewModel.getProfileUpdateResult().observe(this, apiResponse -> {
            if (apiResponse != null) {
                if (apiResponse.isSuccess()) {
                    Toast.makeText(UserDetailsActivity.this, "Cập nhật thông tin thành công!", Toast.LENGTH_SHORT).show();
                    Log.d("UserDetailsActivity", "Profile update successful.");
                    if (idLogin != -1) {
                        viewModel.fetchProfile(idLogin);
                    }
                } else {
                    Toast.makeText(UserDetailsActivity.this, "Cập nhật thông tin thất bại: " + apiResponse.getMessage(), Toast.LENGTH_LONG).show();
                    Log.e("UserDetailsActivity", "Profile update failed: " + apiResponse.getMessage());
                }
                // <-- ĐÃ SỬA LỖI Ở ĐÂY: GỌI PHƯƠNG THỨC MỚI CỦA VIEWMODEL ĐỂ XÓA KẾT QUẢ -->
                // Vì getProfileUpdateResult() trả về LiveData<ApiResponse>, ta không thể dùng setValue(null) trực tiếp.
                // Nếu muốn xóa kết quả, bạn cần thêm phương thức clearProfileUpdateResult() vào ViewModel/Repository.
                // Hiện tại tôi sẽ không xóa để tránh tạo thêm phức tạp. Nếu cần, bạn có thể tự thêm.
                // Một cách đơn giản hơn là chỉ hiển thị Toast và không cần xóa LiveData nếu nó chỉ dùng một lần.
            }
        });

        // Observer cho kết quả đổi mật khẩu
        viewModel.getPasswordResult().observe(this, apiResponse -> {
            if (apiResponse != null) {
                if (apiResponse.isSuccess()) {
                    Toast.makeText(UserDetailsActivity.this, "Đổi mật khẩu thành công!", Toast.LENGTH_SHORT).show();
                    Log.d("UserDetailsActivity", "Password change successful.");
                } else {
                    Toast.makeText(UserDetailsActivity.this, "Đổi mật khẩu thất bại: " + apiResponse.getMessage(), Toast.LENGTH_LONG).show();
                    Log.e("UserDetailsActivity", "Password change failed: " + apiResponse.getMessage());
                }
                // Tương tự, nếu muốn xóa kết quả, cần thêm clearPasswordResult() vào ViewModel/Repository
            }
        });

        // Observer cho kết quả xóa tài khoản
        viewModel.getDeleteResult().observe(this, apiResponse -> {
            if (apiResponse != null) {
                if (apiResponse.isSuccess()) {
                    Toast.makeText(UserDetailsActivity.this, "Xóa tài khoản thành công!", Toast.LENGTH_SHORT).show();
                    Log.d("UserDetailsActivity", "Account deletion successful. Logging out.");
                    handleLogout();
                } else {
                    Toast.makeText(UserDetailsActivity.this, "Xóa tài khoản thất bại: " + apiResponse.getMessage(), Toast.LENGTH_LONG).show();
                    Log.e("UserDetailsActivity", "Account deletion failed: " + apiResponse.getMessage());
                }
                // Tương tự, nếu muốn xóa kết quả, cần thêm clearDeleteResult() vào ViewModel/Repository
            }
        });
    }

    private void displayNoUserData() {
        tvName.setText("Không có dữ liệu");
        tvEmail.setText("Không có dữ liệu");
        tvPhone.setText("Số điện thoại: Chưa cập nhật");
        tvDob.setText("Ngày sinh: Chưa cập nhật");
    }

    private void disableActionButtons() {
        btnEdit.setEnabled(false);
        btnChangePassword.setEnabled(false);
        btnLogout.setEnabled(false);
        btnDelete.setEnabled(false);
    }

    private void enableActionButtons() {
        btnEdit.setEnabled(true);
        btnChangePassword.setEnabled(true);
        btnLogout.setEnabled(true);
        btnDelete.setEnabled(true);
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
                    Toast.makeText(this, "Đang gửi yêu cầu xóa tài khoản...", Toast.LENGTH_SHORT).show();
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