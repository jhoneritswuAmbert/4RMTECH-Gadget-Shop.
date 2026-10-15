package com.fourrmtech.app.models;

public class UpdateAppointmentStatusRequest {

    private int appointment_id;
    private String status;
    private String repair_notes;
    private String repair_total;

    public UpdateAppointmentStatusRequest(
            int appointment_id,
            String status) {

        this.appointment_id = appointment_id;
        this.status = status;
    }

    public UpdateAppointmentStatusRequest(int appointmentId, String status, String notes, String total) {
        this(appointmentId, status);
        this.repair_notes = notes;
        this.repair_total = total;
    }

    public int getAppointment_id() {
        return appointment_id;
    }

    public String getStatus() {
        return status;
    }
}