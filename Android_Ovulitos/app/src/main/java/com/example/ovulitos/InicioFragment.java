package com.example.ovulitos;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.ovulitos.global.GlobalVariables;
import com.google.firebase.auth.FirebaseAuth;

public class InicioFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_inicio, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView tvDias = view.findViewById(R.id.tvDiasParaPeriodo);
        TextView tvFase = view.findViewById(R.id.tvFaseCiclo);
        TextView tvConsejo = view.findViewById(R.id.tvConsejoDia);
        
        String userEmail = GlobalVariables.email;
        if (userEmail == null && FirebaseAuth.getInstance().getCurrentUser() != null) {
            userEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
            GlobalVariables.email = userEmail;
        }

        if (userEmail != null) {
            com.google.firebase.firestore.FirebaseFirestore.getInstance()
                .collection("usuarios").document(userEmail)
                .get().addOnSuccessListener(doc -> {
                    if (doc.exists() && doc.contains("proximaReglaPrevista")) {
                        String proxima = doc.getString("proximaReglaPrevista");
                        long duracionCiclo = 28;
                        if (doc.contains("cicloMedio")) {
                            Long val = doc.getLong("cicloMedio");
                            if (val != null) duracionCiclo = val;
                        } else if (doc.contains("duracionCiclo")) {
                            Long val = doc.getLong("duracionCiclo");
                            if (val != null) duracionCiclo = val;
                        }
                        
                        if (proxima != null && android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                            try {
                                java.time.LocalDate date;
                                if (proxima.contains("T")) {
                                    date = java.time.ZonedDateTime.parse(proxima).toLocalDate();
                                } else {
                                    date = java.time.LocalDate.parse(proxima);
                                }
                                java.time.LocalDate hoy = java.time.LocalDate.now();
                                long dias = java.time.temporal.ChronoUnit.DAYS.between(hoy, date);
                                
                                GlobalVariables.diasProximaRegla = (int) Math.max(0, dias);
                                
                                long diaActualCiclo = duracionCiclo - GlobalVariables.diasProximaRegla + 1;

                                if (tvDias != null) {
                                    if (diaActualCiclo >= 1 && diaActualCiclo <= 5) {
                                        long diasRestantesRegla = 5 - diaActualCiclo;
                                        String text = diasRestantesRegla == 1 ? "1 día de regla restante" : diasRestantesRegla + " días de regla restantes";
                                        if(diasRestantesRegla == 0) text = "Último día de regla";
                                        tvDias.setText(text);
                                    } else {
                                        String text = GlobalVariables.diasProximaRegla + " días para tu periodo";
                                        tvDias.setText(text);
                                    }
                                }
                                
                                if (tvFase != null && tvConsejo != null) {
                                    if (diaActualCiclo >= 1 && diaActualCiclo <= 5) {
                                        tvFase.setText(R.string.fase_menstrual);
                                    } else if (diaActualCiclo >= 6 && diaActualCiclo <= 13) {
                                        tvFase.setText(R.string.fase_folicular);
                                    } else if (diaActualCiclo == 14) {
                                        tvFase.setText(R.string.fase_ovulacion);
                                    } else if (diaActualCiclo >= 15 && diaActualCiclo <= duracionCiclo) {
                                        tvFase.setText(R.string.fase_lutea);
                                    } else {
                                        tvFase.setText(R.string.fase_desconocida);
                                    }

                                    long tipDay = diaActualCiclo > 28 ? 28 : Math.max(1, diaActualCiclo);
                                    com.google.firebase.firestore.FirebaseFirestore.getInstance()
                                            .collection("consejos")
                                            .whereEqualTo("dia", tipDay)
                                            .get()
                                            .addOnSuccessListener(consejosSnap -> {
                                                if (!consejosSnap.isEmpty()) {
                                                    String contenido = consejosSnap.getDocuments().get(0).getString("contenido");
                                                    if (contenido != null) {
                                                        tvConsejo.setText(contenido);
                                                    }
                                                } else {
                                                    tvConsejo.setText(R.string.consejo_default);
                                                }
                                            })
                                            .addOnFailureListener(e -> {
                                                tvConsejo.setText(R.string.consejo_default);
                                            });
                                }
                            } catch(Exception e) {
                                e.printStackTrace();
                            }
                        }
                    } else {
                        if (getActivity() != null) {
                            ConfiguracionCalendarioFragment configFragment = new ConfiguracionCalendarioFragment();
                            Bundle bundle = new Bundle();
                            String safeEmail = GlobalVariables.email != null ? GlobalVariables.email : FirebaseAuth.getInstance().getCurrentUser().getEmail();
                            bundle.putString("email", safeEmail);
                            configFragment.setArguments(bundle);
                            
                            getParentFragmentManager().beginTransaction()
                                    .replace(R.id.main_fragment_container, configFragment)
                                    .commit();
                        }
                    }
                });
        }
    }
}
