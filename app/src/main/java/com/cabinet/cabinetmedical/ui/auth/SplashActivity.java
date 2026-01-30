package com.cabinet.cabinetmedical.ui.auth;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;

import com.cabinet.cabinetmedical.MainActivity;
import com.cabinet.cabinetmedical.R;

/**
 * SplashActivity - Écran de démarrage de l'application
 *
 * FONCTIONS:
 * 1. Afficher logo/nom pendant 2 secondes
 * 2. Vérifier si token JWT existe
 * 3. Rediriger vers:
 *    - LoginActivity si pas de token
 *    - MainActivity si token existe
 *
 * FLOW:
 * App Launch → SplashActivity (2s)
 * ├── SharedPreferences contient "jwt_token" ?
 * │   ├── OUI → MainActivity (TODO: rediriger selon rôle)
 * │   └── NON → LoginActivity
 *
 * NOTES:
 * - Pas de ActionBar (thème NoActionBar)
 * - Durée splash: 2000ms (2 secondes)
 * - Handler pour délai asynchrone
 *
 * TODO FUTUR:
 * - Valider token JWT avec backend (vérifier expiration)
 * - Rediriger selon rôle (PATIENT → HomePatient, etc.)
 * - Afficher animation (fade in/out)
 */
public class SplashActivity extends AppCompatActivity {

    /**
     * Durée d'affichage du splash screen en millisecondes
     */
    private static final int SPLASH_DURATION = 2000; // 2 secondes

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Délai de 2 secondes puis vérification token
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                checkAuthAndNavigate();
            }
        }, SPLASH_DURATION);
    }

    /**
     * Vérifier l'authentification et naviguer vers l'écran approprié
     *
     * LOGIQUE:
     * 1. Récupérer SharedPreferences "auth"
     * 2. Vérifier si "jwt_token" existe
     * 3. Si OUI → MainActivity (utilisateur déjà connecté)
     * 4. Si NON → LoginActivity (utilisateur doit se connecter)
     *
     * AMÉLIORATION FUTURE:
     * - Valider token avec backend (GET /api/auth/validate)
     * - Vérifier expiration token (parser JWT localement)
     * - Rediriger selon rôle stocké
     */
    private void checkAuthAndNavigate() {
        // Récupérer SharedPreferences
        SharedPreferences prefs = getSharedPreferences("auth", MODE_PRIVATE);
        String token = prefs.getString("jwt_token", null);

        Intent intent;

        if (token != null && !token.isEmpty()) {
            // Token existe → Utilisateur déjà connecté
            // TODO: Rediriger selon rôle
            // String role = prefs.getString("user_role", "");
            // if (role.equals("PATIENT")) → HomePatientActivity
            // if (role.equals("DOCTOR")) → HomeDoctorActivity
            // if (role.equals("ADMIN")) → DashboardAdminActivity

            // Pour l'instant, on redirige vers MainActivity
            intent = new Intent(SplashActivity.this, MainActivity.class);
        } else {
            // TODO: Créer LoginActivity
            intent = new Intent(SplashActivity.this, MainActivity.class);
        }

        startActivity(intent);
        finish(); // Fermer SplashActivity (pas de retour arrière)
    }
}