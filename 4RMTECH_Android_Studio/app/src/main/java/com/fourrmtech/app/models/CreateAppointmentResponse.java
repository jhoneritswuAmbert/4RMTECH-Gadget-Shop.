package com.fourrmtech.app.models;

public class CreateAppointmentResponse {

    private boolean success;
    private String message;
    private int appointment_id;

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public int getAppointmentId() {
        return appointment_id;
    }
}