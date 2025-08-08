package com.healthcare.healthcareproject.dto;

public class ProviderRegistrationResponse {
    private boolean success;
    private String message;
    private Data data;

    public static class Data {
        private String providerId;
        private String email;
        private String verificationStatus;
        // Getters and setters
        public String getProviderId() { return providerId; }
        public void setProviderId(String providerId) { this.providerId = providerId; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getVerificationStatus() { return verificationStatus; }
        public void setVerificationStatus(String verificationStatus) { this.verificationStatus = verificationStatus; }
    }

    // Getters and setters
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public Data getData() { return data; }
    public void setData(Data data) { this.data = data; }
} 