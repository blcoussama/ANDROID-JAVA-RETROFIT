package com.cabinet.cabinetmedical.ui.doctor;

import android.app.AlertDialog;
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
import com.cabinet.cabinetmedical.model.request.UpdateTimeSlotRequest;
import com.cabinet.cabinetmedical.model.response.TimeSlotResponse;
import com.cabinet.cabinetmedical.utils.ErrorParser;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * DoctorEditTimeSlotActivity - Modification d'un créneau horaire
 *
 * RÔLE:
 * Formulaire pré-rempli pour modifier ou supprimer un créneau existant.
 * Accessible depuis DoctorTimeSlotsActivity (clic sur un créneau).
 *
 * API: PUT /api/timeslots/{id} (modifier), DELETE /api/timeslots/{id} (supprimer)
 * ACTIONS: Bouton Enregistrer (modifier), Bouton Supprimer (avec confirmation)
 */
public class DoctorEditTimeSlotActivity extends AppCompatActivity {

    private Spinner daySpinner;
    private TextInputEditText startTimeInput;
    private TextInputEditText endTimeInput;
    private MaterialButton saveButton;
    private MaterialButton deleteButton;
    private ProgressBar progressBar;
    private TextView errorMessage;

    private ApiService apiService;
    private Long timeSlotId;

