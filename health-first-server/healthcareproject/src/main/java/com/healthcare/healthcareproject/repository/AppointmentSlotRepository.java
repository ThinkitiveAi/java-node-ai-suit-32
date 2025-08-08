package com.healthcare.healthcareproject.repository;

import com.healthcare.healthcareproject.model.AppointmentSlot;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface AppointmentSlotRepository extends JpaRepository<AppointmentSlot, UUID> {
    List<AppointmentSlot> findByProviderId(UUID providerId);
    List<AppointmentSlot> findByAvailabilityId(UUID availabilityId);
} 