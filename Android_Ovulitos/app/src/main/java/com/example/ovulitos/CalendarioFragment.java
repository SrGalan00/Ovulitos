package com.example.ovulitos;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
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
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
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
            Toast.makeText(getContext(), isKikiActive ? "Modo Kiki activado" : "Modo Kiki desactivado",
                    Toast.LENGTH_SHORT).show();
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
        if (GlobalVariables.email == null)
            return;

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
                    eventos.add(new EventDay(cal, R.drawable.fondo_menstruacion, Color.WHITE));
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
                            String tipo = doc.getString("tipo");
                            boolean isPeriodStart = (tipos != null && tipos.contains("period_start"))
                                    || "period_start".equals(tipo);
                            if (isPeriodStart) {
                                eventos.add(new EventDay(cal, R.drawable.fondo_menstruacion, Color.WHITE));
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
                            eventos.add(new EventDay(cal, R.drawable.fondo_kiki, Color.BLACK));
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
        db.collection("usuarios").document(GlobalVariables.email).collection("Datos").document("datosCalendario")
                .set(legacy);

        String dateKey;
        String nowIso;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            dateKey = LocalDate.parse(fechaSelected, DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            nowIso = java.time.Instant.now().toString();
        } else {
            try {
                java.text.SimpleDateFormat inputSdf = new java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.US);
                java.text.SimpleDateFormat outputSdf = new java.text.SimpleDateFormat("yyyy-MM-dd",
                        java.util.Locale.US);
                java.util.Date date = inputSdf.parse(fechaSelected);
                dateKey = outputSdf.format(date);
            } catch (Exception e) {
                dateKey = fechaSelected; // fallback
            }
            java.text.SimpleDateFormat sdfIso = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
                    java.util.Locale.US);
            sdfIso.setTimeZone(java.util.TimeZone.getTimeZone("UTC"));
            nowIso = sdfIso.format(new java.util.Date());
        }

        Map<String, Object> dayData = new HashMap<>();
        List<String> tipos = new ArrayList<>();
        tipos.add("period_start");
        dayData.put("tipos", tipos);
        dayData.put("tipo", "period_start");
        dayData.put("fecha", dateKey);
        dayData.put("updatedAt", nowIso);

        db.collection("usuarios").document(GlobalVariables.email).collection("Datos").document(dateKey).set(dayData)
                .addOnSuccessListener(aVoid -> {
                    recalculatePredictions(GlobalVariables.email);
                    View fragmentView = getView();
                    if (fragmentView != null) {
                        CalendarView calendarView = fragmentView.findViewById(R.id.calendarViewRegistro);
                        if (calendarView != null) {
                            loadFirebaseData(calendarView);
                        }
                    }
                });
    }

    private void saveKiki(String dateKey) {
        Map<String, Object> kiki = new HashMap<>();
        kiki.put("fecha", dateKey);

        String nowIso;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            nowIso = java.time.Instant.now().toString();
        } else {
            java.text.SimpleDateFormat sdfIso = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
                    java.util.Locale.US);
            sdfIso.setTimeZone(java.util.TimeZone.getTimeZone("UTC"));
            nowIso = sdfIso.format(new java.util.Date());
        }
        kiki.put("registradoEn", nowIso);

        db.collection("usuarios").document(GlobalVariables.email).collection("kikiData").document(dateKey).set(kiki)
                .addOnSuccessListener(aVoid -> {
                    recalculatePredictions(GlobalVariables.email);
                    View fragmentView = getView();
                    if (fragmentView != null) {
                        CalendarView calendarView = fragmentView.findViewById(R.id.calendarViewRegistro);
                        if (calendarView != null) {
                            loadFirebaseData(calendarView);
                        }
                    }
                });
    }

    private void recalculatePredictions(String email) {
        if (email == null || email.isEmpty())
            return;

        db.collection("usuarios").document(email).collection("Datos").get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<String> startDates = new ArrayList<>();
                    for (com.google.firebase.firestore.QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        String id = doc.getId();
                        if (id.matches("\\d{4}-\\d{2}-\\d{2}")) {
                            List<String> tipos = (List<String>) doc.get("tipos");
                            String tipo = doc.getString("tipo");
                            boolean isPeriodStart = (tipos != null && tipos.contains("period_start"))
                                    || "period_start".equals(tipo);
                            if (isPeriodStart) {
                                startDates.add(id);
                            }
                        }
                    }

                    if (startDates.isEmpty())
                        return;

                    // Ordenar fechas cronológicamente
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        startDates.sort(String::compareTo);
                    } else {
                        java.util.Collections.sort(startDates);
                    }

                    int avgCycle = 28; // Por defecto
                    if (startDates.size() >= 2 && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        List<Long> intervals = new ArrayList<>();
                        for (int i = 1; i < startDates.size(); i++) {
                            LocalDate d1 = LocalDate.parse(startDates.get(i - 1));
                            LocalDate d2 = LocalDate.parse(startDates.get(i));
                            long diff = java.time.temporal.ChronoUnit.DAYS.between(d1, d2);
                            if (diff >= 15 && diff <= 45) {
                                intervals.add(diff);
                            }
                        }
                        if (!intervals.isEmpty()) {
                            long sum = 0;
                            for (long val : intervals)
                                sum += val;
                            avgCycle = Math.round((float) sum / intervals.size());
                        }
                    }

                    // Predecir las dos siguientes
                    String proximaReglaStr;
                    String segundaReglaStr;
                    String nowIso;

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        LocalDate lastStart = LocalDate.parse(startDates.get(startDates.size() - 1));
                        LocalDate p1 = lastStart.plusDays(avgCycle);
                        LocalDate p2 = p1.plusDays(avgCycle);
                        proximaReglaStr = p1.toString();
                        segundaReglaStr = p2.toString();
                        nowIso = java.time.Instant.now().toString();

                        // Actualizar variable global inmediatamente
                        LocalDate hoy = LocalDate.now();
                        GlobalVariables.diasProximaRegla = (int) Math.max(0,
                                java.time.temporal.ChronoUnit.DAYS.between(hoy, p1));
                    } else {
                        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd",
                                java.util.Locale.US);
                        try {
                            java.util.Date lastDate = sdf.parse(startDates.get(startDates.size() - 1));
                            Calendar cal = Calendar.getInstance();
                            cal.setTime(lastDate);
                            cal.add(Calendar.DAY_OF_YEAR, avgCycle);
                            proximaReglaStr = sdf.format(cal.getTime());
                            long p1Time = cal.getTimeInMillis();
                            cal.add(Calendar.DAY_OF_YEAR, avgCycle);
                            segundaReglaStr = sdf.format(cal.getTime());

                            java.text.SimpleDateFormat sdfIso = new java.text.SimpleDateFormat(
                                    "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", java.util.Locale.US);
                            sdfIso.setTimeZone(java.util.TimeZone.getTimeZone("UTC"));
                            nowIso = sdfIso.format(new java.util.Date());

                            long diff = p1Time - System.currentTimeMillis();
                            GlobalVariables.diasProximaRegla = (int) Math.max(0, diff / (24 * 60 * 60 * 1000));
                        } catch (Exception e) {
                            return;
                        }
                    }

                    // Guardar en el perfil de usuario (usuarios/{email})
                    Map<String, Object> userUpdates = new HashMap<>();
                    userUpdates.put("cicloMedio", avgCycle);
                    userUpdates.put("proximaReglaPrevista", proximaReglaStr);
                    userUpdates.put("segundaReglaPrevista", segundaReglaStr);
                    userUpdates.put("ultimaActualización", nowIso);

                    db.collection("usuarios").document(email)
                            .set(userUpdates, com.google.firebase.firestore.SetOptions.merge())
                            .addOnSuccessListener(aVoid -> {
                                Log.d("FIRESTORE", "Predicciones actualizadas con éxito tras cambio en Datos");
                                // Recargar el calendario si la vista está activa para pintar el nuevo día de
                                // regla prevista
                                View fragmentView = getView();
                                if (fragmentView != null) {
                                    CalendarView calendarView = fragmentView.findViewById(R.id.calendarViewRegistro);
                                    if (calendarView != null) {
                                        loadFirebaseData(calendarView);
                                    }
                                }
                            });
                });
    }

}