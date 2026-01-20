package com.example.ovulitos; // Asegúrate de que esto coincide con tu paquete

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Temporizador de 2.5 segundos
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                // Aquí decimos: "Ve desde MainActivity hacia AuthActivity"
                Intent intent = new Intent(MainActivity.this, AuthActivity.class);
                startActivity(intent);

                // "Matamos" la MainActivity para que si le dan 'Atrás' no vuelvan al logo
                finish();

            }
        }, 2500);
    }
}