package com.fourrmtech.app.models;

public class Inquiry {

    private int id;
    private String customerName;
    private String email;
    private String contactNumber;
    private String message;
    private String dateSubmitted;

    public Inquiry(
            int id,
            String customerName,
            String email,
            String contactNumber,
            String message,
            String dateSubmitted) {

        this.id = id;
        this.customerName = customerName;
        this.email = email;
        this.contactNumber = contactNumber;
        this.message = message;
        this.dateSubmitted = dateSubmitted;
    }

    public int getId() {
        return id;
    }

    public String getCustomerName() {
        return customerName;
    }

    public String getEmail() {
        return email;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public String getMessage() {
        return message;
    }

    public String getDateSubmitted() {
        return dateSubmitted;
    }
}