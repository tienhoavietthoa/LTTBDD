package com.example.tllttbdd.data.model;

import com.google.gson.annotations.SerializedName;

import kotlinx.serialization.Serializable;

public class ForgotPasswordRequest {
    @SerializedName("phone")
    public String phone;
    public ForgotPasswordRequest(String phone) {
        this.phone = phone;
    }
}
