package com.fourrmtech.app.models;

public class Customer {

    private int id;
    private String firstName;
    private String lastName;
    private String email;
    private String contactNumber;
    private String address;
    private String joinedDate;

    // Separate address fields for profile editing
    private String street;
    private String barangay;
    private String city;
    private String postalCode;

    public Customer(
            int id,
            String firstName,
            String lastName,
            String email,
            String contactNumber,
            String address,
            String joinedDate) {

        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.contactNumber = contactNumber;
        this.address = address;
        this.joinedDate = joinedDate;
    }

    public int getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public String getAddress() {
        return address;
    }

    public String getJoinedDate() {
        return joinedDate;
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }

    public String getInitials() {

        if (firstName == null || firstName.isEmpty()
                || lastName == null || lastName.isEmpty()) {

            return "";
        }

        return String.valueOf(firstName.charAt(0))
                + String.valueOf(lastName.charAt(0));
    }

    // Address fields
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
        return postalCode;
    }

    // Setters
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public void setBarangay(String barangay) {
        this.barangay = barangay;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }
}