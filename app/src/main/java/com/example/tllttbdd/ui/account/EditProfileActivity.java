package com.example.tllttbdd.ui.account;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.tllttbdd.R;
import com.example.tllttbdd.data.model.User;
import com.google.android.material.textfield.TextInputEditText;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.regex.Pattern; // MỚI: Import thư viện Pattern

public class EditProfileActivity extends AppCompatActivity {

    private AccountViewModel viewModel;
    private ImageButton btnBack;
    private TextInputEditText edtName, edtPhone, edtEmail, edtDob;
    private Button btnSave, btnCancel;
    private ProgressBar progressBar;
    private int userId;

    private final Calendar myCalendar = Calendar.getInstance();

    // MỚI: Định nghĩa một biểu thức chính quy (Regex) chặt chẽ hơn để kiểm tra email
    private static final Pattern EMAIL_ADDRESS_PATTERN =
            Pattern.compile(
                    "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
                            "\\@" +
                            "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                            "(" +
                            "\\." +
                            "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                            ")+"
            );

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_edit_profile);

        userId = getIntent().getIntExtra("USER_ID", -1);
        if (userId == -1) {
            Toast.makeText(this, "Lỗi: Không tìm thấy người dùng.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        viewModel = new ViewModelProvider(this).get(AccountViewModel.class);

        initViews();
        setupListeners();
        loadCurrentUserData();
    }

    private void initViews() {
        edtName = findViewById(R.id.edtName);
        edtPhone = findViewById(R.id.edtPhone);
        edtEmail = findViewById(R.id.edtEmail);
        edtDob = findViewById(R.id.edtDob);
        btnBack = findViewById(R.id.btnBack);
        btnSave = findViewById(R.id.btnSave);
        btnCancel = findViewById(R.id.btnCancel);
        progressBar = findViewById(R.id.progressBar);
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());
        btnCancel.setOnClickListener(v -> finish());
        btnSave.setOnClickListener(v -> saveChanges());

        DatePickerDialog.OnDateSetListener dateSetListener = (view, year, month, dayOfMonth) -> {
            Calendar selectedDate = Calendar.getInstance();
            selectedDate.set(year, month, dayOfMonth);

            if (selectedDate.after(Calendar.getInstance())) {
                Toast.makeText(EditProfileActivity.this, "Ngày sinh không hợp lệ. Vui lòng không chọn ngày trong tương lai.", Toast.LENGTH_LONG).show();
                return;
            }

            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, month);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateLabel();
        };

        edtDob.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(EditProfileActivity.this, dateSetListener,
                    myCalendar.get(Calendar.YEAR),
                    myCalendar.get(Calendar.MONTH),
                    myCalendar.get(Calendar.DAY_OF_MONTH));

            datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
            datePickerDialog.show();
        });
    }

    private void loadCurrentUserData() {
        viewModel.getUser().observe(this, user -> {
            if (user != null) {
                edtName.setText(user.name_information);
                edtPhone.setText(user.phone_information);
                edtEmail.setText(user.email);

                if (user.date_of_birth != null && !user.date_of_birth.isEmpty()) {
                    updateCalendarFromString(user.date_of_birth);
                    updateLabel();
                }
            }
        });
        viewModel.fetchProfile(userId);
    }

    private void saveChanges() {
        String name = edtName.getText().toString().trim();
        String phone = edtPhone.getText().toString().trim();
        String email = edtEmail.getText().toString().trim();
        String dob = edtDob.getText().toString().trim();

        if (name.isEmpty() || email.isEmpty()) {
            Toast.makeText(this, "Tên và Email không được để trống", Toast.LENGTH_SHORT).show();
            return;
        }

        // --- SỬA: SỬ DỤNG BIỂU THỨC CHÍNH QUY MỚI ĐỂ KIỂM TRA ---
        if (!EMAIL_ADDRESS_PATTERN.matcher(email).matches()) {
            Toast.makeText(this, "Định dạng email không hợp lệ", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!phone.isEmpty() && !phone.matches("^0\\d{9}$")) {
            Toast.makeText(this, "Định dạng số điện thoại không hợp lệ (phải bắt đầu bằng 0 và có 10 chữ số)", Toast.LENGTH_LONG).show();
            return;
        }

        viewModel.updateProfile(userId, name, phone, email, dob);
        Toast.makeText(this, "Đã gửi yêu cầu cập nhật!", Toast.LENGTH_SHORT).show();
        finish();
    }

    private void updateCalendarFromString(String dateStr) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        try {
            myCalendar.setTime(sdf.parse(dateStr));
        } catch (ParseException e) {
            Log.e("EditProfile", "Error parsing date: " + dateStr, e);
        }
    }

    private void updateLabel() {
        String myFormat = "yyyy-MM-dd";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        edtDob.setText(sdf.format(myCalendar.getTime()));
    }
}
