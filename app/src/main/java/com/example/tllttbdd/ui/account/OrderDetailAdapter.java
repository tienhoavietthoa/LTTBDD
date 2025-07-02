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
import java.util.List;

public class OrderDetailAdapter extends RecyclerView.Adapter<OrderDetailAdapter.ViewHolder> {
    private final List<OrderDetail> items;
    public OrderDetailAdapter(List<OrderDetail> items) { this.items = items; }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order_detail, parent, false);
        return new ViewHolder(v);
    }
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        OrderDetail item = items.get(position);
        holder.tvName.setText(item.name_product);
        holder.tvDesc.setText(item.text_product != null ? item.text_product : "");
        holder.tvQuantity.setText("x" + item.quantity);
        holder.tvPrice.setText(item.price + " đ");
        Glide.with(holder.imgProduct.getContext())
                .load("http://10.0.2.2:3000" + item.image_product)
                .placeholder(R.drawable.ic_launcher_background)
                .into(holder.imgProduct);
    }
    @Override
    public int getItemCount() { return items.size(); }
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