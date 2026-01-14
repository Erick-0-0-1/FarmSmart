package com.agriconnect.backend.dto;

import com.agriconnect.backend.model.user;

public class RegisterRequest {
    private String username;
    private String email;
    private String password;
    private String fullName;
    private String phoneNumber;
    private String barangay;
    private String municipality;
    private user.UserType userType;

    // Getters and Setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getBarangay() {
        return barangay;
    }

    public void setBarangay(String barangay) {
        this.barangay = barangay;
    }

    public String getMunicipality() {
        return municipality;
    }

    public void setMunicipality(String municipality) {
        this.municipality = municipality;
    }

    public user.UserType getUserType() {
        return userType;
    }

    public void setUserType(user.UserType userType) {
        this.userType = userType;
    }
}

