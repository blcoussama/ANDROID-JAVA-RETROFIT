package com.cabinet.cabinetmedical.model.response;

/**
 * LoginResponse - Réponse après connexion ou inscription réussie
 *
 * RÔLE:
 * Contient le token JWT et les infos utilisateur pour authentifier les requêtes suivantes.
 * Utilisé par LoginActivity et RegisterActivity pour stocker les données en SharedPreferences.
 *
 * CONTENU:
 * - token: JWT à envoyer dans Header Authorization (Bearer {token})
 * - userId, email, firstName, lastName, role
 * - patientId (si PATIENT) ou doctorId (si DOCTOR)
 */
public class LoginResponse {

    /**
     * JWT Token
     * À stocker dans SharedPreferences
     * À envoyer dans Header: Authorization: Bearer {token}
     */
    private String token;

    /**
     * ID de l'utilisateur dans la table users
     */
    private Long userId;

    /**
     * Email de l'utilisateur (login)
     */
    private String email;

    /**
     * Prénom de l'utilisateur
     */
    private String firstName;

    /**
     * Nom de l'utilisateur
     */
    private String lastName;

    /**
     * Rôle de l'utilisateur
     * Valeurs: "PATIENT", "DOCTOR", "ADMIN"

     * UTILISATION:
     * Android redirige selon le rôle:
     * - PATIENT → Home Patient
     * - DOCTOR → Home Doctor
     * - ADMIN → Dashboard Admin
     */
    private String role;

    /**
     * ID du patient (si role=PATIENT, sinon null)
     * Utilisé pour: GET /api/appointments/patient/{patientId}
     */
    private Long patientId;

    /**
     * ID du médecin (si role=DOCTOR, sinon null)
     * Utilisé pour: GET /api/appointments/doctor/{doctorId}
     */
    private Long doctorId;

    // ═══════════════════════════════════════════════════════
    // CONSTRUCTEURS
    // ═══════════════════════════════════════════════════════

    /**
     * Constructeur vide (requis par Gson)
     */
    public LoginResponse() {
    }

    /**
     * Constructeur avec tous les paramètres
     */
    public LoginResponse(String token, Long userId, String email,
                         String firstName, String lastName, String role,
                         Long patientId, Long doctorId) {
        this.token = token;
        this.userId = userId;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.role = role;
        this.patientId = patientId;
        this.doctorId = doctorId;
    }

    // ═══════════════════════════════════════════════════════
    // GETTERS & SETTERS
    // ═══════════════════════════════════════════════════════

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Long getPatientId() {
        return patientId;
    }

    public void setPatientId(Long patientId) {
        this.patientId = patientId;
    }

    public Long getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(Long doctorId) {
        this.doctorId = doctorId;
    }
}
