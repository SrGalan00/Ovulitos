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
import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;

import com.example.ovulitos.currentUser.UserData;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.firebase.Firebase;
import com.google.firebase.firestore.FirebaseFirestore;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

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

                    /*La única manera de de poder hacer que los datos pasen a la base de datos es mediante enviándolos
                    * según el usuario los marca*/
                    userDataStore(start, end);

        });

        dateRangePicker.show(getParentFragmentManager(), "DATE_PICKER");

        //botón guardar
        btnGuardar.setOnClickListener(v -> {
            Toast.makeText(getContext(), UserData.getUsuario(), Toast.LENGTH_SHORT).show();
        });
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