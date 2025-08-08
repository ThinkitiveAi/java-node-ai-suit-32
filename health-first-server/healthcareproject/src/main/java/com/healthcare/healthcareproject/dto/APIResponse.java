package com.healthcare.healthcareproject.dto;

import java.util.Map;

public class APIResponse<T> {
    private boolean success;
    private String message;
    private T data;
    private String errorCode;
    private Map<String, Object> errors;

    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public T getData() { return data; }
    public void setData(T data) { this.data = data; }
    public String getErrorCode() { return errorCode; }
    public void setErrorCode(String errorCode) { this.errorCode = errorCode; }
    public Map<String, Object> getErrors() { return errors; }
    public void setErrors(Map<String, Object> errors) { this.errors = errors; }
} 