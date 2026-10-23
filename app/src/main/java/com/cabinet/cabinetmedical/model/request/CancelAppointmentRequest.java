package com.cabinet.cabinetmedical.model.request;

/** Corps de POST /api/appointments/{id}/cancel : raison facultative (500 caractères max). */
public class CancelAppointmentRequest {

    private final String cancellationReason;

    public CancelAppointmentRequest(String cancellationReason) {
        this.cancellationReason = cancellationReason == null || cancellationReason.trim().isEmpty()
                ? null : cancellationReason.trim();
    }

    public String getCancellationReason() {
        return cancellationReason;
    }
}
