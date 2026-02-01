package com.cabinet.cabinetmedical.model.request;

/**
 * RegisterRequest - Inscription d'un nouveau patient
 *
 * RÔLE:
 * Envoie les données pour créer un compte patient (email, mot de passe, nom, prénom, téléphone).
 * Utilisé par RegisterActivity.
 *
 * ENDPOINT: POST /api/auth/register
 * RETOUR: LoginResponse (compte créé + connexion automatique avec token JWT)
 */
public class RegisterRequest {

    /**
     * Email de l'utilisateur (sera le login)
     */
    private String email;

    /**
     * Mot de passe en clair (sera hashé en BCrypt côté backend)
     */
    private String password;

    /**
     * Prénom de l'utilisateur
     */
    private String firstName;

    /**
     * Nom de famille de l'utilisateur
     */
    private String lastName;

    /**
     * Numéro de téléphone (optionnel)
     * Format: 10 chiffres (ex: 0612345678)
     */
    private String phone;

    // ═══════════════════════════════════════════════════════
    // CONSTRUCTEURS
    // ═══════════════════════════════════════════════════════

    /**
     * Constructeur vide (requis par Gson)
     */
    public RegisterRequest() {
    }

    /**
     * Constructeur avec tous les paramètres
     */
    public RegisterRequest(String email, String password, String firstName,
                           String lastName, String phone) {
        this.email = email;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
    }

    // ═══════════════════════════════════════════════════════
    // GETTERS & SETTERS
    // ═══════════════════════════════════════════════════════

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
