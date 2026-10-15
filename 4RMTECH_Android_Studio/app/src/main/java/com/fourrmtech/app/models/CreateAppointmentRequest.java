package com.fourrmtech.app.models;

public class CreateAppointmentRequest {

    private int customer_id;
    private String device_type;
    private String device_model;
    private String service_description;
    private String appointment_date;
    private String appointment_time;

    public CreateAppointmentRequest(
            int customer_id,
            String device_type,
            String device_model,
            String service_description,
            String appointment_date,
            String appointment_time) {

        this.customer_id = customer_id;
        this.device_type = device_type;
        this.device_model = device_model;
        this.service_description = service_description;
        this.appointment_date = appointment_date;
        this.appointment_time = appointment_time;
    }
}