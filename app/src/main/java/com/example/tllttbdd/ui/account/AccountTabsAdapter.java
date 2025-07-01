// File 3: AccountTabsAdapter.java (TẠO FILE MỚI)
// File này để kết nối các Fragment con với ViewPager2.
package com.example.tllttbdd.ui.account;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class AccountTabsAdapter extends FragmentStateAdapter {

    public AccountTabsAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 1) {
            return new OrderHistoryFragment(); // Thay vì PlaceholderFragment
        }
        return new AccountActionsFragment();
    }

    @Override
    public int getItemCount() {
        return 2; // Chúng ta có 2 tab
    }
}