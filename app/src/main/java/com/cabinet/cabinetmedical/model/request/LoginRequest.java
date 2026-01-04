package com.cabinet.cabinetmedical.model.request;

/**
 * LoginRequest - DTO pour la requête de connexion

 * UTILISATION:
 * - UC-P02: Patient se connecte
 * - UC-D01: Doctor se connecte
 * - UC-A01: Admin se connecte

 * ENDPOINT:
 * POST /api/auth/login

 * EXEMPLE JSON:
 * {
 *   "email": "jean@gmail.com",
 *   "password": "password123"
 * }
 */
public class LoginRequest {

    /**
     * Email de l'utilisateur (login)
     */
    private String email;

    /**
     * Mot de passe de l'utilisateur
     */
    private String password;

    // ═══════════════════════════════════════════════════════
    // CONSTRUCTEURS
    // ═══════════════════════════════════════════════════════

    /**
     * Constructeur vide (requis par Gson)
     */
    public LoginRequest() {
    }

    /**
     * Constructeur avec tous les paramètres
     */
    public LoginRequest(String email, String password) {
        this.email = email;
        this.password = password;
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
}
