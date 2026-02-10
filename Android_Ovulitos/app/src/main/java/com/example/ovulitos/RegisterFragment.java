package com.example.ovulitos;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class RegisterFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register, container, false);

        Button btnRegistrar = view.findViewById(R.id.btnRegistrar);
        TextView txtVolver = view.findViewById(R.id.txtVolverLogin);

        // Volver al Login si ya tiene cuenta
        txtVolver.setOnClickListener(v -> {
            getParentFragmentManager().popBackStack();
        });

        // Lógica de registro
        btnRegistrar.setOnClickListener(v -> {
            // Aquí iría tu lógica para guardar el usuario nuevo
            // Y asignar los skill points iniciales
        });

        return view;
    }
}