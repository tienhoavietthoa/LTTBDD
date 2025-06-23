package com.example.tllttbdd.data.model;
public class LoginResponse {
    public boolean success;
    public String error;
    public String message; // nếu backend trả về message
    public User user;
}