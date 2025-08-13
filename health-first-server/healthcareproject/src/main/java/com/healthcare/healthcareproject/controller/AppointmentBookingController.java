package com.healthcare.healthcareproject.controller;

import com.healthcare.healthcareproject.dto.BookAppointmentRequest;
import com.healthcare.healthcareproject.dto.APIResponse;
import com.healthcare.healthcareproject.model.AppointmentSlot;
import com.healthcare.healthcareproject.service.AppointmentBookingService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.UUID;
import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;
import java.util.Optional;
import java.util.List;

@RestController
@RequestMapping("/api/v1/appointments")
public class AppointmentBookingController {
    
    @Autowired
    private AppointmentBookingService bookingService;

    @GetMapping("/appointments")
    @Operation(summary = "Get paginated list of all booked appointments")
    public ResponseEntity<APIResponse<Page<AppointmentSlot>>> getAllAppointments(
            @RequestParam(required = false) UUID providerId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<AppointmentSlot> bookedSlots = bookingService.getBookedAppointments(providerId, startDate, endDate, page, size);
        APIResponse<Page<AppointmentSlot>> resp = new APIResponse<>();
        resp.setSuccess(true);
        resp.setData(bookedSlots);
        return ResponseEntity.ok(resp);
    }

    @GetMapping("/available")
    @Operation(summary = "Get available appointment slots for booking")
    public ResponseEntity<APIResponse<List<AppointmentSlot>>> getAvailableSlots(
            @RequestParam(required = false) UUID providerId,
            @RequestParam(required = false) String specialization,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        List<AppointmentSlot> availableSlots = bookingService.getAvailableSlots(providerId, specialization, startDate, endDate);
        APIResponse<List<AppointmentSlot>> resp = new APIResponse<>();
        resp.setSuccess(true);
        resp.setData(availableSlots);
        return ResponseEntity.ok(resp);
    }

    @PostMapping("/book")
    @Operation(summary = "Book an available appointment slot")
    public ResponseEntity<APIResponse<AppointmentSlot>> bookAppointment(
            @RequestBody @Valid BookAppointmentRequest request) {
        APIResponse<AppointmentSlot> resp = new APIResponse<>();
        Optional<AppointmentSlot> booked = bookingService.bookAppointment(request);
        if (booked.isEmpty()) {
            resp.setSuccess(false);
            resp.setMessage("Slot not available or not found");
            return ResponseEntity.status(HttpStatus.CONFLICT).body(resp);
        }
        resp.setSuccess(true);
        resp.setMessage("Appointment booked successfully");
        resp.setData(booked.get());
        return ResponseEntity.status(HttpStatus.CREATED).body(resp);
    }
}