package com.cabinet.cabinetmedical.ui.patient;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cabinet.cabinetmedical.R;
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
 * MyAppointmentsActivity - Historique des rendez-vous du patient
 *
 * FONCTIONNALITÉS:
 * - Afficher tous les RDV du patient (passés, futurs, annulés)
 * - RecyclerView avec AppointmentAdapter
 * - Appel API: GET /api/appointments/patient/{patientId}
 *
 * USE CASE: UC-P03
 * ÉCRAN: SCR-P06
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

        // Titre de l'écran
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Mes rendez-vous");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Récupérer patientId depuis SharedPreferences
        SharedPreferences prefs = getSharedPreferences("auth", MODE_PRIVATE);
        patientId = prefs.getLong("patient_id", -1);

        if (patientId == -1) {
            // Pas de patient_id → Retour
            finish();
            return;
        }

        // Initialiser Retrofit
        apiService = ApiClient.getClient(this).create(ApiService.class);

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
        finish();
        return true;
    }
}
