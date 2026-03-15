package com.example.ovulitos;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.Calendar;

public class ConfiguracionCalendarioFragment extends Fragment {

    // Variable para guardar la fecha y usarla luego en otros fragmentos
    private String fechaSeleccionadaGlobal = "";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_configuracion_calendario, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

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
            }
        });
    }

    // método para formatear la fecha (MM/DD/YYYY)
    private void actualizarTextoFecha(TextView tv, int dia, int mes, int anio) {
        // formateamos con ceros a la izquierda si es necesario
        String mesFormateado = String.format("%02d", (mes + 1));
        String diaFormateado = String.format("%02d", dia);

        fechaSeleccionadaGlobal = mesFormateado + "/" + diaFormateado + "/" + anio;
        tv.setText(fechaSeleccionadaGlobal); // esto actualiza la fecha superior
    }
}