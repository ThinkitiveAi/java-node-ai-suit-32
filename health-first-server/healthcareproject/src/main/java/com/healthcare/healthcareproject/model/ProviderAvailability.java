package com.healthcare.healthcareproject.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Entity
@EntityListeners(AuditingEntityListener.class)
public class ProviderAvailability {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @NotNull
    private UUID providerId;

    @NotNull
    private LocalDate date;

    @NotBlank
    private String startTime; // HH:mm

    @NotBlank
    private String endTime; // HH:mm

    @NotBlank
    private String timezone;

    private boolean isRecurring = false;

    @Enumerated(EnumType.STRING)
    private RecurrencePattern recurrencePattern;

    private LocalDate recurrenceEndDate;

    @Min(5)
    @Max(180)
    private int slotDuration = 30;

    @Min(0)
    private int breakDuration = 0;

    @Enumerated(EnumType.STRING)
    private Status status = Status.AVAILABLE;

    @Min(1)
    private int maxAppointmentsPerSlot = 1;

    private int currentAppointments = 0;

    @Enumerated(EnumType.STRING)
    private AppointmentType appointmentType = AppointmentType.CONSULTATION;

    @Embedded
    private Location location;

    @Embedded
    private Pricing pricing;

    @Size(max = 500)
    private String notes;

    @ElementCollection
    private List<String> specialRequirements;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;

    public enum RecurrencePattern { DAILY, WEEKLY, MONTHLY }
    public enum Status { AVAILABLE, BOOKED, CANCELLED, BLOCKED, MAINTENANCE }
    public enum AppointmentType { CONSULTATION, FOLLOW_UP, EMERGENCY, TELEMEDICINE }

    @Embeddable
    public static class Location {
        @NotBlank
        private String type; // e.g., clinic, telemedicine
        @NotBlank
        private String address;
        private String roomNumber;
        // Getters and setters
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        public String getAddress() { return address; }
        public void setAddress(String address) { this.address = address; }
        public String getRoomNumber() { return roomNumber; }
        public void setRoomNumber(String roomNumber) { this.roomNumber = roomNumber; }
    }

    @Embeddable
    public static class Pricing {
        @DecimalMin("0.0")
        private Double baseFee;
        private Boolean insuranceAccepted;
        private String currency;
        // Getters and setters
        public Double getBaseFee() { return baseFee; }
        public void setBaseFee(Double baseFee) { this.baseFee = baseFee; }
        public Boolean getInsuranceAccepted() { return insuranceAccepted; }
        public void setInsuranceAccepted(Boolean insuranceAccepted) { this.insuranceAccepted = insuranceAccepted; }
        public String getCurrency() { return currency; }
        public void setCurrency(String currency) { this.currency = currency; }
    }

    // Getters and setters for ProviderAvailability fields
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getProviderId() { return providerId; }
    public void setProviderId(UUID providerId) { this.providerId = providerId; }
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
    public String getStartTime() { return startTime; }
    public void setStartTime(String startTime) { this.startTime = startTime; }
    public String getEndTime() { return endTime; }
    public void setEndTime(String endTime) { this.endTime = endTime; }
    public String getTimezone() { return timezone; }
    public void setTimezone(String timezone) { this.timezone = timezone; }
    public boolean isRecurring() { return isRecurring; }
    public void setRecurring(boolean recurring) { isRecurring = recurring; }
    public RecurrencePattern getRecurrencePattern() { return recurrencePattern; }
    public void setRecurrencePattern(RecurrencePattern recurrencePattern) { this.recurrencePattern = recurrencePattern; }
    public LocalDate getRecurrenceEndDate() { return recurrenceEndDate; }
    public void setRecurrenceEndDate(LocalDate recurrenceEndDate) { this.recurrenceEndDate = recurrenceEndDate; }
    public int getSlotDuration() { return slotDuration; }
    public void setSlotDuration(int slotDuration) { this.slotDuration = slotDuration; }
    public int getBreakDuration() { return breakDuration; }
    public void setBreakDuration(int breakDuration) { this.breakDuration = breakDuration; }
    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }
    public int getMaxAppointmentsPerSlot() { return maxAppointmentsPerSlot; }
    public void setMaxAppointmentsPerSlot(int maxAppointmentsPerSlot) { this.maxAppointmentsPerSlot = maxAppointmentsPerSlot; }
    public int getCurrentAppointments() { return currentAppointments; }
    public void setCurrentAppointments(int currentAppointments) { this.currentAppointments = currentAppointments; }
    public AppointmentType getAppointmentType() { return appointmentType; }
    public void setAppointmentType(AppointmentType appointmentType) { this.appointmentType = appointmentType; }
    public Location getLocation() { return location; }
    public void setLocation(Location location) { this.location = location; }
    public Pricing getPricing() { return pricing; }
    public void setPricing(Pricing pricing) { this.pricing = pricing; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    public List<String> getSpecialRequirements() { return specialRequirements; }
    public void setSpecialRequirements(List<String> specialRequirements) { this.specialRequirements = specialRequirements; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
} 