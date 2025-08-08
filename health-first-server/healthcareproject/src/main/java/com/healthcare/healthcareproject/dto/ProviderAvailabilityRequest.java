package com.healthcare.healthcareproject.dto;

import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class ProviderAvailabilityRequest {
    @NotNull
    private LocalDate date;
    @NotBlank
    private String startTime;
    @NotBlank
    private String endTime;
    @NotBlank
    private String timezone;
    private boolean isRecurring = false;
    private String recurrencePattern;
    private LocalDate recurrenceEndDate;
    @Min(5)
    @Max(180)
    private int slotDuration = 30;
    @Min(0)
    private int breakDuration = 0;
    @NotBlank
    private String appointmentType;
    @NotNull
    private Map<String, Object> location;
    private Map<String, Object> pricing;
    @Size(max = 500)
    private String notes;
    private List<String> specialRequirements;

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
    public String getRecurrencePattern() { return recurrencePattern; }
    public void setRecurrencePattern(String recurrencePattern) { this.recurrencePattern = recurrencePattern; }
    public LocalDate getRecurrenceEndDate() { return recurrenceEndDate; }
    public void setRecurrenceEndDate(LocalDate recurrenceEndDate) { this.recurrenceEndDate = recurrenceEndDate; }
    public int getSlotDuration() { return slotDuration; }
    public void setSlotDuration(int slotDuration) { this.slotDuration = slotDuration; }
    public int getBreakDuration() { return breakDuration; }
    public void setBreakDuration(int breakDuration) { this.breakDuration = breakDuration; }
    public String getAppointmentType() { return appointmentType; }
    public void setAppointmentType(String appointmentType) { this.appointmentType = appointmentType; }
    public Map<String, Object> getLocation() { return location; }
    public void setLocation(Map<String, Object> location) { this.location = location; }
    public Map<String, Object> getPricing() { return pricing; }
    public void setPricing(Map<String, Object> pricing) { this.pricing = pricing; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    public List<String> getSpecialRequirements() { return specialRequirements; }
    public void setSpecialRequirements(List<String> specialRequirements) { this.specialRequirements = specialRequirements; }
} 