package com.cabinet.cabinetmedical.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cabinet.cabinetmedical.R;
import com.cabinet.cabinetmedical.model.response.TimeSlotResponse;

import java.util.ArrayList;
import java.util.List;

/**
 * TimeSlotAdapter - Adapter pour afficher la liste des créneaux dans RecyclerView
 *
 * UTILISATION:
 * - DoctorTimeSlotsActivity: Liste des créneaux du médecin
 *
 * FONCTIONNALITÉS:
 * - Affiche jour, heures, durée
 * - Click → Modifier créneau
 * - Long click → Supprimer créneau
 */
public class TimeSlotAdapter extends RecyclerView.Adapter<TimeSlotAdapter.TimeSlotViewHolder> {

    private Context context;
    private List<TimeSlotResponse> timeSlots;
    private OnTimeSlotClickListener listener;

    // Interface pour les clics
    public interface OnTimeSlotClickListener {
        void onTimeSlotClick(TimeSlotResponse timeSlot);
        void onTimeSlotLongClick(TimeSlotResponse timeSlot);
    }

    // Constructeur
    public TimeSlotAdapter(Context context, OnTimeSlotClickListener listener) {
        this.context = context;
        this.timeSlots = new ArrayList<>();
        this.listener = listener;
    }

    @NonNull
    @Override
    public TimeSlotViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_timeslot, parent, false);
        return new TimeSlotViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TimeSlotViewHolder holder, int position) {
        TimeSlotResponse timeSlot = timeSlots.get(position);
        holder.bind(timeSlot);
    }

    @Override
    public int getItemCount() {
        return timeSlots.size();
    }

    // Mettre à jour la liste
    public void setTimeSlots(List<TimeSlotResponse> timeSlots) {
        this.timeSlots = timeSlots != null ? timeSlots : new ArrayList<>();
        notifyDataSetChanged();
    }

    // ViewHolder
    class TimeSlotViewHolder extends RecyclerView.ViewHolder {
        TextView tvDayOfWeek;
        TextView tvTimeRange;
        TextView tvDuration;

        TimeSlotViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDayOfWeek = itemView.findViewById(R.id.tv_day_of_week);
            tvTimeRange = itemView.findViewById(R.id.tv_time_range);
            tvDuration = itemView.findViewById(R.id.tv_duration);

            // Click listener
            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onTimeSlotClick(timeSlots.get(position));
                }
            });

            // Long click listener
            itemView.setOnLongClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onTimeSlotLongClick(timeSlots.get(position));
                    return true;
                }
                return false;
            });
        }

        void bind(TimeSlotResponse timeSlot) {
            // Jour de la semaine en français
            tvDayOfWeek.setText(timeSlot.getDayOfWeekFr());

            // Plage horaire (09:00 - 12:00)
            tvTimeRange.setText(timeSlot.getTimeRange());

            // Durée (3h ou 1h 30min)
            tvDuration.setText(timeSlot.getFormattedDuration());
        }
    }
}
