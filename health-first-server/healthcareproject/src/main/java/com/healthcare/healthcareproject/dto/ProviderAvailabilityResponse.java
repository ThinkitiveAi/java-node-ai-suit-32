package com.healthcare.healthcareproject.dto;

public class ProviderAvailabilityResponse {
    private String availabilityId;
    private int slotsCreated;
    private DateRange dateRange;
    private int totalAppointmentsAvailable;

    public static class DateRange {
        private String start;
        private String end;
        public String getStart() { return start; }
        public void setStart(String start) { this.start = start; }
        public String getEnd() { return end; }
        public void setEnd(String end) { this.end = end; }
    }

    public String getAvailabilityId() { return availabilityId; }
    public void setAvailabilityId(String availabilityId) { this.availabilityId = availabilityId; }
    public int getSlotsCreated() { return slotsCreated; }
    public void setSlotsCreated(int slotsCreated) { this.slotsCreated = slotsCreated; }
    public DateRange getDateRange() { return dateRange; }
    public void setDateRange(DateRange dateRange) { this.dateRange = dateRange; }
    public int getTotalAppointmentsAvailable() { return totalAppointmentsAvailable; }
    public void setTotalAppointmentsAvailable(int totalAppointmentsAvailable) { this.totalAppointmentsAvailable = totalAppointmentsAvailable; }
} 