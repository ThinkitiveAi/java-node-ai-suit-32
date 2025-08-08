package com.healthcare.healthcareproject.controller;

import com.healthcare.healthcareproject.dto.ProviderRegistrationRequest;
import com.healthcare.healthcareproject.dto.ProviderRegistrationResponse;
import com.healthcare.healthcareproject.dto.FieldErrorResponse;
import com.healthcare.healthcareproject.dto.ProviderLoginRequest;
import com.healthcare.healthcareproject.dto.ProviderLoginResponse;
import com.healthcare.healthcareproject.service.ProviderService;
import com.healthcare.healthcareproject.service.ProviderService.ProviderRegistrationException;
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
@RequestMapping("/api/v1/provider")
public class ProviderController {
    @Autowired
    private ProviderService providerService;

    @Operation(summary = "Register a new healthcare provider")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Provider registered successfully"),
        @ApiResponse(responseCode = "400", description = "Bad Request"),
        @ApiResponse(responseCode = "409", description = "Duplicate email/phone"),
        @ApiResponse(responseCode = "422", description = "Validation errors")
    })
    @PostMapping("/register")
    public ResponseEntity<?> registerProvider(@RequestBody @Valid ProviderRegistrationRequest request) {
        try {
            ProviderRegistrationResponse response = providerService.registerProvider(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (ProviderRegistrationException ex) {
            Map<String, String> errors = ex.getErrors();
            if (errors.containsKey("email") || errors.containsKey("phone_number") || errors.containsKey("license_number")) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(new FieldErrorResponse(errors));
            }
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(new FieldErrorResponse(errors));
        }
    }

    @Operation(summary = "Provider login with email and password")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Login successful"),
        @ApiResponse(responseCode = "401", description = "Invalid credentials or not verified/active"),
        @ApiResponse(responseCode = "400", description = "Validation error")
    })
    @PostMapping("/login")
    public ResponseEntity<?> loginProvider(@RequestBody @Valid ProviderLoginRequest request) {
        ProviderLoginResponse response = providerService.login(request);
        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else if ("INVALID_CREDENTIALS".equals(response.getErrorCode())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        } else if ("NOT_VERIFIED".equals(response.getErrorCode()) || "NOT_ACTIVE".equals(response.getErrorCode())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<FieldErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        BindingResult result = ex.getBindingResult();
        Map<String, String> errors = new HashMap<>();
        result.getFieldErrors().forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new FieldErrorResponse(errors));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<FieldErrorResponse> handleOtherExceptions(Exception ex) {
        Map<String, String> errors = new HashMap<>();
        errors.put("error", "Internal server error");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new FieldErrorResponse(errors));
    }
} 