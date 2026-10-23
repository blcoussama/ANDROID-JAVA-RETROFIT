package com.cabinet.cabinetmedical.ui.auth;

import com.cabinet.cabinetmedical.session.SessionNavigator;
import com.cabinet.cabinetmedical.session.SessionManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.cabinet.cabinetmedical.R;
import com.cabinet.cabinetmedical.api.ApiClient;
import com.cabinet.cabinetmedical.api.ApiService;
import com.cabinet.cabinetmedical.model.request.LoginRequest;
import com.cabinet.cabinetmedical.model.response.LoginResponse;
import com.cabinet.cabinetmedical.utils.ErrorParser;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    /** Extra posé quand l'API a refusé le jeton (expiré ou révoqué). */
    public static final String EXTRA_SESSION_EXPIRED = "session_expired";

    private TextInputEditText emailInput;
    private TextInputEditText passwordInput;
    private MaterialButton loginButton;
    private TextView errorMessage;
    private ProgressBar progressBar;
    private TextView registerLink;

    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialiser Retrofit
        apiService = ApiClient.getService(this);

        // Lier les vues
        emailInput = findViewById(R.id.email_input);
        passwordInput = findViewById(R.id.password_input);
        loginButton = findViewById(R.id.login_button);
        errorMessage = findViewById(R.id.error_message);
        progressBar = findViewById(R.id.progress_bar);
        registerLink = findViewById(R.id.register_link);

        // Click listeners
        loginButton.setOnClickListener(v -> attemptLogin());
        if (getIntent().getBooleanExtra(EXTRA_SESSION_EXPIRED, false)) {
            showError(getString(R.string.session_expired));
        }

        registerLink.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

    }

    private void attemptLogin() {
        // Récupérer les valeurs
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();

        // Validation
        if (email.isEmpty()) {
            emailInput.setError("Email requis");
            emailInput.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            passwordInput.setError("Mot de passe requis");
            passwordInput.requestFocus();
            return;
        }

        // Afficher loading
        setLoading(true);
        hideError();

        // Créer requête
        LoginRequest request = new LoginRequest(email, password);

        // Appel API
        apiService.login(request).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                setLoading(false);

                if (response.isSuccessful() && response.body() != null) {
                    LoginResponse loginResponse = response.body();

                    // Sauvegarder token et infos user
                    saveUserData(loginResponse);

                    // Rediriger selon rôle
                    navigateToHome(loginResponse.getRole());
                } else {
                    String errorMessage = ErrorParser.parse(response);
                    showError(errorMessage);
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                setLoading(false);
                // ✅ Utilise ErrorParser pour message user-friendly
                String errorMessage = ErrorParser.getNetworkErrorMessage(t);
                showError(errorMessage);
            }
        });
    }

    /** Session (jeton + profil) enregistrée chiffrée, jamais en clair. */
    private void saveUserData(LoginResponse response) {
        SessionManager.get(this).save(response);
    }

    private void navigateToHome(String role) {
        startActivity(SessionNavigator.homeIntent(this, role));
        finish();
    }

    private void setLoading(boolean loading) {
        progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        loginButton.setEnabled(!loading);
        emailInput.setEnabled(!loading);
        passwordInput.setEnabled(!loading);
    }

    private void showError(String message) {
        errorMessage.setText(message);
        errorMessage.setVisibility(View.VISIBLE);
    }

    private void hideError() {
        errorMessage.setVisibility(View.GONE);
    }
}
