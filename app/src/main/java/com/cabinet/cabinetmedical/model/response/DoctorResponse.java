package com.cabinet.cabinetmedical.model.response;

/**
 * DoctorResponse - Modèle pour les informations d'un médecin
 *
 * UTILISATION:
 * - Récupéré depuis GET /api/doctors
 * - Affiché dans DoctorListActivity (RecyclerView)
 *
 * STRUCTURE BACKEND:
 * {
 *   "id": 1,
 *   "userId": 2,
 *   "email": "doctor@email.com",
 *   "firstName": "Saad",
 *   "lastName": "Doctor",
 *   "phone": "0612345678",
 *   "specialty": "Pédiatre",
 *   "createdAt": "2026-01-31T16:17:04.422835"
 * }
 */
public class DoctorResponse {

    private Long id;           // ID du médecin (table doctor)
    private Long userId;       // ID de l'utilisateur (table users)
    private String email;
    private String firstName;
    private String lastName;
    private String phone;
    private String specialty;  // Spécialité du médecin
    private String createdAt;  // Date de création

    // Constructeur vide (requis par Gson)
    public DoctorResponse() {
    }

    // Constructeur complet
    public DoctorResponse(Long id, Long userId, String email, String firstName,
                         String lastName, String phone, String specialty, String createdAt) {
        this.id = id;
        this.userId = userId;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
        this.specialty = specialty;
        this.createdAt = createdAt;
    }

    // Getters et Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    // ═══════════════════════════════════════════════════════════
    // MÉTHODES UTILITAIRES
    // ═══════════════════════════════════════════════════════════

    /**
     * Retourne le nom complet du médecin avec titre "Dr."
     *
     * @return "Dr. Saad Doctor"
     */
    public String getFullName() {
        return "Dr. " + firstName + " " + lastName;
    }

    /**
     * Retourne le nom complet avec spécialité
     *
     * @return "Dr. Saad Doctor - Pédiatre"
     */
    public String getFullNameWithSpecialty() {
        String fullName = getFullName();
        if (specialty != null && !specialty.isEmpty()) {
            return fullName + " - " + specialty;
        }
        return fullName;
    }

    /**
     * Retourne la spécialité ou "Médecin généraliste" si null
     *
     * @return Spécialité ou valeur par défaut
     */
    public String getSpecialtyOrDefault() {
        return (specialty != null && !specialty.isEmpty()) ? specialty : "Médecin généraliste";
    }
}
