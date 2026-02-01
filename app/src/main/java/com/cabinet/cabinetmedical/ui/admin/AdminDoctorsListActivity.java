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
import com.cabinet.cabinetmedical.model.response.DoctorResponse;
import com.cabinet.cabinetmedical.utils.ErrorParser;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * AdminDoctorsListActivity - Liste de tous les médecins (ADMIN)
 *
 * FONCTIONNALITÉS:
 * - Afficher tous les médecins du cabinet
 * - Vue d'ensemble pour l'admin
 * - Peut voir détails de chaque médecin
 *
 * USE CASE: UC-A03, UC-A04
 * ÉCRAN: SCR-A02
 */
public class AdminDoctorsListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private TextView errorMessage;
    private TextView emptyMessage;
    private FloatingActionButton fabAddDoctor;

    private ApiService apiService;
    private AdminDoctorAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_doctors_list);

        // Configurer la Toolbar
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Bouton retour vers Dashboard
        toolbar.setNavigationOnClickListener(v -> {
            Intent intent = new Intent(this, DashboardAdminActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        });

        // Initialiser Retrofit
        apiService = ApiClient.getClient(this).create(ApiService.class);

        // Initialiser les vues
        recyclerView = findViewById(R.id.recycler_view);
        progressBar = findViewById(R.id.progress_bar);
        errorMessage = findViewById(R.id.error_message);
        emptyMessage = findViewById(R.id.empty_message);
        fabAddDoctor = findViewById(R.id.fab_add_doctor);

        // Setup RecyclerView avec AdminDoctorAdapter (lecture seule)
        adapter = new AdminDoctorAdapter(new ArrayList<>());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        // Bouton ajouter médecin
        fabAddDoctor.setOnClickListener(v -> {
            Intent intent = new Intent(AdminDoctorsListActivity.this, AdminAddDoctorActivity.class);
            startActivity(intent);
        });

        // Charger la liste des médecins
        loadDoctors();
    }

    /**
     * Charger tous les médecins
     */
    private void loadDoctors() {
        showLoading(true);
        hideError();
        hideEmpty();

        apiService.getAllDoctors().enqueue(new Callback<List<DoctorResponse>>() {
            @Override
            public void onResponse(Call<List<DoctorResponse>> call, Response<List<DoctorResponse>> response) {
                showLoading(false);

                if (response.isSuccessful() && response.body() != null) {
                    List<DoctorResponse> doctors = response.body();

                    if (doctors.isEmpty()) {
                        showEmpty();
                    } else {
                        adapter.updateDoctors(doctors);
                    }
                } else {
                    String error = ErrorParser.parseErrorMessage(response.errorBody());
                    showError(error);
                }
            }

            @Override
            public void onFailure(Call<List<DoctorResponse>> call, Throwable t) {
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
        loadDoctors();
    }
}
