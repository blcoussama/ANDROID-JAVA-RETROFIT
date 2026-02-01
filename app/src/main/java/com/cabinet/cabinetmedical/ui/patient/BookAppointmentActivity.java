package com.cabinet.cabinetmedical.ui.patient;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.cabinet.cabinetmedical.R;
import com.cabinet.cabinetmedical.api.ApiClient;
import com.cabinet.cabinetmedical.api.ApiService;
import com.cabinet.cabinetmedical.model.request.CreateAppointmentRequest;
import com.cabinet.cabinetmedical.model.response.AppointmentResponse;
import com.cabinet.cabinetmedical.utils.ErrorParser;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * BookAppointmentActivity - Confirmation et création d'un rendez-vous
 *
 * RÔLE:
 * Affiche le résumé du RDV choisi (médecin, date, heure), permet de saisir le motif et confirmer.
 * Accessible depuis AvailableTimeSlotsActivity (clic sur une heure).
 *
 * API: POST /api/appointments
 * RETOUR: Succès → MyAppointmentsActivity
 */
public class BookAppointmentActivity extends AppCompatActivity {

    private TextView doctorNameText;
    private TextView dateTimeText;
    private TextInputEditText reasonInput;
    private MaterialButton confirmButton;
    private MaterialButton cancelButton;
    private ProgressBar progressBar;
    private TextView errorMessage;

    private ApiService apiService;
    private Long patientId;
    private Long doctorId;
    private String doctorName;
    private String selectedDate;
    private String selectedTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_appointment);

        // Configurer la Toolbar
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Bouton retour vers créneaux disponibles
        toolbar.setNavigationOnClickListener(v -> finish());

        // Récupérer patientId depuis SharedPreferences
        SharedPreferences prefs = getSharedPreferences("auth", MODE_PRIVATE);
        patientId = prefs.getLong("patient_id", -1);

        // Récupérer les infos depuis l'Intent
        doctorId = getIntent().getLongExtra("doctorId", -1);
        doctorName = getIntent().getStringExtra("doctorName");
        selectedDate = getIntent().getStringExtra("selectedDate");
        selectedTime = getIntent().getStringExtra("selectedTime");

        if (patientId == -1 || doctorId == -1 || selectedDate == null || selectedTime == null) {
            finish();
            return;
        }

        // Initialiser Retrofit
        apiService = ApiClient.getClient(this).create(ApiService.class);

        // Initialiser les vues
        doctorNameText = findViewById(R.id.doctor_name_text);
        dateTimeText = findViewById(R.id.date_time_text);
        reasonInput = findViewById(R.id.reason_input);
        confirmButton = findViewById(R.id.confirm_button);
        cancelButton = findViewById(R.id.cancel_button);
        progressBar = findViewById(R.id.progress_bar);
        errorMessage = findViewById(R.id.error_message);

        // Afficher résumé
        doctorNameText.setText(doctorName);
        dateTimeText.setText(formatDateTime(selectedDate, selectedTime));

        // Bouton confirmer
        confirmButton.setOnClickListener(v -> bookAppointment());

        // Bouton annuler
        cancelButton.setOnClickListener(v -> finish());
    }

    /**
     * Créer le rendez-vous
     */
    private void bookAppointment() {
        String reason = reasonInput.getText().toString().trim();

        if (reason.isEmpty()) {
            reasonInput.setError("Motif requis");
            reasonInput.requestFocus();
            return;
        }

        showLoading(true);
        hideError();

        // Format datetime pour backend: "YYYY-MM-DDTHH:mm:ss"
        String dateTime = selectedDate + "T" + selectedTime;

        CreateAppointmentRequest request = new CreateAppointmentRequest(doctorId, dateTime, reason);

        apiService.createAppointment(patientId, request).enqueue(new Callback<AppointmentResponse>() {
            @Override
            public void onResponse(Call<AppointmentResponse> call, Response<AppointmentResponse> response) {
                showLoading(false);

                if (response.isSuccessful()) {
                    // Succès → Redirection vers Mes rendez-vous
                    Toast.makeText(BookAppointmentActivity.this,
                            "Rendez-vous créé avec succès !", Toast.LENGTH_LONG).show();

                    // Redirection vers MyAppointmentsActivity pour voir le RDV créé
                    Intent intent = new Intent(BookAppointmentActivity.this, MyAppointmentsActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                } else {
                    String error = ErrorParser.parseErrorMessage(response.errorBody());
                    showError(error);
                }
            }

            @Override
            public void onFailure(Call<AppointmentResponse> call, Throwable t) {
                showLoading(false);
                String error = ErrorParser.getNetworkErrorMessage(t);
                showError(error);
            }
        });
    }

    /**
     * Formater date + heure pour affichage
     */
    private String formatDateTime(String date, String time) {
        // date = "2026-02-03", time = "09:00:00"
        String[] dateParts = date.split("-");
        String timeShort = time.length() > 5 ? time.substring(0, 5) : time;

        return String.format("%s/%s/%s à %s",
                dateParts[2], dateParts[1], dateParts[0], timeShort);
    }

    private void showLoading(boolean loading) {
        progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        confirmButton.setEnabled(!loading);
        cancelButton.setEnabled(!loading);
        reasonInput.setEnabled(!loading);
    }

    private void showError(String message) {
        errorMessage.setText(message);
        errorMessage.setVisibility(View.VISIBLE);
    }

    private void hideError() {
        errorMessage.setVisibility(View.GONE);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
