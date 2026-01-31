package com.cabinet.cabinetmedical.ui.admin;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.cabinet.cabinetmedical.R;
import com.cabinet.cabinetmedical.ui.auth.LoginActivity;
import com.google.android.material.button.MaterialButton;

/**
 * DashboardAdminActivity - Tableau de bord pour les ADMIN
 *
 * FONCTIONNALITÉS ACTUELLES:
 * - Affichage message de bienvenue "Admin Dashboard"
 * - Nom de l'admin connecté
 * - Bouton déconnexion (clear token + retour Login)
 *
 * FONCTIONNALITÉS FUTURES (selon ANDROID-CONCEPTION-PLANNING.md):
 * - Total RDV aujourd'hui
 * - Total RDV semaine
 * - RDV par status (PENDING, CONFIRMED, CANCELLED) - Cards ou Chart
 * - Bouton "Gérer Utilisateurs" → Liste users (CRUD)
 * - Bouton "Gérer Créneaux" → Créneaux tous médecins
 * - Bouton "Gérer RDV" → Tous les rendez-vous
 *
 * USE CASES COUVERTS:
 * - UC-A01: Se connecter (redirection ici après login)
 * - UC-A02: Voir tableau de bord (version basique)
 * - UC-A13: Se déconnecter
 *
 * ÉCRAN: SCR-A01 (version basique)
 *
 * API ENDPOINT:
 * - GET /api/admin/dashboard (pour charger statistiques)
 */
public class DashboardAdminActivity extends AppCompatActivity {

    private TextView welcomeMessage;
    private TextView adminName;
    private MaterialButton logoutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard_admin);

        // Initialiser les vues
        welcomeMessage = findViewById(R.id.welcome_message);
        adminName = findViewById(R.id.admin_name);
        logoutButton = findViewById(R.id.logout_button);

        // Charger les infos de l'admin depuis SharedPreferences
        loadAdminInfo();

        // Bouton déconnexion
        logoutButton.setOnClickListener(v -> logout());

        // TODO: Charger statistiques depuis API GET /api/admin/dashboard
    }

    /**
     * Charger les informations de l'admin depuis SharedPreferences
     * et afficher le message de bienvenue personnalisé
     */
    private void loadAdminInfo() {
        SharedPreferences prefs = getSharedPreferences("auth", MODE_PRIVATE);
        String firstName = prefs.getString("user_firstName", "Admin");
        String lastName = prefs.getString("user_lastName", "");

        // Afficher nom de l'admin
        String name = firstName + " " + lastName;
        adminName.setText(name);
    }

    /**
     * Déconnexion : Supprimer le token JWT et retourner au Login
     */
    private void logout() {
        // Supprimer toutes les données d'authentification
        SharedPreferences prefs = getSharedPreferences("auth", MODE_PRIVATE);
        prefs.edit().clear().apply();

        // Rediriger vers Login
        Intent intent = new Intent(DashboardAdminActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
