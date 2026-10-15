package com.fourrmtech.app.models;

public class UpdateCustomerRequest {

    private int customer_id;
    private String first_name;
    private String last_name;
    private String contact_number;
    private String street;
    private String barangay;
    private String city;
    private String postal_code;

    public UpdateCustomerRequest(
            int customer_id,
            String first_name,
            String last_name,
            String contact_number,
            String street,
            String barangay,
            String city,
            String postal_code) {

        this.customer_id = customer_id;
        this.first_name = first_name;
        this.last_name = last_name;
        this.contact_number = contact_number;
        this.street = street;
        this.barangay = barangay;
        this.city = city;
        this.postal_code = postal_code;
    }
}