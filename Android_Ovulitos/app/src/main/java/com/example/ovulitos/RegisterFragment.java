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

    private EditText etNombre, etEmail, etPass;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        auth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        View view = inflater.inflate(R.layout.fragment_register, container, false);

        // Vinculamos los IDs del XML
        etNombre = view.findViewById(R.id.etRegNombre);
        etEmail = view.findViewById(R.id.etRegEmail);
        etPass = view.findViewById(R.id.etRegPass);

        Button btnRegistrar = view.findViewById(R.id.btnRegistrar);
        TextView txtVolver = view.findViewById(R.id.txtVolverLogin);

        txtVolver.setOnClickListener(v -> getParentFragmentManager().popBackStack());

        btnRegistrar.setOnClickListener(v -> registerUser());

        return view;
    }

    private void registerUser(){
        String nombre = etNombre.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPass.getText().toString().trim();

        if(nombre.isEmpty() || email.isEmpty() || password.isEmpty()){
            Toast.makeText(getContext(), "Completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        FirebaseUser user = auth.getCurrentUser();
                        // AQUÍ ESTÁ LA CLAVE: Guardamos el nombre en la Database
                        guardarDatosUsuario(user.getUid(), nombre, email);
                    } else {
                        Toast.makeText(getContext(), "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void guardarDatosUsuario(String userId, String nombre, String email) {
        Map<String, Object> userMap = new HashMap<>();
        userMap.put("nombre", nombre);
        userMap.put("email", email);

        mDatabase.child("Users").child(userId).setValue(userMap)
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        Toast.makeText(getContext(), "Registro completo", Toast.LENGTH_SHORT).show();
                        getParentFragmentManager().beginTransaction()
                                .replace(R.id.fragment_container, new HomeFragment())
                                .commit();
                    }
                });
    }
}