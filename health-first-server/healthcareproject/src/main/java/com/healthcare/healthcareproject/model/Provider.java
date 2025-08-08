package com.healthcare.healthcareproject.model;

import com.healthcare.healthcareproject.ProviderSpecialization;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@EntityListeners(AuditingEntityListener.class)
public class Provider {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @NotBlank
    @Size(min = 2, max = 50)
    private String firstName;

    @NotBlank
    @Size(min = 2, max = 50)
    private String lastName;

    @NotBlank
    @Email
    @Column(unique = true)
    private String email;

    @NotBlank
    @Pattern(regexp = "^\\+\\d{10,15}$", message = "Invalid E.164 phone format")
    @Column(unique = true)
    private String phoneNumber;

    @NotBlank
    private String passwordHash;

    @NotNull
    @Enumerated(EnumType.STRING)
    private ProviderSpecialization specialization;

    @NotBlank
    @Pattern(regexp = "^[a-zA-Z0-9]+$", message = "License must be alphanumeric")
    @Column(unique = true)
    private String licenseNumber;

    @Min(0)
    @Max(50)
    private int yearsOfExperience;

    @Embedded
    private ClinicAddress clinicAddress;

    @NotNull
    @Enumerated(EnumType.STRING)
    private VerificationStatus verificationStatus = VerificationStatus.PENDING;

    private boolean isActive = true;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;

    // Getters and setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    public ProviderSpecialization getSpecialization() { return specialization; }
    public void setSpecialization(ProviderSpecialization specialization) { this.specialization = specialization; }
    public String getLicenseNumber() { return licenseNumber; }
    public void setLicenseNumber(String licenseNumber) { this.licenseNumber = licenseNumber; }
    public int getYearsOfExperience() { return yearsOfExperience; }
    public void setYearsOfExperience(int yearsOfExperience) { this.yearsOfExperience = yearsOfExperience; }
    public ClinicAddress getClinicAddress() { return clinicAddress; }
    public void setClinicAddress(ClinicAddress clinicAddress) { this.clinicAddress = clinicAddress; }
    public VerificationStatus getVerificationStatus() { return verificationStatus; }
    public void setVerificationStatus(VerificationStatus verificationStatus) { this.verificationStatus = verificationStatus; }
    public boolean isIsActive() { return isActive; }
    public void setIsActive(boolean isActive) { this.isActive = isActive; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }

    public enum VerificationStatus {
        PENDING, VERIFIED, REJECTED
    }

    @Embeddable
    public static class ClinicAddress {
        @NotBlank
        @Size(max = 200)
        private String street;
        @NotBlank
        @Size(max = 100)
        private String city;
        @NotBlank
        @Size(max = 50)
        private String state;
        @NotBlank
        @Size(max = 20)
        private String zip;
        // Getters and setters
        public String getStreet() { return street; }
        public void setStreet(String street) { this.street = street; }
        public String getCity() { return city; }
        public void setCity(String city) { this.city = city; }
        public String getState() { return state; }
        public void setState(String state) { this.state = state; }
        public String getZip() { return zip; }
        public void setZip(String zip) { this.zip = zip; }
    }
} 