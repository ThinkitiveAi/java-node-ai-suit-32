package com.healthcare.healthcareproject.dto;

public class PatientRegistrationResponse {
    private boolean success;
    private String message;
    private Data data;

    public static class Data {
        private String patientId;
        private String email;
        private String phoneNumber;
        private boolean emailVerified;
        private boolean phoneVerified;
        // Getters and setters
        public String getPatientId() { return patientId; }
        public void setPatientId(String patientId) { this.patientId = patientId; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getPhoneNumber() { return phoneNumber; }
        public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
        public boolean isEmailVerified() { return emailVerified; }
        public void setEmailVerified(boolean emailVerified) { this.emailVerified = emailVerified; }
        public boolean isPhoneVerified() { return phoneVerified; }
        public void setPhoneVerified(boolean phoneVerified) { this.phoneVerified = phoneVerified; }
    }

    // Getters and setters
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public Data getData() { return data; }
    public void setData(Data data) { this.data = data; }
} 