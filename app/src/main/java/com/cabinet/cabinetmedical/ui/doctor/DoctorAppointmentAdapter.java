package com.cabinet.cabinetmedical.ui.doctor;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.cabinet.cabinetmedical.R;
import com.cabinet.cabinetmedical.model.response.AppointmentResponse;
import com.google.android.material.button.MaterialButton;

import java.util.List;

/**
 * DoctorAppointmentAdapter - Adapter pour RecyclerView des rendez-vous (médecin)
 *
 * RESPONSABILITÉS:
 * - Afficher chaque RDV avec patient, date, motif, status
 * - Afficher boutons Confirmer/Annuler selon le status
 * - Gérer les clics sur Confirmer et Annuler
 */
public class DoctorAppointmentAdapter extends RecyclerView.Adapter<DoctorAppointmentAdapter.ViewHolder> {

    private List<AppointmentResponse> appointments;
    private OnAppointmentActionListener listener;

    public interface OnAppointmentActionListener {
        void onConfirm(AppointmentResponse appointment);
        void onCancel(AppointmentResponse appointment);
    }

    public DoctorAppointmentAdapter(List<AppointmentResponse> appointments, OnAppointmentActionListener listener) {
        this.appointments = appointments;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_doctor_appointment, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AppointmentResponse appointment = appointments.get(position);

        // Afficher patient
        holder.patientName.setText(appointment.getPatientName());
        holder.patientPhone.setText(appointment.getPatientPhone());

        // Afficher date/heure
        holder.dateTime.setText(appointment.getFormattedDateTime());

        // Afficher motif
        holder.reason.setText(appointment.getReason());

        // Afficher status
        holder.status.setText(appointment.getStatusLabel());

        // Couleur du status
        int statusColor;
        switch (appointment.getStatus()) {
            case "PENDING":
                statusColor = ContextCompat.getColor(holder.itemView.getContext(), R.color.status_pending);
                break;
            case "CONFIRMED":
                statusColor = ContextCompat.getColor(holder.itemView.getContext(), R.color.status_confirmed);
                break;
            case "CANCELLED":
                statusColor = ContextCompat.getColor(holder.itemView.getContext(), R.color.status_cancelled);
                break;
            default:
                statusColor = ContextCompat.getColor(holder.itemView.getContext(), R.color.status_pending);
                break;
        }
        holder.status.setTextColor(statusColor);

        // Boutons d'action selon le status
        if (appointment.getStatus().equals("PENDING")) {
            // RDV en attente → Afficher Confirmer et Annuler
            holder.confirmButton.setVisibility(View.VISIBLE);
            holder.cancelButton.setVisibility(View.VISIBLE);

            holder.confirmButton.setOnClickListener(v -> listener.onConfirm(appointment));
            holder.cancelButton.setOnClickListener(v -> listener.onCancel(appointment));
        } else if (appointment.getStatus().equals("CONFIRMED")) {
            // RDV confirmé → Afficher seulement Annuler
            holder.confirmButton.setVisibility(View.GONE);
            holder.cancelButton.setVisibility(View.VISIBLE);

            holder.cancelButton.setOnClickListener(v -> listener.onCancel(appointment));
        } else {
            // RDV annulé → Cacher tous les boutons
            holder.confirmButton.setVisibility(View.GONE);
            holder.cancelButton.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return appointments.size();
    }

    public void updateAppointments(List<AppointmentResponse> newAppointments) {
        this.appointments = newAppointments;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView patientName;
        TextView patientPhone;
        TextView dateTime;
        TextView reason;
        TextView status;
        MaterialButton confirmButton;
        MaterialButton cancelButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            patientName = itemView.findViewById(R.id.patient_name);
            patientPhone = itemView.findViewById(R.id.patient_phone);
            dateTime = itemView.findViewById(R.id.date_time);
            reason = itemView.findViewById(R.id.reason);
            status = itemView.findViewById(R.id.status);
            confirmButton = itemView.findViewById(R.id.confirm_button);
            cancelButton = itemView.findViewById(R.id.cancel_button);
        }
    }
}
