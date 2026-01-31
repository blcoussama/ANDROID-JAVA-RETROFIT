package com.cabinet.cabinetmedical.model.request;

import com.google.gson.annotations.SerializedName;

/**
 * UpdateTimeSlotRequest - DTO pour modifier un créneau horaire
 *
 * UTILISATION:
 * - UC-D02: Doctor modifier ses créneaux
 * - UC-A08: Admin modifier créneaux de n'importe quel médecin
 *
 * ENDPOINT:
 * PUT /api/timeslots/{id}
 *
 * EXEMPLE JSON:
 * {
 *   "dayOfWeek": "TUESDAY",
 *   "startTime": "14:00:00",
 *   "endTime": "18:00:00"
 * }
 *
 * VALIDATION:
 * - dayOfWeek obligatoire (MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY)
 * - startTime obligatoire (format HH:mm:ss)
 * - endTime obligatoire (format HH:mm:ss)
 * - startTime < endTime
 * - Pas de chevauchement avec autres créneaux (vérifié par backend)
 *
 * NOTE:
 * - doctorId ne peut pas être modifié (un créneau appartient toujours au même médecin)
 * - Tous les champs sont obligatoires (pas de modification partielle)
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