    private String selectedStartTime = "";
    private String selectedEndTime = "";
    private String originalDayOfWeek;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_edit_timeslot);

        // Configurer la Toolbar
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Bouton retour vers liste créneaux
        toolbar.setNavigationOnClickListener(v -> finish());

        // Récupérer les données du créneau depuis l'Intent
        timeSlotId = getIntent().getLongExtra("timeslot_id", -1);
        originalDayOfWeek = getIntent().getStringExtra("day_of_week");
        String startTime = getIntent().getStringExtra("start_time");
        String endTime = getIntent().getStringExtra("end_time");

        if (timeSlotId == -1 || originalDayOfWeek == null) {
            finish();
            return;
        }

        // Initialiser Retrofit
        apiService = ApiClient.getClient(this).create(ApiService.class);

        // Initialiser les vues
        daySpinner = findViewById(R.id.spinner_day);
        startTimeInput = findViewById(R.id.input_start_time);
        endTimeInput = findViewById(R.id.input_end_time);
        saveButton = findViewById(R.id.button_save);
        deleteButton = findViewById(R.id.button_delete);
        progressBar = findViewById(R.id.progress_bar);
        errorMessage = findViewById(R.id.error_message);

        // Setup Spinner des jours
        setupDaySpinner();

        // Pré-remplir les valeurs
        prefillForm(startTime, endTime);

        // TimePicker pour heure début
        startTimeInput.setFocusable(false);
        startTimeInput.setClickable(true);
        startTimeInput.setOnClickListener(v -> showStartTimePicker());

        // TimePicker pour heure fin
        endTimeInput.setFocusable(false);
        endTimeInput.setClickable(true);
        endTimeInput.setOnClickListener(v -> showEndTimePicker());

        // Bouton enregistrer
        saveButton.setOnClickListener(v -> validateAndUpdate());

        // Bouton supprimer
        deleteButton.setOnClickListener(v -> showDeleteConfirmation());
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
     * Pré-remplir le formulaire avec les données existantes
     */
    private void prefillForm(String startTime, String endTime) {
        // Sélectionner le jour dans le spinner
        int dayPosition = getDayPosition(originalDayOfWeek);
        daySpinner.setSelection(dayPosition);

        // Afficher les heures (format HH:mm:ss → HH:mm)
        if (startTime != null && startTime.length() >= 5) {
            selectedStartTime = startTime;
            startTimeInput.setText(startTime.substring(0, 5)); // "09:00:00" → "09:00"
        }

        if (endTime != null && endTime.length() >= 5) {
            selectedEndTime = endTime;
            endTimeInput.setText(endTime.substring(0, 5));
        }
    }

    /**
     * Afficher TimePicker pour heure de début
     */
    private void showStartTimePicker() {
        // Extraire heure et minute actuels
        int hour = 9, minute = 0;
        if (!selectedStartTime.isEmpty()) {
            String[] parts = selectedStartTime.split(":");
            hour = Integer.parseInt(parts[0]);
            minute = Integer.parseInt(parts[1]);
        }

        TimePickerDialog picker = new TimePickerDialog(this,
                (view, hourOfDay, min) -> {
                    selectedStartTime = String.format("%02d:%02d:00", hourOfDay, min);
                    startTimeInput.setText(String.format("%02d:%02d", hourOfDay, min));
                    validateTimes();
                }, hour, minute, true);
        picker.show();
    }

    /**
     * Afficher TimePicker pour heure de fin
     */
    private void showEndTimePicker() {
        // Extraire heure et minute actuels
        int hour = 17, minute = 0;
        if (!selectedEndTime.isEmpty()) {
            String[] parts = selectedEndTime.split(":");
            hour = Integer.parseInt(parts[0]);
            minute = Integer.parseInt(parts[1]);
        }

        TimePickerDialog picker = new TimePickerDialog(this,
                (view, hourOfDay, min) -> {
                    selectedEndTime = String.format("%02d:%02d:00", hourOfDay, min);
                    endTimeInput.setText(String.format("%02d:%02d", hourOfDay, min));
                    validateTimes();
                }, hour, minute, true);
        picker.show();
    }

    /**
     * Valider que endTime > startTime
     */
    private void validateTimes() {
        if (!selectedStartTime.isEmpty() && !selectedEndTime.isEmpty()) {
            if (selectedEndTime.compareTo(selectedStartTime) <= 0) {
                showError("L'heure de fin doit être après l'heure de début");
                saveButton.setEnabled(false);
            } else {
                hideError();
                saveButton.setEnabled(true);
            }
        }
    }

    /**
     * Valider et mettre à jour le créneau
     */
    private void validateAndUpdate() {
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

        // Récupérer le jour sélectionné
        int dayPosition = daySpinner.getSelectedItemPosition();
        String dayOfWeek = getDayOfWeekEnum(dayPosition);

        // Créer la requête
        UpdateTimeSlotRequest request = new UpdateTimeSlotRequest();
        request.setDayOfWeek(dayOfWeek);
        request.setStartTime(selectedStartTime);
        request.setEndTime(selectedEndTime);

        // Appel API
        updateTimeSlot(request);
    }

    /**
     * Mettre à jour le créneau via API
     */
    private void updateTimeSlot(UpdateTimeSlotRequest request) {
        showLoading(true);

        apiService.updateTimeSlot(timeSlotId, request).enqueue(new Callback<TimeSlotResponse>() {
            @Override
            public void onResponse(Call<TimeSlotResponse> call, Response<TimeSlotResponse> response) {
                showLoading(false);

                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(DoctorEditTimeSlotActivity.this,
                            "Créneau modifié avec succès", Toast.LENGTH_SHORT).show();
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
     * Afficher dialog de confirmation de suppression
     */
    private void showDeleteConfirmation() {
        new AlertDialog.Builder(this)
                .setTitle("Supprimer ce créneau ?")
                .setMessage("Cette action est irréversible")
                .setPositiveButton("Supprimer", (dialog, which) -> deleteTimeSlot())
                .setNegativeButton("Annuler", null)
                .show();
    }

    /**
     * Supprimer le créneau via API
     */
    private void deleteTimeSlot() {
        showLoading(true);

        apiService.deleteTimeSlot(timeSlotId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                showLoading(false);

                if (response.isSuccessful()) {
                    Toast.makeText(DoctorEditTimeSlotActivity.this,
                            "Créneau supprimé avec succès", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    String error = ErrorParser.parseErrorMessage(response.errorBody());
                    showError(error);
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                showLoading(false);
                String error = ErrorParser.getNetworkErrorMessage(t);
                showError(error);
            }
        });
    }

    /**
     * Obtenir la position dans le spinner pour un DayOfWeek
     */
    private int getDayPosition(String dayOfWeek) {
        switch (dayOfWeek) {
            case "MONDAY": return 0;
            case "TUESDAY": return 1;
            case "WEDNESDAY": return 2;
            case "THURSDAY": return 3;
            case "FRIDAY": return 4;
            case "SATURDAY": return 5;
            case "SUNDAY": return 6;
            default: return 0;
        }
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
        saveButton.setEnabled(!loading);
        deleteButton.setEnabled(!loading);
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
