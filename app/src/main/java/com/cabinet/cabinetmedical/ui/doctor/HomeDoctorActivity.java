package com.cabinet.cabinetmedical.ui.doctor;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.cabinet.cabinetmedical.R;
import com.cabinet.cabinetmedical.ui.auth.LoginActivity;
import com.google.android.material.button.MaterialButton;

/**
 * HomeDoctorActivity - Écran d'accueil pour les DOCTORS
 *
 * FONCTIONNALITÉS ACTUELLES:
 * - Affichage message de bienvenue "Dr. [Nom]"
 * - Bouton déconnexion (clear token + retour Login)
 *
 * FONCTIONNALITÉS FUTURES (selon ANDROID-CONCEPTION-PLANNING.md):
 * - Nombre de RDV aujourd'hui
 * - Prochain patient (nom, heure)
 * - Bouton "Mes RDV" → Calendrier rendez-vous
 * - Bouton "Gérer créneaux" → Gestion disponibilités
 *
 * USE CASES COUVERTS:
 * - UC-D01: Se connecter (redirection ici après login)
 * - UC-D07: Se déconnecter
 *
 * ÉCRAN: SCR-D01 (version basique)
 */
public class HomeDoctorActivity extends AppCompatActivity {

    private TextView welcomeMessage;
    private MaterialButton myAppointmentsButton;
    private MaterialButton manageTimeSlotsButton;
    private MaterialButton logoutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_doctor);

        // Initialiser les vues
        welcomeMessage = findViewById(R.id.welcome_message);
        myAppointmentsButton = findViewById(R.id.button_my_appointments);
        manageTimeSlotsButton = findViewById(R.id.button_manage_timeslots);
        logoutButton = findViewById(R.id.logout_button);

        // Charger les infos du médecin depuis SharedPreferences
        loadDoctorInfo();

        // Bouton mes rendez-vous
        myAppointmentsButton.setOnClickListener(v -> {
            Intent intent = new Intent(HomeDoctorActivity.this, DoctorAppointmentsActivity.class);
            startActivity(intent);
        });

        // Bouton gérer créneaux
        manageTimeSlotsButton.setOnClickListener(v -> {
            Intent intent = new Intent(HomeDoctorActivity.this, DoctorTimeSlotsActivity.class);
            startActivity(intent);
        });

        // Bouton déconnexion
        logoutButton.setOnClickListener(v -> logout());
    }

    /**
     * Charger les informations du médecin depuis SharedPreferences
     * et afficher le message de bienvenue personnalisé
     */
    private void loadDoctorInfo() {
        SharedPreferences prefs = getSharedPreferences("auth", MODE_PRIVATE);
        String firstName = prefs.getString("user_firstName", "Docteur");
        String lastName = prefs.getString("user_lastName", "");

        // Afficher message de bienvenue avec titre "Dr." + prénom + nom
        String welcomeText = "Bienvenue Dr. " + firstName + " " + lastName;
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
        Intent intent = new Intent(HomeDoctorActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
