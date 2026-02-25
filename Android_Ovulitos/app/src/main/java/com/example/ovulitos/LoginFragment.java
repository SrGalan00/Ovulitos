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
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.google.firebase.auth.FirebaseAuth;

public class LoginFragment extends Fragment {

    private FirebaseAuth auth;

    private EditText etUsuario;
    private EditText etPassword;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        auth = FirebaseAuth.getInstance();
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        // 2. INICIALIZARLAS (Sin poner "EditText" delante, porque ya están declaradas arriba)
        etUsuario = view.findViewById(R.id.etUsuario);
        etPassword = view.findViewById(R.id.etPassword);

        // Referencias a iconos y botones locales
        ImageView iconValUsuario = view.findViewById(R.id.iconValUsuario);
        ImageView iconValPass = view.findViewById(R.id.iconValPass);
        Button btnLogin = view.findViewById(R.id.btnLogin);
        Button btnRegistro = view.findViewById(R.id.btnIrARegistro); // Asegúrate que el ID coincide con el XML (a veces es btnIrRegistro)
        View btnGoogle = view.findViewById(R.id.btnGoogle);
        TextView btnOlvidaste = view.findViewById(R.id.btnOlvidastePass);

        // --- VALIDACIONES ---
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

        // --- LISTENERS ---
        btnLogin.setOnClickListener(v -> login()); // Llama al método de abajo

        btnGoogle.setOnClickListener(v -> Toast.makeText(getContext(), "Conectando con Google...", Toast.LENGTH_SHORT).show());

        btnOlvidaste.setOnClickListener(v -> Toast.makeText(getContext(), "Recuperar contraseña...", Toast.LENGTH_SHORT).show());

        btnRegistro.setOnClickListener(v -> {
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new RegisterFragment())
                    .addToBackStack(null)
                    .commit();
        });

        return view;
    }

    private void login(){
        // 3. USARLAS (Ahora este método SÍ puede acceder a etUsuario y etPassword)
        String email = etUsuario.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

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
                                .addToBackStack(null) // Para no volver al login al dar atrás
                                .commit();
                    }else{
                        Toast.makeText(getContext(), "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}