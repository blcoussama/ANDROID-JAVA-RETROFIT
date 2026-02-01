package com.cabinet.cabinetmedical.api;

import com.cabinet.cabinetmedical.model.request.CreateAppointmentRequest;
import com.cabinet.cabinetmedical.model.request.CreateTimeSlotRequest;
import com.cabinet.cabinetmedical.model.request.CreateUserRequest;
import com.cabinet.cabinetmedical.model.request.LoginRequest;
import com.cabinet.cabinetmedical.model.request.RegisterRequest;
import com.cabinet.cabinetmedical.model.request.UpdateTimeSlotRequest;
import com.cabinet.cabinetmedical.model.request.UpdateUserRequest;
import com.cabinet.cabinetmedical.model.response.AppointmentResponse;
import com.cabinet.cabinetmedical.model.response.DashboardResponse;
import com.cabinet.cabinetmedical.model.response.DoctorResponse;
import com.cabinet.cabinetmedical.model.response.LoginResponse;
import com.cabinet.cabinetmedical.model.response.TimeSlotResponse;
import com.cabinet.cabinetmedical.model.response.UserResponse;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.HTTP;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Interface Retrofit définissant tous les endpoints API du backend
 *
 * Notes:
 * - JWT ajouté automatiquement par JwtInterceptor
 * - Conversion JSON automatique via Gson
 * - Utiliser avec ApiClient.getClient(context).create(ApiService.class)
 */
public interface ApiService {

    // ═══════════════════════════════════════════════════════
    // AUTHENTIFICATION
    // ═══════════════════════════════════════════════════════

    /**
     * Inscription d'un nouveau patient
     * POST /api/auth/register
     */
    @POST("api/auth/register")
    Call<LoginResponse> register(@Body RegisterRequest request);

    /**
     * Connexion utilisateur (tous rôles)
     * POST /api/auth/login
     */
    @POST("api/auth/login")
    Call<LoginResponse> login(@Body LoginRequest request);

    /**
     * Vérifier si un email existe
     * GET /api/auth/email-exists?email={email}
     */
    @GET("api/auth/email-exists")
    Call<Map<String, Object>> emailExists(@Query("email") String email);

    /**
     * Valider le token JWT
     * GET /api/auth/validate
     */
    @GET("api/auth/validate")
    Call<Map<String, Object>> validateToken();

    // ═══════════════════════════════════════════════════════
    // DOCTORS (MÉDECINS)
    // ═══════════════════════════════════════════════════════

    /**
     * Récupérer tous les médecins
     * GET /api/doctors
     */
    @GET("api/doctors")
    Call<List<DoctorResponse>> getAllDoctors();

    // ═══════════════════════════════════════════════════════
    // TIMESLOTS (CRÉNEAUX HORAIRES)
    // ═══════════════════════════════════════════════════════

    /**
     * Récupérer les heures disponibles pour un médecin à une date
     * GET /api/timeslots/available?doctorId={id}&date={date}
     *
     * @param doctorId ID du médecin
     * @param date Format "YYYY-MM-DD"
     * @return Liste des heures disponibles (ex: ["09:00:00", "09:30:00"])
     */
    @GET("api/timeslots/available")
    Call<List<String>> getAvailableTimeSlots(@Query("doctorId") Long doctorId, @Query("date") String date);

    /**
     * Obtenir tous les créneaux d'un médecin
     * GET /api/timeslots/doctor/{doctorId}
     */
    @GET("api/timeslots/doctor/{doctorId}")
    Call<List<TimeSlotResponse>> getDoctorTimeSlots(@Path("doctorId") Long doctorId);

    /**
     * Créer un créneau horaire
     * POST /api/timeslots
     */
    @POST("api/timeslots")
    Call<TimeSlotResponse> createTimeSlot(@Body CreateTimeSlotRequest request);

    /**
     * Modifier un créneau horaire
     * PUT /api/timeslots/{id}
     */
    @PUT("api/timeslots/{id}")
    Call<TimeSlotResponse> updateTimeSlot(@Path("id") Long timeSlotId, @Body UpdateTimeSlotRequest request);

