package com.cabinet.cabinetmedical.ui.doctor;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cabinet.cabinetmedical.R;
import com.cabinet.cabinetmedical.model.response.TimeSlotResponse;

import java.util.List;

/**
 * DoctorTimeSlotAdapter - Adapter pour RecyclerView des créneaux horaires
 *
 * RESPONSABILITÉS:
 * - Afficher chaque créneau: jour (FR), horaires, durée
 * - Click → Modifier (DoctorEditTimeSlotActivity)
 * - Long click → Supprimer (avec confirmation)
 */
public class DoctorTimeSlotAdapter extends RecyclerView.Adapter<DoctorTimeSlotAdapter.ViewHolder> {

    private List<TimeSlotResponse> timeSlots;
    private OnTimeSlotActionListener listener;

    public interface OnTimeSlotActionListener {
        void onEdit(TimeSlotResponse timeSlot);
        void onDelete(TimeSlotResponse timeSlot);
    }

    public DoctorTimeSlotAdapter(List<TimeSlotResponse> timeSlots, OnTimeSlotActionListener listener) {
        this.timeSlots = timeSlots;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_timeslot, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TimeSlotResponse timeSlot = timeSlots.get(position);

        // Afficher jour en français
        holder.dayOfWeek.setText(timeSlot.getDayOfWeekFr());

        // Afficher horaires (HH:mm:ss → HH:mm)
        String startTime = timeSlot.getStartTime();
        String endTime = timeSlot.getEndTime();
        if (startTime.length() >= 5) startTime = startTime.substring(0, 5);
        if (endTime.length() >= 5) endTime = endTime.substring(0, 5);
        holder.timeRange.setText(startTime + " - " + endTime);

        // Afficher durée
        long hours = timeSlot.getDurationMinutes() / 60;
        long minutes = timeSlot.getDurationMinutes() % 60;
        String duration = hours + "h";
        if (minutes > 0) duration += minutes;
        holder.duration.setText(duration);

        // Click → Modifier
        holder.itemView.setOnClickListener(v -> listener.onEdit(timeSlot));

        // Long click → Supprimer
        holder.itemView.setOnLongClickListener(v -> {
            listener.onDelete(timeSlot);
            return true; // Consommer l'événement
        });
    }

    @Override
    public int getItemCount() {
        return timeSlots.size();
    }

    public void updateTimeSlots(List<TimeSlotResponse> newTimeSlots) {
        this.timeSlots = newTimeSlots;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView dayOfWeek;
        TextView timeRange;
        TextView duration;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            dayOfWeek = itemView.findViewById(R.id.tv_day_of_week);
            timeRange = itemView.findViewById(R.id.tv_time_range);
            duration = itemView.findViewById(R.id.tv_duration);
        }
    }
}
