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
                        if (proxima != null && android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                            java.time.LocalDate date = java.time.LocalDate.parse(proxima);
                            java.time.LocalDate hoy = java.time.LocalDate.now();
                            long dias = java.time.temporal.ChronoUnit.DAYS.between(hoy, date);
                            
                            GlobalVariables.diasProximaRegla = (int) Math.max(0, dias);
                            
                            if (tvDias != null) {
                                String text = GlobalVariables.diasProximaRegla + " días para tu periodo";
                                tvDias.setText(text);
                            }
                        }
                    }
                });
        }
    }
}
