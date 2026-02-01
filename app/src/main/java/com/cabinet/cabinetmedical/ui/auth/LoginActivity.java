package com.cabinet.cabinetmedical.ui.auth;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.cabinet.cabinetmedical.MainActivity;
import com.cabinet.cabinetmedical.ui.admin.DashboardAdminActivity;
import com.cabinet.cabinetmedical.ui.doctor.HomeDoctorActivity;
import com.cabinet.cabinetmedical.ui.patient.HomePatientActivity;
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
        apiService = ApiClient.getClient(this).create(ApiService.class);

        // Lier les vues
        emailInput = findViewById(R.id.email_input);
        passwordInput = findViewById(R.id.password_input);
        loginButton = findViewById(R.id.login_button);
        errorMessage = findViewById(R.id.error_message);
        progressBar = findViewById(R.id.progress_bar);
        registerLink = findViewById(R.id.register_link);

        // Click listeners
        loginButton.setOnClickListener(v -> attemptLogin());
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
                    String errorMessage = ErrorParser.parseErrorMessage(response.errorBody());
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

    private void saveUserData(LoginResponse response) {
        SharedPreferences prefs = getSharedPreferences("auth", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        editor.putString("jwt_token", response.getToken());
        editor.putLong("user_id", response.getUserId());
        editor.putString("user_email", response.getEmail());
        editor.putString("user_firstName", response.getFirstName());
        editor.putString("user_lastName", response.getLastName());
        editor.putString("user_role", response.getRole());

        if (response.getPatientId() != null) {
            editor.putLong("patient_id", response.getPatientId());
        }
        if (response.getDoctorId() != null) {
            editor.putLong("doctor_id", response.getDoctorId());
        }

        editor.apply();
    }

    /**
     * Rediriger vers l'écran approprié selon le rôle de l'utilisateur
     *
     * REDIRECTION:
     * - PATIENT → HomePatientActivity
     * - DOCTOR → HomeDoctorActivity
     * - ADMIN → DashboardAdminActivity
     *
     * @param role Rôle de l'utilisateur (PATIENT, DOCTOR, ADMIN)
     */
    private void navigateToHome(String role) {
        Intent intent;

        switch (role) {
            case "PATIENT":
                intent = new Intent(LoginActivity.this, HomePatientActivity.class);
                break;

            case "DOCTOR":
                intent = new Intent(LoginActivity.this, HomeDoctorActivity.class);
                break;

            case "ADMIN":
                intent = new Intent(LoginActivity.this, DashboardAdminActivity.class);
                break;

            default:
                // Fallback (ne devrait jamais arriver)
                // MainActivity affiche un écran d'erreur "Rôle inconnu"
                intent = new Intent(LoginActivity.this, MainActivity.class);
                break;
        }

        startActivity(intent);
        finish(); // Fermer LoginActivity
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
