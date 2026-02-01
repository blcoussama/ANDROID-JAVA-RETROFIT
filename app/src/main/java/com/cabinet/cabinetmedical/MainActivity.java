package com.cabinet.cabinetmedical;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.cabinet.cabinetmedical.ui.auth.LoginActivity;

/**
 * MainActivity - Écran d'erreur pour rôle utilisateur inconnu
 *
 * Cette activité sert de fallback de sécurité dans LoginActivity.
 * Elle ne devrait JAMAIS être affichée en conditions normales.
 *
 * QUAND EST-ELLE UTILISÉE?
 * - Si le backend retourne un rôle inconnu (autre que PATIENT, DOCTOR, ADMIN)
 * - Scénario de fallback dans le switch case de LoginActivity
 *
 * ACTION:
 * - Affiche un message d'erreur clair
 * - Permet de retourner au login
 * - Déconnecte l'utilisateur (supprime le token JWT)
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Bouton retour au login
        Button btnBackToLogin = findViewById(R.id.btn_back_to_login);
        btnBackToLogin.setOnClickListener(v -> {
            // Déconnecter l'utilisateur (supprimer le token)
            clearUserData();

            // Retourner au login
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }

    /**
     * Supprimer toutes les données utilisateur sauvegardées
     */
    private void clearUserData() {
        SharedPreferences prefs = getSharedPreferences("auth", MODE_PRIVATE);
        prefs.edit().clear().apply();
    }
}