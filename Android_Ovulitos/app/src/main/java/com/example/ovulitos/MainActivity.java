package com.example.ovulitos; // Asegúrate de que esto coincide con tu paquete

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import android.content.Intent;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.TextView;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import androidx.annotation.NonNull;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //cargamos el fragment de inicio por defecto
        if (savedInstanceState == null) {
            reemplazarFragmento(new PerfilFragment());
        }

        getSupportFragmentManager().addOnBackStackChangedListener(() -> {
            Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.main_fragment_container);
            if (currentFragment != null) {
                updateNavigationBarsVisibility(currentFragment);
            }
        });

        ShapeableImageView btnInicio = findViewById(R.id.btn_1);
        ShapeableImageView btnCalendario = findViewById(R.id.btn_2);
        ShapeableImageView btnRelajacion = findViewById(R.id.btn_3);
        ShapeableImageView btnInformacion = findViewById(R.id.btn_4);
        ShapeableImageView btnEmociones = findViewById(R.id.btn_5);
        ShapeableImageView btnNoticias = findViewById(R.id.btn_6);
        ShapeableImageView btnChat = findViewById(R.id.btn_7);
        ShapeableImageView btnAi = findViewById(R.id.btn_8);

        //eventos de clic
        btnInicio.setOnClickListener(v -> reemplazarFragmento(new InicioFragment()));
        btnCalendario.setOnClickListener(v -> reemplazarFragmento(new CalendarioFragment()));
        btnRelajacion.setOnClickListener(v -> reemplazarFragmento(new RelajacionFragment()));
        btnInformacion.setOnClickListener(v -> reemplazarFragmento(new FertilidadFragment()));
        btnEmociones.setOnClickListener(v -> reemplazarFragmento(new EmocionesFragment()));
        btnNoticias.setOnClickListener(v -> reemplazarFragmento(new NoticiasFragment()));
        btnChat.setOnClickListener(v -> reemplazarFragmento(new ChatListFragment()));
        btnAi.setOnClickListener(v -> reemplazarFragmento(new AiAssistantFragment()));

        // Manejar el botón global de Ajustes en la TopBar
        android.widget.ImageView btnAjustesGlobal = findViewById(R.id.imageView2);
        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);

        if(btnAjustesGlobal != null) {
            btnAjustesGlobal.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.END));
        }

        // Poblar datos y eventos del Drawer Lateral
        setupDrawerInternals(drawerLayout);
        drawerLayout.addDrawerListener(new DrawerLayout.SimpleDrawerListener() {
            @Override
            public void onDrawerOpened(android.view.View drawerView) {
                actualizarDatosDrawer();
            }
        });
        actualizarDatosDrawer();
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
        
        updateNavigationBarsVisibility(fragmento);
    }

    private void updateNavigationBarsVisibility(Fragment fragmento) {
        // Controlar visibilidad de las barras de navegación basadas en el fragmento destino
        android.view.View topBar = findViewById(R.id.topBar);
        android.view.View bottomBar = findViewById(R.id.bottomBar);
        android.view.View mainContainer = findViewById(R.id.main_fragment_container);
        
        if (topBar != null && bottomBar != null && mainContainer != null) {
            androidx.constraintlayout.widget.ConstraintLayout.LayoutParams params = 
                    (androidx.constraintlayout.widget.ConstraintLayout.LayoutParams) mainContainer.getLayoutParams();
                    
            if (fragmento instanceof LoginFragment || fragmento instanceof RegisterFragment || fragmento instanceof PerfilFragment 
                || fragmento instanceof AjustesFragment || fragmento instanceof ChatFragment || fragmento instanceof ConfiguracionCalendarioFragment) {
                topBar.setVisibility(android.view.View.GONE);
                bottomBar.setVisibility(android.view.View.GONE);
                
                // Quitamos el margen inferior para que ocupe todo el espacio
                params.bottomMargin = 0;
            } else {
                topBar.setVisibility(android.view.View.VISIBLE);
                bottomBar.setVisibility(android.view.View.VISIBLE);
                
                // Margen 0 para que el scroll pase por debajo del menú flotante
                params.bottomMargin = 0;
            }
            
            mainContainer.setLayoutParams(params);
        }
    }

    public void actualizarDatosDrawer() {
        TextView txtUserName = findViewById(R.id.drawer_user_name);
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        
        if (user != null && txtUserName != null && user.getEmail() != null) {
            com.google.firebase.firestore.FirebaseFirestore.getInstance()
                .collection("usuarios").document(user.getEmail())
                .get().addOnSuccessListener(snapshot -> {
                    if (snapshot != null && snapshot.exists()) {
                        String nombre = snapshot.getString("nombre");
                        txtUserName.setText(nombre != null && !nombre.isEmpty() ? nombre : "Sin nombre");
                        
                        String avatarUrl = snapshot.getString("avatarUrl");
                        android.widget.ImageView imgAvatar = findViewById(R.id.drawer_avatar);
                        if (avatarUrl != null && !avatarUrl.isEmpty() && imgAvatar != null && !isDestroyed()) {
                            com.bumptech.glide.Glide.with(MainActivity.this).load(avatarUrl).circleCrop().placeholder(R.drawable.ovulito_sin_cara).into(imgAvatar);
                        }
                    } else {
                        txtUserName.setText(user.getEmail());
                    }
                });
        } else if (txtUserName != null) {
            txtUserName.setText("No identificado");
        }
    }

    private void setupDrawerInternals(DrawerLayout drawer) {

        android.view.View btnCuenta = findViewById(R.id.drawer_btn_cuenta);
        android.view.View btnSeguridad = findViewById(R.id.drawer_btn_seguridad);
        android.view.View btnPrivacidad = findViewById(R.id.drawer_btn_privacidad);
        android.view.View btnNotificaciones = findViewById(R.id.drawer_btn_notificaciones);
        android.view.View btnCerrarSesion = findViewById(R.id.drawer_btn_cerrar_sesion);

        if (btnCuenta != null) {
            btnCuenta.setOnClickListener(v -> {
                drawer.closeDrawer(GravityCompat.END);
                reemplazarFragmento(AjustesFragment.newInstance("cuenta"));
            });
        }
        if (btnSeguridad != null) {
            btnSeguridad.setOnClickListener(v -> {
                drawer.closeDrawer(GravityCompat.END);
                reemplazarFragmento(AjustesFragment.newInstance("seguridad"));
            });
        }
        if (btnPrivacidad != null) {
            btnPrivacidad.setOnClickListener(v -> {
                drawer.closeDrawer(GravityCompat.END);
                reemplazarFragmento(AjustesFragment.newInstance("privacidad"));
            });
        }
        if (btnNotificaciones != null) {
            btnNotificaciones.setOnClickListener(v -> {
                drawer.closeDrawer(GravityCompat.END);
                reemplazarFragmento(AjustesFragment.newInstance("notificaciones"));
            });
        }
        if (btnCerrarSesion != null) {
            btnCerrarSesion.setOnClickListener(v -> {
                drawer.closeDrawer(GravityCompat.END);
                FirebaseAuth.getInstance().signOut();
                reemplazarFragmento(new LoginFragment());
            });
        }
    }
}