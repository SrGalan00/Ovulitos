package com.example.ovulitos;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AjustesFragment extends Fragment {

    // Elementos de la "Barra Rosa" (Master)
    private TextView txtTitulo;
    private ImageView btnAtras;
    private FrameLayout contenedor;

    // Firebase
    private FirebaseAuth auth;
    private DatabaseReference mDatabase;

    // Control para saber si estamos dentro de una sub-sección
    private boolean enSubSeccion = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.ajustes_master, container, false); //

        // Inicializar Firebase
        auth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Vincular elementos
        txtTitulo = view.findViewById(R.id.txtTituloSeccion);
        btnAtras = view.findViewById(R.id.btnAtrasInterno);
        contenedor = view.findViewById(R.id.contenedor_interno_ajustes);

        // Cargar menú al inicio
        mostrarMenu();

        // Botón atrás de la barra superior
        btnAtras.setOnClickListener(v -> {
            if (enSubSeccion) {
                mostrarMenu();
            } else {
                getParentFragmentManager().popBackStack();
            }
        });

        return view;
    }

    // --- MENÚ PRINCIPAL ---
    private void mostrarMenu() {
        enSubSeccion = false;
        txtTitulo.setText("Ajustes");
        btnAtras.setVisibility(View.GONE);

        contenedor.removeAllViews();
        View menuView = getLayoutInflater().inflate(R.layout.fragment_ajustes_menu, contenedor, false); //
        contenedor.addView(menuView);

        // Listeners del menú
        LinearLayout btnCuenta = menuView.findViewById(R.id.btnIrACuenta);
        LinearLayout btnSeguridad = menuView.findViewById(R.id.btnIrASeguridad);
        LinearLayout btnPrivacidad = menuView.findViewById(R.id.btnIrAPrivacidad);
        LinearLayout btnNotificaciones = menuView.findViewById(R.id.btnIrANotificaciones);

        btnCuenta.setOnClickListener(v -> mostrarCuenta());
        btnSeguridad.setOnClickListener(v -> mostrarSubSeccion("Seguridad y permisos", R.layout.fragment_ajustes_seguridad));
        btnPrivacidad.setOnClickListener(v -> mostrarSubSeccion("Privacidad", R.layout.fragment_ajustes_privacidad));
        btnNotificaciones.setOnClickListener(v -> mostrarSubSeccion("Notificaciones", R.layout.fragment_ajustes_notificaciones));
    }

    // --- PANTALLA DE CUENTA (CON LÓGICA DE DATOS) ---
    private void mostrarCuenta() {
        enSubSeccion = true;
        txtTitulo.setText("Cuenta");
        btnAtras.setVisibility(View.VISIBLE);

        contenedor.removeAllViews();
        //
        View vistaCuenta = getLayoutInflater().inflate(R.layout.fragment_ajustes_cuenta, contenedor, false);
        contenedor.addView(vistaCuenta);

        // 1. Vincular los TextViews que acabamos de crear
        TextView txtEmail = vistaCuenta.findViewById(R.id.txtEmailUsuario);
        TextView txtNombre = vistaCuenta.findViewById(R.id.txtNombreUsuario);

        // 2. Obtener usuario actual
        FirebaseUser user = auth.getCurrentUser();

        if (user != null) {
            // A. Poner el EMAIL (Esto es inmediato)
            txtEmail.setText(user.getEmail());

            // B. Poner el NOMBRE (Hay que pedirlo a la base de datos)
            // Asumiendo que guardaste el nombre en: Users -> [ID] -> nombre
            mDatabase.child("Users").child(user.getUid()).child("nombre")
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                String nombreGuardado = snapshot.getValue(String.class);
                                txtNombre.setText(nombreGuardado);
                            } else {
                                txtNombre.setText("Usuario sin nombre");
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            // Si falla, no hacemos nada o mostramos error
                        }
                    });
        }
    }

    // Método genérico para las otras pantallas que no tienen lógica todavía
    private void mostrarSubSeccion(String titulo, int layoutId) {
        enSubSeccion = true;
        txtTitulo.setText(titulo);
        btnAtras.setVisibility(View.VISIBLE);

        contenedor.removeAllViews();
        View view = getLayoutInflater().inflate(layoutId, contenedor, false);
        contenedor.addView(view);
    }
    @Override
    public void onResume() {
        super.onResume();
        // Cuando se carga esta pantalla, buscamos la cabecera de la actividad y la ocultamos
        if (getActivity() != null) {
            View header = getActivity().findViewById(R.id.header_container);
            if (header != null) {
                header.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        // Cuando salimos de esta pantalla (volvemos al Home), la mostramos de nuevo
        if (getActivity() != null) {
            View header = getActivity().findViewById(R.id.header_container);
            if (header != null) {
                header.setVisibility(View.VISIBLE);
            }
        }
    }

}