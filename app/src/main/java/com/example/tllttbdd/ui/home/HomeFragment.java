package com.example.tllttbdd.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.example.tllttbdd.R;
import com.example.tllttbdd.databinding.FragmentHomeBinding;

import java.util.Arrays;
import java.util.List;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Thêm banner quảng cáo chạy ngang
        ViewPager2 bannerPager = binding.bannerPager;

        // Danh sách ảnh quảng cáo (thêm nhiều ảnh nếu muốn)
        List<Integer> banners = Arrays.asList(
                R.drawable.banner1, R.drawable.banner2, R.drawable.banner3

        );
        BannerPagerAdapter adapter = new BannerPagerAdapter(banners);
        bannerPager.setAdapter(adapter);

        // Auto scroll nếu muốn (tuỳ chọn)
        /*
        final int delay = 3000; // ms
        final Handler handler = new Handler();
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                int next = (bannerPager.getCurrentItem() + 1) % banners.size();
                bannerPager.setCurrentItem(next, true);
                handler.postDelayed(this, delay);
            }
        };
        handler.postDelayed(runnable, delay);

        // Nhớ huỷ handler trong onDestroyView nếu dùng auto-scroll
        */

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        // Nếu bạn dùng auto-scroll, nhớ huỷ handler ở đây
    }
}