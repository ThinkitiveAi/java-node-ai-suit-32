package com.healthcare.healthcareproject.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Entity
@EntityListeners(AuditingEntityListener.class)
public class Patient {
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
    @Past
    private LocalDate dateOfBirth;

    @NotNull
    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Embedded
    private Address address;

    @Embedded
    private EmergencyContact emergencyContact;

    @ElementCollection
    private List<String> medicalHistory;

    @Embedded
    private InsuranceInfo insuranceInfo;

    private boolean emailVerified = false;
    private boolean phoneVerified = false;
    private boolean isActive = true;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;

    public enum Gender {
        MALE, FEMALE, OTHER, PREFER_NOT_TO_SAY
    }

    @Embeddable
    public static class Address {
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

    @Embeddable
    public static class EmergencyContact {
        @Size(max = 100)
        private String name;
        @Pattern(regexp = "^\\+\\d{10,15}$", message = "Invalid E.164 phone format")
        private String phone;
        @Size(max = 50)
        private String relationship;
        // Getters and setters
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getPhone() { return phone; }
        public void setPhone(String phone) { this.phone = phone; }
        public String getRelationship() { return relationship; }
        public void setRelationship(String relationship) { this.relationship = relationship; }
    }

    @Embeddable
    public static class InsuranceInfo {
        private String provider;
        private String policyNumber;
        // Getters and setters
        public String getProvider() { return provider; }
        public void setProvider(String provider) { this.provider = provider; }
        public String getPolicyNumber() { return policyNumber; }
        public void setPolicyNumber(String policyNumber) { this.policyNumber = policyNumber; }
    }

    // Getters and setters for Patient fields
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
    public LocalDate getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(LocalDate dateOfBirth) { this.dateOfBirth = dateOfBirth; }
    public Gender getGender() { return gender; }
    public void setGender(Gender gender) { this.gender = gender; }
    public Address getAddress() { return address; }
    public void setAddress(Address address) { this.address = address; }
    public EmergencyContact getEmergencyContact() { return emergencyContact; }
    public void setEmergencyContact(EmergencyContact emergencyContact) { this.emergencyContact = emergencyContact; }
    public List<String> getMedicalHistory() { return medicalHistory; }
    public void setMedicalHistory(List<String> medicalHistory) { this.medicalHistory = medicalHistory; }
    public InsuranceInfo getInsuranceInfo() { return insuranceInfo; }
    public void setInsuranceInfo(InsuranceInfo insuranceInfo) { this.insuranceInfo = insuranceInfo; }
    public boolean isEmailVerified() { return emailVerified; }
    public void setEmailVerified(boolean emailVerified) { this.emailVerified = emailVerified; }
    public boolean isPhoneVerified() { return phoneVerified; }
    public void setPhoneVerified(boolean phoneVerified) { this.phoneVerified = phoneVerified; }
    public boolean isIsActive() { return isActive; }
    public void setIsActive(boolean isActive) { this.isActive = isActive; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
} 