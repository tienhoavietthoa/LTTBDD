package com.example.tllttbdd.ui.auth;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.tllttbdd.data.network.ApiClient;
import com.example.tllttbdd.R;
import com.example.tllttbdd.data.model.ForgotPasswordRequest;
import com.example.tllttbdd.data.model.ResetPasswordRequest;

import retrofit2.Response;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;

public class ResetPasswordActivity extends AppCompatActivity {
    private EditText edtPhone, edtOtp, edtNewPassword;
    private Button btnSendOtp, btnResetPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        edtPhone = findViewById(R.id.edtPhone);
        edtOtp = findViewById(R.id.edtOtp);
        edtNewPassword = findViewById(R.id.edtNewPassword);
        btnSendOtp = findViewById(R.id.btnSendOtp);
        btnResetPassword = findViewById(R.id.btnResetPassword);

        btnSendOtp.setOnClickListener(v -> sendOtp());
        btnResetPassword.setOnClickListener(v -> resetPassword());
    }

    private void sendOtp() {
        String phone = edtPhone.getText().toString().trim();
        // Gọi API gửi OTP
        ApiClient.getAuthApi().forgotPassword(new ForgotPasswordRequest(phone))
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), "Đã gửi mã xác nhận", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getApplicationContext(), "Số điện thoại không tồn tại", Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Toast.makeText(getApplicationContext(), "Lỗi kết nối", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void resetPassword() {
        String phone = edtPhone.getText().toString().trim();
        String otp = edtOtp.getText().toString().trim();
        String newPassword = edtNewPassword.getText().toString().trim();
        // Gọi API đổi mật khẩu
        ApiClient.getAuthApi().resetPassword(new ResetPasswordRequest(phone, otp, newPassword))
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), "Đổi mật khẩu thành công", Toast.LENGTH_SHORT).show();
                            finish(); // Quay về màn hình đăng nhập
                        } else {
                            Toast.makeText(getApplicationContext(), "Mã xác nhận sai hoặc hết hạn", Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Toast.makeText(getApplicationContext(), "Lỗi kết nối", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}