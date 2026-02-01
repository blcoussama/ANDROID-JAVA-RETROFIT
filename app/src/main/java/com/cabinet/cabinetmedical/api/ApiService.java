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
 * ApiService - Interface Retrofit pour les appels API

 * RESPONSABILITÉS:
 * - Définir tous les endpoints REST du backend
 * - Retrofit génère automatiquement l'implémentation
 * - Gestion automatique de la sérialisation JSON (Gson)

 * UTILISATION:
 * ApiService apiService = ApiClient.getClient(context).create(ApiService.class);
 * Call<LoginResponse> call = apiService.login(loginRequest);
 * call.enqueue(new Callback<LoginResponse>() { ... });

 * ANNOTATIONS RETROFIT:
 * - @POST : Requête HTTP POST
 * - @GET : Requête HTTP GET
 * - @Body : Corps de la requête (JSON)
 * - @Query : Paramètre query string (?email=xxx)
 * - @Path : Paramètre dans l'URL (/users/{id})
 * - @Header : Header HTTP personnalisé

 * NOTES:
 * - Call<T> : Requête asynchrone (enqueue) ou synchrone (execute)
 * - LoginResponse : Désérialisé automatiquement par Gson
 * - JWT token ajouté automatiquement par JwtInterceptor
 */
public interface ApiService {

    // ═══════════════════════════════════════════════════════
    // AUTHENTIFICATION
    // ═══════════════════════════════════════════════════════

    /**
     * Inscription d'un nouveau patient (Inscription patient)

     * ENDPOINT: POST /api/auth/register

     * REQUEST:
     * {
     *   "email": "jean@gmail.com",
     *   "password": "password123",
     *   "firstName": "Jean",
     *   "lastName": "Dupont",
     *   "phone": "0612345678"
     * }

     * RESPONSE 201 CREATED:
     * LoginResponse avec token JWT

     * UTILISATION:
     * RegisterRequest request = new RegisterRequest(...);
     * Call<LoginResponse> call = apiService.register(request);
     * call.enqueue(new Callback<LoginResponse>() {
     *     @Override
     *     public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
     *         if (response.isSuccessful()) {
     *             LoginResponse loginResponse = response.body();
     *             String token = loginResponse.getToken();
     *             // Stocker token et rediriger
     *         }
     *     }
     * });
     *
     * @param request RegisterRequest
     * @return Call<LoginResponse>
     */
    @POST("api/auth/register")
    Call<LoginResponse> register(@Body RegisterRequest request);

    /**
     * Connexion utilisateur (Connexion tous rôles)

     * ENDPOINT: POST /api/auth/login

     * REQUEST:
     * {
     *   "email": "jean@gmail.com",
     *   "password": "password123"
     * }

     * RESPONSE 200 OK:
     * LoginResponse avec token JWT + infos utilisateur

     * UTILISATION:
     * LoginRequest request = new LoginRequest("jean@gmail.com", "password123");
     * Call<LoginResponse> call = apiService.login(request);
     * call.enqueue(new Callback<LoginResponse>() {
     *     @Override
     *     public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
     *         if (response.isSuccessful()) {
     *             LoginResponse loginResponse = response.body();
     *             // Stocker token dans SharedPreferences
     *             // Rediriger selon role (PATIENT/DOCTOR/ADMIN)
     *         } else if (response.code() == 401) {
     *             // Email ou mot de passe incorrect
     *         }
     *     }
     * });
     *
     * @param request LoginRequest
     * @return Call<LoginResponse>
     */
    @POST("api/auth/login")
    Call<LoginResponse> login(@Body LoginRequest request);

