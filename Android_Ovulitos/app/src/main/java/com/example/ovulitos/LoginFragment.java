package com.example.ovulitos;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.ovulitos.currentUser.UserData;
import com.example.ovulitos.global.GlobalVariables;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.auth.User;

import java.util.HashMap;
import java.util.Map;

public class LoginFragment extends Fragment {

    private FirebaseAuth auth;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private EditText etUsuario;
    private EditText etPassword;
    private ImageView iconValUsuario;
    private ImageView iconValPass;
    private Button btnLogin;
    private Button btnRegistro;
    private View btnGoogle;
    private TextView btnOlvidaste;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        auth = FirebaseAuth.getInstance();
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        // Inicializar todas las vistas
        initViews(view);

        // Configurar listeners
        setupTextWatchers();
        setupClickListeners();

        return view;
    }

    private void initViews(View view) {
        etUsuario = view.findViewById(R.id.etUsuario);
        etPassword = view.findViewById(R.id.etPassword);
        iconValUsuario = view.findViewById(R.id.iconValUsuario);
        iconValPass = view.findViewById(R.id.iconValPass);
        btnLogin = view.findViewById(R.id.btnLogin);
        btnRegistro = view.findViewById(R.id.btnIrARegistro);
        btnGoogle = view.findViewById(R.id.btnGoogle);
        btnOlvidaste = view.findViewById(R.id.btnOlvidastePass);
    }

    private void setupTextWatchers() {
        etUsuario.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) iconValUsuario.setImageResource(R.drawable.ic_validacion_ok);
                else iconValUsuario.setImageResource(R.drawable.ic_validacion_neutro);
            }
            public void afterTextChanged(Editable s) {}
        });

        etPassword.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() >= 6) iconValPass.setImageResource(R.drawable.ic_validacion_ok);
                else if (s.length() > 0) iconValPass.setImageResource(R.drawable.ic_validacion_error);
                else iconValPass.setImageResource(R.drawable.ic_validacion_neutro);
            }
            public void afterTextChanged(Editable s) {}
        });
    }

    private void setupClickListeners() {
        btnLogin.setOnClickListener(v -> login());

        btnGoogle.setOnClickListener(v ->
                Toast.makeText(getContext(), "Conectando con Google...", Toast.LENGTH_SHORT).show()
        );

        btnOlvidaste.setOnClickListener(v ->
                Toast.makeText(getContext(), "Recuperar contraseña...", Toast.LENGTH_SHORT).show()
        );

        btnRegistro.setOnClickListener(v ->
                getParentFragmentManager().beginTransaction()
                        .replace(R.id.main_fragment_container, new RegisterFragment()) // ¡IMPORTANTE! Usa el ID correcto
                        .addToBackStack(null)
                        .commit()
        );
    }

    private void login() {
        String email = etUsuario.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(getContext(), "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        btnLogin.setEnabled(false);
        btnLogin.setText("Iniciando sesión...");

        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Guardar datos en Firestore (en segundo plano)
                        saveUserDataToFirestore(email);
                        UserData.setUsuario(email);

                        GlobalVariables.email = email;

                        // Navegar al HomeFragment
                        navigateToHome();

                    } else {
                        // Restaurar botón en caso de error
                        btnLogin.setEnabled(true);
                        btnLogin.setText("Iniciar sesión");

                        String errorMessage = task.getException() != null ?
                                task.getException().getMessage() : "Error desconocido";
                        Toast.makeText(getContext(), "Error: " + errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void saveUserDataToFirestore(String email) {
        Map<String, Object> userData = new HashMap<>();
        userData.put("email", email);
        String fechaISO;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            fechaISO = java.time.Instant.now().toString();
        } else {
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", java.util.Locale.US);
            sdf.setTimeZone(java.util.TimeZone.getTimeZone("UTC"));
            fechaISO = sdf.format(new java.util.Date());
        }
        userData.put("lastAccess", fechaISO); // Fecha actual
        userData.put("provider", "email");

        db.collection("usuarios").document(email).set(userData, com.google.firebase.firestore.SetOptions.merge())
                .addOnSuccessListener(aVoid ->
                        Log.d("FIRESTORE", "Datos de usuario guardados correctamente")
                )
                .addOnFailureListener(e ->
                        Log.e("FIRESTORE", "Error guardando datos: " + e.getMessage())
                );
    }

    private void navigateToHome() {
        try {
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.main_fragment_container, new InicioFragment())
                    .commit();

        } catch (Exception e) {
            Log.e("NAVIGATION", "Error navegando a Home: " + e.getMessage());
            Toast.makeText(getContext(), "Error al navegar", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }



}