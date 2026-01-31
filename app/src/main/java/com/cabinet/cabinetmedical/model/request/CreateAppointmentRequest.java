package com.cabinet.cabinetmedical.model.request;

/**
 * CreateAppointmentRequest - Modèle pour créer un rendez-vous
 *
 * ENDPOINT: POST /api/appointments
 *
 * STRUCTURE:
 * {
 *   "doctorId": 1,
 *   "dateTime": "2026-02-03T09:00:00",
 *   "reason": "Consultation générale"
 * }
 */
public class CreateAppointmentRequest {

    private Long doctorId;
    private String dateTime;  // Format: "YYYY-MM-DDTHH:mm:ss"
    private String reason;

    public CreateAppointmentRequest(Long doctorId, String dateTime, String reason) {
        this.doctorId = doctorId;
        this.dateTime = dateTime;
        this.reason = reason;
    }

    // Getters et Setters
    public Long getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(Long doctorId) {
        this.doctorId = doctorId;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