    /**
     * Vérifier si un email existe déjà

     * ENDPOINT: GET /api/auth/email-exists?email=test@example.com

     * RESPONSE 200 OK:
     * {
     *   "exists": true,
     *   "message": "Email déjà utilisé"
     * }

     * UTILISATION:
     * Validation en temps réel lors de l'inscription

     * Call<Map<String, Object>> call = apiService.emailExists("jean@gmail.com");
     * call.enqueue(new Callback<Map<String, Object>>() {
     *     @Override
     *     public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
     *         if (response.isSuccessful()) {
     *             Map<String, Object> result = response.body();
     *             boolean exists = (boolean) result.get("exists");
     *             if (exists) {
     *                 // Afficher erreur "Email déjà utilisé"
     *             }
     *         }
     *     }
     * });
     *
     * @param email Email à vérifier
     * @return Call<Map<String, Object>>
     */
    @GET("api/auth/email-exists")
    Call<Map<String, Object>> emailExists(@Query("email") String email);

    /**
     * Valider le token JWT
     *
     * ENDPOINT: GET /api/auth/validate
     *
     * SÉCURITÉ:
     * - JwtInterceptor ajoute automatiquement le token dans le header
     * - Backend valide le token avec JwtAuthenticationFilter
     *
     * RESPONSE 200 OK (token valide):
     * {
     *   "valid": true,
     *   "message": "Token valide"
     * }
     *
     * RESPONSE 401 UNAUTHORIZED (token expiré/invalide):
     * ErrorResponse du backend
     *
     * UTILISATION:
     * Call<Map<String, Object>> call = apiService.validateToken();
     * call.enqueue(new Callback<Map<String, Object>>() {
     *     @Override
     *     public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
     *         if (response.isSuccessful()) {
     *             // Token valide (200 OK) → MainActivity
     *             Map<String, Object> result = response.body();
     *             boolean valid = (boolean) result.get("valid");
     *             if (valid) {
     *                 // Rediriger vers MainActivity
     *             }
     *         } else if (response.code() == 401) {
     *             // Token expiré/invalide (401 UNAUTHORIZED)
     *             // Clear SharedPreferences + LoginActivity
     *         }
     *     }
     *
     *     @Override
     *     public void onFailure(Call<Map<String, Object>> call, Throwable t) {
     *         // Erreur réseau → LoginActivity (sécurité)
     *     }
     * });
     *
     * @return Call<Map<String, Object>>
     */
    @GET("api/auth/validate")
    Call<Map<String, Object>> validateToken();

    // ═══════════════════════════════════════════════════════
    // DOCTORS (MÉDECINS)
    // ═══════════════════════════════════════════════════════

    /**
     * Récupérer la liste de tous les médecins
     *
     * ENDPOINT: GET /api/doctors
     *
     * RESPONSE 200 OK:
     * [
     *   {
     *     "id": 1,
     *     "userId": 2,
     *     "email": "doctor@email.com",
     *     "firstName": "Saad",
     *     "lastName": "Doctor",
     *     "phone": "0612345678",
     *     "specialty": "Pédiatre",
     *     "createdAt": "2026-01-31T16:17:04.422835"
     *   },
     *   ...
     * ]
     *
     * UTILISATION:
     * Call<List<DoctorResponse>> call = apiService.getAllDoctors();
     * call.enqueue(new Callback<List<DoctorResponse>>() {
     *     @Override
     *     public void onResponse(Call<List<DoctorResponse>> call, Response<List<DoctorResponse>> response) {
     *         if (response.isSuccessful()) {
     *             List<DoctorResponse> doctors = response.body();
     *             // Afficher dans RecyclerView
     *         }
     *     }
     * });
     *
     * @return Call<List<DoctorResponse>>
     */
    @GET("api/doctors")
    Call<List<DoctorResponse>> getAllDoctors();

    // ═══════════════════════════════════════════════════════
    // TIMESLOTS (CRÉNEAUX HORAIRES)
    // ═══════════════════════════════════════════════════════

    /**
     * Récupérer les heures disponibles pour un médecin à une date donnée
     *
     * ENDPOINT: GET /api/timeslots/available?doctorId={id}&date={date}
     *
     * RESPONSE 200 OK:
     * ["09:00:00", "09:30:00", "10:00:00", "10:30:00", ...]
     *
     * @param doctorId ID du médecin
     * @param date Date au format "YYYY-MM-DD"
     * @return Call<List<String>> Liste des heures disponibles
     */
    @GET("api/timeslots/available")
    Call<List<String>> getAvailableTimeSlots(
            @Query("doctorId") Long doctorId,
            @Query("date") String date
    );

