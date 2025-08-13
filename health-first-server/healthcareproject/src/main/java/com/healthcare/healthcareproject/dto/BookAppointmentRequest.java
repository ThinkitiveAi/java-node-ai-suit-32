package com.healthcare.healthcareproject.dto;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public class BookAppointmentRequest {
    @NotNull
    private UUID slotId;
    @NotNull
    private UUID patientId;

    public UUID getSlotId() { return slotId; }
    public void setSlotId(UUID slotId) { this.slotId = slotId; }
    public UUID getPatientId() { return patientId; }
    public void setPatientId(UUID patientId) { this.patientId = patientId; }
}