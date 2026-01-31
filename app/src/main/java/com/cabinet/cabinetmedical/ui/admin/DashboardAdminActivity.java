package com.cabinet.cabinetmedical.ui.admin;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.cabinet.cabinetmedical.R;
import com.cabinet.cabinetmedical.api.ApiClient;
import com.cabinet.cabinetmedical.api.ApiService;
import com.cabinet.cabinetmedical.model.response.DashboardResponse;
import com.cabinet.cabinetmedical.ui.auth.LoginActivity;
import com.cabinet.cabinetmedical.utils.ErrorParser;
import com.google.android.material.button.MaterialButton;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * DashboardAdminActivity - Tableau de bord pour les ADMIN
 *
 * FONCTIONNALITÉS:
 * - Affichage statistiques (RDV, médecins, patients)
 * - Cards Material Design avec chiffres clés
 * - Boutons navigation vers sections admin
 *
 * USE CASES: UC-A01, UC-A02
 * ÉCRAN: SCR-A01
 */
public class DashboardAdminActivity extends AppCompatActivity {

    private TextView welcomeMessage;
    private TextView adminName;

    // Statistiques RDV
    private TextView rdvTodayText;
    private TextView rdvWeekText;
    private TextView rdvPendingText;
    private TextView rdvConfirmedText;
    private TextView rdvCancelledText;

    // Statistiques utilisateurs
    private TextView doctorsCountText;
    private TextView patientsCountText;
    private TextView usersCountText;

    private ProgressBar progressBar;
    private TextView errorMessage;

    // Boutons navigation
    private MaterialButton manageDoctorsButton;
    private MaterialButton manageAppointmentsButton;
    private MaterialButton logoutButton;

    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard_admin);

        // Initialiser Retrofit
        apiService = ApiClient.getClient(this).create(ApiService.class);

        // Initialiser les vues
        welcomeMessage = findViewById(R.id.welcome_message);
        adminName = findViewById(R.id.admin_name);

        rdvTodayText = findViewById(R.id.rdv_today_text);
        rdvWeekText = findViewById(R.id.rdv_week_text);
        rdvPendingText = findViewById(R.id.rdv_pending_text);
        rdvConfirmedText = findViewById(R.id.rdv_confirmed_text);
        rdvCancelledText = findViewById(R.id.rdv_cancelled_text);

        doctorsCountText = findViewById(R.id.doctors_count_text);
        patientsCountText = findViewById(R.id.patients_count_text);
        usersCountText = findViewById(R.id.users_count_text);

        progressBar = findViewById(R.id.progress_bar);
        errorMessage = findViewById(R.id.error_message);

        manageDoctorsButton = findViewById(R.id.button_manage_doctors);
        manageAppointmentsButton = findViewById(R.id.button_manage_appointments);
        logoutButton = findViewById(R.id.logout_button);

        // Charger les infos de l'admin
        loadAdminInfo();

        // Charger les statistiques
        loadDashboard();

        // Boutons navigation
        manageDoctorsButton.setOnClickListener(v -> {
            Intent intent = new Intent(DashboardAdminActivity.this, AdminDoctorsListActivity.class);
            startActivity(intent);
        });

        manageAppointmentsButton.setOnClickListener(v -> {
            // TODO: Ouvrir AdminAppointmentsActivity
        });

        // Bouton déconnexion
        logoutButton.setOnClickListener(v -> logout());
    }

    /**
     * Charger les informations de l'admin
     */
    private void loadAdminInfo() {
        SharedPreferences prefs = getSharedPreferences("auth", MODE_PRIVATE);
        String firstName = prefs.getString("user_firstName", "Admin");
        String lastName = prefs.getString("user_lastName", "");

        String name = firstName + " " + lastName;
        adminName.setText(name);
    }

    /**
     * Charger le dashboard avec statistiques
     */
    private void loadDashboard() {
        showLoading(true);
        hideError();

        apiService.getDashboard().enqueue(new Callback<DashboardResponse>() {
            @Override
            public void onResponse(Call<DashboardResponse> call, Response<DashboardResponse> response) {
                showLoading(false);

                if (response.isSuccessful() && response.body() != null) {
                    DashboardResponse dashboard = response.body();
                    displayStatistics(dashboard);
                } else {
                    String error = ErrorParser.parseErrorMessage(response.errorBody());
                    showError(error);
                }
            }

            @Override
            public void onFailure(Call<DashboardResponse> call, Throwable t) {
                showLoading(false);
                String error = ErrorParser.getNetworkErrorMessage(t);
                showError(error);
            }
        });
    }

    /**
     * Afficher les statistiques
     */
    private void displayStatistics(DashboardResponse dashboard) {
        // Statistiques RDV
        rdvTodayText.setText(String.valueOf(dashboard.getTotalAppointmentsToday()));
        rdvWeekText.setText(String.valueOf(dashboard.getTotalAppointmentsWeek()));
        rdvPendingText.setText(String.valueOf(dashboard.getPendingCount()));
        rdvConfirmedText.setText(String.valueOf(dashboard.getConfirmedCount()));
        rdvCancelledText.setText(String.valueOf(dashboard.getCancelledCount()));

        // Statistiques utilisateurs
        doctorsCountText.setText(String.valueOf(dashboard.getTotalDoctors()));
        patientsCountText.setText(String.valueOf(dashboard.getTotalPatients()));
        usersCountText.setText(String.valueOf(dashboard.getTotalUsers()));
    }

    private void showLoading(boolean loading) {
        progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
    }

    private void showError(String message) {
        errorMessage.setText(message);
        errorMessage.setVisibility(View.VISIBLE);
    }

    private void hideError() {
        errorMessage.setVisibility(View.GONE);
    }

    /**
     * Déconnexion
     */
    private void logout() {
        SharedPreferences prefs = getSharedPreferences("auth", MODE_PRIVATE);
        prefs.edit().clear().apply();

        Intent intent = new Intent(DashboardAdminActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
