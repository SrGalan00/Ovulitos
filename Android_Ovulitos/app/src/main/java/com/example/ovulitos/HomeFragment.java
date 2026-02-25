package com.example.ovulitos;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class HomeFragment extends Fragment {

    private FirebaseAuth auth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();

        TextView txtBienvenida = view.findViewById(R.id.txtBienvenida);
        Button btnCerrarSesion = view.findViewById(R.id.btnCerrarSesion);

        if (user != null) {
            txtBienvenida.setText("¡Hola, " + user.getEmail() + "!");
        }

        // Botón para cerrar sesión y volver al inicio
        btnCerrarSesion.setOnClickListener(v -> {
            auth.signOut();
            // Volver a la pantalla de AuthActivity (o Login)
            Intent intent = new Intent(getActivity(), AuthActivity.class);
            startActivity(intent);
            getActivity().finish();
        });

        return view;
    }
}