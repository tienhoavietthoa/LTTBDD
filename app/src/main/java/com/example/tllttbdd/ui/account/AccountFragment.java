package com.example.tllttbdd.ui.account;

import android.app.AlertDialog;
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
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.example.tllttbdd.R;
import com.example.tllttbdd.data.model.User;
import com.example.tllttbdd.ui.auth.LoginActivity;

public class AccountFragment extends Fragment {
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
                tvName.setText(user.name_information);
                tvPhone.setText(user.phone_information);
                tvEmail.setText(user.email);
                tvDob.setText(user.date_of_birth);
            }
        });

        // Quan sát kết quả cập nhật
        viewModel.getUpdateResult().observe(getViewLifecycleOwner(), result -> {
            if (result != null && Boolean.TRUE.equals(result.get("success"))) {
                Toast.makeText(getContext(), "Cập nhật thành công", Toast.LENGTH_SHORT).show();
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
                // Xóa thông tin đăng nhập và chuyển về LoginActivity
                SharedPreferences.Editor editor = prefs.edit();
                editor.clear();
                editor.apply();
                Intent intent = new Intent(requireActivity(), LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                requireActivity().finish();
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
            SharedPreferences.Editor editor = prefs.edit();
            editor.clear();
            editor.apply();
            Intent intent = new Intent(requireActivity(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            requireActivity().finish();
        });

        return view;
    }

    private void showEditDialog() {
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_edit_profile, null);
        TextView edtName = dialogView.findViewById(R.id.edtName);
        TextView edtPhone = dialogView.findViewById(R.id.edtPhone);
        TextView edtEmail = dialogView.findViewById(R.id.edtEmail);
        TextView edtDob = dialogView.findViewById(R.id.edtDob);

        edtName.setText(currentUser.name_information);
        edtPhone.setText(currentUser.phone_information);
        edtEmail.setText(currentUser.email);
        edtDob.setText(currentUser.date_of_birth);

        new AlertDialog.Builder(requireContext())
                .setTitle("Chỉnh sửa thông tin")
                .setView(dialogView)
                .setPositiveButton("Lưu", (dialog, which) -> {
                    viewModel.updateProfile(
                            idLogin,
                            edtName.getText().toString(),
                            edtPhone.getText().toString(),
                            edtEmail.getText().toString(),
                            edtDob.getText().toString()
                    );
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void showChangePasswordDialog() {
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_change_password, null);
        TextView edtOld = dialogView.findViewById(R.id.edtOldPassword);
        TextView edtNew = dialogView.findViewById(R.id.edtNewPassword);

        new AlertDialog.Builder(requireContext())
                .setTitle("Đổi mật khẩu")
                .setView(dialogView)
                .setPositiveButton("Đổi", (dialog, which) -> {
                    viewModel.changePassword(
                            idLogin,
                            edtOld.getText().toString(),
                            edtNew.getText().toString()
                    );
                })
                .setNegativeButton("Hủy", null)
                .show();
    }
}