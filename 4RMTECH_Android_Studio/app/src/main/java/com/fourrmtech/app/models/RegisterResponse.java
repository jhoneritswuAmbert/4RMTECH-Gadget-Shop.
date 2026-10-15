package com.fourrmtech.app.models;

public class RegisterResponse {

    private String token;
    public String getToken() { return token; }

    private boolean success;
    private String message;
    private int customer_id;
    private String first_name;
    private String last_name;

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public int getCustomerId() {
        return customer_id;
    }

    public String getFirstName() {
        return first_name;
    }

    public String getLastName() {
        return last_name;
    }
}