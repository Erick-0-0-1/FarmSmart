package com.agriconnect.backend.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity  // ← This tells Spring: "Hey, this is a database table!"
@Table(name = "users")  // ← The table name in MySQL will be "users"
public class user {

    // PRIMARY KEY - like a unique ID card for each user
    @Id  // ← This is the primary key
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // ← Auto-increment (1, 2, 3...)
    private Long id;

    // USERNAME - must be unique (no two users can have same username)
    @Column(unique = true, nullable = false, length = 50)
    private String username;

    // EMAIL - must be unique
    @Column(unique = true, nullable = false, length = 100)
    private String email;

    // PASSWORD - we'll encrypt this later
    @Column(nullable = false)
    private String password;

    // FULL NAME
    @Column(name = "full_name", length = 100)
    private String fullName;

    // PHONE NUMBER
    @Column(name = "phone_number", length = 20)
    private String phoneNumber;

    // LOCATION
    private String barangay;
    private String municipality;

    // USER TYPE (FARMER, BUYER, SUPPLIER, ADMIN)
    @Enumerated(EnumType.STRING)  // ← Store as text in database
    @Column(name = "user_type")
    private UserType userType = UserType.FARMER;  // Default is FARMER

    // PROFILE IMAGE
    @Column(name = "profile_image_url")
    private String profileImageUrl;

    // TIMESTAMPS - when user was created
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // IS ACTIVE - can disable users without deleting
    @Column(name = "is_active")
    private Boolean isActive = true;

    // ENUM for user types
    public enum UserType {
        FARMER, BUYER, SUPPLIER, ADMIN
    }

    // This runs automatically before saving to database
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    // This runs automatically before updating
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // CONSTRUCTORS
    public user() {
        // Empty constructor required by JPA
    }

    // GETTERS and SETTERS
    // These allow us to get and set values
    // Example: user.getEmail() or user.setEmail("test@example.com")

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public UserType getUserType() {
        return userType;
    }

    public void setUserType(UserType userType) {
        this.userType = userType;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
}