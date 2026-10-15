package com.fourrmtech.app.api;

public class ForgotPasswordResponse {

    private String request_id;
    private String reset_token;
    public String getRequestId() { return request_id; }
    public String getResetToken() { return reset_token; }
    private boolean success;
    private String message;
    private Integer customer_id;

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public Integer getCustomer_id() {
        return customer_id;
    }
}