    /**
     * Obtenir tous les créneaux d'un médecin (Gestion créneaux)
     *
     * ENDPOINT: GET /api/timeslots/doctor/{doctorId}
     *
     * RESPONSE 200 OK:
     * [
     *   {
     *     "id": 1,
     *     "doctorId": 1,
     *     "doctorName": "Dr. Saad Doctor",
     *     "doctorSpecialty": "Pédiatre",
     *     "dayOfWeek": "MONDAY",
     *     "dayOfWeekFr": "Lundi",
     *     "startTime": "09:00:00",
     *     "endTime": "12:00:00",
     *     "durationMinutes": 180
     *   },
     *   ...
     * ]
     *
     * @param doctorId ID du médecin
     * @return Call<List<TimeSlotResponse>> Liste des créneaux du médecin
     */
    @GET("api/timeslots/doctor/{doctorId}")
    Call<List<TimeSlotResponse>> getDoctorTimeSlots(@Path("doctorId") Long doctorId);

    /**
     * Créer un créneau horaire (Gestion créneaux)
     *
     * ENDPOINT: POST /api/timeslots
     *
     * REQUEST BODY:
     * {
     *   "doctorId": 1,
     *   "dayOfWeek": "MONDAY",
     *   "startTime": "09:00:00",
     *   "endTime": "12:00:00"
     * }
     *
     * RESPONSE 201 CREATED:
     * TimeSlotResponse
     *
     * @param request CreateTimeSlotRequest
     * @return Call<TimeSlotResponse> Créneau créé
     */
    @POST("api/timeslots")
    Call<TimeSlotResponse> createTimeSlot(@Body CreateTimeSlotRequest request);

    /**
     * Modifier un créneau horaire (Gestion créneaux)
     *
     * ENDPOINT: PUT /api/timeslots/{id}
     *
     * REQUEST BODY:
     * {
     *   "dayOfWeek": "TUESDAY",
     *   "startTime": "14:00:00",
     *   "endTime": "18:00:00"
     * }
     *
     * RESPONSE 200 OK:
     * TimeSlotResponse
     *
     * @param timeSlotId ID du créneau
     * @param request UpdateTimeSlotRequest
     * @return Call<TimeSlotResponse> Créneau modifié
     */
    @PUT("api/timeslots/{id}")
    Call<TimeSlotResponse> updateTimeSlot(@Path("id") Long timeSlotId, @Body UpdateTimeSlotRequest request);

    /**
     * Supprimer un créneau horaire (Gestion créneaux)
     *
     * ENDPOINT: DELETE /api/timeslots/{id}
     *
     * RESPONSE 204 NO CONTENT
     *
     * @param timeSlotId ID du créneau
     * @return Call<Void>
     */
    @DELETE("api/timeslots/{id}")
    Call<Void> deleteTimeSlot(@Path("id") Long timeSlotId);

    // ═══════════════════════════════════════════════════════
    // APPOINTMENTS (RENDEZ-VOUS)
    // ═══════════════════════════════════════════════════════

    /**
     * Créer un rendez-vous
     *
     * ENDPOINT: POST /api/appointments?patientId={id}
     *
     * REQUEST BODY:
     * {
     *   "doctorId": 1,
     *   "dateTime": "2026-02-03T09:00:00",
     *   "reason": "Consultation générale"
     * }
     *
     * @param patientId ID du patient qui prend RDV
     * @param request CreateAppointmentRequest
     * @return Call<AppointmentResponse> Réponse du backend
     */
    @POST("api/appointments")
    Call<AppointmentResponse> createAppointment(
            @Query("patientId") Long patientId,
            @Body CreateAppointmentRequest request);

