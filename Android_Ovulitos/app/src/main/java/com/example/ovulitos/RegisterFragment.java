package com.example.ovulitos;

import android.content.Intent;
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

import com.example.ovulitos.control.LoginControl;
import com.example.ovulitos.control.RegisterControl;
import com.example.ovulitos.iterface.LoginCallback;

public class RegisterFragment extends Fragment {

    private EditText name, email, password;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register, container, false);


        // Instanciamos la clase que se va a encargar de validar los datos del login
        RegisterControl register = new RegisterControl();

        name = view.findViewById(R.id.etRegNombre);
        email = view.findViewById(R.id.etRegEmail);
        password = view.findViewById(R.id.etRegPass);

        Button btnRegistrar = view.findViewById(R.id.btnRegistrar);
        TextView txtVolver = view.findViewById(R.id.txtVolverLogin);

        // Volver al Login si ya tiene cuenta
        txtVolver.setOnClickListener(v -> {
            getParentFragmentManager().popBackStack();
        });

        // Lógica de registro
        btnRegistrar.setOnClickListener(v -> {
            register.register(email, password, new LoginCallback() {
                @Override
                public void onSuccess() {
                    Toast.makeText(getContext(), "Usuario creado correctamente!", Toast.LENGTH_SHORT).show();

                    // abrir otra Activity
                    startActivity(new Intent(getActivity(), MainActivity.class));
                    requireActivity().finish();
                }

                @Override
                public void onFailure(String error) {
                    Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
                }
            });
        });

        return view;
    }
}