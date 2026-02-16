package com.example.ovulitos;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView; // Importante para el botón de texto
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

// Import de Firebase 
import com.google.firebase.auth.FirebaseAuth;

public class LoginFragment extends Fragment {

    private FirebaseAuth auth; 

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        
        // Declaración de la variable de firebase 
        auth = FirebaseAuth.getInstance();
        
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        // 1. Referencias a los campos de texto
        EditText etUsuario = view.findViewById(R.id.etUsuario);
        EditText etPassword = view.findViewById(R.id.etPassword);

        // 2. Referencias a los iconos de validación (Círculos a la derecha)
        ImageView iconValUsuario = view.findViewById(R.id.iconValUsuario);
        ImageView iconValPass = view.findViewById(R.id.iconValPass);

        // 3. Referencias a los botones
        Button btnLogin = view.findViewById(R.id.btnLogin);
        Button btnRegistro = view.findViewById(R.id.btnIrARegistro);

        // Usamos View genérico para Google porque es un RelativeLayout en el XML
        View btnGoogle = view.findViewById(R.id.btnGoogle);

        // El texto clicable de "¿Olvidaste contraseña?"
        TextView btnOlvidaste = view.findViewById(R.id.btnOlvidastePass);


        // --- LÓGICA DE VALIDACIÓN (TEXTWATCHERS) ---

        // Validación Usuario: Si escribe algo -> Tick Verde
        etUsuario.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    iconValUsuario.setImageResource(R.drawable.ic_validacion_ok);
                } else {
                    iconValUsuario.setImageResource(R.drawable.ic_validacion_neutro);
                }
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Validación Contraseña: < 6 letras -> X Roja | >= 6 letras -> Tick Verde
        etPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() >= 6) {
                    iconValPass.setImageResource(R.drawable.ic_validacion_ok);
                } else if (s.length() > 0) {
                    iconValPass.setImageResource(R.drawable.ic_validacion_error);
                } else {
                    iconValPass.setImageResource(R.drawable.ic_validacion_neutro);
                }
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });


        // --- LISTENERS DE LOS BOTONES ---

        // Botón Siguiente
        btnLogin.setOnClickListener(v -> {
            String usuario = etUsuario.getText().toString();
            // Aquí iría tu lógica real de login
            Toast.makeText(getContext(), "Intentando entrar como: " + usuario, Toast.LENGTH_SHORT).show();
            login(); 
        });

        // Botón Google (Ahora sí funciona al ser View)
        btnGoogle.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Conectando con Google...", Toast.LENGTH_SHORT).show();
        });

        // Texto "Haz click aquí"
        btnOlvidaste.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Recuperar contraseña...", Toast.LENGTH_SHORT).show();
        });

        // Botón Regístrate (Navegación)
        btnRegistro.setOnClickListener(v -> {
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new RegisterFragment())
                    .addToBackStack(null)
                    .commit();
        });

        return view;
    }


    private void login(){
        String email = etUsuario.getText().toString();
        String password = etPassword.getText().toString();

        if(email.isEmpty() || password.isEmpty()){
            Toast.makeText(getContext(), "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        Toast.makeText(getContext(), "Login exitoso", Toast.LENGTH_SHORT).show();
                        getParentFragmentManager().beginTransaction()
                                .replace(R.id.fragment_container, new HomeFragment())
                                .addToBackStack(null)
                                .commit();
                    }else{
                        Toast.makeText(getContext(), "Error al iniciar sesión", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}