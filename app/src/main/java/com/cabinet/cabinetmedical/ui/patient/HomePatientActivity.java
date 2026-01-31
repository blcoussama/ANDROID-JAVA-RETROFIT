package com.cabinet.cabinetmedical.ui.patient;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.cabinet.cabinetmedical.R;
import com.cabinet.cabinetmedical.ui.auth.LoginActivity;
import com.google.android.material.button.MaterialButton;

/**
 * HomePatientActivity - Écran d'accueil pour les PATIENTS
 *
 * FONCTIONNALITÉS ACTUELLES:
 * - Affichage message de bienvenue avec prénom du patient
 * - Bouton déconnexion (clear token + retour Login)
 *
 * FONCTIONNALITÉS FUTURES (selon ANDROID-CONCEPTION-PLANNING.md):
 * - Prochain RDV (si existe)
 * - Bouton "Prendre RDV" → Liste médecins
 * - Bouton "Mes RDV" → Historique rendez-vous
 *
 * USE CASES COUVERTS:
 * - UC-P02: Se connecter (redirection ici après login)
 * - Déconnexion
 *
 * ÉCRAN: SCR-P02 (version basique)
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
        SharedPreferences prefs = getSharedPreferences("auth", MODE_PRIVATE);
        String firstName = prefs.getString("user_firstName", "Patient");
        String lastName = prefs.getString("user_lastName", "");

        // Afficher message de bienvenue
        String welcomeText = "Bienvenue " + firstName + " " + lastName + " !";
        welcomeMessage.setText(welcomeText);
    }

    /**
     * Déconnexion : Supprimer le token JWT et retourner au Login
     */
    private void logout() {
        // Supprimer toutes les données d'authentification
        SharedPreferences prefs = getSharedPreferences("auth", MODE_PRIVATE);
        prefs.edit().clear().apply();

        // Rediriger vers Login
        Intent intent = new Intent(HomePatientActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
