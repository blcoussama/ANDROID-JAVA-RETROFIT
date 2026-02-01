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
import com.cabinet.cabinetmedical.model.request.UpdateUserRequest;
import com.cabinet.cabinetmedical.model.response.UserResponse;
import com.cabinet.cabinetmedical.utils.ErrorParser;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * AdminEditPatientActivity - Modifier un patient (ADMIN)
 *
 * FONCTIONNALITÉS:
 * - Formulaire pré-rempli
 * - Modification: email, password (optionnel), firstName, lastName, phone
 * - Appel API: PUT /api/users/{id}
 *
 * USE CASE: UC-A06
 */
public class AdminEditPatientActivity extends AppCompatActivity {

    private TextInputEditText emailInput;
    private TextInputEditText passwordInput;
    private TextInputEditText firstNameInput;
    private TextInputEditText lastNameInput;
    private TextInputEditText phoneInput;
    private MaterialButton saveButton;
    private ProgressBar progressBar;
    private TextView errorMessage;

    private ApiService apiService;
    private Long userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_edit_patient);

        // Configurer la Toolbar
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Bouton retour vers page précédente (Details)
        toolbar.setNavigationOnClickListener(v -> finish());

        // Initialiser Retrofit
        apiService = ApiClient.getClient(this).create(ApiService.class);

        // Récupérer les données depuis l'Intent
        userId = getIntent().getLongExtra("userId", -1);
        String email = getIntent().getStringExtra("email");
        String firstName = getIntent().getStringExtra("firstName");
        String lastName = getIntent().getStringExtra("lastName");
        String phone = getIntent().getStringExtra("phone");

        if (userId == -1) {
            Toast.makeText(this, "Erreur: ID utilisateur manquant", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Initialiser les vues
        emailInput = findViewById(R.id.input_email);
        passwordInput = findViewById(R.id.input_password);
        firstNameInput = findViewById(R.id.input_first_name);
        lastNameInput = findViewById(R.id.input_last_name);
        phoneInput = findViewById(R.id.input_phone);
        saveButton = findViewById(R.id.button_save);
        progressBar = findViewById(R.id.progress_bar);
        errorMessage = findViewById(R.id.error_message);

        // Pré-remplir les champs
        emailInput.setText(email);
        firstNameInput.setText(firstName);
        lastNameInput.setText(lastName);
        phoneInput.setText(phone != null ? phone : "");

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

        if (!password.isEmpty() && password.length() < 6) {
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

        // Créer la requête
        UpdateUserRequest request = new UpdateUserRequest(
                email,
                password.isEmpty() ? null : password,
                firstName,
                lastName,
                phone.isEmpty() ? null : phone
        );

        // Appel API
        updatePatient(request);
    }

    private void updatePatient(UpdateUserRequest request) {
        showLoading(true);

        apiService.updateUser(userId, request).enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                showLoading(false);

                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(AdminEditPatientActivity.this,
                            "Patient modifié avec succès", Toast.LENGTH_SHORT).show();
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

}
