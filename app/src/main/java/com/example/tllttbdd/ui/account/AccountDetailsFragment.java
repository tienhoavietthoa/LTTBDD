package com.example.tllttbdd.ui.account;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.tllttbdd.R;
import com.example.tllttbdd.data.model.User;
import com.example.tllttbdd.ui.auth.LoginActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class AccountDetailsFragment extends Fragment {
    private AccountViewModel viewModel;
    private TextView tvName, tvPhone, tvEmail, tvDob;
    private Button btnEdit, btnChangePassword, btnDelete, btnLogout;
    private int idLogin;
    private User currentUser;
    private SharedPreferences prefs;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account, container, false);
        tvName = view.findViewById(R.id.tvName);
        tvPhone = view.findViewById(R.id.tvPhone);
        tvEmail = view.findViewById(R.id.tvEmail);
        tvDob = view.findViewById(R.id.tvDob);
        btnEdit = view.findViewById(R.id.btnEdit);
        btnChangePassword = view.findViewById(R.id.btnChangePassword);
        btnDelete = view.findViewById(R.id.btnDelete);
        btnLogout = view.findViewById(R.id.btnLogout);

        prefs = requireActivity().getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        idLogin = prefs.getInt("id_login", -1);

        viewModel = new ViewModelProvider(this).get(AccountViewModel.class);

        // Quan sát user
        viewModel.getUser().observe(getViewLifecycleOwner(), user -> {
            if (user != null) {
                currentUser = user;
                tvName.setText("Tên: " + user.name_information);
                tvPhone.setText("Số điện thoại: " + user.phone_information);
                tvEmail.setText("Email: " + user.email);
                tvDob.setText("Ngày sinh: " + user.date_of_birth);
            }
        });

        // Quan sát kết quả cập nhật
        viewModel.getUpdateResult().observe(getViewLifecycleOwner(), result -> {
            if (result != null && Boolean.TRUE.equals(result.get("success"))) {
                Toast.makeText(getContext(), "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                viewModel.fetchProfile(idLogin); // Tải lại thông tin sau khi cập nhật
            }
        });

        // Quan sát kết quả đổi mật khẩu
        viewModel.getPasswordResult().observe(getViewLifecycleOwner(), result -> {
            if (result != null && Boolean.TRUE.equals(result.get("success"))) {
                Toast.makeText(getContext(), "Đổi mật khẩu thành công", Toast.LENGTH_SHORT).show();
            } else if (result != null && result.get("error") != null) {
                Toast.makeText(getContext(), result.get("error").toString(), Toast.LENGTH_SHORT).show();
            }
        });

        // Quan sát kết quả xóa tài khoản
        viewModel.getDeleteResult().observe(getViewLifecycleOwner(), result -> {
            if (result != null && Boolean.TRUE.equals(result.get("success"))) {
                Toast.makeText(getContext(), "Đã xóa tài khoản", Toast.LENGTH_SHORT).show();
                logout(); // Gọi hàm logout để dọn dẹp và chuyển màn hình
            }
        });

        viewModel.fetchProfile(idLogin);

        btnEdit.setOnClickListener(v -> {
            if (currentUser == null) return;
            showEditDialog();
        });

        btnChangePassword.setOnClickListener(v -> {
            if (currentUser == null) return;
            showChangePasswordDialog();
        });

        btnDelete.setOnClickListener(v -> {
            if (currentUser == null) return;
            new AlertDialog.Builder(requireContext())
                    .setTitle("Xóa tài khoản")
                    .setMessage("Bạn chắc chắn muốn xóa tài khoản?")
                    .setPositiveButton("Xóa", (dialog, which) -> {
                        viewModel.deleteAccount(idLogin);
                    })
                    .setNegativeButton("Hủy", null)
                    .show();
        });

        btnLogout.setOnClickListener(v -> {
            logout();
        });

        return view;
    }

    // --- SỬA LẠI TOÀN BỘ PHƯƠNG THỨC NÀY ---
    private void showEditDialog() {
        // Lấy LayoutInflater và "thổi phồng" layout tùy chỉnh
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_edit_profile, null);

        // Ánh xạ các view BÊN TRONG layout tùy chỉnh
        TextInputEditText edtName = dialogView.findViewById(R.id.edtName);
        TextInputEditText edtPhone = dialogView.findViewById(R.id.edtPhone);
        TextInputEditText edtEmail = dialogView.findViewById(R.id.edtEmail);
        TextInputEditText edtDob = dialogView.findViewById(R.id.edtDob);
        MaterialButton btnCancel = dialogView.findViewById(R.id.btnCancel);
        MaterialButton btnSave = dialogView.findViewById(R.id.btnSave);

        // Điền thông tin cũ của người dùng vào các ô
        edtName.setText(currentUser.name_information);
        edtPhone.setText(currentUser.phone_information);
        edtEmail.setText(currentUser.email);
        edtDob.setText(currentUser.date_of_birth);

        // Xây dựng Dialog mà KHÔNG dùng .setPositiveButton/.setNegativeButton
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setView(dialogView);

        // Tạo đối tượng dialog để có thể đóng nó
        final AlertDialog dialog = builder.create();

        // Gán sự kiện cho các nút BÊN TRONG layout
        btnSave.setOnClickListener(v -> {
            viewModel.updateProfile(
                    idLogin,
                    edtName.getText().toString(),
                    edtPhone.getText().toString(),
                    edtEmail.getText().toString(),
                    edtDob.getText().toString()
            );
            dialog.dismiss(); // Đóng dialog
        });

        btnCancel.setOnClickListener(v -> {
            dialog.dismiss(); // Đóng dialog
        });

        // Hiển thị dialog
        dialog.show();
    }

    // --- SỬA LẠI TOÀN BỘ PHƯƠNG THỨC NÀY CHO ĐỒNG BỘ ---
    private void showChangePasswordDialog() {
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_change_password, null);

        TextInputEditText edtOldPassword = dialogView.findViewById(R.id.edtOldPassword);
        TextInputEditText edtNewPassword = dialogView.findViewById(R.id.edtNewPassword);
        MaterialButton btnCancel = dialogView.findViewById(R.id.btnCancel); // Giả sử có nút này trong layout
        MaterialButton btnSave = dialogView.findViewById(R.id.btnSave); // Giả sử có nút này trong layout

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setView(dialogView);

        final AlertDialog dialog = builder.create();

        btnSave.setOnClickListener(v -> {
            String oldPass = edtOldPassword.getText().toString();
            String newPass = edtNewPassword.getText().toString();
            if (!oldPass.isEmpty() && !newPass.isEmpty()) {
                viewModel.changePassword(idLogin, oldPass, newPass);
                dialog.dismiss();
            } else {
                Toast.makeText(getContext(), "Vui lòng nhập đủ mật khẩu", Toast.LENGTH_SHORT).show();
            }
        });

        btnCancel.setOnClickListener(v -> {
            dialog.dismiss();
        });

        dialog.show();
    }

    // --- THÊM PHƯƠNG THỨC NÀY ĐỂ TRÁNH LẶP CODE ---
    private void logout() {
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        editor.apply();
        Intent intent = new Intent(requireActivity(), LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        requireActivity().finish();
    }
}