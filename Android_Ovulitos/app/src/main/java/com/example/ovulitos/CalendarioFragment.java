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
            this.dateParser = DateTimeFormatter.ofPattern("yyyy-MM-dd"); // Usamos ISO para las keys
            this.fechaSeleccionada = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        }

        loadFirebaseData(calendarView);

        btnKiki.setOnClickListener(v -> {
            isKikiActive = !isKikiActive;
            Toast.makeText(getContext(), isKikiActive ? "Modo Kiki activado" : "Modo Kiki desactivado", Toast.LENGTH_SHORT).show();
        });

        calendarView.setOnDayClickListener(eventDay -> {
            Calendar clickedDayCalendar = eventDay.getCalendar();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                int year = clickedDayCalendar.get(Calendar.YEAR);
                int month = clickedDayCalendar.get(Calendar.MONTH) + 1;
                int dayOfMonth = clickedDayCalendar.get(Calendar.DAY_OF_MONTH);
                LocalDate localDate = LocalDate.of(year, month, dayOfMonth);
                this.fechaSeleccionada = localDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                
                if (isKikiActive) {
                    saveKiki(localDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
                }
            }
        });

        btnGuardar.setOnClickListener(v -> {
            if (!fechaSeleccionada.isEmpty()) {
                int checked = flujo.getCheckedChipId();
                Chip chip = flujo.findViewById(checked);
                String intesidad = chip != null ? chip.getText().toString() : "Leve";
                
                // Guardar en el nuevo formato
                saveDetailedData(fechaSeleccionada, intesidad, "Cólicos");
            } else {
                Toast.makeText(getContext(), "Selecciona una fecha", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadFirebaseData(CalendarView calendarView) {
        if (GlobalVariables.email == null) return;

        // Limpiar eventos previos
        eventos.clear();

        // 1. Cargar Predicción desde el documento del usuario
        db.collection("usuarios").document(GlobalVariables.email).get().addOnSuccessListener(doc -> {
            if (doc.exists() && doc.contains("proximaReglaPrevista")) {
                String proxima = doc.getString("proximaReglaPrevista");
                if (proxima != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    LocalDate date = LocalDate.parse(proxima);
                    Calendar cal = Calendar.getInstance();
                    cal.set(date.getYear(), date.getMonthValue() - 1, date.getDayOfMonth());
                    eventos.add(new EventDay(cal, R.drawable.fondo_rojo, Color.WHITE));
                    calendarView.setEvents(eventos);
                }
            }
        });

        // 2. Cargar Datos de la colección Datos (Regla, etc)
        db.collection("usuarios").document(GlobalVariables.email).collection("Datos")
            .get().addOnSuccessListener(queryDocumentSnapshots -> {
                for (com.google.firebase.firestore.QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                    String id = doc.getId();
                    if (id.matches("\\d{4}-\\d{2}-\\d{2}")) {
                        LocalDate date = LocalDate.parse(id);
                        Calendar cal = Calendar.getInstance();
                        cal.set(date.getYear(), date.getMonthValue() - 1, date.getDayOfMonth());
                        
                        List<String> tipos = (List<String>) doc.get("tipos");
                        if (tipos != null && tipos.contains("period_start")) {
                            eventos.add(new EventDay(cal, R.drawable.fondo_rojo, Color.WHITE));
                        }
                    }
                }
                calendarView.setEvents(eventos);
            });

        // 3. Cargar KikiData de la nueva colección
        db.collection("usuarios").document(GlobalVariables.email).collection("kikiData")
            .get().addOnSuccessListener(queryDocumentSnapshots -> {
                for (com.google.firebase.firestore.QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                    String dateStr = doc.getId();
                    if (dateStr.matches("\\d{4}-\\d{2}-\\d{2}")) {
                        LocalDate date = LocalDate.parse(dateStr);
                        Calendar cal = Calendar.getInstance();
                        cal.set(date.getYear(), date.getMonthValue() - 1, date.getDayOfMonth());
                        eventos.add(new EventDay(cal, R.drawable.fondo_amarillo, Color.BLACK));
                    }
                }
                calendarView.setEvents(eventos);
            });
    }

    private void saveDetailedData(String fechaSelected, String intensidad, String sintomas) {
        // Guardar para compatibilidad app (datosCalendario)
        Map<String, Object> legacy = new HashMap<>();
        legacy.put("fechaSelected", fechaSelected);
        legacy.put("intensidad del sangrado", intensidad);
        legacy.put("Síntomas", sintomas);
        db.collection("usuarios").document(GlobalVariables.email).collection("Datos").document("datosCalendario").set(legacy);

        // Guardar en formato web (una entrada por día)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String dateKey = LocalDate.parse(fechaSelected, DateTimeFormatter.ofPattern("dd/MM/yyyy")).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            Map<String, Object> dayData = new HashMap<>();
            List<String> tipos = new ArrayList<>();
            tipos.add("period_start");
            dayData.put("tipos", tipos);
            dayData.put("fecha", dateKey);
            db.collection("usuarios").document(GlobalVariables.email).collection("Datos").document(dateKey).set(dayData);
        }
    }

    private void saveKiki(String dateKey) {
        Map<String, Object> kiki = new HashMap<>();
        kiki.put("fecha", dateKey);
        kiki.put("registradoEn", System.currentTimeMillis());
        db.collection("usuarios").document(GlobalVariables.email).collection("kikiData").document(dateKey).set(kiki);
    }

}