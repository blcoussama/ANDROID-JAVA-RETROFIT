package com.cabinet.cabinetmedical.ui.common;

import com.cabinet.cabinetmedical.R;

/** Couleur associée au statut d'un rendez-vous. */
public final class StatusColors {

    private StatusColors() {
    }

    public static int forStatus(String status) {
        if ("CONFIRMED".equals(status)) {
            return R.color.status_confirmed;
        }
        if ("CANCELLED".equals(status)) {
            return R.color.status_cancelled;
        }
        return R.color.status_pending;
    }
}
