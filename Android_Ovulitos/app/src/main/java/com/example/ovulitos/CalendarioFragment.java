package com.example.ovulitos;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.firestore.FirebaseFirestore;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class CalendarioFragment extends Fragment {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String fechaSeleccionada = "";
    private DateTimeFormatter dateParser;

    private ChipGroup flujo;
    private ChipGroup sintomas;
    private String selectedOptions1;
    private String selectedOptions2;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_calendario, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        CalendarView calendarView = view.findViewById(R.id.calendarViewRegistro);
        Button btnGuardar = view.findViewById(R.id.btnGuardarRegistro);
        ChipGroup flujo = view.findViewById(R.id.chipGroupFlujo);
        ChipGroup sintomas = view.findViewById(R.id.chipGroupSintomas);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            this.dateParser = DateTimeFormatter.ofPattern("dd/MMMM/yyyy");
            this.fechaSeleccionada = Instant.ofEpochMilli(calendarView.getDate())
                    .atZone(ZoneId.systemDefault())
                    .format(dateParser);
        }

        calendarView.setOnDateChangeListener((view1, year, month, dayOfMonth) -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                LocalDate localDate = LocalDate.of(year, month + 1, dayOfMonth);
                DateTimeFormatter dateParser = DateTimeFormatter.ofPattern("dd/MMMM/yyyy");
                this.fechaSeleccionada = localDate.format(dateParser);

                // Con esto pretendo cambiar la fecha seleccionada dentro del calendario, pero se rompe todo el rato
                //calendarView.setDate(Long.parseLong(this.fechaSeleccionada));
                //calendarView.setDate(Long.parseLong(this.fechaSeleccionada), true, true);
            }
        });

        btnGuardar.setOnClickListener(v -> {


            if (!fechaSeleccionada.isEmpty()) {
                int checked = flujo.getCheckedChipId();
                Chip chip = flujo.findViewById(checked);

                /* Esta parte da error
                int checked2 = sintomas.getCheckedChipId();
                Chip chip2 = sintomas.findViewById(checked2);
                */
                userData(fechaSeleccionada, chip.getText().toString(), "Colicos" );
                Toast.makeText(getContext(), "Guardando: " + fechaSeleccionada, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "Por favor selecciona una fecha", Toast.LENGTH_SHORT).show();
            }
        });





    }

    private void userData(String fechaSelected, String option1, String option2){
        Map<String, Object> user = new HashMap<>();
        user.put("fechaSelected", fechaSelected);
        user.put("intensidad del sangrado", option1);
        user.put("Síntomas", option2);

        db.collection("usuarios").document("andreaorpez@gmail.com").collection("Datos")
                .document("datosCalendario").set(user)

                .addOnFailureListener(e ->
                        Toast.makeText(getContext(), "Problema cargando los datos del usuario!", Toast.LENGTH_SHORT).show()
                );
    }

}