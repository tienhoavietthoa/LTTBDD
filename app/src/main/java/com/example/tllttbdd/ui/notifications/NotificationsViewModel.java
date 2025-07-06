package com.example.tllttbdd.ui.notifications;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.tllttbdd.data.model.ContactMessage;
import com.example.tllttbdd.data.model.ApiResponse;
import com.example.tllttbdd.data.repository.ContactRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationsViewModel extends ViewModel {

    private final MutableLiveData<List<ContactMessage>> messages = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<String> nameContact = new MutableLiveData<>("");
    private final MutableLiveData<String> sendResult = new MutableLiveData<>("");
    private final ContactRepository repository = new ContactRepository();
    private int idLogin = -1;

    public LiveData<List<ContactMessage>> getMessages() { return messages; }
    public LiveData<String> getNameContact() { return nameContact; }
    public LiveData<String> getSendResult() { return sendResult; }

    public void fetchContactInfo(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        idLogin = prefs.getInt("id_login", -1);
        if (idLogin <= 0) return;
        repository.getContactInfo(idLogin).enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Object name = response.body().get("name_contact");
                    nameContact.setValue(name != null ? name.toString() : "");
                    Object msgs = response.body().get("messages");
                    if (msgs instanceof List) {
                        List<ContactMessage> msgList = new ArrayList<>();
                        for (Object obj : (List<?>) msgs) {
                            if (obj instanceof Map) {
                                Map<?, ?> map = (Map<?, ?>) obj;
                                ContactMessage m = new ContactMessage();
                                m.id_contact = (int) (double) map.get("id_contact");
                                m.name_contact = String.valueOf(map.get("name_contact"));
                                // Sửa dòng này:
                                m.phone_contact = String.valueOf(map.get("phone_contact")); // luôn ép về String
                                m.text_contact = String.valueOf(map.get("text_contact"));
                                m.date_contact = String.valueOf(map.get("date_contact"));
                                msgList.add(m);
                            }
                        }
                        messages.setValue(msgList);
                    }
                }
            }
            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) { }
        });
    }

    public void sendMessage(Context context, String phoneContact, String textContact) {
        if (idLogin <= 0) return;
        repository.sendMessage(idLogin, phoneContact, textContact).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    sendResult.setValue(response.body().getMessage());
                    fetchContactInfo(context); // reload messages
                }
            }
            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                sendResult.setValue("Gửi thất bại!");
            }
        });
    }
}