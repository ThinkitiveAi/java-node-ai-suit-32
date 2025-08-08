package com.healthcare.healthcareproject.service;

import com.healthcare.healthcareproject.dto.ProviderAvailabilityRequest;
import com.healthcare.healthcareproject.dto.ProviderAvailabilityResponse;
import com.healthcare.healthcareproject.model.ProviderAvailability;
import com.healthcare.healthcareproject.model.AppointmentSlot;
import com.healthcare.healthcareproject.repository.ProviderAvailabilityRepository;
import com.healthcare.healthcareproject.repository.AppointmentSlotRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class ProviderAvailabilityService {
    @Autowired
    private ProviderAvailabilityRepository availabilityRepository;
    @Autowired
    private AppointmentSlotRepository slotRepository;

    @Transactional
    public ProviderAvailabilityResponse createAvailability(UUID providerId, ProviderAvailabilityRequest request) {
        // Validate time range
        LocalTime start = LocalTime.parse(request.getStartTime());
        LocalTime end = LocalTime.parse(request.getEndTime());
        if (!end.isAfter(start)) {
            throw new IllegalArgumentException("end_time must be after start_time");
        }
        if ((Duration.between(start, end).toMinutes() % request.getSlotDuration()) != 0) {
            throw new IllegalArgumentException("slot_duration must divide evenly into the time range");
        }
        // Validate timezone
        ZoneId zoneId;
        try {
            zoneId = ZoneId.of(request.getTimezone());
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid timezone");
        }
        // Recurrence
        LocalDate recurrenceEnd = request.isRecurring() && request.getRecurrenceEndDate() != null ? request.getRecurrenceEndDate() : request.getDate();
        List<LocalDate> dates = new ArrayList<>();
        LocalDate current = request.getDate();
        if (request.isRecurring() && request.getRecurrencePattern() != null) {
            while (!current.isAfter(recurrenceEnd)) {
                dates.add(current);
                switch (request.getRecurrencePattern().toLowerCase()) {
                    case "daily": current = current.plusDays(1); break;
                    case "weekly": current = current.plusWeeks(1); break;
                    case "monthly": current = current.plusMonths(1); break;
                    default: throw new IllegalArgumentException("Invalid recurrence_pattern");
                }
            }
        } else {
            dates.add(current);
        }
        // Create ProviderAvailability
        ProviderAvailability availability = new ProviderAvailability();
        availability.setProviderId(providerId);
        availability.setDate(request.getDate());
        availability.setStartTime(request.getStartTime());
        availability.setEndTime(request.getEndTime());
        availability.setTimezone(request.getTimezone());
        availability.setRecurring(request.isRecurring());
        if (request.getRecurrencePattern() != null)
            availability.setRecurrencePattern(ProviderAvailability.RecurrencePattern.valueOf(request.getRecurrencePattern().toUpperCase()));
        availability.setRecurrenceEndDate(request.getRecurrenceEndDate());
        availability.setSlotDuration(request.getSlotDuration());
        availability.setBreakDuration(request.getBreakDuration());
        availability.setAppointmentType(ProviderAvailability.AppointmentType.valueOf(request.getAppointmentType().toUpperCase()));
        // Location
        ProviderAvailability.Location location = new ProviderAvailability.Location();
        location.setType((String) request.getLocation().getOrDefault("type", ""));
        location.setAddress((String) request.getLocation().getOrDefault("address", ""));
        location.setRoomNumber((String) request.getLocation().getOrDefault("room_number", null));
        availability.setLocation(location);
        // Pricing
        if (request.getPricing() != null) {
            ProviderAvailability.Pricing pricing = new ProviderAvailability.Pricing();
            pricing.setBaseFee(request.getPricing().get("base_fee") != null ? Double.valueOf(request.getPricing().get("base_fee").toString()) : null);
            pricing.setInsuranceAccepted(request.getPricing().get("insurance_accepted") != null ? Boolean.valueOf(request.getPricing().get("insurance_accepted").toString()) : null);
            pricing.setCurrency((String) request.getPricing().getOrDefault("currency", null));
            availability.setPricing(pricing);
        }
        availability.setNotes(request.getNotes());
        availability.setSpecialRequirements(request.getSpecialRequirements());
        availability.setStatus(ProviderAvailability.Status.AVAILABLE);
        availability.setMaxAppointmentsPerSlot(1);
        availability.setCurrentAppointments(0);
        availability = availabilityRepository.save(availability);
        // Generate slots
        int slotsCreated = 0;
        int totalAppointmentsAvailable = 0;
        for (LocalDate date : dates) {
            LocalTime slotStart = start;
            while (slotStart.plusMinutes(request.getSlotDuration()).isBefore(end) || slotStart.plusMinutes(request.getSlotDuration()).equals(end)) {
                LocalTime slotEnd = slotStart.plusMinutes(request.getSlotDuration());
                ZonedDateTime slotStartZdt = ZonedDateTime.of(date, slotStart, zoneId).withZoneSameInstant(ZoneOffset.UTC);
                ZonedDateTime slotEndZdt = ZonedDateTime.of(date, slotEnd, zoneId).withZoneSameInstant(ZoneOffset.UTC);
                // Overlap/conflict check (simplified: can be expanded)
                List<AppointmentSlot> overlapping = slotRepository.findByProviderId(providerId).stream()
                    .filter(s -> s.getSlotStartTime().isBefore(slotEndZdt) && s.getSlotEndTime().isAfter(slotStartZdt))
                    .toList();
                if (overlapping.isEmpty()) {
                    AppointmentSlot slot = new AppointmentSlot();
                    slot.setAvailabilityId(availability.getId());
                    slot.setProviderId(providerId);
                    slot.setSlotStartTime(slotStartZdt);
                    slot.setSlotEndTime(slotEndZdt);
                    slot.setStatus(AppointmentSlot.Status.AVAILABLE);
                    slot.setAppointmentType(request.getAppointmentType());
                    slot.setBookingReference(UUID.randomUUID().toString());
                    slotRepository.save(slot);
                    slotsCreated++;
                    totalAppointmentsAvailable++;
                }
                slotStart = slotEnd.plusMinutes(request.getBreakDuration());
            }
        }
        // Prepare response
        ProviderAvailabilityResponse resp = new ProviderAvailabilityResponse();
        resp.setAvailabilityId(availability.getId().toString());
        resp.setSlotsCreated(slotsCreated);
        ProviderAvailabilityResponse.DateRange dr = new ProviderAvailabilityResponse.DateRange();
        dr.setStart(request.getDate().toString());
        dr.setEnd(recurrenceEnd.toString());
        resp.setDateRange(dr);
        resp.setTotalAppointmentsAvailable(totalAppointmentsAvailable);
        return resp;
    }
} 