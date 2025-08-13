package com.healthcare.healthcareproject.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.UUID;

@Entity
@EntityListeners(AuditingEntityListener.class)
public class AppointmentSlot {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @NotNull
    private UUID availabilityId;

    @NotNull
    private UUID providerId;

    @NotNull
    private ZonedDateTime slotStartTime;

    @NotNull
    private ZonedDateTime slotEndTime;

    @Enumerated(EnumType.STRING)
    private Status status = Status.AVAILABLE;

    private UUID patientId;

    private String appointmentType;

    private String bookingReference;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;

    public enum Status { AVAILABLE, BOOKED, CANCELLED, BLOCKED }

    // Getters and setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getAvailabilityId() { return availabilityId; }
    public void setAvailabilityId(UUID availabilityId) { this.availabilityId = availabilityId; }
    public UUID getProviderId() { return providerId; }
    public void setProviderId(UUID providerId) { this.providerId = providerId; }
    public ZonedDateTime getSlotStartTime() { return slotStartTime; }
    public void setSlotStartTime(ZonedDateTime slotStartTime) { this.slotStartTime = slotStartTime; }
    public ZonedDateTime getSlotEndTime() { return slotEndTime; }
    public void setSlotEndTime(ZonedDateTime slotEndTime) { this.slotEndTime = slotEndTime; }
    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }
    public UUID getPatientId() { return patientId; }
    public void setPatientId(UUID patientId) { this.patientId = patientId; }
    public String getAppointmentType() { return appointmentType; }
    public void setAppointmentType(String appointmentType) { this.appointmentType = appointmentType; }
    public String getBookingReference() { return bookingReference; }
    public void setBookingReference(String bookingReference) { this.bookingReference = bookingReference; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
} 