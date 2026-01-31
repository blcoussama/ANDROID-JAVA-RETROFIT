package com.cabinet.cabinetmedical.ui.patient;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cabinet.cabinetmedical.R;

import java.util.List;

/**
 * TimeSlotAdapter - Adapter pour afficher les créneaux horaires
 *
 * AFFICHAGE:
 * - Grille 3 colonnes (GridLayoutManager)
 * - Chaque créneau = bouton cliquable
 * - Format: "09:00", "09:30", etc.
 */
public class TimeSlotAdapter extends RecyclerView.Adapter<TimeSlotAdapter.TimeSlotViewHolder> {

    private List<String> timeSlots;
    private OnTimeSlotClickListener listener;

    public interface OnTimeSlotClickListener {
        void onTimeSlotClick(String timeSlot);
    }

    public TimeSlotAdapter(List<String> timeSlots, OnTimeSlotClickListener listener) {
        this.timeSlots = timeSlots;
        this.listener = listener;
    }

    @NonNull
    @Override
    public TimeSlotViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_time_slot, parent, false);
        return new TimeSlotViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TimeSlotViewHolder holder, int position) {
        String timeSlot = timeSlots.get(position);

        // Formater l'heure (enlever les secondes si présentes)
        String displayTime = timeSlot.length() > 5 ? timeSlot.substring(0, 5) : timeSlot;
        holder.timeText.setText(displayTime);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onTimeSlotClick(timeSlot);
            }
        });
    }

    @Override
    public int getItemCount() {
        return timeSlots.size();
    }

    public void updateTimeSlots(List<String> newTimeSlots) {
        this.timeSlots = newTimeSlots;
        notifyDataSetChanged();
    }

    static class TimeSlotViewHolder extends RecyclerView.ViewHolder {
        TextView timeText;

        public TimeSlotViewHolder(@NonNull View itemView) {
            super(itemView);
            timeText = itemView.findViewById(R.id.time_text);
        }
    }
}
