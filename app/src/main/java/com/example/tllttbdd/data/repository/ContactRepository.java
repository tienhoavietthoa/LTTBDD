package com.example.tllttbdd.data.repository;

import com.example.tllttbdd.data.model.ApiResponse;
import com.example.tllttbdd.data.model.ContactMessage;
import com.example.tllttbdd.data.network.ApiClient;
import com.example.tllttbdd.data.network.ContactApi;

import java.util.List;
import java.util.Map;

import retrofit2.Call;

public class ContactRepository {
    private final ContactApi contactApi = ApiClient.getClient().create(ContactApi.class);

    public Call<Map<String, Object>> getContactInfo(int idLogin) {
        return contactApi.getContactInfo(idLogin);
    }

    public Call<Map<String, List<ContactMessage>>> getMessages(int idLogin) {
        return contactApi.getMessages(idLogin);
    }

    public Call<ApiResponse> sendMessage(int idLogin, String phoneContact, String textContact) {
        return contactApi.sendMessage(idLogin, phoneContact, textContact);
    }
}