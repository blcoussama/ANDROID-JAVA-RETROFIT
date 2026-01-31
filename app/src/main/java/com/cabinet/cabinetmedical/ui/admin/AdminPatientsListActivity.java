package com.cabinet.cabinetmedical.ui.admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cabinet.cabinetmedical.R;
import com.cabinet.cabinetmedical.api.ApiClient;
import com.cabinet.cabinetmedical.api.ApiService;
import com.cabinet.cabinetmedical.model.response.UserResponse;
import com.cabinet.cabinetmedical.utils.ErrorParser;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * AdminPatientsListActivity - Liste de tous les patients (ADMIN)
 *
 * FONCTIONNALITÉS:
 * - Afficher tous les patients
 * - Vue d'ensemble pour l'admin
 * - Peut voir détails de chaque patient
 * - Ajouter nouveau patient
 *
 * USE CASE: UC-A03, UC-A04
 * ÉCRAN: SCR-A05 (Gestion patients)
 */
public class AdminPatientsListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private TextView errorMessage;
    private TextView emptyMessage;
    private FloatingActionButton fabAddPatient;

    private ApiService apiService;
    private AdminPatientAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_patients_list);

        // Titre de l'écran
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Gestion des patients");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Initialiser Retrofit
        apiService = ApiClient.getClient(this).create(ApiService.class);

        // Initialiser les vues
        recyclerView = findViewById(R.id.recycler_view);
        progressBar = findViewById(R.id.progress_bar);
        errorMessage = findViewById(R.id.error_message);
        emptyMessage = findViewById(R.id.empty_message);
        fabAddPatient = findViewById(R.id.fab_add_patient);

        // Setup RecyclerView avec AdminPatientAdapter
        adapter = new AdminPatientAdapter(new ArrayList<>());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        // Bouton ajouter patient
        fabAddPatient.setOnClickListener(v -> {
            Intent intent = new Intent(AdminPatientsListActivity.this, AdminAddPatientActivity.class);
            startActivity(intent);
        });

        // Charger la liste des patients
        loadPatients();
    }

    /**
     * Charger tous les patients (filtrer users avec role=PATIENT)
     */
    private void loadPatients() {
        showLoading(true);
        hideError();
        hideEmpty();

        apiService.getAllUsers().enqueue(new Callback<List<UserResponse>>() {
            @Override
            public void onResponse(Call<List<UserResponse>> call, Response<List<UserResponse>> response) {
                showLoading(false);

                if (response.isSuccessful() && response.body() != null) {
                    // Filtrer seulement les patients
                    List<UserResponse> patients = response.body().stream()
                            .filter(user -> "PATIENT".equals(user.getRole()))
                            .collect(Collectors.toList());

                    if (patients.isEmpty()) {
                        showEmpty();
                    } else {
                        adapter.updatePatients(patients);
                    }
                } else {
                    String error = ErrorParser.parseErrorMessage(response.errorBody());
                    showError(error);
                }
            }

            @Override
            public void onFailure(Call<List<UserResponse>> call, Throwable t) {
                showLoading(false);
                String error = ErrorParser.getNetworkErrorMessage(t);
                showError(error);
            }
        });
    }

    private void showLoading(boolean loading) {
        progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        recyclerView.setVisibility(loading ? View.GONE : View.VISIBLE);
    }

    private void showError(String message) {
        errorMessage.setText(message);
        errorMessage.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
    }

    private void hideError() {
        errorMessage.setVisibility(View.GONE);
    }

    private void showEmpty() {
        emptyMessage.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
    }

    private void hideEmpty() {
        emptyMessage.setVisibility(View.GONE);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Recharger la liste au retour de l'ajout/modification
        loadPatients();
    }
}
