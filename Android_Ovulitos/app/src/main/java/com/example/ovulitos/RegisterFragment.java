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

// Import de Firebase 
import com.google.firebase.auth.FirebaseAuth;

public class RegisterFragment extends Fragment {

    private FirebaseAuth auth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        
        auth = FirebaseAuth.getInstance(); 
        
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

    prvate void register(){
        String email = etUsuario.getText().toString();
        String password = etPassword.getText().toString();

        if(email.isEmpty() || password.isEmpty()){
            Toast.makeText(getContext(), "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        Toast.makeText(getContext(), "Registro exitoso", Toast.LENGTH_SHORT).show();
                        getParentFragmentManager().beginTransaction()
                                .replace(R.id.fragment_container, new HomeFragment())
                                .addToBackStack(null)
                                .commit();
                    }else{
                        Toast.makeText(getContext(), "Error al registrar", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}