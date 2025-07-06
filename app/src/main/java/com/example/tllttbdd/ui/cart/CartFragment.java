package com.example.tllttbdd.ui.cart;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.widget.Toast;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.example.tllttbdd.databinding.FragmentCartBinding;
import com.example.tllttbdd.data.model.CartItem;
import com.example.tllttbdd.ui.order.OrderActivity;

public class CartFragment extends Fragment {

    private FragmentCartBinding binding;
    private CartAdapter adapter;
    private CartViewModel cartViewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentCartBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        adapter = new CartAdapter(null, new CartAdapter.OnCartActionListener() {
            @Override
            public void onRemove(CartItem item) {
                cartViewModel.removeFromCart(item.productId);
            }
            @Override
            public void onQuantityChange(CartItem item, int newQuantity) {
                cartViewModel.updateCart(item.productId, newQuantity);
            }
            @Override
            public void onSelectChanged(CartItem item, boolean selected) {
                // Có thể cập nhật tổng tiền các sản phẩm đã chọn ở đây nếu muốn
            }
        });
        binding.cartRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.cartRecyclerView.setAdapter(adapter);

        cartViewModel = new ViewModelProvider(this).get(CartViewModel.class);

        cartViewModel.getCartItems().observe(getViewLifecycleOwner(), items -> adapter.setItems(items));
        cartViewModel.getTotal().observe(getViewLifecycleOwner(), total ->
                binding.cartTotal.setText("Tổng: " + NumberFormat.getInstance(new Locale("vi", "VN")).format(total) + " ₫")
        );

        cartViewModel.fetchCart(requireContext());

        // Xử lý nút thanh toán
        // Trong CartFragment
        binding.btnCheckout.setOnClickListener(v -> {
            List<CartItem> cartItems = adapter.getItems();
            if (cartItems == null || cartItems.isEmpty()) {
                Toast.makeText(getContext(), "Giỏ hàng trống!", Toast.LENGTH_SHORT).show();
                return;
            }
            // Gửi toàn bộ sản phẩm sang màn hình đặt hàng
            Intent intent = new Intent(getContext(), OrderActivity.class);
            intent.putParcelableArrayListExtra("selectedItems", new ArrayList<>(cartItems));
            startActivity(intent);
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}