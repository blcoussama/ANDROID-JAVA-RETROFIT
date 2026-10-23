package com.cabinet.cabinetmedical.session;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;

import com.cabinet.cabinetmedical.MainActivity;
import com.cabinet.cabinetmedical.api.ApiClient;
import com.cabinet.cabinetmedical.ui.admin.DashboardAdminActivity;
import com.cabinet.cabinetmedical.ui.auth.LoginActivity;
import com.cabinet.cabinetmedical.ui.doctor.HomeDoctorActivity;
import com.cabinet.cabinetmedical.ui.patient.HomePatientActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/** Navigation liée à la session : écran d'accueil selon le rôle, déconnexion. */
public final class SessionNavigator {

    private SessionNavigator() {
    }

    /** Écran d'accueil du rôle (écran d'erreur si le rôle est inconnu). */
    public static Intent homeIntent(Context context, String role) {
        Class<?> target;
        switch (role == null ? "" : role) {
            case "PATIENT":
                target = HomePatientActivity.class;
                break;
            case "DOCTOR":
                target = HomeDoctorActivity.class;
                break;
            case "ADMIN":
                target = DashboardAdminActivity.class;
                break;
            default:
                target = MainActivity.class;
                break;
        }
        Intent intent = new Intent(context, target);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        return intent;
    }

    /**
     * Déconnexion : révoque les jetons côté API (sans attendre la réponse), efface la
     * session locale chiffrée et revient à l'écran de connexion.
     */
    public static void logout(Activity activity) {
        SessionManager sessionManager = SessionManager.get(activity);
        String token = sessionManager.getToken();
        if (token != null) {
            ApiClient.getService(activity).logout("Bearer " + token).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                    // Rien à faire : la session locale est déjà effacée
                }

                @Override
                public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                    // Hors ligne : le jeton expirera de lui-même (durée de vie courte)
                }
            });
        }
        sessionManager.clear();
        Intent intent = new Intent(activity, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        activity.startActivity(intent);
        activity.finish();
    }
}
