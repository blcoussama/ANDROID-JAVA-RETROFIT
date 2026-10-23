package com.cabinet.cabinetmedical.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import com.cabinet.cabinetmedical.model.response.AppointmentResponse;
import com.cabinet.cabinetmedical.utils.DateTimes;
import com.google.gson.Gson;

import org.junit.Test;

import java.util.Date;

public class AppointmentResponseTest {

    private static final Date NOW = DateTimes.parse("2026-03-02T10:00:00");

    private static AppointmentResponse parse(String status, String dateTime, String cancelledBy, String reason) {
        String json = "{\"id\":1,\"status\":\"" + status + "\",\"dateTime\":\"" + dateTime + "\""
                + (cancelledBy != null ? ",\"cancelledBy\":\"" + cancelledBy + "\"" : "")
                + (reason != null ? ",\"cancellationReason\":\"" + reason + "\"" : "") + "}";
        return new Gson().fromJson(json, AppointmentResponse.class);
    }

    @Test
    public void upcomingAppointmentCanBeCancelledAndConfirmed() {
        AppointmentResponse appointment = parse("PENDING", "2026-03-05T09:30:00", null, null);
        assertTrue(appointment.canBeCancelled(NOW));
        assertTrue(appointment.canBeConfirmed(NOW));
        assertEquals("05/03/2026 à 09:30", appointment.getFormattedDateTime());
        assertNull(appointment.getCancellationLabel());
    }

    @Test
    public void pastOrCancelledAppointmentHasNoAction() {
        assertFalse(parse("CONFIRMED", "2026-03-01T09:00:00", null, null).canBeCancelled(NOW));
        assertFalse(parse("CONFIRMED", "2026-03-05T09:00:00", null, null).canBeConfirmed(NOW));
        AppointmentResponse cancelled = parse("CANCELLED", "2026-03-05T09:00:00", "DOCTOR", "Absence");
        assertFalse(cancelled.canBeCancelled(NOW));
        assertEquals("Annulé par le médecin : Absence", cancelled.getCancellationLabel());
    }

    @Test
    public void unreadableDateIsNeverActionable() {
        assertFalse(parse("PENDING", "demain", null, null).canBeCancelled(NOW));
        assertEquals("demain", parse("PENDING", "demain", null, null).getFormattedDateTime());
    }
}
