package com.cabinet.cabinetmedical.ui.admin;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.cabinet.cabinetmedical.R;
import com.cabinet.cabinetmedical.api.ApiClient;
import com.cabinet.cabinetmedical.api.ApiService;
import com.cabinet.cabinetmedical.model.request.CreateUserRequest;
import com.cabinet.cabinetmedical.model.response.UserResponse;
import com.cabinet.cabinetmedical.utils.ErrorParser;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * AdminAddPatientActivity - Ajouter un nouveau patient (ADMIN)
 *
 * FONCTIONNALITÉS:
 * - Formulaire: email, password, firstName, lastName, phone
 * - Validation locale
 * - Appel API: POST /api/users/patient
 * - Retour à la liste avec message succès
 *
 * USE CASE: UC-A04
 */
public class AdminAddPatientActivity extends AppCompatActivity {

    private TextInputEditText emailInput;
    private TextInputEditText passwordInput;
    private TextInputEditText firstNameInput;
    private TextInputEditText lastNameInput;
    private TextInputEditText phoneInput;
    private MaterialButton saveButton;
    private ProgressBar progressBar;
    private TextView errorMessage;

    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_add_patient);

        // Titre
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Ajouter un patient");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Initialiser Retrofit
        apiService = ApiClient.getClient(this).create(ApiService.class);

        // Initialiser les vues
        emailInput = findViewById(R.id.input_email);
        passwordInput = findViewById(R.id.input_password);
        firstNameInput = findViewById(R.id.input_first_name);
        lastNameInput = findViewById(R.id.input_last_name);
        phoneInput = findViewById(R.id.input_phone);
        saveButton = findViewById(R.id.button_save);
        progressBar = findViewById(R.id.progress_bar);
        errorMessage = findViewById(R.id.error_message);

        // Bouton enregistrer
        saveButton.setOnClickListener(v -> validateAndSave());
    }

    private void validateAndSave() {
        hideError();

        // Récupérer les valeurs
        String email = emailInput.getText() != null ? emailInput.getText().toString().trim() : "";
        String password = passwordInput.getText() != null ? passwordInput.getText().toString().trim() : "";
        String firstName = firstNameInput.getText() != null ? firstNameInput.getText().toString().trim() : "";
        String lastName = lastNameInput.getText() != null ? lastNameInput.getText().toString().trim() : "";
        String phone = phoneInput.getText() != null ? phoneInput.getText().toString().trim() : "";

        // Validation locale
        if (email.isEmpty()) {
            showError("L'email est obligatoire");
            return;
        }

        if (password.isEmpty() || password.length() < 6) {
            showError("Le mot de passe doit contenir au moins 6 caractères");
            return;
        }

        if (firstName.isEmpty()) {
            showError("Le prénom est obligatoire");
            return;
        }

        if (lastName.isEmpty()) {
            showError("Le nom est obligatoire");
            return;
        }

        if (!phone.isEmpty() && phone.length() != 10) {
            showError("Le téléphone doit contenir 10 chiffres");
            return;
        }

        // Créer la requête (specialty = null pour patient)
        CreateUserRequest request = new CreateUserRequest(
                email,
                password,
                firstName,
                lastName,
                phone.isEmpty() ? null : phone,
                null  // Pas de spécialité pour un patient
        );

        // Appel API
        createPatient(request);
    }

    private void createPatient(CreateUserRequest request) {
        showLoading(true);

        apiService.createPatient(request).enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                showLoading(false);

                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(AdminAddPatientActivity.this,
                            "Patient créé avec succès", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    String error = ErrorParser.parseErrorMessage(response.errorBody());
                    showError(error);
                }
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                showLoading(false);
                String error = ErrorParser.getNetworkErrorMessage(t);
                showError(error);
            }
        });
    }

    private void showLoading(boolean loading) {
        progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        saveButton.setEnabled(!loading);
    }

    private void showError(String message) {
        errorMessage.setText(message);
        errorMessage.setVisibility(View.VISIBLE);
    }

    private void hideError() {
        errorMessage.setVisibility(View.GONE);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
