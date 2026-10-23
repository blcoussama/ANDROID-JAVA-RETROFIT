package com.cabinet.cabinetmedical.session;

import com.cabinet.cabinetmedical.model.response.LoginResponse;

/** Session de l'utilisateur connecté, conservée chiffrée par {@link SessionManager}. */
public final class Session {

    private final String token;
    private final long userId;
    private final String email;
    private final String firstName;
    private final String lastName;
    private final String role;
    private final Long patientId;
    private final Long doctorId;

    public Session(String token, long userId, String email, String firstName, String lastName, String role,
            Long patientId, Long doctorId) {
        this.token = token;
        this.userId = userId;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.role = role;
        this.patientId = patientId;
        this.doctorId = doctorId;
    }

    public static Session from(LoginResponse response) {
        return new Session(response.getToken(), response.getUserId(), response.getEmail(), response.getFirstName(),
                response.getLastName(), response.getRole(), response.getPatientId(), response.getDoctorId());
    }

    public String getToken() {
        return token;
    }

    public long getUserId() {
        return userId;
    }

    public String getEmail() {
        return email;
    }

    public String getFirstName() {
        return firstName != null ? firstName : "";
    }

    public String getLastName() {
        return lastName != null ? lastName : "";
    }

    public String getFullName() {
        return (getFirstName() + " " + getLastName()).trim();
    }

    public String getRole() {
        return role != null ? role : "";
    }

    public Long getPatientId() {
        return patientId;
    }

    public Long getDoctorId() {
        return doctorId;
    }
}
