package com.example.tllttbdd.ui.order;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.tllttbdd.R;
import com.example.tllttbdd.data.model.CartItem;

import java.util.List;

public class OrderProductAdapter extends RecyclerView.Adapter<OrderProductAdapter.ViewHolder> {
    private final List<CartItem> items;
    public OrderProductAdapter(List<CartItem> items) { this.items = items; }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Đổi sang layout mới cho trang thanh toán!
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order_product, parent, false);
        return new ViewHolder(v);
    }
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CartItem item = items.get(position);
        holder.name.setText(item.name);
        holder.price.setText(item.price + " đ");
        holder.txtQuantity.setText("x" + item.quantity);
        Glide.with(holder.image.getContext())
                .load("http://10.0.2.2:3000" + item.image)
                .placeholder(R.drawable.ic_launcher_background)
                .into(holder.image);
    }
    @Override
    public int getItemCount() { return items.size(); }
    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, price, txtQuantity;
        ImageView image;
        ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.cartItemName);
            price = itemView.findViewById(R.id.cartItemPrice);
            txtQuantity = itemView.findViewById(R.id.txtQuantity);
            image = itemView.findViewById(R.id.cartItemImage);
        }
    }
}