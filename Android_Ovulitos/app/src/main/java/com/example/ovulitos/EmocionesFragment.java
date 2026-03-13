package com.example.ovulitos;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class EmocionesFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_emociones, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //ejemplo
        ImageButton btnFeliz = view.findViewById(R.id.btnEmojiFeliz);

        btnFeliz.setOnClickListener(v -> {
            Toast.makeText(getContext(), "¡Me alegra que estés feliz!", Toast.LENGTH_SHORT).show();
            //aquí añadimos la lógica para meter la emoción en el tarro
        });
    }
}