    /**
     * Supprimer un créneau horaire
     * DELETE /api/timeslots/{id}
     */
    @DELETE("api/timeslots/{id}")
    Call<Void> deleteTimeSlot(@Path("id") Long timeSlotId);

    // ═══════════════════════════════════════════════════════
    // APPOINTMENTS (RENDEZ-VOUS)
    // ═══════════════════════════════════════════════════════

    /**
     * Créer un rendez-vous
     * POST /api/appointments?patientId={id}
     *
     * @param patientId ID du patient qui prend RDV
     * @param request Détails du rendez-vous (doctorId, dateTime, reason)
     */
    @POST("api/appointments")
    Call<AppointmentResponse> createAppointment(@Query("patientId") Long patientId, @Body CreateAppointmentRequest request);

    /**
     * Obtenir tous les rendez-vous (Admin uniquement)
     * GET /api/appointments
     */
    @GET("api/appointments")
    Call<List<AppointmentResponse>> getAllAppointments();

    /**
     * Obtenir les rendez-vous d'un patient
     * GET /api/appointments/patient/{patientId}
     */
    @GET("api/appointments/patient/{patientId}")
    Call<List<AppointmentResponse>> getPatientAppointments(@Path("patientId") Long patientId);

    /**
     * Obtenir les rendez-vous d'un médecin
     * GET /api/appointments/doctor/{doctorId}
     */
    @GET("api/appointments/doctor/{doctorId}")
    Call<List<AppointmentResponse>> getDoctorAppointments(@Path("doctorId") Long doctorId);

    /**
     * Confirmer un rendez-vous
     * POST /api/appointments/{id}/confirm
     */
    @POST("api/appointments/{id}/confirm")
    Call<AppointmentResponse> confirmAppointment(@Path("id") Long appointmentId);

    /**
     * Annuler un rendez-vous
     * DELETE /api/appointments/{id}?cancelledBy={who}
     *
     * @param appointmentId ID du rendez-vous
     * @param cancelledBy Qui annule (PATIENT/DOCTOR/ADMIN)
     * @param request Body avec raison (peut être null)
     */
    @HTTP(method = "DELETE", path = "api/appointments/{id}", hasBody = true)
    Call<Void> cancelAppointment(@Path("id") Long appointmentId, @Query("cancelledBy") String cancelledBy, @Body Object request);

    // ═══════════════════════════════════════════════════════
    // ADMIN ENDPOINTS
    // ═══════════════════════════════════════════════════════

    /**
     * Obtenir le dashboard admin avec statistiques
     * GET /api/admin/dashboard
     */
    @GET("api/admin/dashboard")
    Call<DashboardResponse> getDashboard();

    /**
     * Obtenir tous les utilisateurs
     * GET /api/users
     */
    @GET("api/users")
    Call<List<UserResponse>> getAllUsers();

    /**
     * Obtenir un utilisateur par ID
     * GET /api/users/{id}
     */
    @GET("api/users/{id}")
    Call<UserResponse> getUserById(@Path("id") Long userId);

    /**
     * Créer un patient
     * POST /api/users/patient
     */
    @POST("api/users/patient")
    Call<UserResponse> createPatient(@Body CreateUserRequest request);

    /**
     * Créer un médecin
     * POST /api/users/doctor
     */
    @POST("api/users/doctor")
    Call<UserResponse> createDoctor(@Body CreateUserRequest request);

    /**
     * Modifier un utilisateur
     * PUT /api/users/{id}
     *
     * Note: Admin ne peut pas modifier d'autres admins
     */
    @PUT("api/users/{id}")
    Call<UserResponse> updateUser(@Path("id") Long userId, @Body UpdateUserRequest request);

    /**
     * Supprimer un utilisateur
     * DELETE /api/users/{id}
     *
     * Note: Admin ne peut pas supprimer d'autres admins
     */
    @DELETE("api/users/{id}")
    Call<Void> deleteUser(@Path("id") Long userId);
}
