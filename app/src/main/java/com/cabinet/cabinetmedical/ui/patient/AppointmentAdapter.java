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
import com.cabinet.cabinetmedical.ui.common.StatusColors;
import com.google.android.material.button.MaterialButton;

import java.util.Date;
import java.util.List;

/** Rendez-vous du patient connecté, avec annulation des rendez-vous à venir. */
public class AppointmentAdapter extends RecyclerView.Adapter<AppointmentAdapter.AppointmentViewHolder> {

    public interface OnCancelListener {
        void onCancel(AppointmentResponse appointment);
    }

    private List<AppointmentResponse> appointments;
    private final OnCancelListener cancelListener;

    public AppointmentAdapter(List<AppointmentResponse> appointments, OnCancelListener cancelListener) {
        this.appointments = appointments;
        this.cancelListener = cancelListener;
    }

    @NonNull
    @Override
    public AppointmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_appointment, parent, false);
        return new AppointmentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AppointmentViewHolder holder, int position) {
        AppointmentResponse appointment = appointments.get(position);
        holder.doctorName.setText(appointment.getDoctorName());
        holder.doctorSpecialty.setText(appointment.getSpecialtyOrDefault());
        holder.dateTime.setText(appointment.getFormattedDateTime());
        holder.reason.setText(appointment.getReason() != null ? appointment.getReason() : "—");
        holder.status.setText(appointment.getStatusLabel());
        holder.status.setTextColor(ContextCompat.getColor(holder.itemView.getContext(),
                StatusColors.forStatus(appointment.getStatus())));

        String cancellation = appointment.getCancellationLabel();
        holder.cancellationInfo.setVisibility(cancellation != null ? View.VISIBLE : View.GONE);
        holder.cancellationInfo.setText(cancellation);

        boolean cancellable = appointment.canBeCancelled(new Date());
        holder.cancelButton.setVisibility(cancellable ? View.VISIBLE : View.GONE);
        holder.cancelButton.setOnClickListener(cancellable ? v -> cancelListener.onCancel(appointment) : null);
    }

    @Override
    public int getItemCount() {
        return appointments.size();
    }

    public void updateAppointments(List<AppointmentResponse> newAppointments) {
        this.appointments = newAppointments;
        notifyDataSetChanged();
    }

    static class AppointmentViewHolder extends RecyclerView.ViewHolder {
        final TextView doctorName;
        final TextView doctorSpecialty;
        final TextView dateTime;
        final TextView reason;
        final TextView status;
        final TextView cancellationInfo;
        final MaterialButton cancelButton;

        AppointmentViewHolder(@NonNull View itemView) {
            super(itemView);
            doctorName = itemView.findViewById(R.id.doctor_name);
            doctorSpecialty = itemView.findViewById(R.id.doctor_specialty);
            dateTime = itemView.findViewById(R.id.date_time);
            reason = itemView.findViewById(R.id.reason);
            status = itemView.findViewById(R.id.status);
            cancellationInfo = itemView.findViewById(R.id.cancellation_info);
            cancelButton = itemView.findViewById(R.id.cancel_button);
        }
    }
}
