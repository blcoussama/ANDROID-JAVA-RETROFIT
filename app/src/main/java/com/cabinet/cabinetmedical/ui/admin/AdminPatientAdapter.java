package com.cabinet.cabinetmedical.ui.admin;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cabinet.cabinetmedical.R;
import com.cabinet.cabinetmedical.model.response.UserResponse;

import java.util.List;

/**
 * AdminPatientAdapter - Adapter pour la liste des patients (ADMIN)
 *
 * UTILISATION:
 * AdminPatientsListActivity → Liste patients avec click vers détails
 *
 * FONCTIONNALITÉS:
 * - Affiche nom complet et email du patient
 * - Click sur item → AdminPatientDetailsActivity
 */
public class AdminPatientAdapter extends RecyclerView.Adapter<AdminPatientAdapter.ViewHolder> {

    private final List<UserResponse> patients;

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView patientName;
        TextView patientEmail;

        ViewHolder(View itemView) {
            super(itemView);
            patientName = itemView.findViewById(R.id.patient_name);
            patientEmail = itemView.findViewById(R.id.patient_email);
        }
    }

    public AdminPatientAdapter(List<UserResponse> patients) {
        this.patients = patients;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_patient, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        UserResponse patient = patients.get(position);

        // Afficher nom complet
        holder.patientName.setText(patient.getFirstName() + " " + patient.getLastName());

        // Afficher email
        holder.patientEmail.setText(patient.getEmail());

        // Clic pour ouvrir les détails du patient
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), AdminPatientDetailsActivity.class);
            intent.putExtra("userId", patient.getId());
            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return patients.size();
    }

    /**
     * Mettre à jour la liste des patients
     *
     * @param newPatients Nouvelle liste de patients
     */
    public void updatePatients(List<UserResponse> newPatients) {
        this.patients.clear();
        this.patients.addAll(newPatients);
        notifyDataSetChanged();
    }
}
