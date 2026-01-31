package com.cabinet.cabinetmedical.ui.doctor;

import android.app.TimePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.cabinet.cabinetmedical.R;
import com.cabinet.cabinetmedical.api.ApiClient;
import com.cabinet.cabinetmedical.api.ApiService;
import com.cabinet.cabinetmedical.model.request.CreateTimeSlotRequest;
import com.cabinet.cabinetmedical.model.response.TimeSlotResponse;
import com.cabinet.cabinetmedical.utils.ErrorParser;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * DoctorAddTimeSlotActivity - Ajouter un nouveau créneau horaire
 *
 * FONCTIONNALITÉS:
 * - Formulaire: Jour de semaine (Spinner), Heure début, Heure fin (TimePickerDialog)
 * - Validation locale: endTime > startTime
 * - Appel API: POST /api/timeslots
 * - Gestion erreur 409 (chevauchement)
 *
 * USE CASE: UC-D02 (Créer créneau)
 */
public class DoctorAddTimeSlotActivity extends AppCompatActivity {

    private Spinner daySpinner;
    private TextInputEditText startTimeInput;
    private TextInputEditText endTimeInput;
    private MaterialButton createButton;
    private ProgressBar progressBar;
    private TextView errorMessage;

    private ApiService apiService;
    private Long doctorId;

    private String selectedStartTime = "";
    private String selectedEndTime = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_add_timeslot);

        // Titre
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Ajouter un créneau");
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
        daySpinner = findViewById(R.id.spinner_day);
        startTimeInput = findViewById(R.id.input_start_time);
        endTimeInput = findViewById(R.id.input_end_time);
        createButton = findViewById(R.id.button_create);
        progressBar = findViewById(R.id.progress_bar);
        errorMessage = findViewById(R.id.error_message);

        // Setup Spinner des jours
        setupDaySpinner();

        // TimePicker pour heure début
        startTimeInput.setFocusable(false);
        startTimeInput.setClickable(true);
        startTimeInput.setOnClickListener(v -> showStartTimePicker());

        // TimePicker pour heure fin
        endTimeInput.setFocusable(false);
        endTimeInput.setClickable(true);
        endTimeInput.setOnClickListener(v -> showEndTimePicker());

        // Bouton créer
        createButton.setOnClickListener(v -> validateAndCreate());
    }

    /**
     * Configurer le Spinner des jours de la semaine
     */
    private void setupDaySpinner() {
        String[] days = {"Lundi", "Mardi", "Mercredi", "Jeudi", "Vendredi", "Samedi", "Dimanche"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, days);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        daySpinner.setAdapter(adapter);
    }

    /**
     * Afficher TimePicker pour heure de début
     */
    private void showStartTimePicker() {
        // Par défaut 09:00
        TimePickerDialog picker = new TimePickerDialog(this,
                (view, hourOfDay, minute) -> {
                    selectedStartTime = String.format("%02d:%02d:00", hourOfDay, minute);
                    startTimeInput.setText(String.format("%02d:%02d", hourOfDay, minute));
                    validateTimes();
                }, 9, 0, true);
        picker.show();
    }

    /**
     * Afficher TimePicker pour heure de fin
     */
    private void showEndTimePicker() {
        // Par défaut 17:00
        TimePickerDialog picker = new TimePickerDialog(this,
                (view, hourOfDay, minute) -> {
                    selectedEndTime = String.format("%02d:%02d:00", hourOfDay, minute);
                    endTimeInput.setText(String.format("%02d:%02d", hourOfDay, minute));
                    validateTimes();
                }, 17, 0, true);
        picker.show();
    }

    /**
     * Valider que endTime > startTime
     */
    private void validateTimes() {
        if (!selectedStartTime.isEmpty() && !selectedEndTime.isEmpty()) {
            if (selectedEndTime.compareTo(selectedStartTime) <= 0) {
                showError("L'heure de fin doit être après l'heure de début");
                createButton.setEnabled(false);
            } else {
                hideError();
                createButton.setEnabled(true);
            }
        }
    }

    /**
     * Valider et créer le créneau
     */
    private void validateAndCreate() {
        hideError();

        // Validation
        if (selectedStartTime.isEmpty()) {
            showError("Veuillez sélectionner l'heure de début");
            return;
        }

        if (selectedEndTime.isEmpty()) {
            showError("Veuillez sélectionner l'heure de fin");
            return;
        }

        if (selectedEndTime.compareTo(selectedStartTime) <= 0) {
            showError("L'heure de fin doit être après l'heure de début");
            return;
        }

        // Récupérer le jour sélectionné et convertir en DayOfWeek anglais
        int dayPosition = daySpinner.getSelectedItemPosition();
        String dayOfWeek = getDayOfWeekEnum(dayPosition);

        // Créer la requête
        CreateTimeSlotRequest request = new CreateTimeSlotRequest();
        request.setDoctorId(doctorId);
        request.setDayOfWeek(dayOfWeek);
        request.setStartTime(selectedStartTime);
        request.setEndTime(selectedEndTime);

        // Appel API
        createTimeSlot(request);
    }

    /**
     * Créer le créneau via API
     */
    private void createTimeSlot(CreateTimeSlotRequest request) {
        showLoading(true);

        apiService.createTimeSlot(request).enqueue(new Callback<TimeSlotResponse>() {
            @Override
            public void onResponse(Call<TimeSlotResponse> call, Response<TimeSlotResponse> response) {
                showLoading(false);

                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(DoctorAddTimeSlotActivity.this,
                            "Créneau créé avec succès", Toast.LENGTH_SHORT).show();
                    finish();
                } else if (response.code() == 409) {
                    // Erreur de chevauchement
                    showError("Ce créneau chevauche un créneau existant");
                } else {
                    String error = ErrorParser.parseErrorMessage(response.errorBody());
                    showError(error);
                }
            }

            @Override
            public void onFailure(Call<TimeSlotResponse> call, Throwable t) {
                showLoading(false);
                String error = ErrorParser.getNetworkErrorMessage(t);
                showError(error);
            }
        });
    }

    /**
     * Convertir position du spinner en DayOfWeek enum
     */
    private String getDayOfWeekEnum(int position) {
        String[] days = {"MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY", "SATURDAY", "SUNDAY"};
        return days[position];
    }

    private void showLoading(boolean loading) {
        progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        createButton.setEnabled(!loading);
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
