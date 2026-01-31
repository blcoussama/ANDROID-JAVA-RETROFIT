package com.cabinet.cabinetmedical.ui.patient;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.cabinet.cabinetmedical.R;
import com.cabinet.cabinetmedical.model.response.AppointmentResponse;

import java.util.List;

/**
 * AppointmentAdapter - Adapter pour RecyclerView des rendez-vous
 *
 * RESPONSABILITÉS:
 * - Afficher chaque RDV dans un item (médecin, date/heure, motif, status)
 * - Mettre à jour la liste dynamiquement
 */
public class AppointmentAdapter extends RecyclerView.Adapter<AppointmentAdapter.AppointmentViewHolder> {

    private List<AppointmentResponse> appointments;

    public AppointmentAdapter(List<AppointmentResponse> appointments) {
        this.appointments = appointments;
    }

    @NonNull
    @Override
    public AppointmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_appointment, parent, false);
        return new AppointmentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AppointmentViewHolder holder, int position) {
        AppointmentResponse appointment = appointments.get(position);

        // Afficher médecin
        holder.doctorName.setText(appointment.getDoctorName());
        holder.doctorSpecialty.setText(appointment.getSpecialtyOrDefault());

        // Afficher date/heure formatée
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
    }

    @Override
    public int getItemCount() {
        return appointments.size();
    }

    /**
     * Mettre à jour la liste des rendez-vous
     *
     * @param newAppointments Nouvelle liste de RDV
     */
    public void updateAppointments(List<AppointmentResponse> newAppointments) {
        this.appointments = newAppointments;
        notifyDataSetChanged();
    }

    /**
     * ViewHolder pour chaque item rendez-vous
     */
    static class AppointmentViewHolder extends RecyclerView.ViewHolder {

        TextView doctorName;
        TextView doctorSpecialty;
        TextView dateTime;
        TextView reason;
        TextView status;

        public AppointmentViewHolder(@NonNull View itemView) {
            super(itemView);
            doctorName = itemView.findViewById(R.id.doctor_name);
            doctorSpecialty = itemView.findViewById(R.id.doctor_specialty);
            dateTime = itemView.findViewById(R.id.date_time);
            reason = itemView.findViewById(R.id.reason);
            status = itemView.findViewById(R.id.status);
        }
    }
}
