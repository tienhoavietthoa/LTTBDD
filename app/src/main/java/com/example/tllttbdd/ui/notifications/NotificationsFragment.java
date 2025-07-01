package com.example.tllttbdd.ui.notifications;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.tllttbdd.data.model.ContactMessage;
import com.example.tllttbdd.databinding.FragmentNotificationsBinding;

import java.util.List;

public class NotificationsFragment extends Fragment {

    private FragmentNotificationsBinding binding;
    private NotificationsViewModel notificationsViewModel;
    private ContactMessageAdapter adapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentNotificationsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        notificationsViewModel = new ViewModelProvider(this).get(NotificationsViewModel.class);



        // Hiển thị danh sách tin nhắn đã gửi
        adapter = new ContactMessageAdapter();
        binding.recyclerMessages.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerMessages.setAdapter(adapter);

        notificationsViewModel.getMessages().observe(getViewLifecycleOwner(), messages -> {
            adapter.setMessages(messages);
        });

        // Hiển thị kết quả gửi
        notificationsViewModel.getSendResult().observe(getViewLifecycleOwner(), result -> {
            if (!TextUtils.isEmpty(result)) {
                Toast.makeText(getContext(), result, Toast.LENGTH_SHORT).show();
            }
        });

        // Nút gửi tin nhắn
        binding.btnSend.setOnClickListener(v -> {
            String phoneContact = binding.editPhoneContact.getText().toString().trim();
            String textContact = binding.editMessage.getText().toString().trim();
            if (phoneContact.isEmpty() || textContact.isEmpty()) {
                Toast.makeText(getContext(), "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }
            notificationsViewModel.sendMessage(requireContext(), phoneContact, textContact);
            binding.editMessage.setText("");
        });

        notificationsViewModel.fetchContactInfo(requireContext());

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}