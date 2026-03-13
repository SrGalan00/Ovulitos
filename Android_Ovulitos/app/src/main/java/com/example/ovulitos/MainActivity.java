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
            reemplazarFragmento(new LoginFragment());
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

    public void reemplazarFragmento(Fragment fragmento) {
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
        
        // Controlar visibilidad de las barras de navegación basadas en el fragmento destino
        android.view.View topBar = findViewById(R.id.topBar);
        android.view.View bottomBar = findViewById(R.id.bottomBar);
        android.view.View mainContainer = findViewById(R.id.main_fragment_container);
        
        if (topBar != null && bottomBar != null && mainContainer != null) {
            androidx.constraintlayout.widget.ConstraintLayout.LayoutParams params = 
                    (androidx.constraintlayout.widget.ConstraintLayout.LayoutParams) mainContainer.getLayoutParams();
                    
            if (fragmento instanceof LoginFragment || fragmento instanceof RegisterFragment) {
                topBar.setVisibility(android.view.View.GONE);
                bottomBar.setVisibility(android.view.View.GONE);
                
                // Quitamos el margen inferior para que ocupe todo el espacio
                params.bottomMargin = 0;
            } else {
                topBar.setVisibility(android.view.View.VISIBLE);
                bottomBar.setVisibility(android.view.View.VISIBLE);
                
                // Restauramos los 100dp de margen inferior
                float scale = getResources().getDisplayMetrics().density;
                params.bottomMargin = (int) (100 * scale + 0.5f);
            }
            
            mainContainer.setLayoutParams(params);
        }
    }
}