package com.cabinet.cabinetmedical.ui.doctor;

import android.app.AlertDialog;
import android.content.Intent;
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
import com.cabinet.cabinetmedical.model.response.TimeSlotResponse;
import com.cabinet.cabinetmedical.session.Session;
import com.cabinet.cabinetmedical.session.SessionManager;
import com.cabinet.cabinetmedical.utils.ErrorParser;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * DoctorTimeSlotsActivity - Gestion des créneaux de disponibilité
 *
 * RÔLE:
 * Affiche tous les créneaux du médecin avec possibilité d'ajouter, modifier, supprimer.
 * Accessible depuis HomeDoctorActivity (bouton "Gérer mes créneaux").
 *
 * API: GET /api/timeslots/doctor/{doctorId}
 * ACTIONS: FAB → DoctorAddTimeSlotActivity, Clic item → DoctorEditTimeSlotActivity
 */
public class DoctorTimeSlotsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private TextView errorMessage;
    private TextView emptyMessage;
    private FloatingActionButton fabAdd;

    private ApiService apiService;
    private DoctorTimeSlotAdapter adapter;
    private Long doctorId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_timeslots);

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

        // Récupérer doctorId depuis SharedPreferences
        Session session = SessionManager.get(this).getSession();
        doctorId = session != null && session.getDoctorId() != null ? session.getDoctorId() : -1L;

        if (doctorId == -1) {
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
        fabAdd = findViewById(R.id.fab_add);

        // Setup RecyclerView avec adapter
        adapter = new DoctorTimeSlotAdapter(new ArrayList<>(), new DoctorTimeSlotAdapter.OnTimeSlotActionListener() {
            @Override
            public void onEdit(TimeSlotResponse timeSlot) {
                openEditActivity(timeSlot);
            }

            @Override
            public void onDelete(TimeSlotResponse timeSlot) {
                showDeleteConfirmation(timeSlot);
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        // Bouton Ajouter
        fabAdd.setOnClickListener(v -> openAddActivity());

        // Charger les créneaux
        loadTimeSlots();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Rafraîchir la liste à chaque retour (après add/edit)
        loadTimeSlots();
    }

    /**
     * Charger les créneaux du médecin depuis l'API
     */
    private void loadTimeSlots() {
        showLoading(true);
        hideError();
        hideEmpty();

        apiService.getDoctorTimeSlots(doctorId).enqueue(new Callback<List<TimeSlotResponse>>() {
            @Override
            public void onResponse(Call<List<TimeSlotResponse>> call, Response<List<TimeSlotResponse>> response) {
                showLoading(false);

                if (response.isSuccessful() && response.body() != null) {
                    List<TimeSlotResponse> timeSlots = response.body();

                    if (timeSlots.isEmpty()) {
                        showEmpty();
                    } else {
                        // Trier par jour de la semaine (Lundi=1 ... Dimanche=7)
                        timeSlots.sort(new Comparator<TimeSlotResponse>() {
                            @Override
                            public int compare(TimeSlotResponse t1, TimeSlotResponse t2) {
                                int day1 = getDayOfWeekOrder(t1.getDayOfWeek());
                                int day2 = getDayOfWeekOrder(t2.getDayOfWeek());
                                if (day1 != day2) {
                                    return Integer.compare(day1, day2);
                                }
                                // Si même jour, trier par heure de début
                                return t1.getStartTime().compareTo(t2.getStartTime());
                            }
                        });
                        adapter.updateTimeSlots(timeSlots);
                    }
                } else {
                    String error = ErrorParser.parse(response);
                    showError(error);
                }
            }

            @Override
            public void onFailure(Call<List<TimeSlotResponse>> call, Throwable t) {
                showLoading(false);
                String error = ErrorParser.getNetworkErrorMessage(t);
                showError(error);
            }
        });
    }

    /**
     * Ouvrir l'activité d'ajout de créneau
     */
    private void openAddActivity() {
        Intent intent = new Intent(this, DoctorAddTimeSlotActivity.class);
        startActivity(intent);
    }

    /**
     * Ouvrir l'activité d'édition de créneau
     */
    private void openEditActivity(TimeSlotResponse timeSlot) {
        Intent intent = new Intent(this, DoctorEditTimeSlotActivity.class);
        intent.putExtra("timeslot_id", timeSlot.getId());
        intent.putExtra("day_of_week", timeSlot.getDayOfWeek());
        intent.putExtra("start_time", timeSlot.getStartTime());
        intent.putExtra("end_time", timeSlot.getEndTime());
        startActivity(intent);
    }

    /**
     * Afficher dialog de confirmation de suppression
     */
    private void showDeleteConfirmation(TimeSlotResponse timeSlot) {
        new AlertDialog.Builder(this)
                .setTitle("Supprimer ce créneau ?")
                .setMessage(timeSlot.getDayOfWeekFr() + " " + timeSlot.getStartTime() + " - " + timeSlot.getEndTime())
                .setPositiveButton("Supprimer", (dialog, which) -> deleteTimeSlot(timeSlot.getId()))
                .setNegativeButton("Annuler", null)
                .show();
    }

    /**
     * Supprimer un créneau via l'API
     */
    private void deleteTimeSlot(Long timeSlotId) {
        showLoading(true);

        apiService.deleteTimeSlot(timeSlotId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                showLoading(false);

                if (response.isSuccessful()) {
                    // Recharger la liste
                    loadTimeSlots();
                } else {
                    String error = ErrorParser.parse(response);
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
     * Obtenir l'ordre du jour de la semaine (Lundi=1, Dimanche=7)
     */
    private int getDayOfWeekOrder(String dayOfWeek) {
        Map<String, Integer> dayOrder = new HashMap<>();
        dayOrder.put("MONDAY", 1);
        dayOrder.put("TUESDAY", 2);
        dayOrder.put("WEDNESDAY", 3);
        dayOrder.put("THURSDAY", 4);
        dayOrder.put("FRIDAY", 5);
        dayOrder.put("SATURDAY", 6);
        dayOrder.put("SUNDAY", 7);
        return dayOrder.getOrDefault(dayOfWeek, 0);
    }

    private void showLoading(boolean loading) {
        progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        recyclerView.setVisibility(loading ? View.GONE : View.VISIBLE);
        fabAdd.setVisibility(loading ? View.GONE : View.VISIBLE);
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
