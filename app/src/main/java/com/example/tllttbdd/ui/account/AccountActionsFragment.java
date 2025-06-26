// File 2: AccountActionsFragment.java (TẠO FILE MỚI với tên này)
// File này chứa TOÀN BỘ logic xử lý các nút bấm.
package com.example.tllttbdd.ui.account;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

public class AccountActionsFragment extends Fragment {

    private AccountViewModel viewModel;
    private Button btnEdit, btnChangePassword, btnDelete, btnLogout;
    private int idLogin;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Sử dụng layout của Tab 1 (chỉ chứa các nút)
        return inflater.inflate(R.layout.fragment_tab_actions, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Ánh xạ các nút
        btnEdit = view.findViewById(R.id.btnEdit);
        btnChangePassword = view.findViewById(R.id.btnChangePassword);
        btnDelete = view.findViewById(R.id.btnDelete);
        btnLogout = view.findViewById(R.id.btnLogout);

        // Lấy ViewModel được chia sẻ từ AccountFragment cha
        // Điều này cho phép cả hai fragment giao tiếp với cùng một dữ liệu
        viewModel = new ViewModelProvider(requireParentFragment()).get(AccountViewModel.class);

        // Lấy idLogin từ SharedPreferences
        SharedPreferences prefs = requireActivity().getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        idLogin = prefs.getInt("id_login", -1);

        // Gán các sự kiện cho nút
        btnEdit.setOnClickListener(v -> {
            // Lấy user hiện tại từ ViewModel để hiển thị dialog
            User currentUser = viewModel.getUser().getValue();
            if (currentUser != null) {
                showEditDialog(currentUser);
            } else {
                Toast.makeText(getContext(), "Không thể lấy thông tin người dùng", Toast.LENGTH_SHORT).show();
            }
        });

        btnChangePassword.setOnClickListener(v -> showChangePasswordDialog());

        btnDelete.setOnClickListener(v -> {
            new AlertDialog.Builder(requireContext())
                    .setTitle("Xóa tài khoản")
                    .setMessage("Bạn chắc chắn muốn xóa tài khoản?")
                    .setPositiveButton("Xóa", (dialog, which) -> {
                        if (idLogin != -1) viewModel.deleteAccount(idLogin);
                    })
                    .setNegativeButton("Hủy", null)
                    .show();
        });

        btnLogout.setOnClickListener(v -> logout());

        // Quan sát các kết quả từ ViewModel (Toast, chuyển màn hình...)
        viewModel.getUpdateResult().observe(getViewLifecycleOwner(), result -> {
            if (result != null && Boolean.TRUE.equals(result.get("success"))) {
                Toast.makeText(getContext(), "Cập nhật thành công", Toast.LENGTH_SHORT).show();
            }
        });

        viewModel.getPasswordResult().observe(getViewLifecycleOwner(), result -> {
            if (result != null && Boolean.TRUE.equals(result.get("success"))) {
                Toast.makeText(getContext(), "Đổi mật khẩu thành công", Toast.LENGTH_SHORT).show();
            } else if (result != null && result.get("error") != null) {
                Toast.makeText(getContext(), result.get("error").toString(), Toast.LENGTH_SHORT).show();
            }
        });

        viewModel.getDeleteResult().observe(getViewLifecycleOwner(), result -> {
            if (result != null && Boolean.TRUE.equals(result.get("success"))) {
                Toast.makeText(getContext(), "Đã xóa tài khoản", Toast.LENGTH_SHORT).show();
                logout();
            }
        });
    }

    private void showEditDialog(User currentUser) {
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_edit_profile, null);
        TextInputEditText edtName = dialogView.findViewById(R.id.edtName);
        TextInputEditText edtPhone = dialogView.findViewById(R.id.edtPhone);
        TextInputEditText edtEmail = dialogView.findViewById(R.id.edtEmail);
        TextInputEditText edtDob = dialogView.findViewById(R.id.edtDob);
        MaterialButton btnCancel = dialogView.findViewById(R.id.btnCancel);
        MaterialButton btnSave = dialogView.findViewById(R.id.btnSave);

        edtName.setText(currentUser.name_information);
        edtPhone.setText(currentUser.phone_information);
        edtEmail.setText(currentUser.email);
        edtDob.setText(currentUser.date_of_birth);

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setView(dialogView);
        final AlertDialog dialog = builder.create();

        btnSave.setOnClickListener(v -> {
            viewModel.updateProfile(idLogin, edtName.getText().toString(), edtPhone.getText().toString(), edtEmail.getText().toString(), edtDob.getText().toString());
            dialog.dismiss();
        });

        btnCancel.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    private void showChangePasswordDialog() {
        // Code hiển thị dialog đổi mật khẩu của bạn
    }

    private void logout() {
        SharedPreferences prefs = requireActivity().getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        editor.apply();
        Intent intent = new Intent(requireActivity(), LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        requireActivity().finish();
    }
}