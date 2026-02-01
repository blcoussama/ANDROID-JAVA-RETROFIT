package com.cabinet.cabinetmedical.model.response;

import com.google.gson.annotations.SerializedName;

/**
 * TimeSlotResponse - Informations d'un créneau horaire
 *
 * RÔLE:
 * Représente un créneau de disponibilité du médecin (jour, horaires, durée).
 * Utilisé par DoctorTimeSlotsActivity (médecin gère ses créneaux)
 * et AvailableTimeSlotsActivity (patient consulte les créneaux disponibles).
 *
 * MÉTHODES HELPER: getTimeRange() ("09:00 - 12:00"), getFormattedDuration() ("3h")
 */
public class TimeSlotResponse {

    @SerializedName("id")
    private Long id;

    @SerializedName("doctorId")
    private Long doctorId;

    @SerializedName("doctorName")
    private String doctorName;

    @SerializedName("doctorSpecialty")
    private String doctorSpecialty;

    @SerializedName("dayOfWeek")
    private String dayOfWeek; // MONDAY, TUESDAY, etc.

    @SerializedName("dayOfWeekFr")
    private String dayOfWeekFr; // Lundi, Mardi, etc.

    @SerializedName("startTime")
    private String startTime; // "09:00:00"

    @SerializedName("endTime")
    private String endTime; // "12:00:00"

    @SerializedName("durationMinutes")
    private Integer durationMinutes; // 180

    // Constructeurs
    public TimeSlotResponse() {
    }

    public TimeSlotResponse(Long id, Long doctorId, String doctorName, String doctorSpecialty,
                            String dayOfWeek, String dayOfWeekFr, String startTime,
                            String endTime, Integer durationMinutes) {
        this.id = id;
        this.doctorId = doctorId;
        this.doctorName = doctorName;
        this.doctorSpecialty = doctorSpecialty;
        this.dayOfWeek = dayOfWeek;
        this.dayOfWeekFr = dayOfWeekFr;
        this.startTime = startTime;
        this.endTime = endTime;
        this.durationMinutes = durationMinutes;
    }

    // Getters
    public Long getId() {
        return id;
    }

    public Long getDoctorId() {
        return doctorId;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public String getDoctorSpecialty() {
        return doctorSpecialty;
    }

    public String getDayOfWeek() {
        return dayOfWeek;
    }

    public String getDayOfWeekFr() {
        return dayOfWeekFr;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public Integer getDurationMinutes() {
        return durationMinutes;
    }

    // Setters
    public void setId(Long id) {
        this.id = id;
    }

    public void setDoctorId(Long doctorId) {
        this.doctorId = doctorId;
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }

    public void setDoctorSpecialty(String doctorSpecialty) {
        this.doctorSpecialty = doctorSpecialty;
    }

    public void setDayOfWeek(String dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public void setDayOfWeekFr(String dayOfWeekFr) {
        this.dayOfWeekFr = dayOfWeekFr;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public void setDurationMinutes(Integer durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    /**
     * Formatter l'heure de début et de fin
     * Ex: "09:00 - 12:00"
     */
    public String getTimeRange() {
        if (startTime != null && endTime != null) {
            // Supprimer les secondes si présentes
            String start = startTime.substring(0, 5); // "09:00:00" -> "09:00"
            String end = endTime.substring(0, 5);     // "12:00:00" -> "12:00"
            return start + " - " + end;
        }
        return "";
    }

    /**
     * Formatter la durée
     * Ex: "3h 00min" ou "1h 30min"
     */
    public String getFormattedDuration() {
        if (durationMinutes != null) {
            int hours = durationMinutes / 60;
            int minutes = durationMinutes % 60;
            if (minutes == 0) {
                return hours + "h";
            } else {
                return hours + "h " + minutes + "min";
            }
        }
        return "";
    }

    @Override
    public String toString() {
        return "TimeSlotResponse{" +
                "id=" + id +
                ", doctorId=" + doctorId +
                ", doctorName='" + doctorName + '\'' +
                ", dayOfWeek='" + dayOfWeek + '\'' +
                ", dayOfWeekFr='" + dayOfWeekFr + '\'' +
                ", startTime='" + startTime + '\'' +
                ", endTime='" + endTime + '\'' +
                ", durationMinutes=" + durationMinutes +
                '}';
    }
}
