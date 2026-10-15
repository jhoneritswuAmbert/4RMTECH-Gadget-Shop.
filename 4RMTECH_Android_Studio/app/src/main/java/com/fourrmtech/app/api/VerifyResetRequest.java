package com.fourrmtech.app.api;
public class VerifyResetRequest {
    private final String request_id;
    private final String code;
    public VerifyResetRequest(String requestId, String code) { this.request_id = requestId; this.code = code; }
}
