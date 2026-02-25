package com.example.ovulitos;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.google.android.material.card.MaterialCardView;

public class FertilidadFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //inflamos el layout fragment_informacion.xml
        return inflater.inflate(R.layout.fragment_fertilidad, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        MaterialCardView cardHinchazon = view.findViewById(R.id.cardHinchazon);
        MaterialCardView cardParanoia = view.findViewById(R.id.cardParanoia);

        //configuración de clics para mostrar información detallada
        if (cardHinchazon != null) {
            cardHinchazon.setOnClickListener(v -> {
                //aquí cargaría la info
            });
        }

        if (cardParanoia != null) {
            cardParanoia.setOnClickListener(v -> {
                //aquí cargaría la info
            });
        }
    }
}