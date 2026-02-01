package com.cabinet.cabinetmedical.model.request;

import com.google.gson.annotations.SerializedName;

/**
 * CreateTimeSlotRequest - Création d'un créneau horaire par le médecin
 *
 * RÔLE:
 * Envoie les données pour créer un nouveau créneau (jour, heure début/fin).
 * Utilisé par DoctorAddTimeSlotActivity.
 *
 * ENDPOINT: POST /api/timeslots
 * VALIDATION: startTime < endTime, pas de chevauchement (vérifié backend)
 */
public class CreateTimeSlotRequest {

    @SerializedName("doctorId")
    private Long doctorId;

    @SerializedName("dayOfWeek")
    private String dayOfWeek; // MONDAY, TUESDAY, etc.

    @SerializedName("startTime")
    private String startTime; // "09:00:00"

    @SerializedName("endTime")
    private String endTime; // "12:00:00"

    // Constructeurs
    public CreateTimeSlotRequest() {
    }

    public CreateTimeSlotRequest(Long doctorId, String dayOfWeek, String startTime, String endTime) {
        this.doctorId = doctorId;
        this.dayOfWeek = dayOfWeek;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    // Getters
    public Long getDoctorId() {
        return doctorId;
    }

    public String getDayOfWeek() {
        return dayOfWeek;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    // Setters
    public void setDoctorId(Long doctorId) {
        this.doctorId = doctorId;
    }

    public void setDayOfWeek(String dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    @Override
    public String toString() {
        return "CreateTimeSlotRequest{" +
                "doctorId=" + doctorId +
                ", dayOfWeek='" + dayOfWeek + '\'' +
                ", startTime='" + startTime + '\'' +
                ", endTime='" + endTime + '\'' +
                '}';
    }
}