    /**
     * Obtenir TOUS les rendez-vous (Admin uniquement)
     *
     * ENDPOINT: GET /api/appointments
     *
     * RESPONSE 200 OK:
     * [
     *   {
     *     "id": 1,
     *     "patientId": 1,
     *     "patientName": "Jean Dupont",
     *     "doctorId": 1,
     *     "doctorName": "Dr. Pierre Dupont",
     *     "doctorSpecialty": "Pédiatre",
     *     "dateTime": "2026-02-03T14:00:00",
     *     "reason": "Consultation",
     *     "status": "PENDING"
     *   }
     * ]
     *
     * @return Call<List<AppointmentResponse>> Liste de tous les RDV
     */
    @GET("api/appointments")
    Call<List<AppointmentResponse>> getAllAppointments();

    /**
     * Obtenir tous les RDV d'un patient (Historique patient)
     *
     * ENDPOINT: GET /api/appointments/patient/{patientId}
     *
     * RESPONSE 200 OK:
     * [
     *   {
     *     "id": 1,
     *     "patientId": 1,
     *     "patientName": "Jean Dupont",
     *     "doctorId": 1,
     *     "doctorName": "Dr. Pierre Dupont",
     *     "doctorSpecialty": "Pédiatre",
     *     "dateTime": "2026-02-03T14:00:00",
     *     "reason": "Consultation",
     *     "status": "PENDING",
     *     "cancelledBy": null,
     *     "cancellationReason": null
     *   }
     * ]
     *
     * @param patientId ID du patient
     * @return Call<List<AppointmentResponse>> Liste des RDV du patient
     */
    @GET("api/appointments/patient/{patientId}")
    Call<List<AppointmentResponse>> getPatientAppointments(@Path("patientId") Long patientId);

    /**
     * Obtenir tous les RDV d'un médecin (Historique médecin)
     *
     * ENDPOINT: GET /api/appointments/doctor/{doctorId}
     *
     * RESPONSE 200 OK:
     * [
     *   {
     *     "id": 1,
     *     "patientId": 1,
     *     "patientName": "Fatima Patient",
     *     "patientEmail": "patient@email.com",
     *     "patientPhone": "0612345678",
     *     "doctorId": 1,
     *     "doctorName": "Dr. Saad Doctor",
     *     "doctorSpecialty": "Pédiatre",
     *     "dateTime": "2026-02-03T14:00:00",
     *     "reason": "Consultation",
     *     "status": "PENDING"
     *   }
     * ]
     *
     * @param doctorId ID du médecin
     * @return Call<List<AppointmentResponse>> Liste des RDV du médecin
     */
    @GET("api/appointments/doctor/{doctorId}")
    Call<List<AppointmentResponse>> getDoctorAppointments(@Path("doctorId") Long doctorId);

    /**
     * Confirmer un rendez-vous (Confirmer RDV)
     *
     * ENDPOINT: POST /api/appointments/{id}/confirm
     *
     * RESPONSE 200 OK:
     * {
     *   "id": 1,
     *   "status": "CONFIRMED",
     *   ...
     * }
     *
     * @param appointmentId ID du RDV
     * @return Call<AppointmentResponse> RDV confirmé
     */
    @POST("api/appointments/{id}/confirm")
    Call<AppointmentResponse> confirmAppointment(@Path("id") Long appointmentId);

    /**
     * Annuler un rendez-vous (Annuler RDV)
     *
     * ENDPOINT: DELETE /api/appointments/{id}?cancelledBy={who}
     *
     * @param appointmentId ID du RDV
     * @param cancelledBy Qui annule (PATIENT/DOCTOR/ADMIN)
     * @param request Body avec raison (passer null si pas de raison)
     * @return Call<Void>
     */
    @HTTP(method = "DELETE", path = "api/appointments/{id}", hasBody = true)
    Call<Void> cancelAppointment(
            @Path("id") Long appointmentId,
            @Query("cancelledBy") String cancelledBy,
            @Body Object request
    );

    // ═══════════════════════════════════════════════════════
    // ADMIN ENDPOINTS
    // ═══════════════════════════════════════════════════════

