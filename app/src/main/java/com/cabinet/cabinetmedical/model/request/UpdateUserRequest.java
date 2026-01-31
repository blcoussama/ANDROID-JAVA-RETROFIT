package com.cabinet.cabinetmedical.model.request;

/**
 * UpdateUserRequest - DTO pour modifier un utilisateur (Admin uniquement)
 *
 * UTILISATION:
 * - Admin modifie un utilisateur: PUT /api/users/{id}
 *
 * STRUCTURE BACKEND:
 * {
 *   "email": "newemail@email.com",
 *   "password": "",  // Vide = ne pas modifier, ou nouveau password
 *   "firstName": "Jean",
 *   "lastName": "Dupont",
 *   "phone": "0612345678"
 * }
 *
 * NOTE:
 * - Password OPTIONNEL: si vide ou null = pas de changement
 * - Si password fourni = sera mis à jour
 */
public class UpdateUserRequest {
    private String email;
    private String password;  // Optionnel
    private String firstName;
    private String lastName;
    private String phone;

    /**
     * Constructeur complet
     */
    public UpdateUserRequest(String email, String password, String firstName,
                            String lastName, String phone) {
        this.email = email;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
    }

    /**
     * Constructeur sans password (pour ne pas le changer)
     */
    public UpdateUserRequest(String email, String firstName, String lastName, String phone) {
        this(email, "", firstName, lastName, phone);
    }

    // Getters et Setters
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
