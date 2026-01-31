package com.cabinet.cabinetmedical.model.response;

import java.util.List;
import java.util.Map;

/**
 * DashboardResponse - DTO pour le tableau de bord admin
 *
 * UTILISATION:
 * - UC-A02: Admin voir tableau de bord état rendez-vous
 *
 * ENDPOINT: GET /api/dashboard
 *
 * CONTENU:
 * - Statistiques RDV (total, par status, récents)
 * - Statistiques utilisateurs (médecins, patients)
 */
public class DashboardResponse {

    private Long totalAppointmentsToday;
    private Long totalAppointmentsWeek;
    private Map<String, Long> appointmentsByStatus;
    private List<AppointmentResponse> recentAppointments;
    private Long totalDoctors;
    private Long totalPatients;
    private Long totalUsers;

    // Constructeur vide (requis par Gson)
    public DashboardResponse() {
    }

    // Getters et Setters

    public Long getTotalAppointmentsToday() {
        return totalAppointmentsToday != null ? totalAppointmentsToday : 0L;
    }

    public void setTotalAppointmentsToday(Long totalAppointmentsToday) {
        this.totalAppointmentsToday = totalAppointmentsToday;
    }

    public Long getTotalAppointmentsWeek() {
        return totalAppointmentsWeek != null ? totalAppointmentsWeek : 0L;
    }

    public void setTotalAppointmentsWeek(Long totalAppointmentsWeek) {
        this.totalAppointmentsWeek = totalAppointmentsWeek;
    }

    public Map<String, Long> getAppointmentsByStatus() {
        return appointmentsByStatus;
    }

    public void setAppointmentsByStatus(Map<String, Long> appointmentsByStatus) {
        this.appointmentsByStatus = appointmentsByStatus;
    }

    public List<AppointmentResponse> getRecentAppointments() {
        return recentAppointments;
    }

    public void setRecentAppointments(List<AppointmentResponse> recentAppointments) {
        this.recentAppointments = recentAppointments;
    }

    public Long getTotalDoctors() {
        return totalDoctors != null ? totalDoctors : 0L;
    }

    public void setTotalDoctors(Long totalDoctors) {
        this.totalDoctors = totalDoctors;
    }

    public Long getTotalPatients() {
        return totalPatients != null ? totalPatients : 0L;
    }

    public void setTotalPatients(Long totalPatients) {
        this.totalPatients = totalPatients;
    }

    public Long getTotalUsers() {
        return totalUsers != null ? totalUsers : 0L;
    }

    public void setTotalUsers(Long totalUsers) {
        this.totalUsers = totalUsers;
    }

    // Helper methods

    public Long getPendingCount() {
        return appointmentsByStatus != null ? appointmentsByStatus.getOrDefault("PENDING", 0L) : 0L;
    }

    public Long getConfirmedCount() {
        return appointmentsByStatus != null ? appointmentsByStatus.getOrDefault("CONFIRMED", 0L) : 0L;
    }

    public Long getCancelledCount() {
        return appointmentsByStatus != null ? appointmentsByStatus.getOrDefault("CANCELLED", 0L) : 0L;
    }
}
