package com.example.tllttbdd.ui.home;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.tllttbdd.R;
import com.example.tllttbdd.data.model.Product;
import java.text.DecimalFormat;
import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    private List<Product> products;
    private OnItemClickListener listener;

    // Interface để gửi sự kiện click ra bên ngoài (Fragment)
    public interface OnItemClickListener {
        void onItemClick(Product product);
    }

    // Phương thức để Fragment có thể gán listener cho Adapter
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public ProductAdapter(List<Product> products) {
        this.products = products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_product, parent, false);
        // Truyền listener vào ViewHolder khi nó được tạo
        return new ProductViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product p = products.get(position);
        if (p == null) return;

        holder.name.setText(p.name_product);

        // Định dạng lại giá tiền cho đẹp hơn
        try {
            DecimalFormat formatter = new DecimalFormat("#,###");
            String formattedPrice = formatter.format(Double.parseDouble(p.price)) + " đ";
            holder.price.setText(formattedPrice);
        } catch (NumberFormatException e) {
            holder.price.setText(p.price + " đ");
        }

        String imageUrl = "http://10.0.2.2:3000" + p.image_product;
        Glide.with(holder.image.getContext())
                .load(imageUrl)
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_launcher_foreground) // Hiển thị ảnh lỗi nếu không tải được
                .into(holder.image);
    }

    @Override
    public int getItemCount() {
        return products == null ? 0 : products.size();
    }

    // --- ViewHolder đã được cập nhật ---
    public class ProductViewHolder extends RecyclerView.ViewHolder {
        TextView name, price;
        ImageView image;

        public ProductViewHolder(@NonNull View itemView, final OnItemClickListener clickListener) {
            super(itemView);
            name = itemView.findViewById(R.id.productTitle);
            price = itemView.findViewById(R.id.productPrice);
            image = itemView.findViewById(R.id.productImage);

            // Gán sự kiện click cho toàn bộ item
            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                // Đảm bảo vị trí hợp lệ và listener đã được gán
                if (clickListener != null && position != RecyclerView.NO_POSITION) {
                    // Gọi ra phương thức của interface, truyền vào sản phẩm được click
                    clickListener.onItemClick(products.get(position));
                }
            });
        }
    }
}