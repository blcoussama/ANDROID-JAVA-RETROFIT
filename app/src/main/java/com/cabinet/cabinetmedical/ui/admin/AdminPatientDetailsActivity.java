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
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * AdminPatientDetailsActivity - Détails et actions sur un patient
 *
 * RÔLE:
 * Affiche les infos complètes d'un patient avec possibilité de modifier ou supprimer.
 * Accessible depuis AdminPatientsListActivity (clic sur un item).
 *
 * ACTIONS: Bouton Modifier → AdminEditPatientActivity, Bouton Supprimer → Dialog confirmation
 */
public class AdminPatientDetailsActivity extends AppCompatActivity {

    private TextView patientName;
    private TextView patientEmail;
    private TextView patientPhone;
    private TextView patientRole;
    private TextView patientCreatedAt;
    private TextView errorMessage;
    private ProgressBar progressBar;
    private MaterialButton editButton;
    private MaterialButton deleteButton;

    private ApiService apiService;
    private Long userId;
    private UserResponse currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_patient_details);

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

        // Récupérer userId depuis l'Intent
        userId = getIntent().getLongExtra("userId", -1);
        if (userId == -1) {
            Toast.makeText(this, "Erreur: ID utilisateur manquant", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Initialiser les vues
        patientName = findViewById(R.id.patient_name);
        patientEmail = findViewById(R.id.patient_email);
        patientPhone = findViewById(R.id.patient_phone);
        patientRole = findViewById(R.id.patient_role);
        patientCreatedAt = findViewById(R.id.patient_created_at);
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

    private void displayUserDetails(UserResponse user) {
        patientName.setText(user.getFullName());
        patientEmail.setText(user.getEmail());
        patientPhone.setText(user.getPhone() != null ? user.getPhone() : "Non renseigné");
        patientRole.setText(user.getRole());

        // Formater la date de création
        String formattedDate = formatDate(user.getCreatedAt());
        patientCreatedAt.setText(formattedDate);
    }

    private String formatDate(String isoDate) {
        if (isoDate == null || isoDate.isEmpty()) {
            return "Non disponible";
        }

        try {
            SimpleDateFormat isoFormat = new SimpleDateFormat(
                    "yyyy-MM-dd'T'HH:mm:ss.SSSSSS",
                    Locale.FRANCE
            );

            Date date;
            try {
                date = isoFormat.parse(isoDate);
            } catch (Exception e) {
                SimpleDateFormat isoFormatShort = new SimpleDateFormat(
                        "yyyy-MM-dd'T'HH:mm:ss",
                        Locale.FRANCE
                );
                date = isoFormatShort.parse(isoDate);
            }

            SimpleDateFormat displayFormat = new SimpleDateFormat(
                    "dd/MM/yyyy 'à' HH:mm",
                    Locale.FRANCE
            );

            return displayFormat.format(date);

        } catch (Exception e) {
            return isoDate;
        }
    }

    private void openEditActivity() {
        Intent intent = new Intent(this, AdminEditPatientActivity.class);
        intent.putExtra("userId", userId);
        intent.putExtra("email", currentUser.getEmail());
        intent.putExtra("firstName", currentUser.getFirstName());
        intent.putExtra("lastName", currentUser.getLastName());
        intent.putExtra("phone", currentUser.getPhone());
        startActivity(intent);
    }

    private void showDeleteConfirmation() {
        new AlertDialog.Builder(this)
                .setTitle("Confirmer la suppression")
                .setMessage("Êtes-vous sûr de vouloir supprimer ce patient ?\n\n" +
                        currentUser.getFullName() + "\n" +
                        currentUser.getEmail() + "\n\n" +
                        "Cette action est irréversible.")
                .setPositiveButton("Supprimer", (dialog, which) -> deleteUser())
                .setNegativeButton("Annuler", null)
                .show();
    }

    private void deleteUser() {
        showLoading(true);

        apiService.deleteUser(userId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                showLoading(false);

                if (response.isSuccessful()) {
                    Toast.makeText(AdminPatientDetailsActivity.this,
                            "Patient supprimé avec succès", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    String error = ErrorParser.parse(response);
                    Toast.makeText(AdminPatientDetailsActivity.this, error, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                showLoading(false);
                String error = ErrorParser.getNetworkErrorMessage(t);
                Toast.makeText(AdminPatientDetailsActivity.this, error, Toast.LENGTH_LONG).show();
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
    protected void onResume() {
        super.onResume();
        if (currentUser != null) {
            loadUserDetails();
        }
    }
}
