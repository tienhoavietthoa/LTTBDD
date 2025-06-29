package com.example.tllttbdd.ui.account;

import android.util.Log;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import com.example.tllttbdd.data.model.User;
import com.example.tllttbdd.data.repository.AccountRepository;

import java.util.Map;

public class AccountViewModel extends ViewModel {
    private final AccountRepository repository = new AccountRepository();

    // Các hàm này đã đúng, giữ nguyên
    public LiveData<User> getUser() {
        return repository.getUser();
    }

    public LiveData<Map<String, Object>> getUpdateResult() {
        return repository.getUpdateResult();
    }

    public LiveData<Map<String, Object>> getPasswordResult() {
        return repository.getPasswordResult();
    }

    public LiveData<Map<String, Object>> getDeleteResult() {
        return repository.getDeleteResult();
    }

    public void fetchProfile(int idLogin) {
        Log.d("AccountViewModel", "Fetching profile for ID: " + idLogin);
        repository.fetchProfile(idLogin);
    }

    public void changePassword(int idLogin, String oldPassword, String newPassword) {
        repository.changePassword(idLogin, oldPassword, newPassword);
    }

    public void deleteAccount(int idLogin) {
        repository.deleteAccount(idLogin);
    }

    // --- HÀM updateProfile ĐÃ ĐƯỢC SỬA LẠI VỚI ĐỦ 5 THAM SỐ ---
    /**
     * Hàm này nhận đủ 5 tham số từ UI và chuyển yêu cầu xuống cho Repository.
     * Giờ đây nó đã khớp với định nghĩa trong AccountRepository.
     */
    public void updateProfile(int userId, String name, String phone, String email, String dob) {
        Log.d("AccountViewModel", "Delegating update request for user " + userId + " to repository.");
        repository.updateProfile(userId, name, phone, email, dob);
    }
}