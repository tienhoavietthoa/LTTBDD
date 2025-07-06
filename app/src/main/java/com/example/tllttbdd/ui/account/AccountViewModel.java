package com.example.tllttbdd.ui.account;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.example.tllttbdd.data.model.ApiResponse;
import com.example.tllttbdd.data.model.Order;
import com.example.tllttbdd.data.model.User;
import com.example.tllttbdd.data.repository.AccountRepository;

import java.util.List;
import java.util.Map;

public class AccountViewModel extends ViewModel {

    private final AccountRepository repository = new AccountRepository();

    // --- LiveData cho User và các hành động liên quan (Lấy từ Repository) ---
    public LiveData<User> getUser() {
        return repository.getUser();
    }
    public LiveData<ApiResponse> getProfileUpdateResult() {
        return repository.getProfileUpdateResult();
    }
    public LiveData<ApiResponse> getPasswordResult() {
        return repository.getPasswordResult();
    }
    public LiveData<ApiResponse> getDeleteResult() {
        return repository.getDeleteResult();
    }
    public LiveData<String> getErrorMessage() {
        return repository.getErrorMessage(); // Thêm LiveData cho lỗi chung từ Repository
    }

    // <-- ĐÃ THÊM: Phương thức để xóa thông báo lỗi -->
    public void clearErrorMessage() {
        repository.clearErrorMessage(); // Gọi phương thức clearErrorMessage của Repository
    }


    // --- LiveData cho Lịch sử đơn hàng (Lấy từ Repository và tính toán trong ViewModel) ---
    private final LiveData<List<Order>> orders = repository.getOrders();
    private final LiveData<Double> totalSpent = Transformations.map(orders, this::calculateTotalSpent);

    public LiveData<List<Order>> getOrders() {
        return orders;
    }
    public LiveData<Double> getTotalSpent() {
        return totalSpent;
    }

    private Double calculateTotalSpent(List<Order> orderList) {
        if (orderList == null) {
            return 0.0;
        }
        double total = 0;
        for (Order order : orderList) {
            total += order.total;
        }
        return total;
    }

    // --- CÁC PHƯƠNG THỨC GỌI TỪ UI ĐỂ THỰC HIỆN CÁC HÀNH ĐỘNG ---
    public void fetchProfile(int userId) {
        repository.fetchProfile(userId);
    }
    public void changePassword(int userId, String oldPassword, String newPassword) {
        repository.changePassword(userId, oldPassword, newPassword);
    }
    public void deleteAccount(int userId) {
        repository.deleteAccount(userId);
    }
    public void updateProfile(int userId, String name, String phone, String email, String dob) {
        repository.updateProfile(userId, name, phone, email, dob);
    }
    public void fetchOrderHistory(int userId) {
        repository.fetchOrders(userId);
    }
}