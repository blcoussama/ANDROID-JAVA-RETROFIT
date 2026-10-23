package com.cabinet.cabinetmedical.ui.patient;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cabinet.cabinetmedical.R;
import com.google.android.material.appbar.MaterialToolbar;
import com.cabinet.cabinetmedical.api.ApiClient;
import com.cabinet.cabinetmedical.api.ApiService;
import com.cabinet.cabinetmedical.model.response.DoctorResponse;
import com.cabinet.cabinetmedical.utils.ErrorParser;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * DoctorListActivity - Liste de tous les médecins du cabinet
 *
 * RÔLE:
 * Affiche tous les médecins (nom, spécialité) pour que le patient choisisse avec qui prendre RDV.
 * Accessible depuis HomePatientActivity (bouton "Voir les médecins").
 *
 * API: GET /api/doctors
 * ACTIONS: Clic médecin → AvailableTimeSlotsActivity
 */
public class DoctorListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private DoctorAdapter adapter;
    private ProgressBar progressBar;
    private TextView errorMessage;

    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_list);

        // Configurer la Toolbar
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Bouton retour vers Home
        toolbar.setNavigationOnClickListener(v -> {
            Intent intent = new Intent(this, HomePatientActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        });

        // Initialiser Retrofit
        apiService = ApiClient.getService(this);

        // Initialiser les vues
        recyclerView = findViewById(R.id.recycler_view_doctors);
        progressBar = findViewById(R.id.progress_bar);
        errorMessage = findViewById(R.id.error_message);

        // Configurer RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new DoctorAdapter(new ArrayList<>());
        recyclerView.setAdapter(adapter);

        // Charger la liste des médecins
        loadDoctors();
    }

    /**
     * Charger la liste des médecins depuis le backend
     */
    private void loadDoctors() {
        showLoading(true);
        hideError();

        apiService.getAllDoctors().enqueue(new Callback<List<DoctorResponse>>() {
            @Override
            public void onResponse(Call<List<DoctorResponse>> call, Response<List<DoctorResponse>> response) {
                showLoading(false);

                if (response.isSuccessful() && response.body() != null) {
                    List<DoctorResponse> doctors = response.body();

                    if (doctors.isEmpty()) {
                        showError("Aucun médecin disponible pour le moment.");
                    } else {
                        // Mettre à jour le RecyclerView
                        adapter.updateDoctors(doctors);
                    }
                } else {
                    String error = ErrorParser.parse(response);
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

    /**
     * Afficher/masquer le loading
     */
    private void showLoading(boolean loading) {
        progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        recyclerView.setVisibility(loading ? View.GONE : View.VISIBLE);
    }

    /**
     * Afficher un message d'erreur
     */
    private void showError(String message) {
        errorMessage.setText(message);
        errorMessage.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
    }

    /**
     * Masquer le message d'erreur
     */
    private void hideError() {
        errorMessage.setVisibility(View.GONE);
    }

    /**
     * Gérer le bouton retour dans l'ActionBar
     */
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
