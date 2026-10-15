package com.fourrmtech.app.models;

public class InquiryRequest {

    private int customer_id;
    private String message;

    public InquiryRequest(
            int customer_id,
            String message) {

        this.customer_id = customer_id;
        this.message = message;
    }

    public int getCustomer_id() {
        return customer_id;
    }

    public String getMessage() {
        return message;
    }
}