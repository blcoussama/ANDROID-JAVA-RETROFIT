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
import com.cabinet.cabinetmedical.utils.Validators;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * AdminEditDoctorActivity - Modification d'un médecin existant
 *
 * RÔLE:
 * Formulaire pré-rempli pour modifier les données d'un médecin.
 * Accessible depuis AdminDoctorDetailsActivity (bouton Modifier).
 *
 * API: PUT /api/users/{id}
 * NOTE: Password optionnel (vide = pas de changement)
 */
public class AdminEditDoctorActivity extends AppCompatActivity {

    private TextInputEditText emailInput;
    private TextInputEditText passwordInput;
    private TextInputEditText firstNameInput;
    private TextInputEditText lastNameInput;
    private TextInputEditText phoneInput;
    private TextInputEditText specialtyInput;
    private MaterialButton saveButton;
    private ProgressBar progressBar;
    private TextView errorMessage;

    private ApiService apiService;
    private Long userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_edit_doctor);

        // Configurer la Toolbar
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Bouton retour vers page précédente (Details)
        toolbar.setNavigationOnClickListener(v -> finish());

        // Initialiser Retrofit
        apiService = ApiClient.getService(this);

        // Récupérer les données depuis Intent
        userId = getIntent().getLongExtra("userId", -1);
        String email = getIntent().getStringExtra("email");
        String firstName = getIntent().getStringExtra("firstName");
        String lastName = getIntent().getStringExtra("lastName");
        String phone = getIntent().getStringExtra("phone");
        String specialty = getIntent().getStringExtra("specialty");

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
        specialtyInput = findViewById(R.id.input_specialty);
        saveButton = findViewById(R.id.button_save);
        progressBar = findViewById(R.id.progress_bar);
        errorMessage = findViewById(R.id.error_message);

        // Pré-remplir les champs
        emailInput.setText(email);
        firstNameInput.setText(firstName);
        lastNameInput.setText(lastName);
        phoneInput.setText(phone);
        specialtyInput.setText(specialty);

        // Bouton enregistrer
        saveButton.setOnClickListener(v -> validateAndSave());
    }

    /**
     * Valider et sauvegarder
     */
    private void validateAndSave() {
        hideError();

        // Récupérer les valeurs
        String email = emailInput.getText() != null ? emailInput.getText().toString().trim() : "";
        String password = passwordInput.getText() != null ? passwordInput.getText().toString().trim() : "";
        String firstName = firstNameInput.getText() != null ? firstNameInput.getText().toString().trim() : "";
        String lastName = lastNameInput.getText() != null ? lastNameInput.getText().toString().trim() : "";
        String phone = phoneInput.getText() != null ? phoneInput.getText().toString().trim() : "";
        String specialty = specialtyInput.getText() != null ? specialtyInput.getText().toString().trim() : "";

        // Validation locale
        if (email.isEmpty()) {
            showError("L'email est obligatoire");
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

        if (specialty.isEmpty()) {
            showError("La spécialité est obligatoire pour un médecin");
            return;
        }

        // Valider password seulement s'il est fourni
        if (!password.isEmpty() && !Validators.isValidPassword(password)) {
            showError(Validators.PASSWORD_RULE);
            return;
        }

        if (!phone.isEmpty() && phone.length() != 10) {
            showError("Le téléphone doit contenir 10 chiffres");
            return;
        }

        // Créer la requête (password peut être vide)
        UpdateUserRequest request = new UpdateUserRequest(
                email,
                password.isEmpty() ? "" : password,  // Vide = ne pas modifier
                firstName,
                lastName,
                phone.isEmpty() ? null : phone,
                specialty
        );

        // Appel API
        updateUser(request);
    }

    /**
     * Mettre à jour le médecin via API
     */
    private void updateUser(UpdateUserRequest request) {
        showLoading(true);

        apiService.updateUser(userId, request).enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                showLoading(false);

                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(AdminEditDoctorActivity.this,
                            "Médecin modifié avec succès", Toast.LENGTH_SHORT).show();
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
