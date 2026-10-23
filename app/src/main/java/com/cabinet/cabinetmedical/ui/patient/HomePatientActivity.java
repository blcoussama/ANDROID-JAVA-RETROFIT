package com.cabinet.cabinetmedical.ui.patient;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.cabinet.cabinetmedical.R;
import com.cabinet.cabinetmedical.session.Session;
import com.cabinet.cabinetmedical.session.SessionManager;
import com.cabinet.cabinetmedical.session.SessionNavigator;
import com.cabinet.cabinetmedical.ui.auth.LoginActivity;
import com.google.android.material.button.MaterialButton;

/**
 * HomePatientActivity - Écran d'accueil du patient
 *
 * RÔLE:
 * Dashboard patient avec navigation vers les fonctionnalités principales.
 * Premier écran affiché après connexion patient (depuis SplashActivity ou LoginActivity).
 *
 * NAVIGATION:
 * - Bouton "Voir les médecins" → DoctorListActivity
 * - Bouton "Mes rendez-vous" → MyAppointmentsActivity
 * - Bouton "Déconnexion" → Retour LoginActivity
 */
public class HomePatientActivity extends AppCompatActivity {

    private TextView welcomeMessage;
    private MaterialButton seeDoctorsButton;
    private MaterialButton myAppointmentsButton;
    private MaterialButton logoutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_patient);

        // Initialiser les vues
        welcomeMessage = findViewById(R.id.welcome_message);
        seeDoctorsButton = findViewById(R.id.button_see_doctors);
        myAppointmentsButton = findViewById(R.id.button_my_appointments);
        logoutButton = findViewById(R.id.logout_button);

        // Charger les infos du patient depuis SharedPreferences
        loadPatientInfo();

        // Bouton voir les médecins
        seeDoctorsButton.setOnClickListener(v -> {
            Intent intent = new Intent(HomePatientActivity.this, DoctorListActivity.class);
            startActivity(intent);
        });

        // Bouton mes rendez-vous
        myAppointmentsButton.setOnClickListener(v -> {
            Intent intent = new Intent(HomePatientActivity.this, MyAppointmentsActivity.class);
            startActivity(intent);
        });

        // Bouton déconnexion
        logoutButton.setOnClickListener(v -> logout());
    }

    /**
     * Charger les informations du patient depuis SharedPreferences
     * et afficher le message de bienvenue personnalisé
     */
    private void loadPatientInfo() {
        Session session = SessionManager.get(this).getSession();
        String firstName = session != null ? session.getFirstName() : "Patient";
        String lastName = session != null ? session.getLastName() : "";

        // Afficher message de bienvenue
        String welcomeText = "Bienvenue " + firstName + " " + lastName + " !";
        welcomeMessage.setText(welcomeText);
    }

    /**
     * Déconnexion : Supprimer le token JWT et retourner au Login
     */
    /** Déconnexion : jetons révoqués côté API, session locale effacée, retour à la connexion. */
    private void logout() {
        SessionNavigator.logout(this);
    }
}
