package com.example.ovulitos;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

public class PerfilFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_perfil, container, false);

        // CORRECCIÓN AQUÍ:
        // Usamos la clase genérica "View" o "ViewGroup" porque en el XML son RelativeLayouts, no Buttons.
        View btnOvulita = view.findViewById(R.id.btnOvulita);
        View btnOvulito = view.findViewById(R.id.btnOvulito);

        // El resto del código funciona igual porque todos los "View" tienen setOnClickListener
        btnOvulita.setOnClickListener(v -> {
            irAlLogin("Ovulita");
        });

        btnOvulito.setOnClickListener(v -> {
            irAlLogin("Ovulito");
        });

        return view;
    }

    private void irAlLogin(String perfilSeleccionado) {
        getParentFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new LoginFragment())
                .addToBackStack(null)
                .commit();
    }
}