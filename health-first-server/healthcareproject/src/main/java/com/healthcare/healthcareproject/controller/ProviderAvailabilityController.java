package com.healthcare.healthcareproject.controller;

import com.healthcare.healthcareproject.dto.ProviderAvailabilityRequest;
import com.healthcare.healthcareproject.dto.ProviderAvailabilityResponse;
import com.healthcare.healthcareproject.dto.APIResponse;
import com.healthcare.healthcareproject.service.ProviderAvailabilityService;
import com.healthcare.healthcareproject.model.AppointmentSlot;
import com.healthcare.healthcareproject.repository.ProviderAvailabilityRepository;
import com.healthcare.healthcareproject.repository.AppointmentSlotRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.UUID;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.format.annotation.DateTimeFormat;

@RestController
@RequestMapping("/api/v1/provider/availability")
public class ProviderAvailabilityController {
    @Autowired
    private ProviderAvailabilityService availabilityService;
    @Autowired
    private ProviderAvailabilityRepository availabilityRepository;
    @Autowired
    private AppointmentSlotRepository slotRepository;

    @Operation(summary = "Create provider availability slots (with recurrence and time zone support)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Availability slots created successfully"),
        @ApiResponse(responseCode = "400", description = "Validation error or conflict")
    })
    @PostMapping
    public ResponseEntity<APIResponse<ProviderAvailabilityResponse>> createAvailability(
            @RequestHeader("X-Provider-Id") UUID providerId,
            @RequestBody @Valid ProviderAvailabilityRequest request) {
        try {
            ProviderAvailabilityResponse resp = availabilityService.createAvailability(providerId, request);
            APIResponse<ProviderAvailabilityResponse> apiResp = new APIResponse<>();
            apiResp.setSuccess(true);
            apiResp.setMessage("Availability slots created successfully");
            apiResp.setData(resp);
            return ResponseEntity.status(HttpStatus.CREATED).body(apiResp);
        } catch (IllegalArgumentException ex) {
            APIResponse<ProviderAvailabilityResponse> apiResp = new APIResponse<>();
            apiResp.setSuccess(false);
            apiResp.setMessage(ex.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiResp);
        }
    }

    @GetMapping("/api/v1/provider/{providerId}/availability")
    @Operation(summary = "Get provider availability by date range and filters")
    public ResponseEntity<APIResponse<List<AppointmentSlot>>> getProviderAvailability(
            @PathVariable UUID providerId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start_date,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end_date,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String appointment_type,
            @RequestParam(required = false) String timezone) {
        List<AppointmentSlot> slots = slotRepository.findByProviderId(providerId).stream()
            .filter(slot -> !slot.getSlotStartTime().toLocalDate().isBefore(start_date) && !slot.getSlotEndTime().toLocalDate().isAfter(end_date))
            .toList();
        // Optionally filter by status, appointment_type, timezone
        APIResponse<List<AppointmentSlot>> resp = new APIResponse<>();
        resp.setSuccess(true);
        resp.setData(slots);
        return ResponseEntity.ok(resp);
    }

    @PutMapping("/{slotId}")
    @Operation(summary = "Update an availability slot")
    public ResponseEntity<APIResponse<AppointmentSlot>> updateSlot(
            @PathVariable UUID slotId,
            @RequestBody Map<String, Object> updates) {
        Optional<AppointmentSlot> slotOpt = slotRepository.findById(slotId);
        APIResponse<AppointmentSlot> resp = new APIResponse<>();
        if (slotOpt.isEmpty()) {
            resp.setSuccess(false);
            resp.setMessage("Slot not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(resp);
        }
        AppointmentSlot slot = slotOpt.get();
        if (updates.containsKey("start_time")) {
            // parse and update start_time
        }
        if (updates.containsKey("end_time")) {
            // parse and update end_time
        }
        if (updates.containsKey("status")) {
            slot.setStatus(AppointmentSlot.Status.valueOf(updates.get("status").toString().toUpperCase()));
        }
        if (updates.containsKey("notes")) {
            // handle notes if present in model
        }
        if (updates.containsKey("pricing")) {
            // handle pricing if present in model
        }
        slotRepository.save(slot);
        resp.setSuccess(true);
        resp.setData(slot);
        return ResponseEntity.ok(resp);
    }

    @DeleteMapping("/{slotId}")
    @Operation(summary = "Delete an availability slot")
    public ResponseEntity<APIResponse<Void>> deleteSlot(
            @PathVariable UUID slotId,
            @RequestParam(required = false, defaultValue = "false") boolean delete_recurring,
            @RequestParam(required = false) String reason) {
        Optional<AppointmentSlot> slotOpt = slotRepository.findById(slotId);
        APIResponse<Void> resp = new APIResponse<>();
        if (slotOpt.isEmpty()) {
            resp.setSuccess(false);
            resp.setMessage("Slot not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(resp);
        }
        AppointmentSlot slot = slotOpt.get();
        if (slot.getStatus() == AppointmentSlot.Status.BOOKED) {
            resp.setSuccess(false);
            resp.setMessage("Cannot delete a booked slot");
            return ResponseEntity.status(HttpStatus.CONFLICT).body(resp);
        }
        slotRepository.delete(slot);
        resp.setSuccess(true);
        resp.setMessage("Slot deleted");
        return ResponseEntity.ok(resp);
    }

    @GetMapping("/api/v1/availability/search")
    @Operation(summary = "Patient slot search")
    public ResponseEntity<APIResponse<List<AppointmentSlot>>> searchSlots(
            @RequestParam LocalDate start_date,
            @RequestParam LocalDate end_date,
            @RequestParam(required = false) String specialization,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) String appointment_type,
            @RequestParam(required = false) Boolean insurance_accepted,
            @RequestParam(required = false) Double max_price,
            @RequestParam(required = false, defaultValue = "true") boolean available_only,
            @RequestParam(required = false) String timezone) {
        // This is a simplified search; real implementation would join with provider, filter by specialization, etc.
        List<AppointmentSlot> slots = slotRepository.findAll().stream()
            .filter(slot -> !slot.getSlotStartTime().toLocalDate().isBefore(start_date) && !slot.getSlotEndTime().toLocalDate().isAfter(end_date))
            .filter(slot -> !available_only || slot.getStatus() == AppointmentSlot.Status.AVAILABLE)
            .toList();
        APIResponse<List<AppointmentSlot>> resp = new APIResponse<>();
        resp.setSuccess(true);
        resp.setData(slots);
        return ResponseEntity.ok(resp);
    }
} 