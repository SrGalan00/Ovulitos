package com.example.ovulitos;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class InicioFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //inflamos el diseño de la pantalla principal
        return inflater.inflate(R.layout.fragment_inicio, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //referencias a los textos que cambian
        TextView tvDias = view.findViewById(R.id.tvDiasParaPeriodo);
        TextView tvFase = view.findViewById(R.id.tvFaseCiclo);

        //por ahora lo dejamos estático, pero aquí es donde iría la lógica
        // para calcular los días restantes basándose en la fecha
        if (tvDias != null) {
            tvDias.setText("19 días para tu periodo");
        }
    }
}
