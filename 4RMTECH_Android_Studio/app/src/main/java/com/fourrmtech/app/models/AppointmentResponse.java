package com.fourrmtech.app.models;

import java.util.List;

public class AppointmentResponse {

    private boolean success;
    private String message;
    private List<AppointmentData> appointments;

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public List<AppointmentData> getAppointments() {
        return appointments;
    }

    public static class AppointmentData {

        private int appointment_id;
        private int customer_id;
        private int employee_id;
        private int device_id;

        private String appointment_date;
        private String appointment_time;
        private String service_description;
        private String status;
        private String repair_notes;
        private String repair_total;

        private String first_name;
        private String last_name;
        private String contact_number;
        private String email;

        private String device_model;
        private String device_type;

        private String employee_name;
        private String employee_role;

        public int getAppointmentId() {
            return appointment_id;
        }

        public int getCustomerId() {
            return customer_id;
        }

        public int getEmployeeId() {
            return employee_id;
        }

        public int getDeviceId() {
            return device_id;
        }

        public String getAppointmentDate() {
            return appointment_date;
        }

        public String getAppointmentTime() {
            return appointment_time;
        }

        public String getServiceDescription() {
            return service_description;
        }

        public String getRepairNotes() { return repair_notes; }
        public String getRepairTotal() { return repair_total; }

        public String getStatus() {
            return status;
        }

        public String getDeviceModel() {
            return device_model;
        }

        public String getDeviceType() {
            return device_type;
        }

        public String getEmployeeName() {
            return employee_name;
        }

        public String getEmployeeRole() {
            return employee_role;
        }

        public String getFirstName() {
            return first_name;
        }

        public String getLastName() {
            return last_name;
        }

        public String getContactNumber() {
            return contact_number;
        }

        public String getEmail() {
            return email;
        }
    }
}