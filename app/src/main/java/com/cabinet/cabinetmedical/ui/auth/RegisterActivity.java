package com.cabinet.cabinetmedical.ui.auth;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.cabinet.cabinetmedical.MainActivity;
import com.cabinet.cabinetmedical.R;
import com.cabinet.cabinetmedical.api.ApiClient;
import com.cabinet.cabinetmedical.api.ApiService;
import com.cabinet.cabinetmedical.model.request.RegisterRequest;
import com.cabinet.cabinetmedical.model.response.LoginResponse;
import com.cabinet.cabinetmedical.utils.ErrorParser;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    private TextInputEditText emailInput;
    private TextInputEditText passwordInput;
    private TextInputEditText firstNameInput;
    private TextInputEditText lastNameInput;
    private TextInputEditText phoneInput;
    private MaterialButton registerButton;
    private TextView errorMessage;
    private ProgressBar progressBar;
    private TextView loginLink;

    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Initialiser Retrofit
        apiService = ApiClient.getClient(this).create(ApiService.class);

        // Lier les vues
        emailInput = findViewById(R.id.email_input);
        passwordInput = findViewById(R.id.password_input);
        firstNameInput = findViewById(R.id.firstname_input);
        lastNameInput = findViewById(R.id.lastname_input);
        phoneInput = findViewById(R.id.phone_input);
        registerButton = findViewById(R.id.register_button);
        errorMessage = findViewById(R.id.error_message);
        progressBar = findViewById(R.id.progress_bar);
        loginLink = findViewById(R.id.login_link);

        // Click listeners
        registerButton.setOnClickListener(v -> attemptRegister());
        loginLink.setOnClickListener(v -> {
            finish(); // Retour au LoginActivity
        });
    }

    private void attemptRegister() {
        // Récupérer les valeurs
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();
        String firstName = firstNameInput.getText().toString().trim();
        String lastName = lastNameInput.getText().toString().trim();
        String phone = phoneInput.getText().toString().trim();

        // Validation
        if (email.isEmpty()) {
            emailInput.setError("Email requis");
            emailInput.requestFocus();
            return;
        }

        if (password.isEmpty() || password.length() < 6) {
            passwordInput.setError("Mot de passe minimum 6 caractères");
            passwordInput.requestFocus();
            return;
        }

        if (firstName.isEmpty()) {
            firstNameInput.setError("Prénom requis");
            firstNameInput.requestFocus();
            return;
        }

        if (lastName.isEmpty()) {
            lastNameInput.setError("Nom requis");
            lastNameInput.requestFocus();
            return;
        }

        if (!phone.isEmpty() && !phone.matches("^[0-9]{10}$")) {
            phoneInput.setError("Téléphone doit contenir 10 chiffres");
            phoneInput.requestFocus();
            return;
        }

        // Afficher loading
        setLoading(true);
        hideError();

        // Créer requête
        RegisterRequest request = new RegisterRequest(email, password, firstName, lastName, phone);

        // Appel API
        apiService.register(request).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                setLoading(false);

                if (response.isSuccessful() && response.body() != null) {
                    LoginResponse loginResponse = response.body();

                    // Sauvegarder token et infos user
                    saveUserData(loginResponse);

                    // Rediriger vers home
                    navigateToHome();
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

        editor.apply();
    }

    private void navigateToHome() {
        // TODO: Créer HomePatientActivity
        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void setLoading(boolean loading) {
        progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        registerButton.setEnabled(!loading);
        emailInput.setEnabled(!loading);
        passwordInput.setEnabled(!loading);
        firstNameInput.setEnabled(!loading);
        lastNameInput.setEnabled(!loading);
        phoneInput.setEnabled(!loading);
    }

    private void showError(String message) {
        errorMessage.setText(message);
        errorMessage.setVisibility(View.VISIBLE);
    }

    private void hideError() {
        errorMessage.setVisibility(View.GONE);
    }
}
