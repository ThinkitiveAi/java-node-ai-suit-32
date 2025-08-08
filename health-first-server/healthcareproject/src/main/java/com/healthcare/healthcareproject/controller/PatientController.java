package com.healthcare.healthcareproject.controller;

import com.healthcare.healthcareproject.dto.PatientRegistrationRequest;
import com.healthcare.healthcareproject.dto.PatientRegistrationResponse;
import com.healthcare.healthcareproject.dto.PatientLoginRequest;
import com.healthcare.healthcareproject.dto.PatientLoginResponse;
import com.healthcare.healthcareproject.service.PatientService;
import com.healthcare.healthcareproject.service.PatientService.PatientRegistrationException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/patient")
public class PatientController {
    @Autowired
    private PatientService patientService;

    @Operation(summary = "Register a new patient with secure credentials and optional insurance data")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Patient registered successfully"),
        @ApiResponse(responseCode = "422", description = "Validation errors"),
        @ApiResponse(responseCode = "409", description = "Duplicate entry - email or phone")
    })
    @PostMapping("/register")
    public ResponseEntity<?> registerPatient(@RequestBody @Valid PatientRegistrationRequest request) {
        try {
            PatientRegistrationResponse response = patientService.registerPatient(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (PatientRegistrationException ex) {
            Map<String, String[]> errors = ex.getErrors();
            if (errors.containsKey("email") || errors.containsKey("phone_number")) {
                Map<String, Object> err = new HashMap<>();
                err.put("success", false);
                err.put("message", "Email or phone number already exists");
                err.put("error_code", "DUPLICATE_ENTRY");
                return ResponseEntity.status(HttpStatus.CONFLICT).body(err);
            }
            Map<String, Object> err = new HashMap<>();
            err.put("success", false);
            err.put("message", "Validation failed");
            err.put("errors", errors);
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(err);
        }
    }

    @Operation(summary = "Patient login with email or phone and password")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Login successful"),
        @ApiResponse(responseCode = "401", description = "Invalid credentials or inactive account"),
        @ApiResponse(responseCode = "400", description = "Validation error")
    })
    @PostMapping("/login")
    public ResponseEntity<?> loginPatient(@RequestBody @Valid PatientLoginRequest request) {
        PatientLoginResponse response = patientService.login(request);
        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else if ("INVALID_CREDENTIALS".equals(response.getErrorCode())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        } else if ("MISSING_CREDENTIALS".equals(response.getErrorCode())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationExceptions(MethodArgumentNotValidException ex) {
        BindingResult result = ex.getBindingResult();
        Map<String, String[]> errors = new HashMap<>();
        result.getFieldErrors().forEach(error -> errors.put(error.getField(), new String[]{error.getDefaultMessage()}));
        Map<String, Object> err = new HashMap<>();
        err.put("success", false);
        err.put("message", "Validation failed");
        err.put("errors", errors);
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(err);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleOtherExceptions(Exception ex) {
        Map<String, Object> err = new HashMap<>();
        err.put("success", false);
        err.put("message", "Internal server error");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(err);
    }
} 