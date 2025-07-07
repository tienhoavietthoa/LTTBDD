package com.example.tllttbdd.ui.dashboard;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.tllttbdd.R;
import com.example.tllttbdd.data.model.Category;
import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {
    public interface OnCategoryClickListener {
        void onCategoryClick(Category category);
    }
    private final List<Category> categories;
    private final OnCategoryClickListener listener;

    public CategoryAdapter(List<Category> categories, OnCategoryClickListener listener) {
        this.categories = categories;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category, parent, false);
        return new CategoryViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        Category c = categories.get(position);
        holder.tvName.setText(c.name_category);

        if (c.image != null && !c.image.isEmpty()) {
            Glide.with(holder.imgCategory.getContext())
                    .load(c.image)
                    .placeholder(R.drawable.ic_book_placeholder)
                    .into(holder.imgCategory);
        } else {
            holder.imgCategory.setImageResource(R.drawable.ic_book_placeholder);
        }
        holder.itemView.setOnClickListener(v -> listener.onCategoryClick(c));
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    static class CategoryViewHolder extends RecyclerView.ViewHolder {
        TextView tvName;
        ImageView imgCategory; // Thêm biến này
        CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvCategoryName);
            imgCategory = itemView.findViewById(R.id.imgCategory);
        }
    }
}