package com.cabinet.cabinetmedical.ui.doctor;

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
import com.cabinet.cabinetmedical.model.request.CancelAppointmentRequest;
import com.cabinet.cabinetmedical.model.response.AppointmentResponse;
import com.cabinet.cabinetmedical.ui.common.CancelReasonDialog;
import com.cabinet.cabinetmedical.utils.ErrorParser;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * DoctorAppointmentsActivity - Gestion des rendez-vous du médecin
 *
 * RÔLE:
 * Affiche tous les RDV du médecin avec possibilité de confirmer ou annuler.
 * Accessible depuis HomeDoctorActivity (bouton "Mes rendez-vous").
 *
 * API: GET /api/appointments/doctor/{doctorId}
 * ACTIONS: Confirmer (PENDING → CONFIRMED), Annuler (avec raison)
 */
public class DoctorAppointmentsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private TextView errorMessage;
    private TextView emptyMessage;

    private ApiService apiService;
    private DoctorAppointmentAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_appointments);

        // Configurer la Toolbar
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Bouton retour vers Home
        toolbar.setNavigationOnClickListener(v -> {
            Intent intent = new Intent(this, HomeDoctorActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        });

        // Initialiser Retrofit
        apiService = ApiClient.getService(this);

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
                CancelReasonDialog.show(DoctorAppointmentsActivity.this,
                        appointment.getPatientName() + "\n" + appointment.getFormattedDateTime(),
                        reason -> cancelAppointment(appointment.getId(), reason));
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

        apiService.getMyAppointments().enqueue(new Callback<List<AppointmentResponse>>() {
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
                    String error = ErrorParser.parse(response);
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
                    String error = ErrorParser.parse(response);
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

    /** Annulation par le médecin, avec la raison saisie dans la boîte de dialogue. */
    private void cancelAppointment(Long appointmentId, String reason) {
        apiService.cancelAppointment(appointmentId, new CancelAppointmentRequest(reason))
                .enqueue(new Callback<AppointmentResponse>() {
                    @Override
                    public void onResponse(Call<AppointmentResponse> call, Response<AppointmentResponse> response) {
                        if (response.isSuccessful()) {
                            loadAppointments();
                        } else {
                            showError(ErrorParser.parse(response));
                        }
                    }

                    @Override
                    public void onFailure(Call<AppointmentResponse> call, Throwable t) {
                        showError(ErrorParser.getNetworkErrorMessage(t));
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
