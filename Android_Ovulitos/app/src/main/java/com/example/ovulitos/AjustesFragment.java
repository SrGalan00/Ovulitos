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
    private String seccionFoco = "menu";

    public static AjustesFragment newInstance(String seccion) {
        AjustesFragment fragment = new AjustesFragment();
        Bundle args = new Bundle();
        args.putString("seccion", seccion);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            seccionFoco = getArguments().getString("seccion", "menu");
        }
    }

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

        // Cargar vista directa en base al argumento
        if(seccionFoco.equals("cuenta")) {
            mostrarCuenta();
        } else if(seccionFoco.equals("seguridad")) {
            mostrarSubSeccion("Seguridad y permisos", R.layout.fragment_ajustes_seguridad);
        } else if(seccionFoco.equals("privacidad")) {
            mostrarSubSeccion("Privacidad", R.layout.fragment_ajustes_privacidad);
        } else if(seccionFoco.equals("notificaciones")) {
            mostrarSubSeccion("Notificaciones", R.layout.fragment_ajustes_notificaciones);
        } else {
            mostrarMenu();
        }

        // Botón atrás de la barra superior (como ahora venimos directo del Drawer, volver es ir al Home)
        btnAtras.setOnClickListener(v -> {
            getParentFragmentManager().popBackStack();
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

    // --- PANTALLA DE CUENTA (CON LÓGICA DE DATOS Y EDICIÓN) ---
    private void mostrarCuenta() {
        enSubSeccion = true;
        txtTitulo.setText("Cuenta");
        btnAtras.setVisibility(View.VISIBLE);

        contenedor.removeAllViews();
        View vistaCuenta = getLayoutInflater().inflate(R.layout.fragment_ajustes_cuenta, contenedor, false);
        contenedor.addView(vistaCuenta);

        TextView txtEmail = vistaCuenta.findViewById(R.id.txtEmailUsuario);
        TextView txtNombre = vistaCuenta.findViewById(R.id.txtNombreUsuario);
        TextView txtEstadoSocial = vistaCuenta.findViewById(R.id.txtEstadoSocial);

        FirebaseUser user = auth.getCurrentUser();

        if (user != null) {
            String userEmail = user.getEmail();
            txtEmail.setText(userEmail != null ? userEmail : "Sin correo");

            com.google.firebase.firestore.FirebaseFirestore firestore = com.google.firebase.firestore.FirebaseFirestore.getInstance();
            if (userEmail != null) {
                firestore.collection("usuarios").document(userEmail)
                        .addSnapshotListener((documentSnapshot, error) -> {
                            if (error != null || documentSnapshot == null) return;
                            if (documentSnapshot.exists()) {
                                String nombreGuardado = documentSnapshot.getString("nombre");
                                txtNombre.setText(nombreGuardado != null ? nombreGuardado : "Sin nombre");

                                String estadoSocial = documentSnapshot.getString("estado_social");
                                txtEstadoSocial.setText(estadoSocial != null ? estadoSocial : "(Toca para añadir)");
                            } else {
                                txtNombre.setText("Usuario sin perfil");
                                txtEstadoSocial.setText("(Toca para añadir)");
                            }
                        });
            }

            // Click Listeners for Editing
            txtNombre.setOnClickListener(v -> mostrarDialogoEdicion("Editar Nombre", "nombre", txtNombre.getText().toString(), auth.getCurrentUser().getEmail(), firestore));
            txtEstadoSocial.setOnClickListener(v -> mostrarDialogoEstadoSocial(txtEstadoSocial.getText().toString(), auth.getCurrentUser().getEmail(), firestore));
            txtEmail.setOnClickListener(v -> mostrarDialogoEdicionEmail("Editar Correo", txtEmail.getText().toString(), auth.getCurrentUser().getEmail(), firestore));
        }
    }

    private void mostrarDialogoEdicion(String titulo, String campo, String valorActual, String docId, com.google.firebase.firestore.FirebaseFirestore firestore) {
        if (docId == null) return;
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getContext());
        builder.setTitle(titulo);

        final android.widget.EditText input = new android.widget.EditText(getContext());
        input.setText(!valorActual.contains("Sin nombre") && !valorActual.contains("Toca para añadir") ? valorActual : "");
        input.setInputType(android.text.InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        builder.setPositiveButton("Guardar", (dialog, which) -> {
            String nuevoValor = input.getText().toString().trim();
            if (!nuevoValor.isEmpty()) {
                java.util.Map<String, Object> update = new java.util.HashMap<>();
                update.put(campo, nuevoValor);
                firestore.collection("usuarios").document(docId).set(update, com.google.firebase.firestore.SetOptions.merge());
            }
        });
        builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void mostrarDialogoEstadoSocial(String valorActual, String docId, com.google.firebase.firestore.FirebaseFirestore firestore) {
        if (docId == null) return;
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getContext());
        builder.setTitle("Estado Social");

        String[] opciones = {"Soltera", "Casada", "En una relación", "Prefiero no decirlo"};
        int selectedIndex = -1;
        for (int i = 0; i < opciones.length; i++) {
            if (opciones[i].equals(valorActual)) selectedIndex = i;
        }

        builder.setSingleChoiceItems(opciones, selectedIndex, (dialog, which) -> {
            String seleccionado = opciones[which];
            java.util.Map<String, Object> update = new java.util.HashMap<>();
            update.put("estado_social", seleccionado);
            firestore.collection("usuarios").document(docId).set(update, com.google.firebase.firestore.SetOptions.merge());
            dialog.dismiss();
        });
        
        builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void mostrarDialogoEdicionEmail(String titulo, String valorActual, String oldEmail, com.google.firebase.firestore.FirebaseFirestore firestore) {
        if (oldEmail == null) return;
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getContext());
        builder.setTitle(titulo);

        final android.widget.EditText input = new android.widget.EditText(getContext());
        input.setText(valorActual);
        input.setInputType(android.text.InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        builder.setView(input);

        builder.setPositiveButton("Guardar", (dialog, which) -> {
            String nuevoEmail = input.getText().toString().trim();
            if (!nuevoEmail.isEmpty() && !nuevoEmail.equals(oldEmail) && android.util.Patterns.EMAIL_ADDRESS.matcher(nuevoEmail).matches()) {
                FirebaseUser user = auth.getCurrentUser();
                if (user != null) {
                    user.updateEmail(nuevoEmail).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            // Migrate Document in Firestore to new email key
                            firestore.collection("usuarios").document(oldEmail).get().addOnSuccessListener(documentSnapshot -> {
                                if (documentSnapshot.exists()) {
                                    java.util.Map<String, Object> data = documentSnapshot.getData();
                                    if (data != null) {
                                        data.put("email", nuevoEmail);
                                        data.put("user", nuevoEmail);
                                        firestore.collection("usuarios").document(nuevoEmail).set(data).addOnSuccessListener(aVoid -> {
                                            firestore.collection("usuarios").document(oldEmail).delete();
                                            com.example.ovulitos.currentUser.UserData.setUsuario(nuevoEmail);
                                            android.widget.Toast.makeText(getContext(), "Correo actualizado. Vuelve a iniciar sesión si algo falla.", android.widget.Toast.LENGTH_SHORT).show();
                                            mostrarCuenta(); // Refrescar vista
                                        });
                                    }
                                }
                            });
                        } else {
                            // Re-authentication needed or other error
                            java.lang.Exception e = task.getException();
                            String msg = e != null ? e.getMessage() : "Error";
                            android.widget.Toast.makeText(getContext(), "Error actualizando: " + msg, android.widget.Toast.LENGTH_LONG).show();
                        }
                    });
                }
            } else {
                android.widget.Toast.makeText(getContext(), "Correo inválido o igual", android.widget.Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.cancel());
        builder.show();
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
    }

    @Override
    public void onStop() {
        super.onStop();
    }

}