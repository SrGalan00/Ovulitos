package com.example.ovulitos;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class RegisterFragment extends Fragment {

    private FirebaseAuth auth;
    private DatabaseReference mDatabase;

    // Variables para los campos
    private EditText etNombre, etEmail, etPass;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        // 1. Inicializar Firebase Auth y Database
        auth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        View view = inflater.inflate(R.layout.fragment_register, container, false);

        // 2. Vincular con los IDs correctos (según tu XML)
        etNombre = view.findViewById(R.id.etRegNombre);
        etEmail = view.findViewById(R.id.etRegEmail);
        etPass = view.findViewById(R.id.etRegPass);

        Button btnRegistrar = view.findViewById(R.id.btnRegistrar);
        TextView txtVolver = view.findViewById(R.id.txtVolverLogin);

        // Listener para volver
        txtVolver.setOnClickListener(v -> getParentFragmentManager().popBackStack());

        // Listener para Registrar
        btnRegistrar.setOnClickListener(v -> registerUser());

        return view;
    }

    private void registerUser(){
        String nombre = etNombre.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPass.getText().toString().trim();

        if(nombre.isEmpty() || email.isEmpty() || password.isEmpty()){
            Toast.makeText(getContext(), "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        if(password.length() < 6){
            Toast.makeText(getContext(), "La contraseña debe tener al menos 6 caracteres", Toast.LENGTH_SHORT).show();
            return;
        }

        // Crear usuario en Authentication
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        // Si se crea la cuenta, guardamos el nombre en la base de datos
                        FirebaseUser user = auth.getCurrentUser();
                        guardarDatosUsuario(user.getUid(), nombre, email);
                    }else{
                        Toast.makeText(getContext(), "Error al registrar: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void guardarDatosUsuario(String userId, String nombre, String email) {
        // Mapa con los datos del usuario
        Map<String, Object> userMap = new HashMap<>();
        userMap.put("nombre", nombre); // <--- ESTO ES LO QUE LEERÁ AJUSTES
        userMap.put("email", email);

        // (Opcional) Inicializar Skill Points si los necesitas para el juego
        Map<String, Object> personajes = new HashMap<>();
        personajes.put("Guerrero", 10);
        personajes.put("Mago", 10);
        userMap.put("personajes", personajes);

        // Guardar en: Users -> [ID del usuario]
        mDatabase.child("Users").child(userId).setValue(userMap)
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        Toast.makeText(getContext(), "¡Cuenta creada con éxito!", Toast.LENGTH_SHORT).show();
                        // Ir al Home
                        getParentFragmentManager().beginTransaction()
                                .replace(R.id.fragment_container, new HomeFragment())
                                .commit();
                    } else {
                        Toast.makeText(getContext(), "Error al guardar datos", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}