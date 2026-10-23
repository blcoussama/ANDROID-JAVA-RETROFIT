package com.cabinet.cabinetmedical.ui.patient;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CalendarView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cabinet.cabinetmedical.R;
import com.cabinet.cabinetmedical.api.ApiClient;
import com.cabinet.cabinetmedical.api.ApiService;
import com.cabinet.cabinetmedical.utils.ErrorParser;
import com.google.android.material.appbar.MaterialToolbar;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * AvailableTimeSlotsActivity - Consultation des créneaux disponibles d'un médecin
 *
 * RÔLE:
 * Affiche un calendrier pour choisir une date, puis montre les heures disponibles.
 * Accessible depuis DoctorListActivity (clic sur un médecin).
 *
 * API: GET /api/timeslots/available?doctorId={id}&date={date}
 * ACTIONS: Clic sur une heure → BookAppointmentActivity
 */
public class AvailableTimeSlotsActivity extends AppCompatActivity {

    private CalendarView calendarView;
    private RecyclerView recyclerView;
    private TimeSlotAdapter adapter;
    private ProgressBar progressBar;
    private TextView errorMessage;
    private TextView doctorNameText;
    private TextView selectedDateText;

    private ApiService apiService;
    private Long doctorId;
    private String doctorName;
    private String selectedDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_available_time_slots);

        // Configurer la Toolbar
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Bouton retour vers liste médecins
        toolbar.setNavigationOnClickListener(v -> finish());

        // Récupérer les infos du médecin depuis l'Intent
        doctorId = getIntent().getLongExtra("doctorId", -1);
        doctorName = getIntent().getStringExtra("doctorName");

        if (doctorId == -1) {
            finish();
            return;
        }

        // Initialiser Retrofit
        apiService = ApiClient.getService(this);

        // Initialiser les vues
        doctorNameText = findViewById(R.id.doctor_name_text);
        selectedDateText = findViewById(R.id.selected_date_text);
        calendarView = findViewById(R.id.calendar_view);
        recyclerView = findViewById(R.id.recycler_view_timeslots);
        progressBar = findViewById(R.id.progress_bar);
        errorMessage = findViewById(R.id.error_message);

        // Afficher nom du médecin
        doctorNameText.setText(doctorName);

        // Bloquer les dates passées dans le calendrier
        calendarView.setMinDate(System.currentTimeMillis() - 1000);

        // Configurer RecyclerView (grille 3 colonnes)
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        adapter = new TimeSlotAdapter(new ArrayList<>(), timeSlot -> {
            // Clic sur un créneau horaire → Prendre RDV
            openBookAppointmentActivity(timeSlot);
        });
        recyclerView.setAdapter(adapter);

        // Date initiale = aujourd'hui
        long today = System.currentTimeMillis();
        selectedDate = formatDate(today);
        selectedDateText.setText("Date sélectionnée : " + formatDateDisplay(today));

        // Charger les créneaux pour aujourd'hui
        loadAvailableTimeSlots();

        // Listener changement de date
        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            // month est 0-indexed (0 = Janvier)
            String date = String.format(Locale.getDefault(), "%04d-%02d-%02d", year, month + 1, dayOfMonth);
            selectedDate = date;
            selectedDateText.setText(String.format("Date sélectionnée : %02d/%02d/%04d", dayOfMonth, month + 1, year));
            loadAvailableTimeSlots();
        });
    }

    /**
     * Charger les créneaux disponibles depuis le backend
     */
    private void loadAvailableTimeSlots() {
        showLoading(true);
        hideError();

        apiService.getAvailableTimeSlots(doctorId, selectedDate).enqueue(new Callback<List<String>>() {
            @Override
            public void onResponse(Call<List<String>> call, Response<List<String>> response) {
                showLoading(false);

                if (response.isSuccessful() && response.body() != null) {
                    List<String> timeSlots = response.body();

                    if (timeSlots.isEmpty()) {
                        showError("Aucun créneau disponible pour cette date.");
                    } else {
                        adapter.updateTimeSlots(timeSlots);
                    }
                } else {
                    String error = ErrorParser.parseErrorMessage(response.errorBody());
                    showError(error);
                }
            }

            @Override
            public void onFailure(Call<List<String>> call, Throwable t) {
                showLoading(false);
                String error = ErrorParser.getNetworkErrorMessage(t);
                showError(error);
            }
        });
    }

    /**
     * Ouvrir l'écran de prise de rendez-vous
     */
    private void openBookAppointmentActivity(String timeSlot) {
        Intent intent = new Intent(this, BookAppointmentActivity.class);
        intent.putExtra("doctorId", doctorId);
        intent.putExtra("doctorName", doctorName);
        intent.putExtra("selectedDate", selectedDate);
        intent.putExtra("selectedTime", timeSlot);
        startActivity(intent);
    }

    /**
     * Formater date pour API (YYYY-MM-DD)
     */
    private String formatDate(long millis) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return sdf.format(new Date(millis));
    }

    /**
     * Formater date pour affichage (DD/MM/YYYY)
     */
    private String formatDateDisplay(long millis) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        return sdf.format(new Date(millis));
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

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
