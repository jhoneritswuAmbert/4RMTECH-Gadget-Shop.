package com.fourrmtech.app.models;

public class Appointment {
    public enum Status { PENDING, APPROVED, READY_TO_CLAIM, COMPLETED, CANCELLED }

    private int id;
    private String customerName;
    private String contactNumber;
    private String deviceType;
    private String deviceModel;
    private String issue;
    private String date;
    private String time;
    private Status status;
    private String repairNotes;
    private String repairTotal;

    public Appointment(int id, String customerName, String contactNumber,
                       String deviceType, String deviceModel, String issue,
                       String date, String time, Status status) {
        this.id = id;
        this.customerName = customerName;
        this.contactNumber = contactNumber;
        this.deviceType = deviceType;
        this.deviceModel = deviceModel;
        this.issue = issue;
        this.date = date;
        this.time = time;
        this.status = status;
    }

    public int getId() { return id; }
    public String getCustomerName() { return customerName; }
    public String getContactNumber() { return contactNumber; }
    public String getDeviceType() { return deviceType; }
    public String getDeviceModel() { return deviceModel; }
    public String getIssue() { return issue; }
    public String getDate() { return date; }
    public String getTime() { return time; }
    public Status getStatus() { return status; }

    public void setRepairSummary(String notes, String total) { repairNotes = notes; repairTotal = total; }
    public String getRepairSummary() {
        if (repairNotes == null || repairNotes.trim().isEmpty()) return "";
        String total = repairTotal == null ? "Not specified" : "₱" + repairTotal;
        return "Repair summary\n\n" + repairNotes + "\n\nTotal: " + total
                + "\nPayment is handled at the shop. This is a repair summary.";
    }

    public void setStatus(Status status) { this.status = status; }

    public String getStatusLabel() {
        switch (status) {
            case PENDING:   return "Pending";
            case APPROVED:  return "Approved";
            case READY_TO_CLAIM: return "Ready to Claim";
            case COMPLETED: return "Completed";
            case CANCELLED: return "Cancelled";
            default:        return "Unknown";
        }
    }

    public String getDeviceSummary() {
        return deviceType + " — " + deviceModel;
    }
}
