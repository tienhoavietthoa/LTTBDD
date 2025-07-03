package com.example.tllttbdd.data.model;

public class User {
    public int id_login;
    public String name_login;
    public int id_level;
    public int id_information;
    public String name_information;
    public String phone_information;
    public String email;
    public String date_of_birth;

    // ✅ Thêm 2 trường mới để thống kê
    private int totalOrders;
    private int totalSpent;

    // ✅ Getter và Setter cho thống kê
    public int getTotalOrders() {
        return totalOrders;
    }

    public void setTotalOrders(int totalOrders) {
        this.totalOrders = totalOrders;
    }

    public int getTotalSpent() {
        return totalSpent;
    }

    public void setTotalSpent(int totalSpent) {
        this.totalSpent = totalSpent;
    }

    // ❗ Bạn có thể thêm constructor nếu cần, nhưng Firebase/Room cần constructor rỗng
    public User() {
    }
}
