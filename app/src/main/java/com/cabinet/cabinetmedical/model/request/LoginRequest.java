package com.cabinet.cabinetmedical.model.request;

/**
 * LoginRequest - Requête de connexion pour tous les utilisateurs
 *
 * RÔLE:
 * Envoie les identifiants (email + mot de passe) au backend pour authentification.
 * Utilisé par LoginActivity pour connecter patient, médecin ou admin.
 *
 * ENDPOINT: POST /api/auth/login
 * RETOUR: LoginResponse (token JWT + infos utilisateur)
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

    // CONSTRUCTEURS

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

    // GETTERS & SETTERS

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
