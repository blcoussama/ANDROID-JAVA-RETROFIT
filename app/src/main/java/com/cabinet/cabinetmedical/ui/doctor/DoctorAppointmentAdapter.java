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
import com.cabinet.cabinetmedical.ui.common.StatusColors;
import com.google.android.material.button.MaterialButton;

import java.util.Date;
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
        holder.patientName.setText(appointment.getPatientName());
        holder.patientPhone.setText(appointment.getPatientPhone() != null ? appointment.getPatientPhone() : "");
        holder.dateTime.setText(appointment.getFormattedDateTime());
        holder.reason.setText(appointment.getReason() != null ? appointment.getReason() : "—");
        String cancellation = appointment.getCancellationLabel();
        holder.status.setText(cancellation != null ? cancellation : appointment.getStatusLabel());
        holder.status.setTextColor(ContextCompat.getColor(holder.itemView.getContext(),
                StatusColors.forStatus(appointment.getStatus())));

        // Actions seulement sur les rendez-vous à venir : l'API refuse (409) le reste
        Date now = new Date();
        boolean confirmable = appointment.canBeConfirmed(now);
        boolean cancellable = appointment.canBeCancelled(now);
        holder.confirmButton.setVisibility(confirmable ? View.VISIBLE : View.GONE);
        holder.cancelButton.setVisibility(cancellable ? View.VISIBLE : View.GONE);
        holder.confirmButton.setOnClickListener(confirmable ? v -> listener.onConfirm(appointment) : null);
        holder.cancelButton.setOnClickListener(cancellable ? v -> listener.onCancel(appointment) : null);
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
