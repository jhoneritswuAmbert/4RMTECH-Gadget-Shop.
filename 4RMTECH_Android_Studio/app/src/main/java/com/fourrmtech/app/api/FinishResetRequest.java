package com.fourrmtech.app.api;
public class FinishResetRequest {
    private final String reset_token;
    private final String password;
    private final String confirm_password;
    public FinishResetRequest(String token, String password, String confirmation) {
        this.reset_token = token; this.password = password; this.confirm_password = confirmation;
    }
}
