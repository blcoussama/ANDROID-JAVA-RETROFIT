package com.cabinet.cabinetmedical.model.request;

/**
 * CreateUserRequest - DTO pour créer un utilisateur (Admin uniquement)
 *
 * UTILISATION:
 * - Admin crée un patient: POST /api/users/patient
 * - Admin crée un médecin: POST /api/users/doctor
 *
 * STRUCTURE BACKEND:
 * {
 *   "email": "doctor@email.com",
 *   "password": "Password123",
 *   "firstName": "Jean",
 *   "lastName": "Dupont",
 *   "phone": "0612345678",
 *   "specialty": "Cardiologue"  // Obligatoire pour DOCTOR, null pour PATIENT
 * }
 *
 * VALIDATION:
 * - Email unique
 * - Password minimum 6 caractères
 * - Specialty obligatoire SI on crée un DOCTOR
 */
public class CreateUserRequest {
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private String phone;
    private String specialty;  // Uniquement pour DOCTOR

    /**
     * Constructeur complet
     */
    public CreateUserRequest(String email, String password, String firstName,
                            String lastName, String phone, String specialty) {
        this.email = email;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
        this.specialty = specialty;
    }

    /**
     * Constructeur pour créer un PATIENT (sans specialty)
     */
    public CreateUserRequest(String email, String password, String firstName,
                            String lastName, String phone) {
        this(email, password, firstName, lastName, phone, null);
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

    public String getSpecialty() {
        return specialty;
    }

    public void setSpecialty(String specialty) {
        this.specialty = specialty;
    }
}
