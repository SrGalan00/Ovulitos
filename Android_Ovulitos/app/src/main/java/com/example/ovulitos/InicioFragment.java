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

public class InicioFragment extends Fragment {


    private String email;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //inflamos el diseño de la pantalla principal
        return inflater.inflate(R.layout.fragment_inicio, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Datos pasados de una pantalla a otra
        if(getArguments() != null) {
            System.out.println("Esto se ejecuta");
            this.email = getArguments().getString("email");
        }

        //referencias a los textos que cambian
        TextView tvDias = view.findViewById(R.id.tvDiasParaPeriodo);
        TextView tvFase = view.findViewById(R.id.tvFaseCiclo);

        // Cargar datos desde Firebase para sincronización web
        if (GlobalVariables.email != null) {
            com.google.firebase.firestore.FirebaseFirestore.getInstance()
                .collection("usuarios").document(GlobalVariables.email)
                .get().addOnSuccessListener(doc -> {
                    if (doc.exists() && doc.contains("proximaReglaPrevista")) {
                        String proxima = doc.getString("proximaReglaPrevista");
                        // Asumimos 28 por defecto, priorizando cicloMedio de la web
                        long duracionCiclo = 28;
                        if (doc.contains("cicloMedio")) {
                            Long val = doc.getLong("cicloMedio");
                            if (val != null) duracionCiclo = val;
                        } else if (doc.contains("duracionCiclo")) {
                            Long val = doc.getLong("duracionCiclo");
                            if (val != null) duracionCiclo = val;
                        }
                        
                        if (proxima != null && android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                            java.time.LocalDate date = java.time.LocalDate.parse(proxima);
                            java.time.LocalDate hoy = java.time.LocalDate.now();
                            long dias = java.time.temporal.ChronoUnit.DAYS.between(hoy, date);
                            
                            GlobalVariables.diasProximaRegla = (int) Math.max(0, dias);
                            
                            // Calcular fase del ciclo y consejo
                            long diaActualCiclo = duracionCiclo - GlobalVariables.diasProximaRegla + 1;
                            TextView tvConsejo = view.findViewById(R.id.tvConsejoDia);

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

                                // Obtener consejo desde Firebase como en la web
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
                        }
                    } else {
                        // Si no hay datos de periodo (primera vez o no configurado), redirigir a configuración de calendario
                        if (getActivity() != null) {
                            ConfiguracionCalendarioFragment configFragment = new ConfiguracionCalendarioFragment();
                            Bundle bundle = new Bundle();
                            String userEmail = GlobalVariables.email != null ? GlobalVariables.email : email;
                            bundle.putString("email", userEmail);
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
