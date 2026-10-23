package com.cabinet.cabinetmedical.ui.patient;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cabinet.cabinetmedical.R;
import com.cabinet.cabinetmedical.api.ApiClient;
import com.cabinet.cabinetmedical.api.ApiService;
import com.cabinet.cabinetmedical.model.request.CancelAppointmentRequest;
import com.cabinet.cabinetmedical.model.response.AppointmentResponse;
import com.cabinet.cabinetmedical.ui.common.CancelReasonDialog;
import com.cabinet.cabinetmedical.utils.ErrorParser;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/** Rendez-vous du patient connecté (GET /api/appointments/me), annulation des rendez-vous à venir. */
public class MyAppointmentsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private TextView errorMessage;
    private LinearLayout emptyMessage;

    private ApiService apiService;
    private AppointmentAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_appointments);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> backToHome());

        apiService = ApiClient.getService(this);
        recyclerView = findViewById(R.id.recycler_view);
        progressBar = findViewById(R.id.progress_bar);
        errorMessage = findViewById(R.id.error_message);
        emptyMessage = findViewById(R.id.empty_message);

        adapter = new AppointmentAdapter(new ArrayList<>(), this::askCancellation);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        loadAppointments();
    }

    private void loadAppointments() {
        showLoading(true);
        errorMessage.setVisibility(View.GONE);
        emptyMessage.setVisibility(View.GONE);

        apiService.getMyAppointments().enqueue(new Callback<List<AppointmentResponse>>() {
            @Override
            public void onResponse(@NonNull Call<List<AppointmentResponse>> call,
                    @NonNull Response<List<AppointmentResponse>> response) {
                showLoading(false);
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().isEmpty()) {
                        emptyMessage.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                    } else {
                        adapter.updateAppointments(response.body());
                    }
                } else {
                    showError(ErrorParser.parse(response));
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<AppointmentResponse>> call, @NonNull Throwable t) {
                showLoading(false);
                showError(ErrorParser.getNetworkErrorMessage(t));
            }
        });
    }

    private void askCancellation(AppointmentResponse appointment) {
        CancelReasonDialog.show(this, appointment.getDoctorName() + "\n" + appointment.getFormattedDateTime(),
                reason -> cancel(appointment.getId(), reason));
    }

    private void cancel(Long appointmentId, String reason) {
        apiService.cancelAppointment(appointmentId, new CancelAppointmentRequest(reason))
                .enqueue(new Callback<AppointmentResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<AppointmentResponse> call,
                            @NonNull Response<AppointmentResponse> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(MyAppointmentsActivity.this, "Rendez-vous annulé",
                                    Toast.LENGTH_SHORT).show();
                            loadAppointments();
                        } else {
                            showError(ErrorParser.parse(response));
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<AppointmentResponse> call, @NonNull Throwable t) {
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
    }

    private void backToHome() {
        Intent intent = new Intent(this, HomePatientActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onSupportNavigateUp() {
        backToHome();
        return true;
    }
}
