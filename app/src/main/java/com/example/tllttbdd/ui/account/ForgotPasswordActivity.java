package com.example.tllttbdd.ui.account;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.tllttbdd.R;
import com.example.tllttbdd.data.model.LoginResponse;
import com.example.tllttbdd.data.network.ApiClient;
import com.example.tllttbdd.data.network.AuthApi;
import com.example.tllttbdd.ui.auth.LoginActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.util.Random;

public class ForgotPasswordActivity extends AppCompatActivity {
    private EditText edtPhone, edtOTP, edtNewPassword, edtConfirmPassword;
    private Button btnSendOTP, btnResetPassword;
    private TextView tvTimer, tvResendOTP, tvBackToLogin;
    private LinearLayout layoutOTP, layoutPassword;

    private String generatedOTP;
    private CountDownTimer countDownTimer;
    private AuthApi authApi; // Sử dụng AuthApi thay vì ApiService
    private boolean isOTPSent = false;
    private boolean isOTPVerified = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        initViews();
        setupAPI();
        setupListeners();
        showPhoneSection();
    }

    private void initViews() {
        edtPhone = findViewById(R.id.edtPhone);
        edtOTP = findViewById(R.id.edtOTP);
        edtNewPassword = findViewById(R.id.edtNewPassword);
        edtConfirmPassword = findViewById(R.id.edtConfirmPassword);

        btnSendOTP = findViewById(R.id.btnSendOTP);
        btnResetPassword = findViewById(R.id.btnResetPassword);

        tvTimer = findViewById(R.id.tvTimer);
        tvResendOTP = findViewById(R.id.tvResendOTP);
        tvBackToLogin = findViewById(R.id.tvBackToLogin);

        layoutOTP = findViewById(R.id.layoutOTP);
        layoutPassword = findViewById(R.id.layoutPassword);
    }

    private void setupAPI() {
        authApi = ApiClient.getClient().create(AuthApi.class);
    }

    private void setupListeners() {
        btnSendOTP.setOnClickListener(v -> {
            String phoneNumber = edtPhone.getText().toString().trim();

            if (phoneNumber.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập số điện thoại", Toast.LENGTH_SHORT).show();
                return;
            }

            if (phoneNumber.length() < 10) {
                Toast.makeText(this, "Số điện thoại không hợp lệ", Toast.LENGTH_SHORT).show();
                return;
            }

            // Tạo và gửi OTP
            generateAndSendOTP(phoneNumber);
        });

        btnResetPassword.setOnClickListener(v -> {
            if (!isOTPVerified) {
                // Kiểm tra OTP trước
                String enteredOTP = edtOTP.getText().toString().trim();
                if (enteredOTP.isEmpty()) {
                    Toast.makeText(this, "Vui lòng nhập mã OTP", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!enteredOTP.equals(generatedOTP)) {
                    Toast.makeText(this, "Mã OTP không đúng!", Toast.LENGTH_SHORT).show();
                    return;
                }

                // OTP đúng, hiển thị phần mật khẩu
                isOTPVerified = true;
                showPasswordSection();
                return;
            }

            // Xử lý đặt lại mật khẩu
            handleResetPassword();
        });

        tvResendOTP.setOnClickListener(v -> {
            String phoneNumber = edtPhone.getText().toString().trim();
            generateAndSendOTP(phoneNumber);
        });

        tvBackToLogin.setOnClickListener(v -> finish());
    }

    private void generateAndSendOTP(String phoneNumber) {
        generatedOTP = generateOTP();

        // Hiển thị OTP giả lập
        Toast.makeText(this, "Mã OTP của bạn là: " + generatedOTP, Toast.LENGTH_LONG).show();

        isOTPSent = true;
        showOTPSection();
        startTimer();
    }

    private void handleResetPassword() {
        String phoneNumber = edtPhone.getText().toString().trim();
        String newPassword = edtNewPassword.getText().toString().trim();
        String confirmPassword = edtConfirmPassword.getText().toString().trim();

        if (newPassword.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            Toast.makeText(this, "Mật khẩu xác nhận không khớp", Toast.LENGTH_SHORT).show();
            return;
        }

        if (newPassword.length() < 6) {
            Toast.makeText(this, "Mật khẩu phải có ít nhất 6 ký tự", Toast.LENGTH_SHORT).show();
            return;
        }

        // Gọi API đặt lại mật khẩu
        resetPassword(phoneNumber, newPassword);
    }

    private void resetPassword(String phone, String newPassword) {
        btnResetPassword.setEnabled(false);
        btnResetPassword.setText("Đang xử lý...");

        // Gọi API reset password
        Call<LoginResponse> call = authApi.resetPassword(phone, newPassword);
        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                btnResetPassword.setEnabled(true);
                btnResetPassword.setText("Đặt lại mật khẩu");

                if (response.isSuccessful() && response.body() != null) {
                    LoginResponse loginResponse = response.body();

                    // Kiểm tra field success trực tiếp
                    if (loginResponse.success) {
                        Toast.makeText(ForgotPasswordActivity.this, "Đổi mật khẩu thành công!", Toast.LENGTH_SHORT).show();

                        // Chuyển về màn hình đăng nhập
                        Intent intent = new Intent(ForgotPasswordActivity.this, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    } else {
                        // Hiển thị lỗi (ưu tiên error, fallback về message)
                        String errorMessage = loginResponse.error != null ? loginResponse.error : loginResponse.message;
                        Toast.makeText(ForgotPasswordActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(ForgotPasswordActivity.this, "Có lỗi xảy ra", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                btnResetPassword.setEnabled(true);
                btnResetPassword.setText("Đặt lại mật khẩu");
                Toast.makeText(ForgotPasswordActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showPhoneSection() {
        layoutOTP.setVisibility(View.GONE);
        layoutPassword.setVisibility(View.GONE);
        btnSendOTP.setVisibility(View.VISIBLE);
        btnResetPassword.setVisibility(View.GONE);
    }

    private void showOTPSection() {
        layoutOTP.setVisibility(View.VISIBLE);
        layoutPassword.setVisibility(View.GONE);
        btnSendOTP.setVisibility(View.GONE);
        btnResetPassword.setVisibility(View.VISIBLE);
        btnResetPassword.setText("Xác nhận OTP");
    }

    private void showPasswordSection() {
        layoutOTP.setVisibility(View.VISIBLE);
        layoutPassword.setVisibility(View.VISIBLE);
        btnResetPassword.setText("Đặt lại mật khẩu");

        Toast.makeText(this, "OTP xác thực thành công! Nhập mật khẩu mới", Toast.LENGTH_SHORT).show();
    }

    private void startTimer() {
        tvResendOTP.setEnabled(false);

        countDownTimer = new CountDownTimer(60000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                tvTimer.setText("Gửi lại sau: " + millisUntilFinished / 1000 + "s");
            }

            @Override
            public void onFinish() {
                tvTimer.setText("Có thể gửi lại mã OTP");
                tvResendOTP.setEnabled(true);
            }
        }.start();
    }

    private String generateOTP() {
        Random random = new Random();
        int otp = 1000 + random.nextInt(9000);
        return String.valueOf(otp);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }
}