package com.healthcare.healthcareproject.service;

import com.healthcare.healthcareproject.dto.ProviderRegistrationRequest;
import com.healthcare.healthcareproject.dto.ProviderRegistrationResponse;
import com.healthcare.healthcareproject.dto.ProviderLoginRequest;
import com.healthcare.healthcareproject.dto.ProviderLoginResponse;
import com.healthcare.healthcareproject.model.Provider;
import com.healthcare.healthcareproject.repository.ProviderRepository;
import com.healthcare.healthcareproject.ProviderSpecialization;
import com.healthcare.healthcareproject.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class ProviderService {
    private static final int BCRYPT_ROUNDS = 12;
    private static final Set<String> SPECIALIZATIONS = Arrays.stream(ProviderSpecialization.values())
            .map(Enum::name).collect(java.util.stream.Collectors.toSet());

    @Autowired
    private ProviderRepository providerRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Transactional
    public ProviderRegistrationResponse registerProvider(ProviderRegistrationRequest request) {
        Map<String, String> errors = new HashMap<>();
        // Check specialization
        if (!SPECIALIZATIONS.contains(request.getSpecialization().toUpperCase())) {
            errors.put("specialization", "Invalid specialization");
        }
        // Check password match
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            errors.put("confirm_password", "Passwords do not match");
        }
        // Check duplicates
        if (providerRepository.findByEmail(request.getEmail()).isPresent()) {
            errors.put("email", "Email already in use.");
        }
        if (providerRepository.findByPhoneNumber(request.getPhoneNumber()).isPresent()) {
            errors.put("phone_number", "Phone number already in use.");
        }
        if (providerRepository.findByLicenseNumber(request.getLicenseNumber()).isPresent()) {
            errors.put("license_number", "License number already in use.");
        }
        if (!errors.isEmpty()) {
            throw new ProviderRegistrationException(errors);
        }
        // Hash password
        String passwordHash = BCrypt.hashpw(request.getPassword(), BCrypt.gensalt(BCRYPT_ROUNDS));
        // Map DTO to entity
        Provider provider = new Provider();
        provider.setFirstName(request.getFirstName());
        provider.setLastName(request.getLastName());
        provider.setEmail(request.getEmail());
        provider.setPhoneNumber(request.getPhoneNumber());
        provider.setPasswordHash(passwordHash);
        provider.setSpecialization(ProviderSpecialization.valueOf(request.getSpecialization().toUpperCase()));
        provider.setLicenseNumber(request.getLicenseNumber());
        provider.setYearsOfExperience(request.getYearsOfExperience());
        Provider.ClinicAddress address = new Provider.ClinicAddress();
        Map<String, String> addr = request.getClinicAddress();
        address.setStreet(addr.getOrDefault("street", ""));
        address.setCity(addr.getOrDefault("city", ""));
        address.setState(addr.getOrDefault("state", ""));
        address.setZip(addr.getOrDefault("zip", ""));
        provider.setClinicAddress(address);
        provider.setVerificationStatus(Provider.VerificationStatus.PENDING);
        provider.setIsActive(true);
        providerRepository.save(provider);
        // Prepare response
        ProviderRegistrationResponse resp = new ProviderRegistrationResponse();
        resp.setSuccess(true);
        resp.setMessage("Provider registered successfully. Verification email sent.");
        ProviderRegistrationResponse.Data data = new ProviderRegistrationResponse.Data();
        data.setProviderId(provider.getId().toString());
        data.setEmail(provider.getEmail());
        data.setVerificationStatus(provider.getVerificationStatus().name().toLowerCase());
        resp.setData(data);
        return resp;
    }

    public ProviderLoginResponse login(ProviderLoginRequest request) {
        ProviderLoginResponse response = new ProviderLoginResponse();
        Optional<Provider> providerOpt = providerRepository.findByEmail(request.getEmail());
        if (providerOpt.isEmpty()) {
            response.setSuccess(false);
            response.setMessage("Invalid credentials");
            response.setErrorCode("INVALID_CREDENTIALS");
            return response;
        }
        Provider provider = providerOpt.get();
        if (!provider.getVerificationStatus().equals(Provider.VerificationStatus.VERIFIED)) {
            response.setSuccess(false);
            response.setMessage("Provider is not verified");
            response.setErrorCode("NOT_VERIFIED");
            return response;
        }
        if (!provider.isIsActive()) {
            response.setSuccess(false);
            response.setMessage("Provider is not active");
            response.setErrorCode("NOT_ACTIVE");
            return response;
        }
        if (!BCrypt.checkpw(request.getPassword(), provider.getPasswordHash())) {
            response.setSuccess(false);
            response.setMessage("Invalid credentials");
            response.setErrorCode("INVALID_CREDENTIALS");
            return response;
        }
        // JWT claims
        String token = jwtUtil.generateTokenWithClaims(provider.getId().toString(), provider.getEmail(), "provider", 3600);
        Map<String, Object> providerData = new java.util.HashMap<>();
        providerData.put("id", provider.getId().toString());
        providerData.put("first_name", provider.getFirstName());
        providerData.put("last_name", provider.getLastName());
        providerData.put("email", provider.getEmail());
        providerData.put("phone_number", provider.getPhoneNumber());
        providerData.put("specialization", provider.getSpecialization().name());
        providerData.put("license_number", provider.getLicenseNumber());
        providerData.put("verification_status", provider.getVerificationStatus().name().toLowerCase());
        providerData.put("is_active", provider.isIsActive());
        Map<String, Object> data = new java.util.HashMap<>();
        data.put("access_token", token);
        data.put("expires_in", 3600);
        data.put("token_type", "Bearer");
        data.put("provider", providerData);
        response.setSuccess(true);
        response.setMessage("Login successful");
        response.setData(data);
        return response;
    }

    public static class ProviderRegistrationException extends RuntimeException {
        private final Map<String, String> errors;
        public ProviderRegistrationException(Map<String, String> errors) {
            this.errors = errors;
        }
        public Map<String, String> getErrors() { return errors; }
    }
} 