    /**
     * Obtenir le dashboard admin avec statistiques (Dashboard admin)
     *
     * ENDPOINT: GET /api/admin/dashboard
     *
     * RESPONSE 200 OK:
     * {
     *   "totalAppointmentsToday": 2,
     *   "totalAppointmentsWeek": 5,
     *   "appointmentsByStatus": {
     *     "PENDING": 2,
     *     "CONFIRMED": 1,
     *     "CANCELLED": 2
     *   },
     *   "recentAppointments": [...],
     *   "totalDoctors": 2,
     *   "totalPatients": 3,
     *   "totalUsers": 6
     * }
     *
     * @return Call<DashboardResponse> Statistiques du dashboard
     */
    @GET("api/admin/dashboard")
    Call<DashboardResponse> getDashboard();

    /**
     * Obtenir la liste de tous les utilisateurs (Liste utilisateurs)
     *
     * ENDPOINT: GET /api/users
     *
     * RESPONSE 200 OK:
     * [
     *   {
     *     "id": 1,
     *     "email": "user@email.com",
     *     "firstName": "Jean",
     *     "lastName": "Dupont",
     *     "phone": "0612345678",
     *     "role": "DOCTOR",
     *     "createdAt": "2026-01-31T10:00:00",
     *     "lastLoginAt": "2026-01-31T18:00:00"
     *   },
     *   ...
     * ]
     *
     * @return Call<List<UserResponse>> Liste de tous les utilisateurs
     */
    @GET("api/users")
    Call<List<UserResponse>> getAllUsers();

    /**
     * Obtenir les détails d'un utilisateur
     *
     * ENDPOINT: GET /api/users/{id}
     *
     * @param userId ID de l'utilisateur
     * @return Call<UserResponse> Détails de l'utilisateur
     */
    @GET("api/users/{id}")
    Call<UserResponse> getUserById(@Path("id") Long userId);

    /**
     * Créer un patient (Créer patient)
     *
     * ENDPOINT: POST /api/users/patient
     *
     * REQUEST BODY:
     * {
     *   "email": "patient@email.com",
     *   "password": "Password123",
     *   "firstName": "Marie",
     *   "lastName": "Dupont",
     *   "phone": "0612345678",
     *   "specialty": null
     * }
     *
     * @param request CreateUserRequest (sans specialty)
     * @return Call<UserResponse> Utilisateur créé
     */
    @POST("api/users/patient")
    Call<UserResponse> createPatient(@Body CreateUserRequest request);

    /**
     * Créer un médecin (Créer médecin)
     *
     * ENDPOINT: POST /api/users/doctor
     *
     * REQUEST BODY:
     * {
     *   "email": "doctor@email.com",
     *   "password": "Password123",
     *   "firstName": "Pierre",
     *   "lastName": "Martin",
     *   "phone": "0612345678",
     *   "specialty": "Cardiologue"
     * }
     *
     * @param request CreateUserRequest (avec specialty obligatoire)
     * @return Call<UserResponse> Médecin créé
     */
    @POST("api/users/doctor")
    Call<UserResponse> createDoctor(@Body CreateUserRequest request);

    /**
     * Modifier un utilisateur (Modifier utilisateur)
     *
     * ENDPOINT: PUT /api/users/{id}
     *
     * REQUEST BODY:
     * {
     *   "email": "newemail@email.com",
     *   "password": "",  // Vide = ne pas modifier
     *   "firstName": "Jean",
     *   "lastName": "Dupont",
     *   "phone": "0612345678"
     * }
     *
     * NOTE: Admin ne peut pas modifier d'autres admins (sécurité)
     *
     * @param userId ID de l'utilisateur à modifier
     * @param request UpdateUserRequest
     * @return Call<UserResponse> Utilisateur modifié
     */
    @PUT("api/users/{id}")
    Call<UserResponse> updateUser(@Path("id") Long userId, @Body UpdateUserRequest request);

    /**
     * Supprimer un utilisateur (Supprimer utilisateur)
     *
     * ENDPOINT: DELETE /api/users/{id}
     *
     * NOTE: Admin ne peut pas supprimer d'autres admins (sécurité)
     *
     * @param userId ID de l'utilisateur à supprimer
     * @return Call<Void> 204 NO CONTENT si succès
     */
    @DELETE("api/users/{id}")
    Call<Void> deleteUser(@Path("id") Long userId);

    // On ajoutera d'autres endpoints au fur et à mesure
}