package com.healthcare.healthcareproject.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class PatientLoginRequest {
    private String email;
    private String phoneNumber;
    @NotBlank
    private String password;

    @Email
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    @Pattern(regexp = "^\\+\\d{10,15}$", message = "Invalid E.164 phone format")
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
} 