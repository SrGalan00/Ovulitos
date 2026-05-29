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
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import android.net.Uri;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.bumptech.glide.Glide;
import android.widget.Toast;

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

    private ImageView imgFotoPerfil;
    private ActivityResultLauncher<String> pickImageLauncher;
    private Uri imageUri;

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

        pickImageLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
            if (uri != null) {
                imageUri = uri;
                if (imgFotoPerfil != null) {
                    Glide.with(this).load(imageUri).circleCrop().into(imgFotoPerfil);
                }
                uploadProfilePicture();
            }
        });
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
            
            imgFotoPerfil = vistaCuenta.findViewById(R.id.imgFotoPerfil);
            if (imgFotoPerfil != null) {
                imgFotoPerfil.setOnClickListener(v -> pickImageLauncher.launch("image/*"));
            }

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
                                
                                String avatarUrl = documentSnapshot.getString("avatarUrl");
                                if (avatarUrl != null && imgFotoPerfil != null && isAdded()) {
                                    Glide.with(this).load(avatarUrl).circleCrop().placeholder(R.drawable.ic_avatar_placeholder).into(imgFotoPerfil);
                                }
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
            
            TextView btnEliminarCuenta = vistaCuenta.findViewById(R.id.btnEliminarCuenta);
            if (btnEliminarCuenta != null) {
                btnEliminarCuenta.setOnClickListener(v -> eliminarCuenta(userEmail));
            }
        }
    }
    
    private void eliminarCuenta(String userEmail) {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getContext());
        builder.setTitle("Eliminar Cuenta");
        builder.setMessage("¿Estás segura de que quieres eliminar tu cuenta permanentemente? Esta acción no se puede deshacer.");
        builder.setPositiveButton("Eliminar", (dialog, which) -> {
            com.google.firebase.firestore.FirebaseFirestore.getInstance().collection("usuarios").document(userEmail).delete()
                .addOnSuccessListener(aVoid -> {
                    if (auth.getCurrentUser() != null) {
                        auth.getCurrentUser().delete().addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(getContext(), "Cuenta eliminada", Toast.LENGTH_SHORT).show();
                                getParentFragmentManager().beginTransaction()
                                        .replace(R.id.main_fragment_container, new LoginFragment())
                                        .commit();
                            } else {
                                Toast.makeText(getContext(), "Error eliminando autenticación, es posible que debas iniciar sesión nuevamente.", Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                });
        });
        builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss());
        builder.show();
    }
    
    private void uploadProfilePicture() {
        if (imageUri != null && auth.getCurrentUser() != null) {
            Toast.makeText(getContext(), "Subiendo imagen...", Toast.LENGTH_SHORT).show();
            String userEmail = auth.getCurrentUser().getEmail();
            if (userEmail == null) return;

            StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("profile_pictures/" + userEmail + ".jpg");
            storageRef.putFile(imageUri).addOnSuccessListener(taskSnapshot -> {
                storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    com.google.firebase.firestore.FirebaseFirestore.getInstance().collection("usuarios")
                            .document(userEmail)
                            .update("avatarUrl", uri.toString())
                            .addOnSuccessListener(aVoid -> Toast.makeText(getContext(), "Foto de perfil actualizada", Toast.LENGTH_SHORT).show());
                });
            }).addOnFailureListener(e -> {
                Toast.makeText(getContext(), "Error subiendo la imagen", Toast.LENGTH_SHORT).show();
            });
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
        
        if (layoutId == R.layout.fragment_ajustes_seguridad) {
            configurarSeguridad(view);
        } else if (layoutId == R.layout.fragment_ajustes_privacidad) {
            configurarPrivacidad(view);
        } else if (layoutId == R.layout.fragment_ajustes_notificaciones) {
            configurarNotificaciones(view);
        }
    }
    
    private void configurarPrivacidad(View view) {
        android.widget.Switch switchVisibilidadChat = view.findViewById(R.id.switchVisibilidadChat);
        android.widget.Switch switchCompartirDatos = view.findViewById(R.id.switchCompartirDatos);
        TextView btnTerminosPrivacidad = view.findViewById(R.id.btnTerminosPrivacidad);
        TextView btnExportarDatos = view.findViewById(R.id.btnExportarDatos);
        
        if (btnTerminosPrivacidad != null) {
            btnTerminosPrivacidad.setOnClickListener(v -> {
                android.content.Intent browserIntent = new android.content.Intent(android.content.Intent.ACTION_VIEW, Uri.parse("https://www.google.com")); // TODO: Enlace real
                startActivity(browserIntent);
            });
        }
        
        if (btnExportarDatos != null) {
            btnExportarDatos.setOnClickListener(v -> {
                android.content.Intent sendIntent = new android.content.Intent();
                sendIntent.setAction(android.content.Intent.ACTION_SEND);
                sendIntent.putExtra(android.content.Intent.EXTRA_TEXT, "Datos exportados: Usuario activo.");
                sendIntent.setType("text/plain");
                startActivity(android.content.Intent.createChooser(sendIntent, "Compartir datos vía"));
            });
        }
        
        // Simular guardado de preferencias en Firestore
        if (switchVisibilidadChat != null && auth.getCurrentUser() != null) {
            switchVisibilidadChat.setOnCheckedChangeListener((buttonView, isChecked) -> {
                com.google.firebase.firestore.FirebaseFirestore.getInstance().collection("usuarios")
                    .document(auth.getCurrentUser().getEmail()).update("visible_chat", isChecked);
            });
        }
    }
    
    private void configurarNotificaciones(View view) {
        android.widget.Switch switchNotifMenstruacion = view.findViewById(R.id.switchNotifMenstruacion);
        android.widget.Switch switchNotifConsejos = view.findViewById(R.id.switchNotifConsejos);
        android.widget.Switch switchNotifChat = view.findViewById(R.id.switchNotifChat);
        
        // La lógica de FCM irá aquí
        if (switchNotifMenstruacion != null) {
            switchNotifMenstruacion.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    Toast.makeText(getContext(), "Suscrito a alertas de periodo", Toast.LENGTH_SHORT).show();
                    // FirebaseMessaging.getInstance().subscribeToTopic("periodo");
                }
            });
        }
    }
    
    private void configurarSeguridad(View view) {
        TextView btnCambiarContrasena = view.findViewById(R.id.btnCambiarContrasena);
        android.widget.Switch switchBiometrico = view.findViewById(R.id.switchBiometrico);
        TextView btnGestionarPermisos = view.findViewById(R.id.btnGestionarPermisos);

        if (btnCambiarContrasena != null) {
            btnCambiarContrasena.setOnClickListener(v -> {
                FirebaseUser user = auth.getCurrentUser();
                if (user != null && user.getEmail() != null) {
                    auth.sendPasswordResetEmail(user.getEmail()).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(getContext(), "Correo para restablecer contraseña enviado.", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getContext(), "Error al enviar correo.", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
        }

        if (btnGestionarPermisos != null) {
            btnGestionarPermisos.setOnClickListener(v -> {
                android.content.Intent intent = new android.content.Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                intent.setData(Uri.parse("package:" + requireContext().getPackageName()));
                startActivity(intent);
            });
        }
        
        // La lógica de biometría requiere dependencias adicionales y configuración específica
        // por ahora dejamos el switch funcional visualmente
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