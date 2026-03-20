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
                // aquí navegarías al InicioFragment
                getParentFragmentManager().beginTransaction()
                        .replace(R.id.main_fragment_container, new InicioFragment())
                        .addToBackStack(null)
                        .commit();

                //Toast.makeText(getContext(), this.email, Toast.LENGTH_SHORT).show();
            }
        });



        // Registrar los últimos periodos
        // Toda esta es la lógica que debe de estar en el calendario del registro

        MaterialDatePicker<Pair<Long, Long>> dateRangePicker =
                MaterialDatePicker.Builder.dateRangePicker()
                        .setTitleText("Selecciona las fechas")
                        .build();
                dateRangePicker.addOnPositiveButtonClickListener(selection -> {

                    String start = "";
                    String end = "";


                    DateTimeFormatter dateParser = null;
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                        dateParser = DateTimeFormatter.ofPattern("dd/MMMM/yyyy");
                    }

                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                        start = Instant.ofEpochMilli(selection.first).atZone(ZoneId.of("UTC"))
                                .format(dateParser);
                    }

                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                        end = Instant.ofEpochMilli(selection.second).atZone(ZoneId.of("UTC"))
                                .format(dateParser);
                    }

                    //La única manera de de poder hacer que los datos pasen a la base de datos es mediante enviándolos
                    // según el usuario los marca
                    userDataStore(start, end);

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


    private void userDataStore(String  start, String end){
        Map<String, Object> userData = new HashMap<>();
        userData.put("startPeriod", start);
        userData.put("endPeriod", end);

        db.collection("usuarios").document("andreaorpez@gmail.com").collection("Datos")
                .document("fechas").set(userData)
                .addOnSuccessListener(aVoid ->
                        Log.d("FIRESTORE", "Datos de usuario guardados correctamente")
                )
                .addOnFailureListener(e ->
                        Log.e("FIRESTORE", "Error guardando datos: " + e.getMessage())
                );
    }


}