package com.cabinet.cabinetmedical.model.response;

/**
 * UserResponse - DTO pour les informations d'un utilisateur
 *
 * UTILISATION:
 * - Admin liste tous les utilisateurs: GET /api/users
 * - Admin voit détails utilisateur: GET /api/users/{id}
 * - Admin crée/modifie utilisateur: retour des endpoints POST/PUT
 *
 * STRUCTURE BACKEND:
 * {
 *   "id": 1,
 *   "email": "user@email.com",
 *   "firstName": "Jean",
 *   "lastName": "Dupont",
 *   "phone": "0612345678",
 *   "role": "DOCTOR",  // PATIENT, DOCTOR, ADMIN
 *   "createdAt": "2026-01-31T10:00:00",
 *   "lastLoginAt": "2026-01-31T18:00:00"
 * }
 */
public class UserResponse {
    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    private String phone;
    private String role;  // PATIENT, DOCTOR, ADMIN
    private String createdAt;
    private String lastLoginAt;

    // Constructeur vide
    public UserResponse() {
    }

    // Constructeur complet
    public UserResponse(Long id, String email, String firstName, String lastName,
                       String phone, String role, String createdAt, String lastLoginAt) {
        this.id = id;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
        this.role = role;
        this.createdAt = createdAt;
        this.lastLoginAt = lastLoginAt;
    }

    // Getters et Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getLastLoginAt() {
        return lastLoginAt;
    }

    public void setLastLoginAt(String lastLoginAt) {
        this.lastLoginAt = lastLoginAt;
    }

    /**
     * Obtenir le nom complet
     */
    public String getFullName() {
        return firstName + " " + lastName;
    }

    /**
     * Vérifier si c'est un médecin
     */
    public boolean isDoctor() {
        return "DOCTOR".equals(role);
    }

    /**
     * Vérifier si c'est un patient
     */
    public boolean isPatient() {
        return "PATIENT".equals(role);
    }

    /**
     * Vérifier si c'est un admin
     */
    public boolean isAdmin() {
        return "ADMIN".equals(role);
    }
}
