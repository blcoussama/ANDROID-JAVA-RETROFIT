package com.cabinet.cabinetmedical.ui.common;

import android.content.Context;
import android.text.InputFilter;
import android.text.InputType;
import android.widget.EditText;
import android.widget.FrameLayout;

import androidx.appcompat.app.AlertDialog;

/** Boîte de confirmation d'annulation avec une raison facultative (500 caractères max). */
public final class CancelReasonDialog {

    public interface OnConfirm {
        void onConfirm(String reason);
    }

    private CancelReasonDialog() {
    }

    public static void show(Context context, String appointmentLabel, OnConfirm onConfirm) {
        EditText input = new EditText(context);
        input.setHint("Raison de l'annulation (facultative)");
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES
                | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        input.setFilters(new InputFilter[] { new InputFilter.LengthFilter(500) });

        FrameLayout container = new FrameLayout(context);
        int padding = (int) (20 * context.getResources().getDisplayMetrics().density);
        container.setPadding(padding, padding / 2, padding, 0);
        container.addView(input);

        new AlertDialog.Builder(context)
                .setTitle("Annuler le rendez-vous ?")
                .setMessage(appointmentLabel)
                .setView(container)
                .setPositiveButton("Annuler le rendez-vous",
                        (dialog, which) -> onConfirm.onConfirm(input.getText().toString().trim()))
                .setNegativeButton("Retour", null)
                .show();
    }
}
