package com.example.tllttbdd.ui.payment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.tllttbdd.R;
import com.example.tllttbdd.data.model.PaymentStatusResponse;
import com.example.tllttbdd.data.network.ApiClient;
import com.example.tllttbdd.data.network.OrderApi;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VNPayActivity extends AppCompatActivity {
    private WebView webView;
    private int orderId;
    private Handler handler = new Handler(Looper.getMainLooper());
    private Runnable statusChecker;
    private boolean isPaymentCompleted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vnpay);

        webView = findViewById(R.id.webView);

        String vnpayUrl = getIntent().getStringExtra("vnpayUrl");
        orderId = getIntent().getIntExtra("orderId", -1);

        if (vnpayUrl == null || orderId == -1) {
            Toast.makeText(this, "Lỗi khởi tạo thanh toán", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        setupWebView(vnpayUrl);
        startPaymentStatusChecker();
    }

    private void setupWebView(String vnpayUrl) {
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // Nếu URL chứa callback từ VNPay, kiểm tra kết quả
                if (url.contains("/order/vnpay/return")) {
                    checkFinalPaymentStatus();
                    return true;
                }
                return false;
            }
        });

        webView.loadUrl(vnpayUrl);
    }

    private void startPaymentStatusChecker() {
        statusChecker = new Runnable() {
            @Override
            public void run() {
                if (!isPaymentCompleted) {
                    checkPaymentStatus();
                    handler.postDelayed(this, 3000); // Check mỗi 3 giây
                }
            }
        };
        handler.postDelayed(statusChecker, 3000);
    }

    private void checkPaymentStatus() {
        OrderApi api = ApiClient.getClient().create(OrderApi.class);
        api.checkPaymentStatus(orderId).enqueue(new Callback<PaymentStatusResponse>() {
            @Override
            public void onResponse(@NonNull Call<PaymentStatusResponse> call, @NonNull Response<PaymentStatusResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    PaymentStatusResponse status = response.body();
                    if (status.isPaid) {
                        isPaymentCompleted = true;
                        handler.removeCallbacks(statusChecker);
                        showPaymentSuccess();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<PaymentStatusResponse> call, @NonNull Throwable t) {
                // Ignore network errors during polling
            }
        });
    }

    private void checkFinalPaymentStatus() {
        OrderApi api = ApiClient.getClient().create(OrderApi.class);
        api.checkPaymentStatus(orderId).enqueue(new Callback<PaymentStatusResponse>() {
            @Override
            public void onResponse(@NonNull Call<PaymentStatusResponse> call, @NonNull Response<PaymentStatusResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    PaymentStatusResponse status = response.body();
                    if (status.isPaid) {
                        showPaymentSuccess();
                    } else {
                        showPaymentFailed();
                    }
                } else {
                    showPaymentFailed();
                }
            }

            @Override
            public void onFailure(@NonNull Call<PaymentStatusResponse> call, @NonNull Throwable t) {
                showPaymentFailed();
            }
        });
    }

    private void showPaymentSuccess() {
        Toast.makeText(this, "Thanh toán thành công!", Toast.LENGTH_LONG).show();
        setResult(Activity.RESULT_OK);
        finish();
    }

    private void showPaymentFailed() {
        Toast.makeText(this, "Thanh toán thất bại!", Toast.LENGTH_LONG).show();
        setResult(Activity.RESULT_CANCELED);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (handler != null && statusChecker != null) {
            handler.removeCallbacks(statusChecker);
        }
    }
}
