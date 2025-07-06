package com.example.tllttbdd.ui.order;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.tllttbdd.R;

public class VnpayOtpActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vnpay_otp);

        EditText editOtp = findViewById(R.id.editOtp);
        Button btnConfirm = findViewById(R.id.btnConfirm);

        btnConfirm.setOnClickListener(v -> {
            String otp = editOtp.getText().toString().trim();
            if (otp.equals("123456")) {
                // Trả kết quả về OrderActivity
                setResult(RESULT_OK, getIntent());
                finish();
            } else {
                Toast.makeText(this, "OTP không đúng!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}