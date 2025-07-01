package com.example.tllttbdd.ui.cart;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.tllttbdd.R;
import com.example.tllttbdd.data.model.CartItem;

import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {
    public interface OnCartActionListener {
        void onRemove(CartItem item);
        void onQuantityChange(CartItem item, int newQuantity);
        void onSelectChanged(CartItem item, boolean selected); // Thêm dòng này
    }

    private List<CartItem> items;
    private final OnCartActionListener listener; // Thêm từ khóa final

    public CartAdapter(List<CartItem> items, OnCartActionListener listener) {
        this.items = items;
        this.listener = listener;
    }

    public void setItems(List<CartItem> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cart, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        CartItem item = items.get(position);
        holder.name.setText(item.name);
        holder.price.setText(item.price + " đ");
        holder.txtQuantity.setText(String.valueOf(item.quantity)); // Đúng chỗ!

        Glide.with(holder.image.getContext())
                .load("http://10.0.2.2:3000" + item.image)
                .placeholder(R.drawable.ic_launcher_background)
                .into(holder.image);

        holder.btnRemove.setOnClickListener(v -> {
            if (listener != null) listener.onRemove(item);
        });
        holder.btnIncrease.setOnClickListener(v -> {
            if (listener != null) listener.onQuantityChange(item, item.quantity + 1);
        });
        holder.btnDecrease.setOnClickListener(v -> {
            if (item.quantity > 1 && listener != null) listener.onQuantityChange(item, item.quantity - 1);
        });
    }

    // Thêm getter cho danh sách item
    public List<CartItem> getItems() {
        return items;
    }
    @Override
    public int getItemCount() {
        return items == null ? 0 : items.size();
    }
    static class CartViewHolder extends RecyclerView.ViewHolder {
        TextView name, price, txtQuantity;
        ImageView image;
        ImageButton btnRemove, btnIncrease, btnDecrease;

        CartViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.cartItemName);
            price = itemView.findViewById(R.id.cartItemPrice);
            txtQuantity = itemView.findViewById(R.id.txtQuantity); // Đổi tên biến
            image = itemView.findViewById(R.id.cartItemImage);
            btnRemove = itemView.findViewById(R.id.btnRemove);
            btnIncrease = itemView.findViewById(R.id.btnIncrease);
            btnDecrease = itemView.findViewById(R.id.btnDecrease);
        }
    }
}