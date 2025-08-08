package com.healthcare.healthcareproject.dto;

import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class PatientRegistrationRequest {
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

    @NotNull
    @Past
    private LocalDate dateOfBirth;

    @NotBlank
    private String gender;

    @NotNull
    private Map<String, String> address;

    private Map<String, String> emergencyContact;
    private List<String> medicalHistory;
    private Map<String, String> insuranceInfo;

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
    public LocalDate getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(LocalDate dateOfBirth) { this.dateOfBirth = dateOfBirth; }
    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }
    public Map<String, String> getAddress() { return address; }
    public void setAddress(Map<String, String> address) { this.address = address; }
    public Map<String, String> getEmergencyContact() { return emergencyContact; }
    public void setEmergencyContact(Map<String, String> emergencyContact) { this.emergencyContact = emergencyContact; }
    public List<String> getMedicalHistory() { return medicalHistory; }
    public void setMedicalHistory(List<String> medicalHistory) { this.medicalHistory = medicalHistory; }
    public Map<String, String> getInsuranceInfo() { return insuranceInfo; }
    public void setInsuranceInfo(Map<String, String> insuranceInfo) { this.insuranceInfo = insuranceInfo; }
} 