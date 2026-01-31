package com.cabinet.cabinetmedical.ui.doctor;

import android.content.SharedPreferences;
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
import com.cabinet.cabinetmedical.model.response.AppointmentResponse;
import com.cabinet.cabinetmedical.utils.ErrorParser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * DoctorAppointmentsActivity - Liste des rendez-vous du médecin
 *
 * FONCTIONNALITÉS:
 * - Afficher tous les RDV du médecin
 * - Confirmer un RDV (PENDING → CONFIRMED)
 * - Annuler un RDV (avec raison)
 * - Appel API: GET /api/appointments/doctor/{doctorId}
 *
 * USE CASE: UC-D03
 * ÉCRAN: SCR-D02
 */
public class DoctorAppointmentsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private TextView errorMessage;
    private TextView emptyMessage;

    private ApiService apiService;
    private DoctorAppointmentAdapter adapter;
    private Long doctorId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_appointments);

        // Titre de l'écran
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Mes rendez-vous");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Récupérer doctorId depuis SharedPreferences
        SharedPreferences prefs = getSharedPreferences("auth", MODE_PRIVATE);
        doctorId = prefs.getLong("doctor_id", -1);

        if (doctorId == -1) {
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

        // Setup RecyclerView avec adapter
        adapter = new DoctorAppointmentAdapter(new ArrayList<>(), new DoctorAppointmentAdapter.OnAppointmentActionListener() {
            @Override
            public void onConfirm(AppointmentResponse appointment) {
                confirmAppointment(appointment.getId());
            }

            @Override
            public void onCancel(AppointmentResponse appointment) {
                cancelAppointment(appointment.getId());
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        // Charger les rendez-vous
        loadAppointments();
    }

    /**
     * Charger les rendez-vous du médecin
     */
    private void loadAppointments() {
        showLoading(true);
        hideError();
        hideEmpty();

        apiService.getDoctorAppointments(doctorId).enqueue(new Callback<List<AppointmentResponse>>() {
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

    /**
     * Confirmer un rendez-vous
     */
    private void confirmAppointment(Long appointmentId) {
        apiService.confirmAppointment(appointmentId).enqueue(new Callback<AppointmentResponse>() {
            @Override
            public void onResponse(Call<AppointmentResponse> call, Response<AppointmentResponse> response) {
                if (response.isSuccessful()) {
                    // Recharger la liste pour voir le changement
                    loadAppointments();
                } else {
                    String error = ErrorParser.parseErrorMessage(response.errorBody());
                    showError(error);
                }
            }

            @Override
            public void onFailure(Call<AppointmentResponse> call, Throwable t) {
                String error = ErrorParser.getNetworkErrorMessage(t);
                showError(error);
            }
        });
    }

    /**
     * Annuler un rendez-vous
     */
    private void cancelAppointment(Long appointmentId) {
        // TODO: Afficher dialog pour demander raison d'annulation
        // Créer un body vide (JSON {})
        Map<String, String> emptyBody = new HashMap<>();

        apiService.cancelAppointment(appointmentId, "DOCTOR", emptyBody).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    // Recharger la liste
                    loadAppointments();
                } else {
                    String error = ErrorParser.parseErrorMessage(response.errorBody());
                    showError(error);
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
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
