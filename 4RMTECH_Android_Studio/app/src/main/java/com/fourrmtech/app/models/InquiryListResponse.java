package com.fourrmtech.app.models;

import java.util.List;

public class InquiryListResponse {

    private boolean success;
    private String message;
    private List<InquiryData> inquiries;

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public List<InquiryData> getInquiries() {
        return inquiries;
    }

    public static class InquiryData {

        private int inquiry_id;
        private boolean is_new;
        public boolean isNew() { return is_new; }
        private int customer_id;
        private String message;
        private String date_submitted;

        private String first_name;
        private String last_name;
        private String email;
        private String contact_number;

        public int getInquiryId() {
            return inquiry_id;
        }

        public int getCustomerId() {
            return customer_id;
        }

        public String getMessage() {
            return message;
        }

        public String getDateSubmitted() {
            return date_submitted;
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
    }
}