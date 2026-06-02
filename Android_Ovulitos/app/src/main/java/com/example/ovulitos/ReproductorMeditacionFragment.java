package com.example.ovulitos;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.airbnb.lottie.LottieAnimationView;
import com.google.firebase.storage.FirebaseStorage;

public class ReproductorMeditacionFragment extends Fragment {

    private ImageView btnPlay;
    private SeekBar seekBar;
    private TextView tvTime;
    private LottieAnimationView lottieFlor;

    private boolean isPlaying = false;
    private boolean isMediaPlayerPrepared = false;
    
    private Handler handler = new Handler(Looper.getMainLooper());
    private Runnable updateProgressAction;

    private MediaPlayer mediaPlayer;

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

        updateTimeText(0, 0);

        FirebaseStorage.getInstance().getReference("Meditacion.mp4").getDownloadUrl()
            .addOnSuccessListener(uri -> {
                try {
                    mediaPlayer = new MediaPlayer();
                    mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                    mediaPlayer.setDataSource(uri.toString());
                    mediaPlayer.prepareAsync();
                    mediaPlayer.setOnPreparedListener(mp -> {
                        isMediaPlayerPrepared = true;
                        mediaPlayer.setVolume(1.0f, 1.0f);
                        seekBar.setMax(mp.getDuration());
                        updateTimeText(0, mp.getDuration());
                    });
                    mediaPlayer.setOnCompletionListener(mp -> {
                        isPlaying = false;
                        btnPlay.setImageResource(R.drawable.ic_play_arrow);
                        seekBar.setProgress(0);
                        updateTimeText(0, mp.getDuration());
                        handler.removeCallbacks(updateProgressAction);
                    });
                } catch (Exception e) {
                    Log.e("Meditacion", "Error configurando MediaPlayer", e);
                }
            })
            .addOnFailureListener(e -> {
                Log.e("Meditacion", "Error descargando URL", e);
                if (getContext() != null) Toast.makeText(getContext(), "Error cargando audio desde la nube", Toast.LENGTH_SHORT).show();
            });

        updateProgressAction = new Runnable() {
            @Override
            public void run() {
                if (isPlaying && mediaPlayer != null && isMediaPlayerPrepared) {
                    int current = mediaPlayer.getCurrentPosition();
                    seekBar.setProgress(current);
                    updateTimeText(current, mediaPlayer.getDuration());
                    handler.postDelayed(this, 1000);
                }
            }
        };

        btnPlay.setOnClickListener(v -> {
            if (!isMediaPlayerPrepared || mediaPlayer == null) {
                Toast.makeText(getContext(), "Cargando audio, por favor espera...", Toast.LENGTH_SHORT).show();
                return;
            }

            if (isPlaying) {
                // Pausar
                isPlaying = false;
                btnPlay.setImageResource(R.drawable.ic_play_arrow);
                mediaPlayer.pause();
                handler.removeCallbacks(updateProgressAction);
            } else {
                // Reproducir
                isPlaying = true;
                btnPlay.setImageResource(R.drawable.ic_pause);
                mediaPlayer.start();
                handler.postDelayed(updateProgressAction, 1000);
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser && mediaPlayer != null && isMediaPlayerPrepared) {
                    mediaPlayer.seekTo(progress);
                    updateTimeText(progress, mediaPlayer.getDuration());
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
        
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}
