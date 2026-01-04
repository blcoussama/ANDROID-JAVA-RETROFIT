package com.cabinet.cabinetmedical.model.request;

/**
 * RegisterRequest - DTO pour l'inscription d'un patient

 * UTILISATION:
 * - UC-P01: Créer un compte patient

 * ENDPOINT:
 * POST /api/auth/register

 * FLOW:
 * 1. Android envoie RegisterRequest
 * 2. Backend valide données
 * 3. Crée User (role=PATIENT)
 * 4. Crée Patient (lié à User)
 * 5. Retourne LoginResponse (avec JWT token)

 * EXEMPLE JSON:
 * {
 *   "email": "jean@gmail.com",
 *   "password": "password123",
 *   "firstName": "Jean",
 *   "lastName": "Dupont",
 *   "phone": "0612345678"
 * }
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
