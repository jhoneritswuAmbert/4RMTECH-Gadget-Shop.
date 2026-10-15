package com.fourrmtech.app.models;

public class CancelAppointmentRequest {

    private int appointment_id;
    private int customer_id;

    public CancelAppointmentRequest(
            int appointment_id,
            int customer_id) {

        this.appointment_id = appointment_id;
        this.customer_id = customer_id;
    }
}