package com.example.ovulitos;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import java.util.Random;

public class EmocionesFragment extends Fragment {

    private FrameLayout containerTarro;
    private Random random = new Random();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_emociones, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        containerTarro = view.findViewById(R.id.containerTarro);

        ImageButton btnFeliz = view.findViewById(R.id.btnEmojiFeliz);
        ImageButton btnTriste = view.findViewById(R.id.btnEmojiTriste);
        ImageButton btnEnojo = view.findViewById(R.id.btnEmojiEnojo);
        ImageButton btnSorpresa = view.findViewById(R.id.btnEmojiSorpresa);
        ImageButton btnCalma = view.findViewById(R.id.btnEmojiCalma);

        btnFeliz.setOnClickListener(v -> agregarEmocionAlTarro(R.drawable.alegria));
        btnTriste.setOnClickListener(v -> agregarEmocionAlTarro(R.drawable.tristeza));
        btnEnojo.setOnClickListener(v -> agregarEmocionAlTarro(R.drawable.ira));
        btnSorpresa.setOnClickListener(v -> agregarEmocionAlTarro(R.drawable.miedo));
        btnCalma.setOnClickListener(v -> agregarEmocionAlTarro(R.drawable.serenidad));
    }

    private void agregarEmocionAlTarro(int drawableId) {
        ImageView emoji = new ImageView(getContext());
        emoji.setImageResource(drawableId);

        // tamaño del emoji
        int size = (int) (40 * getResources().getDisplayMetrics().density);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(size, size);

        // obtenemos el tamaño del tarro
        int w = containerTarro.getWidth();
        int h = containerTarro.getHeight();

        // ZONA SEGURA: solo el centro del tarro para que no floten fuera
        // reducimos el ancho de caída para que se queden dentro del cristal
        int anchoSeguro = (int) (w * 0.6);
        int altoSeguro = (int) (h * 0.5);  // solo la mitad inferior/central

        if (w > 0 && h > 0) {
            // calculamos el margen izquierdo para que el anchoSeguro esté centrado
            int inicioX = (w - anchoSeguro) / 2;
            int inicioY = h / 3; // empezamos a mitad del bote, debajo de la tapa

            params.leftMargin = inicioX + random.nextInt(anchoSeguro - size);
            params.topMargin = inicioY + random.nextInt(altoSeguro - size);
        }

        emoji.setLayoutParams(params);
        containerTarro.addView(emoji);

        // animación de entrada
        emoji.setAlpha(0f);
        emoji.setScaleX(0f);
        emoji.setScaleY(0f);
        emoji.animate().alpha(1f).scaleX(1f).scaleY(1f).setDuration(400).start();
    }
}