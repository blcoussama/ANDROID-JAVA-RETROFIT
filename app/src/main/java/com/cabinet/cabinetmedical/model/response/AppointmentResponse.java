package com.cabinet.cabinetmedical.model.response;

import com.cabinet.cabinetmedical.utils.DateTimes;

import java.util.Date;

/**
 * AppointmentResponse - Informations complètes d'un rendez-vous
 *
 * RÔLE:
 * Représente un rendez-vous avec toutes ses données (patient, médecin, date, statut).
 * Utilisé par MyAppointmentsActivity, DoctorAppointmentsActivity, AdminAllAppointmentsActivity.
 *
 * STATUTS POSSIBLES: PENDING, CONFIRMED, CANCELLED
 * MÉTHODES HELPER: getFormattedDateTime(), getStatusLabel(), getSpecialtyOrDefault()
 */
public class AppointmentResponse {

    private Long id;
    private Long patientId;
    private String patientName;
    private String patientEmail;
    private String patientPhone;
    private Long doctorId;
    private String doctorName;
    private String doctorSpecialty;
    private String dateTime;  // Format: "YYYY-MM-DDTHH:mm:ss"
    private String reason;
    private String status;  // "PENDING", "CONFIRMED", "CANCELLED"
    private String cancelledBy;  // "PATIENT", "DOCTOR", "ADMIN" (si annulé)
    private String cancellationReason;
    private String createdAt;
    private String updatedAt;

    // Constructeur vide (requis par Gson)
    public AppointmentResponse() {
    }

    // Getters et Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPatientId() {
        return patientId;
    }

    public void setPatientId(Long patientId) {
        this.patientId = patientId;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public String getPatientEmail() {
        return patientEmail;
    }

    public void setPatientEmail(String patientEmail) {
        this.patientEmail = patientEmail;
    }

    public String getPatientPhone() {
        return patientPhone;
    }

    public void setPatientPhone(String patientPhone) {
        this.patientPhone = patientPhone;
    }

    public Long getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(Long doctorId) {
        this.doctorId = doctorId;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }

    public String getDoctorSpecialty() {
        return doctorSpecialty;
    }

    public void setDoctorSpecialty(String doctorSpecialty) {
        this.doctorSpecialty = doctorSpecialty;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCancelledBy() {
        return cancelledBy;
    }

    public void setCancelledBy(String cancelledBy) {
        this.cancelledBy = cancelledBy;
    }

    public String getCancellationReason() {
        return cancellationReason;
    }

    public void setCancellationReason(String cancellationReason) {
        this.cancellationReason = cancellationReason;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    // --- Aides à l'affichage ---

    /** "03/02/2026 à 14:00". */
    public String getFormattedDateTime() {
        return DateTimes.format(dateTime);
    }

    public String getStatusLabel() {
        if (status == null) {
            return "En attente";
        }
        switch (status) {
            case "PENDING":
                return "En attente";
            case "CONFIRMED":
                return "Confirmé";
            case "CANCELLED":
                return "Annulé";
            default:
                return status;
        }
    }

    public String getSpecialtyOrDefault() {
        return doctorSpecialty == null || doctorSpecialty.isEmpty() ? "Médecin généraliste" : doctorSpecialty;
    }

    public boolean isCancelled() {
        return "CANCELLED".equals(status);
    }

    /** Annulable : pas encore annulé et pas encore passé (l'API applique la même règle). */
    public boolean canBeCancelled(Date now) {
        return !isCancelled() && DateTimes.isAfter(dateTime, now);
    }

    /** Confirmable : en attente et pas encore passé. */
    public boolean canBeConfirmed(Date now) {
        return "PENDING".equals(status) && DateTimes.isAfter(dateTime, now);
    }

    /** "Annulé par le patient : raison", ou null si le rendez-vous n'est pas annulé. */
    public String getCancellationLabel() {
        if (!isCancelled()) {
            return null;
        }
        String by;
        if ("PATIENT".equals(cancelledBy)) {
            by = "le patient";
        } else if ("DOCTOR".equals(cancelledBy)) {
            by = "le médecin";
        } else if ("ADMIN".equals(cancelledBy)) {
            by = "le cabinet";
        } else {
            by = null;
        }
        String label = by != null ? "Annulé par " + by : "Annulé";
        return cancellationReason != null && !cancellationReason.isEmpty()
                ? label + " : " + cancellationReason : label;
    }
}
