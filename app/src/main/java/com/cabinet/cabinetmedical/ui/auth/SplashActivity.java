package com.cabinet.cabinetmedical.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.cabinet.cabinetmedical.R;
import com.cabinet.cabinetmedical.api.ApiClient;
import com.cabinet.cabinetmedical.session.Session;
import com.cabinet.cabinetmedical.session.SessionManager;
import com.cabinet.cabinetmedical.session.SessionNavigator;

import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Écran de démarrage : s'il existe une session, le jeton est vérifié auprès de l'API
 * ({@code GET /api/auth/validate}) avant d'ouvrir l'accueil du rôle.
 * 401 ou 403 : session effacée puis connexion ; erreur réseau : écran de connexion.
 */
public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_DURATION_MS = 1200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        new Handler(Looper.getMainLooper()).postDelayed(this::checkSession, SPLASH_DURATION_MS);
    }

    private void checkSession() {
        SessionManager sessionManager = SessionManager.get(this);
        if (!sessionManager.isLoggedIn()) {
            openLogin();
            return;
        }
        ApiClient.getService(this).validateToken().enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(@NonNull Call<Map<String, Object>> call,
                    @NonNull Response<Map<String, Object>> response) {
                Session session = sessionManager.getSession();
                if (response.isSuccessful() && session != null) {
                    startActivity(SessionNavigator.homeIntent(SplashActivity.this, session.getRole()));
                    finish();
                } else {
                    if (response.code() == 401 || response.code() == 403) {
                        sessionManager.clear();
                    }
                    openLogin();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Map<String, Object>> call, @NonNull Throwable t) {
                openLogin();
            }
        });
    }

    private void openLogin() {
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }
}
