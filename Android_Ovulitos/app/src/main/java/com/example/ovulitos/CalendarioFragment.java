package com.example.ovulitos;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.ovulitos.currentUser.UserData;
import com.google.firebase.Firebase;
import com.google.firebase.firestore.FirebaseFirestore;

public class CalendarioFragment extends Fragment {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

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

        //escuchar cuando el usuario cambia de fecha
        calendarView.setOnDateChangeListener((view1, year, month, dayOfMonth) -> {
            String fechaSeleccionada = dayOfMonth + "/" + (month + 1) + "/" + year;
        });

        //botón guardar
        btnGuardar.setOnClickListener(v -> {
            //aquí iría el código para guardar en la bbdd
            Toast.makeText(getContext(), UserData.getUsuario(), Toast.LENGTH_SHORT).show();
            //Toast.makeText(getContext(), "Síntomas registrados con éxito", Toast.LENGTH_SHORT).show();
        });
    }
}