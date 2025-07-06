package com.example.tllttbdd.ui.account;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.example.tllttbdd.R;
import com.example.tllttbdd.data.model.OrderDetail;
import java.text.DecimalFormat; // MỚI: Import để định dạng số
import java.util.List;

public class OrderDetailAdapter extends RecyclerView.Adapter<OrderDetailAdapter.ViewHolder> {
    private List<OrderDetail> items; // Giữ lại tên biến của bạn

    public OrderDetailAdapter(List<OrderDetail> items) {
        this.items = items;
    }

    // MỚI: Thêm phương thức để cập nhật dữ liệu cho Adapter
    public void updateProducts(List<OrderDetail> newItems) {
        this.items.clear();
        this.items.addAll(newItems);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Giữ lại layout item của bạn
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order_detail, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        OrderDetail item = items.get(position);
        holder.tvName.setText(item.name_product);
        holder.tvDesc.setText(item.text_product != null ? item.text_product : "");
        holder.tvQuantity.setText("x" + item.quantity);

        // SỬA: Áp dụng định dạng tiền tệ
        try {
            DecimalFormat formatter = new DecimalFormat("#,###");
            String formattedPrice = formatter.format(item.price) + "đ";
            holder.tvPrice.setText(formattedPrice);
        } catch (Exception e) {
            // Fallback nếu giá không phải là số
            holder.tvPrice.setText(item.price + "đ");
        }

        Glide.with(holder.imgProduct.getContext())
                .load("http://10.0.2.2:3000" + item.image_product)
                .placeholder(R.drawable.ic_launcher_background)
                .into(holder.imgProduct);
    }

    @Override
    public int getItemCount() {
        return items != null ? items.size() : 0;
    }

    // Giữ lại ViewHolder của bạn
    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgProduct;
        TextView tvName, tvDesc, tvQuantity, tvPrice;
        ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgProduct = itemView.findViewById(R.id.imgProduct);
            tvName = itemView.findViewById(R.id.tvProductName);
            tvDesc = itemView.findViewById(R.id.tvProductDesc);
            tvQuantity = itemView.findViewById(R.id.tvProductQuantity);
            tvPrice = itemView.findViewById(R.id.tvProductPrice);
        }
    }
}
