package com.example.ovulitos;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class LoginFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        EditText etUsuario = view.findViewById(R.id.etUsuario);
        EditText etPassword = view.findViewById(R.id.etPassword);
        Button btnLogin = view.findViewById(R.id.btnLogin);
        Button btnRegistro = view.findViewById(R.id.btnIrARegistro);

        // Lógica del botón "Siguiente"
        btnLogin.setOnClickListener(v -> {
            String usuario = etUsuario.getText().toString();
            // Aquí validaremos el usuario y cargaremos sus Stats (Fuerza, Salud, etc.)
            // dependiendo de si eligió Ovulita u Ovulito en la pantalla anterior.

            Toast.makeText(getContext(), "Iniciando sesión como: " + usuario, Toast.LENGTH_SHORT).show();
        });

        // Lógica del botón "Regístrate" - CORREGIDO (Descomentado)
        btnRegistro.setOnClickListener(v -> {
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new RegisterFragment())
                    .addToBackStack(null)
                    .commit();
        });

        return view;
    }
}