package com.cabinet.cabinetmedical.model.request;

/**
 * CreateUserRequest - Création d'un utilisateur par l'admin
 *
 * RÔLE:
 * Envoie les données pour créer un patient ou un médecin.
 * Utilisé par AdminAddPatientActivity et AdminAddDoctorActivity.
 *
 * ENDPOINTS:
 * - POST /api/users/patient (specialty = null)
 * - POST /api/users/doctor (specialty obligatoire)
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
