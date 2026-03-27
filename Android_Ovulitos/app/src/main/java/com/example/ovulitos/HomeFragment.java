package com.example.ovulitos;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class HomeFragment extends Fragment {

    private FirebaseAuth auth;
    private DatabaseReference mDatabase;

    private TextView txtBienvenida;
    private TextView txtPuntosInfo;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        auth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        FirebaseUser user = auth.getCurrentUser();

        txtBienvenida = view.findViewById(R.id.txtBienvenida);
        txtPuntosInfo = view.findViewById(R.id.txtPuntosInfo);

        Button btnCerrarSesion = view.findViewById(R.id.btnCerrarSesion);
        View btnAjustes = view.findViewById(R.id.btnIrAjustes); // El nuevo botón

        // 1. Cargar datos si el usuario existe
        if (user != null) {
            txtBienvenida.setText("Cargando...");
            cargarDatosUsuario(user.getUid());
        }

        // 2. Acción: Ir a Ajustes
        btnAjustes.setOnClickListener(v -> {
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.main_fragment_container, new AjustesFragment()) // CORREGIDO PARA EVITAR CRASHO
                    .addToBackStack(null) // Importante para poder volver al Home
                    .commit();
        });

        // 3. Acción: Cerrar Sesión
        btnCerrarSesion.setOnClickListener(v -> {
            auth.signOut();
            Intent intent = new Intent(getActivity(), AuthActivity.class);
            startActivity(intent);
            getActivity().finish();
        });

        return view;
    }

    private void cargarDatosUsuario(String userId) {
        mDatabase.child("Users").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // Nombre
                    String nombre = snapshot.child("nombre").getValue(String.class);
                    if (nombre != null) {
                        txtBienvenida.setText("¡Hola, " + nombre + "!");
                    } else {
                        txtBienvenida.setText("¡Hola!");
                    }

                    // (Ejemplo) Mostrar puntos si existen
                    // Esto confirma que los skill points se guardaron bien
                    if(snapshot.hasChild("personajes")){
                        long puntosGuerrero = snapshot.child("personajes").child("Guerrero").getValue(Long.class);
                        txtPuntosInfo.setText("Fuerza Guerrero: " + puntosGuerrero);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Error de conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }
}