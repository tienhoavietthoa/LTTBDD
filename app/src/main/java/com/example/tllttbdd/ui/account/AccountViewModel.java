package com.example.tllttbdd.ui.account;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.example.tllttbdd.data.model.Order;
import com.example.tllttbdd.data.model.User;
import com.example.tllttbdd.data.repository.AccountRepository;

import java.util.List;
import java.util.Map;

public class AccountViewModel extends ViewModel {

    private final AccountRepository repository = new AccountRepository();

    // --- DỮ LIỆU USER (Giữ nguyên) ---
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
        repository.fetchProfile(idLogin);
    }
    public void changePassword(int idLogin, String oldPassword, String newPassword) {
        repository.changePassword(idLogin, oldPassword, newPassword);
    }
    public void deleteAccount(int idLogin) {
        repository.deleteAccount(idLogin);
    }
    public void updateProfile(int userId, String name, String phone, String email, String dob) {
        repository.updateProfile(userId, name, phone, email, dob);
    }

    // --- DỮ LIỆU ĐƠN HÀNG ---

    // LiveData gốc chứa danh sách đơn hàng từ Repository
    private final LiveData<List<Order>> orders = repository.getOrdersFromDatabase();

    // LiveData mới để chứa tổng tiền đã được tính toán
    // Dùng Transformations.map để tự động tính toán lại khi danh sách đơn hàng thay đổi
    private final LiveData<Double> totalSpent = Transformations.map(orders, this::calculateTotalSpent);

    public LiveData<List<Order>> getOrders() {
        return orders;
    }

    // Getter cho LiveData tổng tiền
    public LiveData<Double> getTotalSpent() {
        return totalSpent;
    }

    /**
     * Phương thức này nhận vào danh sách đơn hàng và trả về tổng số tiền.
     * Nó sẽ được gọi tự động mỗi khi LiveData 'orders' có dữ liệu mới.
     * @param orderList Danh sách đơn hàng từ LiveData.
     * @return Tổng số tiền đã tính toán.
     */
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

    /**
     * Yêu cầu Repository tải dữ liệu đơn hàng về.
     * Khi Repository tải xong, LiveData 'orders' sẽ được cập nhật,
     * và LiveData 'totalSpent' cũng sẽ tự động được tính toán lại.
     */
    public void loadOrdersFromRepository(int userId) {
        repository.fetchOrders(userId);
    }
}
