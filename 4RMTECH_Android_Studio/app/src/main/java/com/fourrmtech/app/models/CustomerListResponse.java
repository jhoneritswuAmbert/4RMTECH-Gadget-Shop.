package com.fourrmtech.app.models;

import java.util.List;

public class CustomerListResponse {

    private boolean success;
    private String message;
    private List<CustomerData> customers;

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public List<CustomerData> getCustomers() {
        return customers;
    }

    public static class CustomerData {

        private int customer_id;
        private String first_name;
        private String last_name;
        private String email;
        private String contact_number;

        private String street;
        private String barangay;
        private String city;
        private String postal_code;

        public int getCustomerId() {
            return customer_id;
        }

        public String getFirstName() {
            return first_name;
        }

        public String getLastName() {
            return last_name;
        }

        public String getEmail() {
            return email;
        }

        public String getContactNumber() {
            return contact_number;
        }

        public String getStreet() {
            return street;
        }

        public String getBarangay() {
            return barangay;
        }

        public String getCity() {
            return city;
        }

        public String getPostalCode() {
            return postal_code;
        }
    }
}