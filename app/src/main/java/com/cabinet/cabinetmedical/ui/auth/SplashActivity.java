package com.cabinet.cabinetmedical.ui.auth;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.cabinet.cabinetmedical.MainActivity;
import com.cabinet.cabinetmedical.ui.admin.DashboardAdminActivity;
import com.cabinet.cabinetmedical.ui.doctor.HomeDoctorActivity;
import com.cabinet.cabinetmedical.ui.patient.HomePatientActivity;
import com.cabinet.cabinetmedical.R;
import com.cabinet.cabinetmedical.api.ApiClient;
import com.cabinet.cabinetmedical.api.ApiService;

import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * SplashActivity - Écran de démarrage de l'application
 *
 * FONCTIONS:
 * 1. Afficher logo/nom pendant 2 secondes
 * 2. Vérifier si token JWT existe dans SharedPreferences
 * 3. Si token existe → Valider avec backend (GET /api/auth/validate)
 * 4. Rediriger vers:
 *    - MainActivity si token valide (200 OK)
 *    - LoginActivity si pas de token OU token expiré (401)
 *
 * FLOW COMPLET:
 * App Launch → SplashActivity (2s)
 * ├── SharedPreferences contient "jwt_token" ?
 * │   ├── NON → LoginActivity
 * │   └── OUI → Appeler GET /api/auth/validate
 * │       ├── 200 OK → MainActivity (token valide)
 * │       ├── 401 UNAUTHORIZED → Clear token + LoginActivity (token expiré)
 * │       └── Erreur réseau → LoginActivity (sécurité)
 *
 * SÉCURITÉ:
 * - Token validé avec backend (pas juste vérifier existence)
 * - Token expiré automatiquement supprimé
 * - En cas d'erreur réseau → LoginActivity (principe de sécurité)
 *
 * NOTES:
 * - Pas de ActionBar (thème NoActionBar)
 * - Durée splash: 2000ms (2 secondes)
 * - Handler pour délai asynchrone
 * - JwtInterceptor ajoute automatiquement le token dans le header
 *
 * TODO FUTUR:
 * - Rediriger selon rôle (PATIENT → HomePatient, DOCTOR → HomeDoctor)
 * - Afficher animation (fade in/out)
 */
public class SplashActivity extends AppCompatActivity {

    /**
     * Tag pour les logs (débogage)
     */
    private static final String TAG = "SplashActivity";

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
     * 3. Si NON → LoginActivity (pas de token)
     * 4. Si OUI → Valider token avec backend
     *    - 200 OK → MainActivity (token valide)
     *    - 401 UNAUTHORIZED → Clear + LoginActivity (token expiré)
     *    - Erreur réseau → LoginActivity (sécurité)
     */
    private void checkAuthAndNavigate() {
        SharedPreferences prefs = getSharedPreferences("auth", MODE_PRIVATE);
        String token = prefs.getString("jwt_token", null);

        if (token == null || token.isEmpty()) {
            // Pas de token → Redirection Login
            Log.d(TAG, "Pas de token JWT trouvé → LoginActivity");
            navigateToLogin();
            return;
        }

        // Token existe → Valider avec backend
        Log.d(TAG, "Token JWT trouvé → Validation avec backend...");
        validateTokenWithBackend(prefs);
    }

    /**
     * Valider le token JWT avec le backend
     *
     * ENDPOINT: GET /api/auth/validate
     *
     * FLOW:
     * 1. JwtInterceptor ajoute automatiquement le token dans le header
     * 2. Backend valide le token (signature + expiration)
     * 3. Réponses possibles:
     *    - 200 OK → Token valide → MainActivity
     *    - 401 UNAUTHORIZED → Token expiré/invalide → Clear + Login
     *    - Erreur réseau → LoginActivity (sécurité)
     *
     * @param prefs SharedPreferences pour clear si token expiré
     */
    private void validateTokenWithBackend(SharedPreferences prefs) {
        ApiService apiService = ApiClient.getClient(this).create(ApiService.class);
        Call<Map<String, Object>> call = apiService.validateToken();

        call.enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                if (response.isSuccessful()) {
                    // ✅ Token valide (200 OK) → MainActivity
                    Log.d(TAG, "Token valide (200 OK) → MainActivity");
                    navigateToMain();

                } else if (response.code() == 401) {
                    // ❌ Token expiré/invalide (401 UNAUTHORIZED)
                    Log.d(TAG, "Token expiré ou invalide (401) → Clear + LoginActivity");
                    clearAuthAndNavigateToLogin(prefs);

                } else {
                    // ❌ Autre erreur (500, etc.) → LoginActivity par sécurité
                    Log.e(TAG, "Erreur serveur (" + response.code() + ") → LoginActivity");
                    navigateToLogin();
                }
            }

            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                // ❌ Erreur réseau (backend offline, timeout, etc.)
                Log.e(TAG, "Erreur réseau → LoginActivity", t);
                navigateToLogin();
            }
        });
    }

    /**
     * Rediriger vers l'écran approprié selon le rôle (utilisateur authentifié)
     *
     * REDIRECTION:
     * - PATIENT → HomePatientActivity (SCR-P02)
     * - DOCTOR → HomeDoctorActivity (SCR-D01)
     * - ADMIN → DashboardAdminActivity (SCR-A01)
     * - Pas de rôle (fallback sécurisé) → LoginActivity
     *
     * SÉCURITÉ:
     * Si le rôle n'existe pas ou est invalide, on redirige vers Login
     * (principe de sécurité : en cas de doute, déconnecter)
     */
    private void navigateToMain() {
        SharedPreferences prefs = getSharedPreferences("auth", MODE_PRIVATE);
        String role = prefs.getString("user_role", "");

        Intent intent;

        switch (role) {
            case "PATIENT":
                intent = new Intent(SplashActivity.this, HomePatientActivity.class);
                break;

            case "DOCTOR":
                intent = new Intent(SplashActivity.this, HomeDoctorActivity.class);
                break;

            case "ADMIN":
                intent = new Intent(SplashActivity.this, DashboardAdminActivity.class);
                break;

            default:
                // Rôle invalide ou absent → Login (sécurité)
                Log.w(TAG, "Rôle invalide ou absent : " + role + " → LoginActivity");
                intent = new Intent(SplashActivity.this, LoginActivity.class);
                break;
        }

        startActivity(intent);
        finish(); // Fermer SplashActivity (pas de retour arrière)
    }

    /**
     * Rediriger vers LoginActivity (utilisateur non authentifié)
     */
    private void navigateToLogin() {
        Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
        startActivity(intent);
        finish(); // Fermer SplashActivity (pas de retour arrière)
    }

    /**
     * Supprimer le token expiré et rediriger vers Login
     *
     * UTILISATION:
     * - Token expiré détecté par le backend (401)
     * - Nettoie SharedPreferences pour forcer nouvelle connexion
     *
     * @param prefs SharedPreferences à nettoyer
     */
    private void clearAuthAndNavigateToLogin(SharedPreferences prefs) {
        // Supprimer toutes les données d'authentification
        prefs.edit().clear().apply();
        Log.d(TAG, "SharedPreferences 'auth' nettoyées");

        // Rediriger vers Login
        navigateToLogin();
    }
}