package com.example.ovulitos;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.graphics.Color;
import android.widget.Button;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.ovulitos.global.GlobalVariables;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.firestore.FirebaseFirestore;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import com.applandeo.materialcalendarview.CalendarView;
import com.applandeo.materialcalendarview.EventDay;

public class CalendarioFragment extends Fragment {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String fechaSeleccionada = "";
    private DateTimeFormatter dateParser;

    private ChipGroup flujo;
    private ChipGroup sintomas;
    private String selectedOptions1;
    private String selectedOptions2;
    private boolean isKikiActive = false;
    private List<EventDay> eventos = new ArrayList<>();
    private List<String> kikiDates = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_calendario, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // ================ Variables (NO TOCAR) =============================

        CalendarView calendarView = view.findViewById(R.id.calendarViewRegistro);
        Button btnGuardar = view.findViewById(R.id.btnGuardarRegistro);
        Button btnKiki = view.findViewById(R.id.btnKiki);
        ChipGroup flujo = view.findViewById(R.id.chipGroupFlujo);
        ChipGroup sintomas = view.findViewById(R.id.chipGroupSintomas);

        // ====================================================================


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            this.dateParser = DateTimeFormatter.ofPattern("dd/MMMM/yyyy");
            this.fechaSeleccionada = Instant.ofEpochMilli(System.currentTimeMillis())
                    .atZone(ZoneId.systemDefault())
                    .format(dateParser);
        }

       // eventos.clear();
        
        Calendar dayRegla = Calendar.getInstance();
        dayRegla.add(Calendar.DAY_OF_YEAR, GlobalVariables.diasProximaRegla);

        eventos.add(new EventDay((Calendar) dayRegla.clone(), R.drawable.fondo_rojo, Color.WHITE));

        Calendar rosaAntes2 = (Calendar) dayRegla.clone(); rosaAntes2.add(Calendar.DAY_OF_YEAR, -2);
        Calendar rosaAntes1 = (Calendar) dayRegla.clone(); rosaAntes1.add(Calendar.DAY_OF_YEAR, -1);
        Calendar rosaDespues1 = (Calendar) dayRegla.clone(); rosaDespues1.add(Calendar.DAY_OF_YEAR, 1);
        Calendar rosaDespues2 = (Calendar) dayRegla.clone(); rosaDespues2.add(Calendar.DAY_OF_YEAR, 2);

        eventos.add(new EventDay(rosaAntes2, R.drawable.fondo_rosa, Color.WHITE));
        eventos.add(new EventDay(rosaAntes1, R.drawable.fondo_rosa, Color.WHITE));
        eventos.add(new EventDay(rosaDespues1, R.drawable.fondo_rosa, Color.WHITE));
        eventos.add(new EventDay(rosaDespues2, R.drawable.fondo_rosa, Color.WHITE));

        calendarView.setEvents(eventos);

        btnKiki.setOnClickListener(v -> {
            isKikiActive = !isKikiActive;
            if (isKikiActive) {
                Toast.makeText(getContext(), "Modo Kiki activado. Selecciona un día.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "Modo Kiki desactivado.", Toast.LENGTH_SHORT).show();
            }
        });

        calendarView.setOnDayClickListener(eventDay -> {
            Calendar clickedDayCalendar = eventDay.getCalendar();

            if (isKikiActive) {
                eventos.add(new EventDay((Calendar) clickedDayCalendar.clone(), R.drawable.fondo_amarillo, Color.BLACK));
                calendarView.setEvents(eventos);
                
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    int year = clickedDayCalendar.get(Calendar.YEAR);
                    int month = clickedDayCalendar.get(Calendar.MONTH) + 1;
                    int dayOfMonth = clickedDayCalendar.get(Calendar.DAY_OF_MONTH);
                    LocalDate localDate = LocalDate.of(year, month, dayOfMonth);
                    kikiDates.add(localDate.format(dateParser));
                }
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                int year = clickedDayCalendar.get(Calendar.YEAR);
                int month = clickedDayCalendar.get(Calendar.MONTH) + 1;
                int dayOfMonth = clickedDayCalendar.get(Calendar.DAY_OF_MONTH);
                LocalDate localDate = LocalDate.of(year, month, dayOfMonth);
                this.fechaSeleccionada = localDate.format(dateParser);
            }
        });



        
        btnGuardar.setOnClickListener(v -> {


            if (!fechaSeleccionada.isEmpty()) {
                int checked = flujo.getCheckedChipId();
                Chip chip = flujo.findViewById(checked);


                userKiki(this.kikiDates); 
                userData(fechaSeleccionada, chip.getText().toString(), "Colicos" );
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

        db.collection("usuarios").document(GlobalVariables.email).collection("Datos")
                .document("datosCalendario").set(user)

                .addOnFailureListener(e ->
                        Toast.makeText(getContext(), "Problema cargando los datos del usuario!", Toast.LENGTH_SHORT).show()
                );
    }

    private void userKiki(List<String> fechasKiki){
        Map<String, Object> user = new HashMap<>();
        user.put("diasKikiSeleccionados", fechasKiki);

        db.collection("usuarios").document(GlobalVariables.email).collection("Datos")
                .document("kikiData").set(user)

                .addOnFailureListener(e ->
                        Toast.makeText(getContext(), "Problema cargando los datos del usuario!", Toast.LENGTH_SHORT).show()
                );
    }

}