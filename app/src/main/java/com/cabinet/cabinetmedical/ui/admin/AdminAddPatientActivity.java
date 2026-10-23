package com.cabinet.cabinetmedical.ui.admin;

import android.content.Intent;
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
import com.cabinet.cabinetmedical.utils.Validators;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * AdminAddPatientActivity - Création d'un nouveau patient par l'admin
 *
 * RÔLE:
 * Formulaire pour ajouter un patient (email, password, nom, prénom, téléphone).
 * Accessible depuis AdminPatientsListActivity (bouton FAB).
 *
 * API: POST /api/users/patient
 * VALIDATION: Email unique, mot de passe de 8 caractères minimum (lettre + chiffre)
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

        // Configurer la Toolbar
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Bouton retour vers AdminPatientsListActivity
        toolbar.setNavigationOnClickListener(v -> {
            Intent intent = new Intent(this, AdminPatientsListActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        });

        // Initialiser Retrofit
        apiService = ApiClient.getService(this);

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

        if (!Validators.isValidPassword(password)) {
            showError(Validators.PASSWORD_RULE);
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
                    String error = ErrorParser.parse(response);
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

}
