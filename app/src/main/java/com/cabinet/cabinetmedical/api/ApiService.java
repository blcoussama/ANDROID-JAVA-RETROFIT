package com.cabinet.cabinetmedical.api;

import com.cabinet.cabinetmedical.model.request.CancelAppointmentRequest;
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
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Routes de l'API SPRING-BOOT-JWT-REST-API utilisées par l'application.
 * L'identité de l'utilisateur vient toujours du jeton : aucune route ne reçoit
 * l'identifiant du patient ou du médecin connecté pour décider d'un accès.
 */
public interface ApiService {

    // --- Authentification ---

    @POST("api/auth/register")
    Call<LoginResponse> register(@Body RegisterRequest request);

    @POST("api/auth/login")
    Call<LoginResponse> login(@Body LoginRequest request);

    /** 200 si le jeton est valide, 401 sinon. */
    @GET("api/auth/validate")
    Call<Map<String, Object>> validateToken();

    /** Révoque côté serveur tous les jetons de l'utilisateur ; l'en-tête est passé explicitement. */
    @POST("api/auth/logout")
    Call<Void> logout(@Header("Authorization") String bearerToken);

    // --- Médecins (annuaire public) ---

    @GET("api/doctors")
    Call<List<DoctorResponse>> getAllDoctors();

    // --- Créneaux ---

    /** Heures encore réservables ("09:00:00", ...) pour un médecin à une date (yyyy-MM-dd). */
    @GET("api/timeslots/available")
    Call<List<String>> getAvailableTimeSlots(@Query("doctorId") Long doctorId, @Query("date") String date);

    @GET("api/timeslots/doctor/{doctorId}")
    Call<List<TimeSlotResponse>> getDoctorTimeSlots(@Path("doctorId") Long doctorId);

    /** Médecin : créneau toujours créé à son nom (doctorId ignoré par l'API). */
    @POST("api/timeslots")
    Call<TimeSlotResponse> createTimeSlot(@Body CreateTimeSlotRequest request);

    @PUT("api/timeslots/{id}")
    Call<TimeSlotResponse> updateTimeSlot(@Path("id") Long timeSlotId, @Body UpdateTimeSlotRequest request);

    @DELETE("api/timeslots/{id}")
    Call<Void> deleteTimeSlot(@Path("id") Long timeSlotId);

    // --- Rendez-vous ---

    /** Réservation par le patient connecté. */
    @POST("api/appointments")
    Call<AppointmentResponse> createAppointment(@Body CreateAppointmentRequest request);

    /** Rendez-vous du patient ou du médecin connecté. */
    @GET("api/appointments/me")
    Call<List<AppointmentResponse>> getMyAppointments();

    /** Tous les rendez-vous (administrateur). */
    @GET("api/appointments")
    Call<List<AppointmentResponse>> getAllAppointments();

    @POST("api/appointments/{id}/confirm")
    Call<AppointmentResponse> confirmAppointment(@Path("id") Long appointmentId);

    /** Annulation ; l'API déduit qui annule (patient, médecin, admin) du jeton. */
    @POST("api/appointments/{id}/cancel")
    Call<AppointmentResponse> cancelAppointment(@Path("id") Long appointmentId,
            @Body CancelAppointmentRequest request);

    // --- Administration ---

    @GET("api/admin/dashboard")
    Call<DashboardResponse> getDashboard();

    /** Comptes filtrés par rôle : PATIENT, DOCTOR ou ADMIN. */
    @GET("api/users")
    Call<List<UserResponse>> getUsers(@Query("role") String role);

    @GET("api/users/{id}")
    Call<UserResponse> getUserById(@Path("id") Long userId);

    @POST("api/users/patient")
    Call<UserResponse> createPatient(@Body CreateUserRequest request);

    @POST("api/users/doctor")
    Call<UserResponse> createDoctor(@Body CreateUserRequest request);

    @PUT("api/users/{id}")
    Call<UserResponse> updateUser(@Path("id") Long userId, @Body UpdateUserRequest request);

    @DELETE("api/users/{id}")
    Call<Void> deleteUser(@Path("id") Long userId);
}
