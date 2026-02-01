package com.cabinet.cabinetmedical.model.request;

import com.google.gson.annotations.SerializedName;

/**
 * UpdateTimeSlotRequest - Modification d'un créneau horaire par le médecin
 *
 * RÔLE:
 * Envoie les nouvelles données pour modifier un créneau existant (jour, heures).
 * Utilisé par DoctorEditTimeSlotActivity.
 *
 * ENDPOINT: PUT /api/timeslots/{id}
 * NOTE: Tous les champs sont obligatoires (pas de modification partielle)
 */
public class UpdateTimeSlotRequest {

    @SerializedName("dayOfWeek")
    private String dayOfWeek; // MONDAY, TUESDAY, etc.

    @SerializedName("startTime")
    private String startTime; // "09:00:00"

    @SerializedName("endTime")
    private String endTime; // "12:00:00"

    // Constructeurs
    public UpdateTimeSlotRequest() {
    }

    public UpdateTimeSlotRequest(String dayOfWeek, String startTime, String endTime) {
        this.dayOfWeek = dayOfWeek;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    // Getters
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
        return "UpdateTimeSlotRequest{" +
                "dayOfWeek='" + dayOfWeek + '\'' +
                ", startTime='" + startTime + '\'' +
                ", endTime='" + endTime + '\'' +
                '}';
    }
}
