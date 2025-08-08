package com.healthcare.healthcareproject.dto;

import java.util.Map;

public class FieldErrorResponse {
    private boolean success = false;
    private Map<String, String> errors;

    public FieldErrorResponse(Map<String, String> errors) {
        this.errors = errors;
    }

    public boolean isSuccess() { return success; }
    public Map<String, String> getErrors() { return errors; }
    public void setErrors(Map<String, String> errors) { this.errors = errors; }
} 