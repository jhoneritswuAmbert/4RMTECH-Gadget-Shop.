package com.fourrmtech.app.models;

public class AdminLoginResponse {

    private String token;
    public String getToken() { return token; }

    private boolean success;
    private String message;
    private Employee employee;

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public Employee getEmployee() {
        return employee;
    }

    public static class Employee {

        private int employee_id;
        private String full_name;
        private String username;
        private String contact_number;
        private String role;

        public int getEmployee_id() {
            return employee_id;
        }

        public String getFull_name() {
            return full_name;
        }

        public String getUsername() {
            return username;
        }

        public String getContact_number() {
            return contact_number;
        }

        public String getRole() {
            return role;
        }
    }
}