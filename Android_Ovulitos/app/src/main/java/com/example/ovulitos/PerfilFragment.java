package com.example.ovulitos;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

public class PerfilFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Conectamos con el diseño xml que acabamos de hacer
        View view = inflater.inflate(R.layout.fragment_perfil, container, false);

        Button btnOvulita = view.findViewById(R.id.btnOvulita);
        Button btnOvulito = view.findViewById(R.id.btnOvulito);

        // Configurar clic en "Ovulita"
        btnOvulita.setOnClickListener(v -> {
            irAlLogin("Ovulita");
        });

        // Configurar clic en "Ovulito"
        btnOvulito.setOnClickListener(v -> {
            irAlLogin("Ovulito");
        });

        return view;
    }

    private void irAlLogin(String perfilSeleccionado) {
        // Aquí guardaremos qué perfil eligió para usar sus Puntos de Habilidad después.
        // Por ahora, solo cambiamos de pantalla al Login.

        getParentFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new LoginFragment())
                .addToBackStack(null) // Permite volver atrás si se equivocaron
                .commit();
    }
}