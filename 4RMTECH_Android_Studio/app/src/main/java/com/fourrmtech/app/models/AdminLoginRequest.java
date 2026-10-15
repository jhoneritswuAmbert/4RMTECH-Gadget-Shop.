package com.fourrmtech.app.models;

public class AdminLoginRequest {

    private String username;
    private String password;

    public AdminLoginRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}