package com.cabinet.cabinetmedical.ui.patient;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cabinet.cabinetmedical.R;
import com.cabinet.cabinetmedical.model.response.DoctorResponse;

import java.util.List;

/**
 * DoctorAdapter - Adapter pour RecyclerView des médecins
 *
 * RESPONSABILITÉS:
 * - Afficher chaque médecin dans un item (nom + spécialité)
 * - Ouvrir les créneaux disponibles du médecin au clic
 * - Mettre à jour la liste dynamiquement
 */
public class DoctorAdapter extends RecyclerView.Adapter<DoctorAdapter.DoctorViewHolder> {

    private List<DoctorResponse> doctors;

    public DoctorAdapter(List<DoctorResponse> doctors) {
        this.doctors = doctors;
    }

    @NonNull
    @Override
    public DoctorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_doctor, parent, false);
        return new DoctorViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DoctorViewHolder holder, int position) {
        DoctorResponse doctor = doctors.get(position);

        // Afficher le nom complet avec titre "Dr."
        holder.doctorName.setText(doctor.getFullName());

        // Afficher la spécialité ou "Médecin généraliste"
        holder.doctorSpecialty.setText(doctor.getSpecialtyOrDefault());

        // Clic pour voir créneaux disponibles
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), AvailableTimeSlotsActivity.class);
            intent.putExtra("doctorId", doctor.getId());
            intent.putExtra("doctorName", doctor.getFullName());
            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return doctors.size();
    }

    /**
     * Mettre à jour la liste des médecins
     *
     * @param newDoctors Nouvelle liste de médecins
     */
    public void updateDoctors(List<DoctorResponse> newDoctors) {
        this.doctors = newDoctors;
        notifyDataSetChanged();
    }

    /**
     * ViewHolder pour chaque item médecin
     */
    static class DoctorViewHolder extends RecyclerView.ViewHolder {

        TextView doctorName;
        TextView doctorSpecialty;

        public DoctorViewHolder(@NonNull View itemView) {
            super(itemView);
            doctorName = itemView.findViewById(R.id.doctor_name);
            doctorSpecialty = itemView.findViewById(R.id.doctor_specialty);
        }
    }
}
