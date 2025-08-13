package com.healthcare.healthcareproject.repository;

import com.healthcare.healthcareproject.model.AppointmentSlot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.UUID;

public interface AppointmentSlotRepository extends JpaRepository<AppointmentSlot, UUID> {
    List<AppointmentSlot> findByProviderId(UUID providerId);
    List<AppointmentSlot> findByAvailabilityId(UUID availabilityId);

    Page<AppointmentSlot> findByProviderIdAndStatus(UUID providerId, AppointmentSlot.Status status, Pageable pageable);
    Page<AppointmentSlot> findByStatus(AppointmentSlot.Status status, Pageable pageable);
} 