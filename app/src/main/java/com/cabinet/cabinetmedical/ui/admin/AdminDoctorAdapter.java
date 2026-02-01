package com.cabinet.cabinetmedical.ui.admin;

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
 * AdminDoctorAdapter - Adapter pour la liste des médecins (ADMIN)
 *
 * DIFFÉRENCE AVEC DoctorAdapter:
 * - Affiche TOUS les médecins (pas de filtrage)
 * - Clic ouvre AdminDoctorDetailsActivity pour gestion complète
 * - DoctorAdapter (patient) ouvre AvailableTimeSlotsActivity pour réserver
 *
 * UTILISATION:
 * AdminDoctorsListActivity → Liste médecins avec accès détails/modification
 *
 * FONCTIONNALITÉS:
 * - Affichage nom complet et spécialité
 * - Clic item → Détails médecin (AdminDoctorDetailsActivity)
 */
public class AdminDoctorAdapter extends RecyclerView.Adapter<AdminDoctorAdapter.ViewHolder> {

    private final List<DoctorResponse> doctors;

    /**
     * ViewHolder pour les items de la liste
     */
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView doctorName;
        TextView doctorSpecialty;

        ViewHolder(View itemView) {
            super(itemView);
            doctorName = itemView.findViewById(R.id.doctor_name);
            doctorSpecialty = itemView.findViewById(R.id.doctor_specialty);
        }
    }

    /**
     * Constructeur
     *
     * @param doctors Liste des médecins à afficher
     */
    public AdminDoctorAdapter(List<DoctorResponse> doctors) {
        this.doctors = doctors;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_doctor, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DoctorResponse doctor = doctors.get(position);

        // Afficher nom complet
        holder.doctorName.setText(doctor.getFullName());

        // Afficher la spécialité ou "Médecin généraliste"
        holder.doctorSpecialty.setText(doctor.getSpecialtyOrDefault());

        // Clic pour ouvrir les détails du médecin
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), AdminDoctorDetailsActivity.class);
            intent.putExtra("userId", doctor.getUserId());
            intent.putExtra("specialty", doctor.getSpecialty());
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
        this.doctors.clear();
        this.doctors.addAll(newDoctors);
        notifyDataSetChanged();
    }
}
