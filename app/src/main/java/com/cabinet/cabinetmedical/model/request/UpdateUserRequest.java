package com.cabinet.cabinetmedical.model.request;

/**
 * UpdateUserRequest - Modification d'un utilisateur par l'admin
 *
 * RÔLE:
 * Envoie les données pour modifier un patient ou un médecin existant.
 * Utilisé par AdminEditPatientActivity et AdminEditDoctorActivity.
 *
 * ENDPOINT: PUT /api/users/{id}
 * NOTE: Password optionnel (vide = pas de changement)
 */
public class UpdateUserRequest {
    private String email;
    private String password;  // Optionnel
    private String firstName;
    private String lastName;
    private String phone;
    private String specialty;  // Pour médecins uniquement

    /**
     * Constructeur complet avec specialty (pour médecins)
     */
    public UpdateUserRequest(String email, String password, String firstName,
                            String lastName, String phone, String specialty) {
        this.email = email;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
        this.specialty = specialty;
    }

    /**
     * Constructeur sans specialty (pour patients)
     */
    public UpdateUserRequest(String email, String password, String firstName,
                            String lastName, String phone) {
        this(email, password, firstName, lastName, phone, null);
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

    public String getSpecialty() {
        return specialty;
    }

    public void setSpecialty(String specialty) {
        this.specialty = specialty;
    }
}
