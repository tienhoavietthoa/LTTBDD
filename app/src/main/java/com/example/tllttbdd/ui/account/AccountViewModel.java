package com.example.tllttbdd.ui.account;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.tllttbdd.data.model.Order;
import com.example.tllttbdd.data.model.User;
import com.example.tllttbdd.data.repository.AccountRepository;

import java.util.ArrayList; // Import ArrayList
import java.util.List; // Import List
import java.util.Map;

public class AccountViewModel extends ViewModel {

    private final AccountRepository repository = new AccountRepository();

    // --- DỮ LIỆU USER ---
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

    public void updateProfile(int userId, String name, String phone, String email, String dob) {
        Log.d("AccountViewModel", "Delegating update request for user " + userId + " to repository.");
        repository.updateProfile(userId, name, phone, email, dob);
    }

    // --- DỮ LIỆU ĐƠN HÀNG ---
    // LiveData này sẽ nhận dữ liệu từ repository
    private final LiveData<List<Order>> orders = repository.getOrdersFromDatabase();

    public LiveData<List<Order>> getOrders() {
        return orders;
    }

    // Phương thức này CẦN nhận một tham số userId
    public void loadOrdersFromRepository(int userId) { // <-- Đảm bảo phương thức có tham số 'int userId'
        Log.d("AccountViewModel", "Requesting orders from repository for user ID: " + userId);
        repository.fetchOrders(userId); // Gọi phương thức fetchOrders từ repository
    }
}
