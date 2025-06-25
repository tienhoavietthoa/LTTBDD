package com.example.tllttbdd.ui.account;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import com.example.tllttbdd.data.model.User;
import com.example.tllttbdd.data.repository.AccountRepository;
import java.util.Map;

public class AccountViewModel extends ViewModel {
    private final AccountRepository repository = new AccountRepository();

    public LiveData<User> getUser() { return repository.getUser(); }
    public LiveData<Map<String, Object>> getUpdateResult() { return repository.getUpdateResult(); }
    public LiveData<Map<String, Object>> getPasswordResult() { return repository.getPasswordResult(); }
    public LiveData<Map<String, Object>> getDeleteResult() { return repository.getDeleteResult(); }

    public void fetchProfile(int idLogin) {
        repository.fetchProfile(idLogin);
    }

    public void updateProfile(int idLogin, String name, String phone, String email, String dob) {
        repository.updateProfile(idLogin, name, phone, email, dob);
    }

    public void changePassword(int idLogin, String oldPass, String newPass) {
        repository.changePassword(idLogin, oldPass, newPass);
    }

    public void deleteAccount(int idLogin) {
        repository.deleteAccount(idLogin);
    }
}