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

public class SimilarProductAdapter extends RecyclerView.Adapter<SimilarProductAdapter.SimilarViewHolder> {

    private List<Product> productList;
    private ProductAdapter.OnItemClickListener listener; // Dùng lại interface từ ProductAdapter

    public SimilarProductAdapter(List<Product> productList) {
        this.productList = productList;
    }

    public void setOnItemClickListener(ProductAdapter.OnItemClickListener listener) {
        this.listener = listener;
    }

    public void setProducts(List<Product> products) {
        this.productList = products;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SimilarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product_similar, parent, false);
        return new SimilarViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SimilarViewHolder holder, int position) {
        Product product = productList.get(position);
        holder.bind(product, listener);
    }

    @Override
    public int getItemCount() {
        return productList != null ? productList.size() : 0;
    }

    static class SimilarViewHolder extends RecyclerView.ViewHolder {
        ImageView productImage;
        TextView productTitle, productPrice;

        public SimilarViewHolder(@NonNull View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.productImage);
            productTitle = itemView.findViewById(R.id.productTitle);
            productPrice = itemView.findViewById(R.id.productPrice);
        }

        public void bind(final Product product, final ProductAdapter.OnItemClickListener listener) {
            productTitle.setText(product.name_product);
            try {
                DecimalFormat formatter = new DecimalFormat("#,###");
                String formattedPrice = formatter.format(Double.parseDouble(product.price)) + " đ";
                productPrice.setText(formattedPrice);
            } catch (Exception e) {
                productPrice.setText(product.price + " đ");
            }
            Glide.with(itemView.getContext())
                    .load("http://10.0.2.2:3000" + product.image_product)
                    .into(productImage);

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onItemClick(product);
                }
            });
        }
    }
}