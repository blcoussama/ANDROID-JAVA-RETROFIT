package com.cabinet.cabinetmedical.ui.patient;

import com.cabinet.cabinetmedical.session.SessionManager;
import com.cabinet.cabinetmedical.session.Session;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cabinet.cabinetmedical.R;
import com.google.android.material.appbar.MaterialToolbar;
import com.cabinet.cabinetmedical.api.ApiClient;
import com.cabinet.cabinetmedical.api.ApiService;
import com.cabinet.cabinetmedical.model.response.AppointmentResponse;
import com.cabinet.cabinetmedical.utils.ErrorParser;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * MyAppointmentsActivity - Liste de tous les rendez-vous du patient
 *
 * RÔLE:
 * Affiche l'historique complet des RDV du patient (futurs, passés, annulés).
 * Accessible depuis HomePatientActivity (bouton "Mes rendez-vous").
 *
 * API: GET /api/appointments/patient/{patientId}
 * AFFICHAGE: RecyclerView avec AppointmentAdapter
 */
public class MyAppointmentsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private TextView errorMessage;
    private LinearLayout emptyMessage;

    private ApiService apiService;
    private AppointmentAdapter adapter;
    private Long patientId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_appointments);

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

        // Récupérer patientId depuis SharedPreferences
        Session session = SessionManager.get(this).getSession();
        patientId = session != null && session.getPatientId() != null ? session.getPatientId() : -1L;

        if (patientId == -1) {
            // Pas de patient_id → Retour
            finish();
            return;
        }

        // Initialiser Retrofit
        apiService = ApiClient.getService(this);

        // Initialiser les vues
        recyclerView = findViewById(R.id.recycler_view);
        progressBar = findViewById(R.id.progress_bar);
        errorMessage = findViewById(R.id.error_message);
        emptyMessage = findViewById(R.id.empty_message);

        // Setup RecyclerView
        adapter = new AppointmentAdapter(new ArrayList<>());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        // Charger les rendez-vous
        loadAppointments();
    }

    /**
     * Charger les rendez-vous du patient
     */
    private void loadAppointments() {
        showLoading(true);
        hideError();
        hideEmpty();

        apiService.getPatientAppointments(patientId).enqueue(new Callback<List<AppointmentResponse>>() {
            @Override
            public void onResponse(Call<List<AppointmentResponse>> call, Response<List<AppointmentResponse>> response) {
                showLoading(false);

                if (response.isSuccessful() && response.body() != null) {
                    List<AppointmentResponse> appointments = response.body();

                    if (appointments.isEmpty()) {
                        showEmpty();
                    } else {
                        adapter.updateAppointments(appointments);
                    }
                } else {
                    String error = ErrorParser.parseErrorMessage(response.errorBody());
                    showError(error);
                }
            }

            @Override
            public void onFailure(Call<List<AppointmentResponse>> call, Throwable t) {
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
        // Retour vers Home Patient
        Intent intent = new Intent(this, HomePatientActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
        return true;
    }
}
