package com.healthcare.healthcareproject.service;

import com.healthcare.healthcareproject.dto.PatientRegistrationRequest;
import com.healthcare.healthcareproject.dto.PatientRegistrationResponse;
import com.healthcare.healthcareproject.dto.PatientLoginRequest;
import com.healthcare.healthcareproject.dto.PatientLoginResponse;
import com.healthcare.healthcareproject.model.Patient;
import com.healthcare.healthcareproject.repository.PatientRepository;
import com.healthcare.healthcareproject.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Period;
import java.util.HashMap;
import java.util.Map;

@Service
public class PatientService {
    private static final int BCRYPT_ROUNDS = 12;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Transactional
    public PatientRegistrationResponse registerPatient(PatientRegistrationRequest request) {
        Map<String, String[]> errors = new HashMap<>();
        // Validate age
        if (request.getDateOfBirth() == null || !request.getDateOfBirth().isBefore(LocalDate.now())) {
            errors.put("date_of_birth", new String[]{"Date of birth must be in the past"});
        } else if (Period.between(request.getDateOfBirth(), LocalDate.now()).getYears() < 13) {
            errors.put("date_of_birth", new String[]{"Must be at least 13 years old"});
        }
        // Validate gender
        if (request.getGender() == null ||
            !(request.getGender().equalsIgnoreCase("male") ||
              request.getGender().equalsIgnoreCase("female") ||
              request.getGender().equalsIgnoreCase("other") ||
              request.getGender().equalsIgnoreCase("prefer_not_to_say"))) {
            errors.put("gender", new String[]{"Invalid gender value"});
        }
        // Password match
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            errors.put("confirm_password", new String[]{"Passwords do not match"});
        }
        // Duplicate checks
        if (patientRepository.findByEmail(request.getEmail()).isPresent()) {
            errors.put("email", new String[]{"Email is already registered"});
        }
        if (patientRepository.findByPhoneNumber(request.getPhoneNumber()).isPresent()) {
            errors.put("phone_number", new String[]{"Phone number is already registered"});
        }
        if (!errors.isEmpty()) {
            throw new PatientRegistrationException(errors);
        }
        // Hash password
        String passwordHash = BCrypt.hashpw(request.getPassword(), BCrypt.gensalt(BCRYPT_ROUNDS));
        // Map DTO to entity
        Patient patient = new Patient();
        patient.setFirstName(request.getFirstName());
        patient.setLastName(request.getLastName());
        patient.setEmail(request.getEmail());
        patient.setPhoneNumber(request.getPhoneNumber());
        patient.setPasswordHash(passwordHash);
        patient.setDateOfBirth(request.getDateOfBirth());
        patient.setGender(Patient.Gender.valueOf(request.getGender().toUpperCase()));
        // Address
        Patient.Address address = new Patient.Address();
        Map<String, String> addr = request.getAddress();
        address.setStreet(addr.getOrDefault("street", ""));
        address.setCity(addr.getOrDefault("city", ""));
        address.setState(addr.getOrDefault("state", ""));
        address.setZip(addr.getOrDefault("zip", ""));
        patient.setAddress(address);
        // Emergency Contact
        if (request.getEmergencyContact() != null) {
            Patient.EmergencyContact ec = new Patient.EmergencyContact();
            Map<String, String> ecMap = request.getEmergencyContact();
            ec.setName(ecMap.getOrDefault("name", null));
            ec.setPhone(ecMap.getOrDefault("phone", null));
            ec.setRelationship(ecMap.getOrDefault("relationship", null));
            patient.setEmergencyContact(ec);
        }
        // Medical History
        patient.setMedicalHistory(request.getMedicalHistory());
        // Insurance Info
        if (request.getInsuranceInfo() != null) {
            Patient.InsuranceInfo ins = new Patient.InsuranceInfo();
            Map<String, String> insMap = request.getInsuranceInfo();
            ins.setProvider(insMap.getOrDefault("provider", null));
            ins.setPolicyNumber(insMap.getOrDefault("policy_number", null));
            patient.setInsuranceInfo(ins);
        }
        patient.setEmailVerified(false);
        patient.setPhoneVerified(false);
        patient.setIsActive(true);
        patientRepository.save(patient);
        // Prepare response
        PatientRegistrationResponse resp = new PatientRegistrationResponse();
        resp.setSuccess(true);
        resp.setMessage("Patient registered successfully. Verification email sent.");
        PatientRegistrationResponse.Data data = new PatientRegistrationResponse.Data();
        data.setPatientId(patient.getId().toString());
        data.setEmail(patient.getEmail());
        data.setPhoneNumber(patient.getPhoneNumber());
        data.setEmailVerified(false);
        data.setPhoneVerified(false);
        resp.setData(data);
        return resp;
    }

    public PatientLoginResponse login(PatientLoginRequest request) {
        PatientLoginResponse response = new PatientLoginResponse();
        if ((request.getEmail() == null || request.getEmail().isBlank()) && (request.getPhoneNumber() == null || request.getPhoneNumber().isBlank())) {
            response.setSuccess(false);
            response.setMessage("Email or phone number is required");
            response.setErrorCode("MISSING_CREDENTIALS");
            return response;
        }
        if (request.getPassword() == null || request.getPassword().isBlank()) {
            response.setSuccess(false);
            response.setMessage("Password is required");
            response.setErrorCode("MISSING_CREDENTIALS");
            return response;
        }
        Patient patient = null;
        if (request.getEmail() != null && !request.getEmail().isBlank()) {
            patient = patientRepository.findByEmail(request.getEmail()).orElse(null);
        } else if (request.getPhoneNumber() != null && !request.getPhoneNumber().isBlank()) {
            patient = patientRepository.findByPhoneNumber(request.getPhoneNumber()).orElse(null);
        }
        if (patient == null || !patient.isIsActive()) {
            response.setSuccess(false);
            response.setMessage("Invalid credentials");
            response.setErrorCode("INVALID_CREDENTIALS");
            return response;
        }
        if (!BCrypt.checkpw(request.getPassword(), patient.getPasswordHash())) {
            response.setSuccess(false);
            response.setMessage("Invalid credentials");
            response.setErrorCode("INVALID_CREDENTIALS");
            return response;
        }
        // JWT claims
        String token = jwtUtil.generateTokenWithClaims(patient.getId().toString(), patient.getEmail(), "patient", 1800);
        Map<String, Object> patientData = new java.util.HashMap<>();
        patientData.put("id", patient.getId().toString());
        patientData.put("first_name", patient.getFirstName());
        patientData.put("last_name", patient.getLastName());
        patientData.put("email", patient.getEmail());
        patientData.put("phone_number", patient.getPhoneNumber());
        patientData.put("gender", patient.getGender().name().toLowerCase());
        patientData.put("email_verified", patient.isEmailVerified());
        patientData.put("phone_verified", patient.isPhoneVerified());
        patientData.put("is_active", patient.isIsActive());
        Map<String, Object> data = new java.util.HashMap<>();
        data.put("access_token", token);
        data.put("expires_in", 1800);
        data.put("token_type", "Bearer");
        data.put("patient", patientData);
        response.setSuccess(true);
        response.setMessage("Login successful");
        response.setData(data);
        return response;
    }

    public static class PatientRegistrationException extends RuntimeException {
        private final Map<String, String[]> errors;
        public PatientRegistrationException(Map<String, String[]> errors) {
            this.errors = errors;
        }
        public Map<String, String[]> getErrors() { return errors; }
    }
} 