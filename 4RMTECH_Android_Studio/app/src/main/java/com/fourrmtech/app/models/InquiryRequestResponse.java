package com.fourrmtech.app.models;

public class InquiryRequestResponse {

    private boolean success;
    private String message;
    private int inquiry_id;

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public int getInquiryId() {
        return inquiry_id;
    }
}