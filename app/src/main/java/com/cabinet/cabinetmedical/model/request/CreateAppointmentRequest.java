package com.cabinet.cabinetmedical.model.request;

/**
 * CreateAppointmentRequest - Création d'un rendez-vous par le patient
 *
 * RÔLE:
 * Envoie les données pour réserver un rendez-vous (médecin, date/heure, motif).
 * Utilisé par BookAppointmentActivity.
 *
 * ENDPOINT: POST /api/appointments
 * FORMAT dateTime: "YYYY-MM-DDTHH:mm:ss" (ex: "2026-02-03T09:00:00")
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
