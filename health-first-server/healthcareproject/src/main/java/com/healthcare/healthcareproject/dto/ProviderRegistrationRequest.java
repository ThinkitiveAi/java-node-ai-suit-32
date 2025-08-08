package com.healthcare.healthcareproject.dto;

import jakarta.validation.constraints.*;
import java.util.Map;

public class ProviderRegistrationRequest {
    @NotBlank
    @Size(min = 2, max = 50)
    private String firstName;

    @NotBlank
    @Size(min = 2, max = 50)
    private String lastName;

    @NotBlank
    @Email
    private String email;

    @NotBlank
    @Pattern(regexp = "^\\+\\d{10,15}$", message = "Invalid E.164 phone format")
    private String phoneNumber;

    @NotBlank
    @Size(min = 8, message = "Password must be at least 8 characters")
    @Pattern(
        regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
        message = "Password must include uppercase, lowercase, number, and special character."
    )
    private String password;

    @NotBlank
    private String confirmPassword;

    @NotBlank
    private String specialization;

    @NotBlank
    @Pattern(regexp = "^[a-zA-Z0-9]+$", message = "License must be alphanumeric")
    private String licenseNumber;

    @Min(0)
    @Max(50)
    private int yearsOfExperience;

    @NotNull
    private Map<String, String> clinicAddress;

    // Getters and setters
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getConfirmPassword() { return confirmPassword; }
    public void setConfirmPassword(String confirmPassword) { this.confirmPassword = confirmPassword; }
    public String getSpecialization() { return specialization; }
    public void setSpecialization(String specialization) { this.specialization = specialization; }
    public String getLicenseNumber() { return licenseNumber; }
    public void setLicenseNumber(String licenseNumber) { this.licenseNumber = licenseNumber; }
    public int getYearsOfExperience() { return yearsOfExperience; }
    public void setYearsOfExperience(int yearsOfExperience) { this.yearsOfExperience = yearsOfExperience; }
    public Map<String, String> getClinicAddress() { return clinicAddress; }
    public void setClinicAddress(Map<String, String> clinicAddress) { this.clinicAddress = clinicAddress; }
} 