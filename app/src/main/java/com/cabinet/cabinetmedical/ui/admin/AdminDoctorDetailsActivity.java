package com.cabinet.cabinetmedical.ui.admin;

import android.app.AlertDialog;
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
import com.cabinet.cabinetmedical.model.response.UserResponse;
import com.cabinet.cabinetmedical.utils.ErrorParser;
import com.google.android.material.button.MaterialButton;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * AdminDoctorDetailsActivity - Détails d'un médecin (ADMIN)
 *
 * FONCTIONNALITÉS:
 * - Afficher toutes les infos du médecin
 * - Bouton "Modifier" → Ouvrir AdminEditDoctorActivity
 * - Bouton "Supprimer" → Dialog confirmation + suppression
 *
 * USE CASE: UC-A06, UC-A07
 */
public class AdminDoctorDetailsActivity extends AppCompatActivity {

    private TextView doctorName;
    private TextView doctorEmail;
    private TextView doctorPhone;
    private TextView doctorSpecialty;
    private TextView doctorRole;
    private TextView doctorCreatedAt;
    private TextView errorMessage;
    private ProgressBar progressBar;
    private MaterialButton editButton;
    private MaterialButton deleteButton;

    private ApiService apiService;
    private Long userId;
    private String specialty;  // Stocké depuis l'Intent
    private UserResponse currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_doctor_details);

        // Titre
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Détails du médecin");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Initialiser Retrofit
        apiService = ApiClient.getClient(this).create(ApiService.class);

        // Récupérer userId et specialty depuis l'Intent
        userId = getIntent().getLongExtra("userId", -1);
        specialty = getIntent().getStringExtra("specialty");
        if (userId == -1) {
            Toast.makeText(this, "Erreur: ID utilisateur manquant", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Initialiser les vues
        doctorName = findViewById(R.id.doctor_name);
        doctorEmail = findViewById(R.id.doctor_email);
        doctorPhone = findViewById(R.id.doctor_phone);
        doctorSpecialty = findViewById(R.id.doctor_specialty);
        doctorRole = findViewById(R.id.doctor_role);
        doctorCreatedAt = findViewById(R.id.doctor_created_at);
        errorMessage = findViewById(R.id.error_message);
        progressBar = findViewById(R.id.progress_bar);
        editButton = findViewById(R.id.button_edit);
        deleteButton = findViewById(R.id.button_delete);

        // Charger les détails
        loadUserDetails();

        // Bouton modifier
        editButton.setOnClickListener(v -> openEditActivity());

        // Bouton supprimer
        deleteButton.setOnClickListener(v -> showDeleteConfirmation());
    }

    /**
     * Charger les détails de l'utilisateur
     */
    private void loadUserDetails() {
        showLoading(true);
        hideError();

        apiService.getUserById(userId).enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                showLoading(false);

                if (response.isSuccessful() && response.body() != null) {
                    currentUser = response.body();
                    displayUserDetails(currentUser);
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

    /**
     * Afficher les détails
     */
    private void displayUserDetails(UserResponse user) {
        doctorName.setText(user.getFullName());
        doctorEmail.setText(user.getEmail());
        doctorPhone.setText(user.getPhone() != null ? user.getPhone() : "Non renseigné");
        doctorSpecialty.setText(user.getSpecialty() != null ? user.getSpecialty() : "Non renseignée");
        doctorRole.setText(user.getRole());

        // Formater la date de création
        String formattedDate = formatDate(user.getCreatedAt());
        doctorCreatedAt.setText(formattedDate);

        // Mettre à jour la specialty locale pour l'édition
        specialty = user.getSpecialty();
    }

    /**
     * Formater une date ISO en format lisible
     *
     * @param isoDate Date au format ISO (ex: "2026-01-31T18:58:25.775566")
     * @return Date formatée (ex: "31/01/2026 à 18:58")
     */
    private String formatDate(String isoDate) {
        if (isoDate == null || isoDate.isEmpty()) {
            return "Non disponible";
        }

        try {
            // Parser la date ISO (avec ou sans millisecondes)
            SimpleDateFormat isoFormat = new SimpleDateFormat(
                    "yyyy-MM-dd'T'HH:mm:ss.SSSSSS",
                    Locale.FRANCE
            );

            // Essayer d'abord avec les microsecondes
            Date date;
            try {
                date = isoFormat.parse(isoDate);
            } catch (ParseException e) {
                // Si ça échoue, essayer sans les microsecondes
                SimpleDateFormat isoFormatShort = new SimpleDateFormat(
                        "yyyy-MM-dd'T'HH:mm:ss",
                        Locale.FRANCE
                );
                date = isoFormatShort.parse(isoDate);
            }

            // Formater en format lisible français
            SimpleDateFormat displayFormat = new SimpleDateFormat(
                    "dd/MM/yyyy 'à' HH:mm",
                    Locale.FRANCE
            );

            return displayFormat.format(date);

        } catch (Exception e) {
            // Si le parsing échoue, retourner la date brute
            return isoDate;
        }
    }

    /**
     * Ouvrir l'activité de modification
     */
    private void openEditActivity() {
        Intent intent = new Intent(this, AdminEditDoctorActivity.class);
        intent.putExtra("userId", userId);
        intent.putExtra("email", currentUser.getEmail());
        intent.putExtra("firstName", currentUser.getFirstName());
        intent.putExtra("lastName", currentUser.getLastName());
        intent.putExtra("phone", currentUser.getPhone());
        intent.putExtra("specialty", specialty);
        startActivity(intent);
    }

    /**
     * Afficher dialog de confirmation de suppression
     */
    private void showDeleteConfirmation() {
        new AlertDialog.Builder(this)
                .setTitle("Confirmer la suppression")
                .setMessage("Êtes-vous sûr de vouloir supprimer ce médecin ?\n\n" +
                        currentUser.getFullName() + "\n" +
                        currentUser.getEmail() + "\n\n" +
                        "Cette action est irréversible.")
                .setPositiveButton("Supprimer", (dialog, which) -> deleteUser())
                .setNegativeButton("Annuler", null)
                .show();
    }

    /**
     * Supprimer l'utilisateur
     */
    private void deleteUser() {
        showLoading(true);

        apiService.deleteUser(userId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                showLoading(false);

                if (response.isSuccessful()) {
                    Toast.makeText(AdminDoctorDetailsActivity.this,
                            "Médecin supprimé avec succès", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    String error = ErrorParser.parseErrorMessage(response.errorBody());
                    Toast.makeText(AdminDoctorDetailsActivity.this, error, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                showLoading(false);
                String error = ErrorParser.getNetworkErrorMessage(t);
                Toast.makeText(AdminDoctorDetailsActivity.this, error, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void showLoading(boolean loading) {
        progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        editButton.setEnabled(!loading);
        deleteButton.setEnabled(!loading);
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

    @Override
    protected void onResume() {
        super.onResume();
        // Recharger les détails au retour de l'édition
        if (currentUser != null) {
            loadUserDetails();
        }
    }
}
