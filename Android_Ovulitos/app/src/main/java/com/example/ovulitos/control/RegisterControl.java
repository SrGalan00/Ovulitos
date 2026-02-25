package com.example.ovulitos.control;

import android.text.TextUtils;
import android.widget.EditText;

import com.example.ovulitos.iterface.LoginCallback;
import com.example.ovulitos.validaciones.Validacion;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RegisterControl {

    private FirebaseAuth auth;

    public RegisterControl(){
        auth = FirebaseAuth.getInstance();
    }

    public void register(EditText mail, EditText pass, LoginCallback callback) {
        String email = mail.getText().toString().trim();
        String password = pass.getText().toString().trim();

        // Validaciones
        if(TextUtils.isEmpty(email)){
            mail.setError("No has puesto un correo válido!");
            callback.onFailure("Correo vacío");
            return;
        } else if(!Validacion.validarEmail(email)){
            mail.setError("El correo no cumple los requisitos necesarios!");
            callback.onFailure("Correo inválido");
            return;
        }

        if(TextUtils.isEmpty(password)){
            pass.setError("No has ingresado una contraseña válida!");
            callback.onFailure("Contraseña vacía");
            return;
        } else if(!Validacion.validarPass(password)){
            pass.setError("La contraseña no cumple los requisitos necesarios!");
            callback.onFailure("Contraseña inválida");
            return;
        }

        // Login con Firebase
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(tarea -> {
                    if(tarea.isSuccessful()){
                        FirebaseUser user = auth.getCurrentUser();
                        callback.onSuccess();
                    } else {
                        String error = tarea.getException() != null ?
                                tarea.getException().getMessage() :
                                "Error desconocido";
                        callback.onFailure(error);
                    }
                });
    }
}