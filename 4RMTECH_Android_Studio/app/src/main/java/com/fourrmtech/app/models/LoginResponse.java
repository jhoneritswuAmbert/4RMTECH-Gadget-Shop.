package com.fourrmtech.app.models;

public class LoginResponse {

    private String token;
    public String getToken() { return token; }

    private boolean success;
    private String message;

    private int customer_id;
    private String first_name;
    private String last_name;
    private String email;
    private String contact_number;

    private String address;

    // Separate address fields
    private String street;
    private String barangay;
    private String city;
    private String postal_code;

    private String joined_date;

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

    public String getEmail() {
        return email;
    }

    public String getContactNumber() {
        return contact_number;
    }

    public String getAddress() {
        return address;
    }

    public String getStreet() {
        return street;
    }

    public String getBarangay() {
        return barangay;
    }

    public String getCity() {
        return city;
    }

    public String getPostalCode() {
        return postal_code;
    }

    public String getJoinedDate() {
        return joined_date;
    }
}