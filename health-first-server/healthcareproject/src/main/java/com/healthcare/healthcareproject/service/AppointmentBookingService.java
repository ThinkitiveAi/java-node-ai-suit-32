package com.healthcare.healthcareproject.service;

import com.healthcare.healthcareproject.dto.BookAppointmentRequest;
import com.healthcare.healthcareproject.model.AppointmentSlot;
import com.healthcare.healthcareproject.repository.AppointmentSlotRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.*;

@Service
public class AppointmentBookingService {

    @Autowired
    private AppointmentSlotRepository slotRepository;

    public Page<AppointmentSlot> getBookedAppointments(UUID providerId, LocalDate startDate, LocalDate endDate, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("slotStartTime").descending());
        Page<AppointmentSlot> pageResult;

        if (providerId != null) {
            pageResult = slotRepository.findByProviderIdAndStatus(providerId, AppointmentSlot.Status.BOOKED, pageable);
        } else {
            pageResult = slotRepository.findByStatus(AppointmentSlot.Status.BOOKED, pageable);
        }

        // Filter by date range if provided
        if (startDate != null || endDate != null) {
            List<AppointmentSlot> filtered = pageResult.getContent().stream()
                .filter(slot -> startDate == null || !slot.getSlotStartTime().toLocalDate().isBefore(startDate))
                .filter(slot -> endDate == null || !slot.getSlotEndTime().toLocalDate().isAfter(endDate))
                .toList();
            return new PageImpl<>(filtered, pageable, filtered.size());
        }
        return pageResult;
    }

    public List<AppointmentSlot> getAvailableSlots(UUID providerId, String specialization, LocalDate startDate, LocalDate endDate) {
        List<AppointmentSlot> allSlots;
        
        if (providerId != null) {
            allSlots = slotRepository.findByProviderId(providerId);
        } else {
            allSlots = slotRepository.findAll();
        }
        
        return allSlots.stream()
            .filter(slot -> slot.getStatus() == AppointmentSlot.Status.AVAILABLE)
            .filter(slot -> startDate == null || !slot.getSlotStartTime().toLocalDate().isBefore(startDate))
            .filter(slot -> endDate == null || !slot.getSlotEndTime().toLocalDate().isAfter(endDate))
            .toList();
    }

    public Optional<AppointmentSlot> bookAppointment(BookAppointmentRequest request) {
        Optional<AppointmentSlot> slotOpt = slotRepository.findById(request.getSlotId());
        if (slotOpt.isEmpty()) return Optional.empty();

        AppointmentSlot slot = slotOpt.get();
        if (slot.getStatus() != AppointmentSlot.Status.AVAILABLE) return Optional.empty();

        slot.setStatus(AppointmentSlot.Status.BOOKED);
        slot.setPatientId(request.getPatientId());
        
        // Generate a unique booking reference
        String bookingRef = "BK-" + System.currentTimeMillis() + "-" + request.getPatientId().toString().substring(0, 8);
        slot.setBookingReference(bookingRef);
        
        slotRepository.save(slot);
        return Optional.of(slot);
    }
}