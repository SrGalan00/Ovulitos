package com.example.ovulitos;

import android.os.Bundle;
import android.util.Log;
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
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;

public class RegisterFragment extends Fragment {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth auth;
    private EditText etNombre, etEmail, etPass;
    private Button btnRegistrar;
    private TextView txtVolver;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        auth = FirebaseAuth.getInstance();
        View view = inflater.inflate(R.layout.fragment_register, container, false);

        // Vincular vistas
        etNombre = view.findViewById(R.id.etRegNombre);
        etEmail = view.findViewById(R.id.etRegEmail);
        etPass = view.findViewById(R.id.etRegPass);
        btnRegistrar = view.findViewById(R.id.btnRegistrar);
        txtVolver = view.findViewById(R.id.txtVolverLogin);

        // Listener para volver al login
        txtVolver.setOnClickListener(v -> {
            if (getActivity() != null) {
                requireActivity().getSupportFragmentManager().popBackStack();
            }
        });

        // Listener para registrar usuario
        btnRegistrar.setOnClickListener(v -> registerUser());

        return view;
    }

    private void registerUser() {
        String nombre = etNombre.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPass.getText().toString().trim();

        // Validar campos vacíos
        if (nombre.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(getContext(), "Completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        // Deshabilitar botón mientras se procesa
        btnRegistrar.setEnabled(false);
        btnRegistrar.setText("Registrando...");

        // Crear usuario en Firebase Auth
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = auth.getCurrentUser();

                        // Preparar datos del usuario
                        Map<String, Object> userMap = new HashMap<>();
                        userMap.put("nombre", nombre);
                        userMap.put("email", email);
                        if (user != null) {
                            userMap.put("uid", user.getUid());
                        }
                        userMap.put("fecha_registro", new java.util.Date().toString());
                        userMap.put("provider", "email");

                        // Guardar en Firestore
                        db.collection("usuarios").document(email).set(userMap)
                                .addOnSuccessListener(aVoid -> {
                                    Log.d("FIRESTORE", "Datos guardados correctamente");
                                    Toast.makeText(getContext(), "Registro exitoso", Toast.LENGTH_SHORT).show();

                                    // 👇🏻👇🏻👇🏻 PARTE CRÍTICA CORREGIDA 👇🏻👇🏻👇🏻
                                    // Navegar a HomeFragment usando requireActivity()
                                    if (getActivity() != null) {
                                        requireActivity().getSupportFragmentManager()
                                                .beginTransaction()
                                                .replace(R.id.fragment_container, new HomeFragment())
                                                .commitAllowingStateLoss();
                                    } else {
                                        Log.e("NAVIGATION", "getActivity() es null");
                                    }
                                })
                                .addOnFailureListener(e -> {
                                    Log.e("FIRESTORE", "Error guardando datos: " + e.getMessage());
                                    Toast.makeText(getContext(), "Error al guardar datos", Toast.LENGTH_SHORT).show();
                                    btnRegistrar.setEnabled(true);
                                    btnRegistrar.setText("Registrarse");
                                });
                    } else {
                        // Error en autenticación
                        String errorMsg = task.getException() != null ?
                                task.getException().getMessage() : "Error desconocido";
                        Toast.makeText(getContext(), "Error: " + errorMsg, Toast.LENGTH_SHORT).show();
                        btnRegistrar.setEnabled(true);
                        btnRegistrar.setText("Registrarse");
                    }
                });
    }
}