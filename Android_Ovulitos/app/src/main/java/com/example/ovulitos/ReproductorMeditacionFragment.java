package com.example.ovulitos;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.airbnb.lottie.LottieAnimationView;

public class ReproductorMeditacionFragment extends Fragment {

    private ImageView btnPlay;
    private SeekBar seekBar;
    private TextView tvTime;
    private LottieAnimationView lottieFlor;

    private boolean isPlaying = false;
    
    // Variables de simulación ya que el audio real aún no está insertado
    private int simulatedCurrentPosition = 0; // ms
    private int simulatedDuration = 4 * 60 * 1000 + 10 * 1000; // 4:10
    
    private Handler handler = new Handler(Looper.getMainLooper());
    private Runnable updateProgressAction;

    // TODO: Descomentar esto cuando tengas tu archivo de audio
    // private MediaPlayer mediaPlayer;

    public ReproductorMeditacionFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_reproductor_meditacion, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        btnPlay = view.findViewById(R.id.btnPlay);
        seekBar = view.findViewById(R.id.seekBar);
        tvTime = view.findViewById(R.id.tvTime);
        lottieFlor = view.findViewById(R.id.ivFlor);

        // TODO: Inicializa el MediaPlayer real aquí, por ejemplo:
        // mediaPlayer = MediaPlayer.create(requireContext(), R.raw.tu_audio_aqui);
        // int duration = mediaPlayer.getDuration();
        // seekBar.setMax(duration);
        // updateTimeText(0, duration);
        
        // --- SIMULACIÓN VISUAL (Borrar cuando uses MediaPlayer real) ---
        seekBar.setMax(simulatedDuration);
        updateTimeText(0, simulatedDuration);
        // -------------------------------------------------------------

        updateProgressAction = new Runnable() {
            @Override
            public void run() {
                if (isPlaying) {
                    // TODO: Usar el valor real con: int current = mediaPlayer.getCurrentPosition();
                    // --- SIMULACIÓN ---
                    simulatedCurrentPosition += 1000; 
                    if (simulatedCurrentPosition > simulatedDuration) {
                        simulatedCurrentPosition = 0;
                        isPlaying = false;
                        btnPlay.setImageResource(R.drawable.ic_play_arrow);
                    }
                    int current = simulatedCurrentPosition;
                    // ------------------
                    
                    seekBar.setProgress(current);
                    
                    // TODO: Cambiar simulatedDuration por mediaPlayer.getDuration()
                    updateTimeText(current, simulatedDuration);
                    
                    if (isPlaying) {
                        handler.postDelayed(this, 1000);
                    }
                }
            }
        };

        btnPlay.setOnClickListener(v -> {
            if (isPlaying) {
                // Pausar
                isPlaying = false;
                btnPlay.setImageResource(R.drawable.ic_play_arrow);
                handler.removeCallbacks(updateProgressAction);
                
                // TODO: mediaPlayer.pause();
            } else {
                // Reproducir
                isPlaying = true;
                btnPlay.setImageResource(R.drawable.ic_pause);
                
                // TODO: mediaPlayer.start();
                
                handler.postDelayed(updateProgressAction, 1000);
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    // TODO: mediaPlayer.seekTo(progress);
                    
                    // --- SIMULACIÓN ---
                    simulatedCurrentPosition = progress;
                    // ------------------
                    
                    updateTimeText(progress, simulatedDuration);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }

    private void updateTimeText(int currentPosition, int totalDuration) {
        String currentTimeString = formatTime(currentPosition);
        String totalTimeString = formatTime(totalDuration);
        tvTime.setText(String.format("%s / %s", currentTimeString, totalTimeString));
    }

    private String formatTime(int ms) {
        int minutes = (ms / 1000) / 60;
        int seconds = (ms / 1000) % 60;
        return String.format("%d:%02d", minutes, seconds);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        handler.removeCallbacks(updateProgressAction);
        
        // TODO: Libera recursos del MediaPlayer
        // if (mediaPlayer != null) {
        //     mediaPlayer.release();
        //     mediaPlayer = null;
        // }
    }
}
