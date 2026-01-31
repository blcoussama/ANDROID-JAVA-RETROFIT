package com.cabinet.cabinetmedical.model.response;

/**
 * AppointmentResponse - DTO pour les informations d'un rendez-vous
 *
 * UTILISATION:
 * - UC-P03: Patient consulte historique RDV
 * - UC-P06: Patient prend RDV (confirmation)
 * - UC-D03: Doctor consulte ses RDV
 * - UC-A09: Admin voir tous les RDV
 *
 * CONTENU:
 * - Informations RDV (date, heure, motif, status)
 * - Informations Patient (nom complet)
 * - Informations Doctor (nom complet, spécialité)
 * - Informations annulation (si annulé)
 *
 * EXEMPLE JSON:
 * {
 *   "id": 1,
 *   "patientId": 1,
 *   "patientName": "Jean Dupont",
 *   "patientPhone": "0612345678",
 *   "doctorId": 1,
 *   "doctorName": "Dr. Martin Durand",
 *   "doctorSpecialty": "Cardiologue",
 *   "dateTime": "2026-02-03T14:00:00",
 *   "reason": "Consultation cardiaque",
 *   "status": "PENDING",
 *   "cancelledBy": null,
 *   "cancellationReason": null,
 *   "createdAt": "2026-01-31T10:00:00",
 *   "updatedAt": "2026-01-31T10:00:00"
 * }
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

    // Helper methods

    /**
     * Formater date/heure pour affichage
     * Input: "2026-02-03T14:00:00"
     * Output: "03/02/2026 à 14:00"
     */
    public String getFormattedDateTime() {
        if (dateTime == null) return "";
        try {
            // Split date et time
            String[] parts = dateTime.split("T");
            String datePart = parts[0];  // "2026-02-03"
            String timePart = parts[1];  // "14:00:00"

            // Format date
            String[] dateParts = datePart.split("-");
            String day = dateParts[2];
            String month = dateParts[1];
            String year = dateParts[0];

            // Format time (remove seconds)
            String timeShort = timePart.length() > 5 ? timePart.substring(0, 5) : timePart;

            return String.format("%s/%s/%s à %s", day, month, year, timeShort);
        } catch (Exception e) {
            return dateTime;
        }
    }

    /**
     * Status avec couleur
     * PENDING → "En attente"
     * CONFIRMED → "Confirmé"
     * CANCELLED → "Annulé"
     */
    public String getStatusLabel() {
        if (status == null) return "En attente";
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

    /**
     * Spécialité du médecin ou "Médecin généraliste"
     */
    public String getSpecialtyOrDefault() {
        if (doctorSpecialty == null || doctorSpecialty.isEmpty()) {
            return "Médecin généraliste";
        }
        return doctorSpecialty;
    }
}
