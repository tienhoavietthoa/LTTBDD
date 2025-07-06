package com.example.tllttbdd.ui.account; // Đảm bảo đúng package

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.tllttbdd.R;
import com.example.tllttbdd.data.model.Order;
import java.text.DecimalFormat;
import java.util.List;
import java.util.ArrayList; // Thêm import này

public class OrderHistoryAdapter extends RecyclerView.Adapter<OrderHistoryAdapter.OrderViewHolder> {

    public interface OnOrderClickListener {
        void onOrderClick(Order order);
    }

    private List<Order> orders; // Giờ sẽ dùng ArrayList để dễ dàng thêm/bớt
    private final OnOrderClickListener listener;

    public OrderHistoryAdapter(List<Order> orders, OnOrderClickListener listener) {
        this.orders = new ArrayList<>(orders); // Khởi tạo với một bản sao của list
        this.listener = listener;
    }

    // Phương thức MỚI để cập nhật danh sách đơn hàng
    public void setOrders(List<Order> newOrders) {
        this.orders.clear(); // Xóa dữ liệu cũ
        if (newOrders != null) {
            this.orders.addAll(newOrders); // Thêm dữ liệu mới
        }
        notifyDataSetChanged(); // Thông báo cho RecyclerView cập nhật lại UI
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order_history, parent, false);
        return new OrderViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = orders.get(position);

        holder.tvOrderId.setText("Mã đơn: " + order.id_order);
        holder.tvOrderDate.setText("Ngày: " + order.date_order);

        try {
            DecimalFormat formatter = new DecimalFormat("#,###");
            String formattedTotal = formatter.format(order.total);
            holder.tvOrderTotal.setText("Tổng: " + formattedTotal + "đ");
        } catch (Exception e) {
            holder.tvOrderTotal.setText("Tổng: " + order.total + "đ");
            // Log.e("OrderAdapter", "Error formatting order total: " + e.getMessage()); // Thêm log nếu cần
        }

        // Gán sự kiện click
        holder.itemView.setOnClickListener(v -> listener.onOrderClick(order));
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView tvOrderId, tvOrderDate, tvOrderTotal;
        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvOrderId = itemView.findViewById(R.id.tvOrderId);
            tvOrderDate = itemView.findViewById(R.id.tvOrderDate);
            tvOrderTotal = itemView.findViewById(R.id.tvOrderTotal);
        }
    }
}