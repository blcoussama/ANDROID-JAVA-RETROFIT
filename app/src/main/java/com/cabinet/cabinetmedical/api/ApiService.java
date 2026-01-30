package com.cabinet.cabinetmedical.api;

import com.cabinet.cabinetmedical.model.request.LoginRequest;
import com.cabinet.cabinetmedical.model.request.RegisterRequest;
import com.cabinet.cabinetmedical.model.response.LoginResponse;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
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
     * Inscription d'un nouveau patient (UC-P01)

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
     * Connexion utilisateur (UC-P02, UC-D01, UC-A01)

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

    // ═══════════════════════════════════════════════════════
    // AUTRES ENDPOINTS (À AJOUTER PLUS TARD)
    // ═══════════════════════════════════════════════════════

    // GET /api/doctors
    // GET /api/appointments/patient/{id}
    // POST /api/appointments
    // etc.

    // On ajoutera ces endpoints au fur et à mesure
    // Pour l'instant, on se concentre sur l'authentification
}