package com.example.tllttbdd.data.model;

public class ResetPasswordRequest {
    public String phone, code, newPassword;
    public ResetPasswordRequest(String phone, String code, String newPassword) {
        this.phone = phone; this.code = code; this.newPassword = newPassword;
    }
}