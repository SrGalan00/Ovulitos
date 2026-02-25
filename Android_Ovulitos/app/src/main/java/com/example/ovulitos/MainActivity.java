package com.example.ovulitos; // Asegúrate de que esto coincide con tu paquete

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import com.google.android.material.imageview.ShapeableImageView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //cargamos el fragment de inicio por defecto
        if (savedInstanceState == null) {
            reemplazarFragmento(new InicioFragment());
        }

        ShapeableImageView btnInicio = findViewById(R.id.btn_1);
        ShapeableImageView btnCalendario = findViewById(R.id.btn_2);
        ShapeableImageView btnRelajacion = findViewById(R.id.btn_3);
        ShapeableImageView btnInformacion = findViewById(R.id.btn_4);
        ShapeableImageView btnEmociones = findViewById(R.id.btn_5);
        ShapeableImageView btnNoticias = findViewById(R.id.btn_6);

        //eventos de clic
        btnInicio.setOnClickListener(v -> reemplazarFragmento(new InicioFragment()));
        btnCalendario.setOnClickListener(v -> reemplazarFragmento(new CalendarioFragment()));
        btnRelajacion.setOnClickListener(v -> reemplazarFragmento(new RelajacionFragment()));
        btnInformacion.setOnClickListener(v -> reemplazarFragmento(new FertilidadFragment()));
        btnEmociones.setOnClickListener(v -> reemplazarFragmento(new EmocionesFragment()));
        btnNoticias.setOnClickListener(v -> reemplazarFragmento(new NoticiasFragment()));
    }

    private void reemplazarFragmento(Fragment fragmento) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        //animamos con una transición suave
        fragmentTransaction.setCustomAnimations(
                android.R.anim.fade_in,
                android.R.anim.fade_out
        );

        fragmentTransaction.replace(R.id.main_fragment_container, fragmento);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.setReorderingAllowed(true);
        fragmentTransaction.commit();
    }
}