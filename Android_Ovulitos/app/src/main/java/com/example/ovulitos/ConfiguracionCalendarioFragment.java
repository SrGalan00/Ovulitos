package com.example.ovulitos;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;

import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.firebase.firestore.FirebaseFirestore;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import android.os.Build;
import com.example.ovulitos.global.GlobalVariables;

public class ConfiguracionCalendarioFragment extends Fragment {

    // Variable para guardar la fecha y usarla luego en otros fragmentos
    private String fechaSeleccionadaGlobal = "";

    // Dato que hemos pasado de una pantalla a otra
    private String email;

    // Firebase Firestore
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_configuracion_calendario, container, false);


    }
    /*
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Datos pasados de una pantalla a otra
        if(getArguments() != null) {
            System.out.println("Esto se ejecuta");
            this.email = getArguments().getString("email");
        }
    }*/

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Datos pasados de una pantalla a otra
        if(getArguments() != null) {
            System.out.println("Esto se ejecuta");
            this.email = getArguments().getString("email");
        }

        System.out.println("Hola");
        CalendarView calendarView = view.findViewById(R.id.calendarConfig);
        TextView tvFechaMostrar = view.findViewById(R.id.tvFechaSeleccionada);
        Button btnOk = view.findViewById(R.id.btnOk);

        // establece una fecha inicial por defecto
        Calendar hoy = Calendar.getInstance();
        actualizarTextoFecha(tvFechaMostrar, hoy.get(Calendar.DAY_OF_MONTH), hoy.get(Calendar.MONTH), hoy.get(Calendar.YEAR));

        // escuchar los clics del usuario en el calendario
        calendarView.setOnDateChangeListener((viewCalendar, year, month, dayOfMonth) -> {
            //el calendario de Android cuenta los meses de 0 a 11, por eso sumamos 1
            actualizarTextoFecha(tvFechaMostrar, dayOfMonth, month, year);
        });

        // acción al pulsar OK
        btnOk.setOnClickListener(v -> {
            if (!fechaSeleccionadaGlobal.isEmpty()) {

                // ============= Pasar los datos a la siguiente pantalla (Pantalla de inicio) =================
                InicioFragment inicioFragment = new InicioFragment();
                Bundle bundle = new Bundle();
                bundle.putString("email", email);
                inicioFragment.setArguments(bundle);

                getParentFragmentManager().beginTransaction()
                        .replace(R.id.main_fragment_container, inicioFragment)
                        .addToBackStack(null)
                        .commit();
                // =============================================================================================
            }
        });



        // Registrar los últimos periodos
        // Toda esta es la lógica que debe de estar en el calendario del registro

        MaterialDatePicker<Pair<Long, Long>> dateRangePicker =
                MaterialDatePicker.Builder.dateRangePicker()
                        .setTitleText("Selecciona las fechas")
                        .build();
                dateRangePicker.addOnPositiveButtonClickListener(selection -> {
                    if (selection != null && selection.first != null && selection.second != null) {
                        saveOnboardingDataToFirestore(selection.first, selection.second);
                    }
                });

        dateRangePicker.show(getParentFragmentManager(), "DATE_PICKER");


    }

    // método para formatear la fecha (MM/DD/YYYY)
    private void actualizarTextoFecha(TextView tv, int dia, int mes, int anio) {
        // formateamos con ceros a la izquierda si es necesario
        String mesFormateado = String.format("%02d", (mes + 1));
        String diaFormateado = String.format("%02d", dia);

        fechaSeleccionadaGlobal = mesFormateado + "/" + diaFormateado + "/" + anio;
        tv.setText(fechaSeleccionadaGlobal); // esto actualiza la fecha superior
    }


    private void saveOnboardingDataToFirestore(long startMillis, long endMillis) {
        if (email == null || email.isEmpty()) {
            Toast.makeText(getContext(), "Error: No se encontró el email del usuario", Toast.LENGTH_SHORT).show();
            return;
        }

        String startKey;
        String endKey;
        String proximaReglaStr;
        String segundaReglaStr;
        String nowIso;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            java.time.LocalDate startDate = Instant.ofEpochMilli(startMillis).atZone(ZoneId.of("UTC")).toLocalDate();
            java.time.LocalDate endDate = Instant.ofEpochMilli(endMillis).atZone(ZoneId.of("UTC")).toLocalDate();

            startKey = startDate.toString(); // yyyy-MM-dd
            endKey = endDate.toString(); // yyyy-MM-dd

            java.time.LocalDate proximaRegla = startDate.plusDays(28);
            java.time.LocalDate segundaRegla = proximaRegla.plusDays(28);

            proximaReglaStr = proximaRegla.toString();
            segundaReglaStr = segundaRegla.toString();
            nowIso = java.time.Instant.now().toString();

            // Calcular y establecer variable global inmediatamente
            java.time.LocalDate hoy = java.time.LocalDate.now();
            GlobalVariables.diasProximaRegla = (int) Math.max(0, java.time.temporal.ChronoUnit.DAYS.between(hoy, proximaRegla));
        } else {
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US);
            sdf.setTimeZone(java.util.TimeZone.getTimeZone("UTC"));
            startKey = sdf.format(new java.util.Date(startMillis));
            endKey = sdf.format(new java.util.Date(endMillis));

            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(startMillis);
            cal.add(Calendar.DAY_OF_YEAR, 28);
            proximaReglaStr = sdf.format(cal.getTime());
            cal.add(Calendar.DAY_OF_YEAR, 28);
            segundaReglaStr = sdf.format(cal.getTime());

            java.text.SimpleDateFormat sdfIso = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", java.util.Locale.US);
            sdfIso.setTimeZone(java.util.TimeZone.getTimeZone("UTC"));
            nowIso = sdfIso.format(new java.util.Date());

            // Variable global aproximada
            long diff = (startMillis + (28L * 24 * 60 * 60 * 1000)) - System.currentTimeMillis();
            GlobalVariables.diasProximaRegla = (int) Math.max(0, diff / (24 * 60 * 60 * 1000));
        }

        // 1. Guardar Inicio del Periodo
        Map<String, Object> startData = new HashMap<>();
        startData.put("fecha", startKey);
        startData.put("tipo", "period_start");
        java.util.List<String> startTipos = new java.util.ArrayList<>();
        startTipos.add("period_start");
        startData.put("tipos", startTipos);
        startData.put("updatedAt", nowIso);

        db.collection("usuarios").document(email).collection("Datos").document(startKey).set(startData);

        // 2. Guardar Fin del Periodo (si es distinto al inicio)
        if (!startKey.equals(endKey)) {
            Map<String, Object> endData = new HashMap<>();
            endData.put("fecha", endKey);
            endData.put("tipo", "period_end");
            java.util.List<String> endTipos = new java.util.ArrayList<>();
            endTipos.add("period_end");
            endData.put("tipos", endTipos);
            endData.put("updatedAt", nowIso);

            db.collection("usuarios").document(email).collection("Datos").document(endKey).set(endData);
        }

        // 3. Guardar Datos de predicción en el perfil de usuario (usuarios/{email})
        Map<String, Object> userUpdates = new HashMap<>();
        userUpdates.put("cicloMedio", 28);
        userUpdates.put("proximaReglaPrevista", proximaReglaStr);
        userUpdates.put("segundaReglaPrevista", segundaReglaStr);
        userUpdates.put("ultimaActualización", nowIso);

        db.collection("usuarios").document(email).set(userUpdates, com.google.firebase.firestore.SetOptions.merge())
                .addOnSuccessListener(aVoid -> Log.d("FIRESTORE", "Datos de onboarding guardados con éxito"))
                .addOnFailureListener(e -> Log.e("FIRESTORE", "Error al guardar datos de onboarding", e));
    }


}