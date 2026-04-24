package com.example.ovulitos;

import android.os.Bundle;
import android.os.SystemClock;
import android.view.Choreographer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class EmocionesFragment extends Fragment implements Choreographer.FrameCallback {

    private final Random generadorAleatorios = new Random();
    private FrameLayout contenedorBote;
    private final List<EmocionFisica> listaEmociones = new ArrayList<>();
    private boolean animacionFisicaActiva = false;
    private long tiempoUltimoFotograma = 0;

    //límite máximo de emociones que caben en el frasco antes de avisar al usuario
    private static final int LIMITE_EMOCIONES = 25;

    //esta clase representa las propiedades de cada "bolita" de emoción que cae
    private static class EmocionFisica {
        ImageView vista; //la imagen en la pantalla
        float posicionX, posicionY; //dónde está
        float velX, velY; //hacia dónde y a qué velocidad se mueve
        float radio; //su tamaño

        EmocionFisica(ImageView vista, float x, float y, float radio) {
            this.vista = vista;
            this.posicionX = x;
            this.posicionY = y;
            this.radio = radio;
        }
    }

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

<<<<<<< HEAD
        contenedorBote = view.findViewById(R.id.flTarroContenido);

        view.findViewById(R.id.btnEmojiFeliz).setOnClickListener(v -> agregarEmocionBola(R.drawable.alegria));
        view.findViewById(R.id.btnEmojiTriste).setOnClickListener(v -> agregarEmocionBola(R.drawable.tristeza));
        view.findViewById(R.id.btnEmojiEnojo).setOnClickListener(v -> agregarEmocionBola(R.drawable.ira));
        view.findViewById(R.id.btnEmojiSorpresa).setOnClickListener(v -> agregarEmocionBola(R.drawable.miedo));
        view.findViewById(R.id.btnEmojiCalma).setOnClickListener(v -> agregarEmocionBola(R.drawable.serenidad));
    }

    private void agregarEmocionBola(int drawableResId) {
        if (listaEmociones.size() >= LIMITE_EMOCIONES) {
            mostrarDialogoLimite();
            return;
        }

        if (contenedorBote == null || contenedorBote.getWidth() == 0) return;

        ImageView nuevaEmocion = new ImageView(getContext());
        nuevaEmocion.setImageResource(drawableResId);
        
        //calcular el tamaño pasándolo a píxeles
        int tamanoPixeles = (int) (55 * getResources().getDisplayMetrics().density);
        float radio = tamanoPixeles / 2f;
        
        FrameLayout.LayoutParams parametros = new FrameLayout.LayoutParams(tamanoPixeles, tamanoPixeles);
        nuevaEmocion.setLayoutParams(parametros);
        contenedorBote.addView(nuevaEmocion);

        //posición de inicio: arriba del todo, pero con un desplazamiento horizontal aleatorio
        float inicioX = radio + generadorAleatorios.nextFloat() * (contenedorBote.getWidth() - tamanoPixeles);
        float inicioY = radio; 

        EmocionFisica nuevaBola = new EmocionFisica(nuevaEmocion, inicioX, inicioY, radio);
        //le damos un pequeño empujón lateral aleatorio para que no caigan siempre rectas y aburridas
        nuevaBola.velX = (generadorAleatorios.nextFloat() - 0.5f) * 1000f; 
        nuevaBola.velY = 200f; //empuje suave hacia abajo

        listaEmociones.add(nuevaBola);

        //si el motor de físicas estaba parado (porque ya no se movía nada), lo despertamos
        if (!animacionFisicaActiva) {
            animacionFisicaActiva = true;
            tiempoUltimoFotograma = SystemClock.uptimeMillis();
            Choreographer.getInstance().postFrameCallback(this);
        }
    }

    //este es el "corazón" de las físicas. Se ejecuta automáticamente cada vez que la pantalla dibuja un fotograma (unas 60 veces por segundo)
    @Override
    public void doFrame(long frameTimeNanos) {
        if (!animacionFisicaActiva || getView() == null || contenedorBote == null) return;

        long tiempoActual = SystemClock.uptimeMillis();
        //calculamos cuánto tiempo ha pasado desde el dibujo anterior (limitado a 0.03 seg. máximo para que no se descontrole si el móvil va lento)
        float pasoDeTiempo = Math.min((tiempoActual - tiempoUltimoFotograma) / 1000f, 0.03f); 
        tiempoUltimoFotograma = tiempoActual;

        int anchoBote = contenedorBote.getWidth();
        int altoBote = contenedorBote.getHeight();
        
        //---- fORMA INVISIBLE DEL BOTE ----
        //aquí dibujamos líneas invisibles para que las emociones no se salgan.
        //si la imagen de tu bote tiene el cuello más estrecho, cambia estos valores. (0.15f significa el 15% del tamaño).
        float margenCuelloAncho = anchoBote * 0.15f; 
        float margenCuelloAlto = altoBote * 0.10f;
        float margenEsquinaInfAncho = anchoBote * 0.28f; 
        float margenEsquinaInfAlto = altoBote * 0.15f;

        //cada 4 números representan una línea: [InicioX, InicioY, FinX, FinY]
        float[] paredesBote = {
                //suelo central (completamente horizontal)
                margenEsquinaInfAncho, altoBote, anchoBote - margenEsquinaInfAncho, altoBote, 
                //tope diagonal inferior izquierdo (para que rueden y no se queden trabadas en las esquinas)
                0, altoBote - margenEsquinaInfAlto, margenEsquinaInfAncho, altoBote, 
                //tope diagonal inferior derecho
                anchoBote, altoBote - margenEsquinaInfAlto, anchoBote - margenEsquinaInfAncho, altoBote, 
                //pared de cristal izquierda
                0, margenCuelloAlto, 0, altoBote - margenEsquinaInfAlto, 
                //pared de cristal derecha
                anchoBote, margenCuelloAlto, anchoBote, altoBote - margenEsquinaInfAlto, 
                //cuello izquierdo (diagonal hacia arriba como un embudo)
                margenCuelloAncho, 0, 0, margenCuelloAlto, 
                //cuello derecho
                anchoBote - margenCuelloAncho, 0, anchoBote, margenCuelloAlto, 
                //suelo de la tapa invisible para que no puedan salirse disparadas por arriba
                margenCuelloAncho, 0, anchoBote - margenCuelloAncho, 0 
        };

        //reglas generales del 'mundo'
        float fuerzaGravedad = 3500f; //fuerza con la que caen hacia abajo
        float capacidadRebote = 0.3f; //0 significa que son de plomo (no botan), 1 significa que son pelotas locas

        //1. aPLICAR GRAVEDAD: Mover todas las emociones basándonos en su velocidad
        for (EmocionFisica emocion : listaEmociones) {
            emocion.velY += fuerzaGravedad * pasoDeTiempo; //aceleran hacia abajo
            emocion.posicionX += emocion.velX * pasoDeTiempo;
            emocion.posicionY += emocion.velY * pasoDeTiempo;
        }

        //2. dETECTAR CHOQUES: Hacemos comprobaciones múltiples veces por fotograma para que las emociones se acoplen bien si hay muchas apiladas
        int intentosSuperposicion = 4;
        for (int repeticion = 0; repeticion < intentosSuperposicion; repeticion++) {
            for (int i = 0; i < listaEmociones.size(); i++) {
                EmocionFisica bola1 = listaEmociones.get(i);
                
                //¿chocó contra alguna pared invisible del frasco?
                for (int posPared = 0; posPared < paredesBote.length; posPared += 4) {
                    evitarAtravesarLinea(bola1, paredesBote[posPared], paredesBote[posPared+1], paredesBote[posPared+2], paredesBote[posPared+3], capacidadRebote);
                }

                //¿chocó contra OTRA emoción diferente?
                for (int j = i + 1; j < listaEmociones.size(); j++) {
                    EmocionFisica bola2 = listaEmociones.get(j);
                    
                    float distEjeX = bola2.posicionX - bola1.posicionX;
                    float distEjeY = bola2.posicionY - bola1.posicionY;
                    float distanciaAlCuadrado = distEjeX * distEjeX + distEjeY * distEjeY;
                    
                    float sumaRadios = bola1.radio + bola2.radio; //la distancia mínima permitida para no superponerse
                    
                    if (distanciaAlCuadrado < sumaRadios * sumaRadios) {
                        float distanciaReal = (float) Math.sqrt(distanciaAlCuadrado);
                        if (distanciaReal == 0) continue;
                        
                        float cantidadQueSeAtraviesan = sumaRadios - distanciaReal;
                        float impactoX = distEjeX / distanciaReal;
                        float impactoY = distEjeY / distanciaReal;
                        
                        //separarlas para que dejen de penetrarse (mitad a cada lado visualmente)
                        float separarX = impactoX * cantidadQueSeAtraviesan * 0.5f;
                        float separarY = impactoY * cantidadQueSeAtraviesan * 0.5f;
                        
                        bola1.posicionX -= separarX;
                        bola1.posicionY -= separarY;
                        bola2.posicionX += separarX;
                        bola2.posicionY += separarY;
                        
                        //hacer que las velocidades se intercambien (el choque que vemos, cómo salen disparadas u oscilan)
                        float difVelX = bola2.velX - bola1.velX;
                        float difVelY = bola2.velY - bola1.velY;
                        float fuerzaChoque = difVelX * impactoX + difVelY * impactoY;
                        
                        //solo rebotan si se están acercando, no si ya se están separando
                        if (fuerzaChoque < 0) {
                            float impulso = -(1 + capacidadRebote) * fuerzaChoque;
                            impulso /= 2f; //divide la fuerza a la mitad porque ambas bolas tienen el mismo peso (masa)
                            
                            bola1.velX -= impactoX * impulso;
                            bola1.velY -= impactoY * impulso;
                            bola2.velX += impactoX * impulso;
                            bola2.velY += impactoY * impulso;
                        }
                    }
                }
            }
        }

        boolean hayMovimiento = false;
        
        //3. aCTUALIZAR PANTALLA: Por fin le ponemos a la imagen la posición matemática final calculada
        for (EmocionFisica emocion : listaEmociones) {
            emocion.vista.setTranslationX(emocion.posicionX - emocion.radio);
            emocion.vista.setTranslationY(emocion.posicionY - emocion.radio);
            
            //un toque de gracia: las hacemos girar en el aire según su velocidad horizontal
            emocion.vista.setRotation(emocion.vista.getRotation() + emocion.velX * pasoDeTiempo * 0.5f);

            //comprobar si todavía hay alguna bola bailando
            if (Math.abs(emocion.velX) > 5f || Math.abs(emocion.velY) > 5f || emocion.posicionY < altoBote - emocion.radio - 2f) {
                hayMovimiento = true;
            }
        }

        //si algo se está moviendo, pedimos seguir dibujando las leyes de la física.
        //si todo está inmóvil en el fondo, detenemos la simulación para no malgastar batería del móvil tontamente.
        if (hayMovimiento || listaEmociones.size() > 0) {
            Choreographer.getInstance().postFrameCallback(this);
        } else {
            animacionFisicaActiva = false;
        }
    }
    
    //función matemática secundaria: Ayuda a una bola a rebotar si pega contra alguna de las líneas del bote
    private void evitarAtravesarLinea(EmocionFisica emocion, float x1, float y1, float x2, float y2, float capacidadRebote) {
        float longitudLineaCuadrada = (x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1);
        if (longitudLineaCuadrada == 0) return;
        
        float progresoLínea = ((emocion.posicionX - x1) * (x2 - x1) + (emocion.posicionY - y1) * (y2 - y1)) / longitudLineaCuadrada;
        progresoLínea = Math.max(0, Math.min(1, progresoLínea));
        
        //buscar el punto de la pared más cercano al centro de la bola
        float puntoCercanoX = x1 + progresoLínea * (x2 - x1);
        float puntoCercanoY = y1 + progresoLínea * (y2 - y1);
        
        float distanciaX = emocion.posicionX - puntoCercanoX;
        float distanciaY = emocion.posicionY - puntoCercanoY;
        float distanciaRealAlCuadrado = distanciaX * distanciaX + distanciaY * distanciaY;
        
        if (distanciaRealAlCuadrado < emocion.radio * emocion.radio && distanciaRealAlCuadrado > 0) {
            float distanciaReal = (float) Math.sqrt(distanciaRealAlCuadrado);
            float cantidadSuperpuesta = emocion.radio - distanciaReal;
            
            float normalX = distanciaX / distanciaReal;
            float normalY = distanciaY / distanciaReal;
            
            //empujar a la fuerza la emoción para que salga de cruzar la línea
            emocion.posicionX += normalX * cantidadSuperpuesta;
            emocion.posicionY += normalY * cantidadSuperpuesta;
            
            float velocidadDeChoque = emocion.velX * normalX + emocion.velY * normalY;
            float resbalonX = -normalY;
            float resbalonY = normalX;
            float cantidadResbalon = emocion.velX * resbalonX + emocion.velY * resbalonY;

            //rebotar
            if (velocidadDeChoque < 0) { 
                velocidadDeChoque = -velocidadDeChoque * capacidadRebote;
            }
            
            //fricción táctil (rozamiento) contra el cristal del bote para evitar que resbalen como si fuese aceite
            cantidadResbalon *= 0.85f;

            //ensamblar la velocidad resultante en direcciones 'arriba' y 'lados'
            emocion.velX = normalX * velocidadDeChoque + resbalonX * cantidadResbalon;
            emocion.velY = normalY * velocidadDeChoque + resbalonY * cantidadResbalon;
        }
    }

    private void mostrarDialogoLimite() {
        android.app.Dialog dialog = new android.app.Dialog(requireContext());
        dialog.setContentView(R.layout.dialog_limite);
        
        android.view.Window window = dialog.getWindow();
        if (window != null) {
            //fondo transparente para que se vean nuestras esquinas redondeadas
            window.setBackgroundDrawable(new android.graphics.drawable.ColorDrawable(android.graphics.Color.TRANSPARENT));
            //que el fondo de toda la pantalla NO se oscurezca al abrirlo
            window.clearFlags(android.view.WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            //animación chula de entrada y salida
            window.getAttributes().windowAnimations = R.style.AnimacionDialogoLimite;
        }

        dialog.findViewById(R.id.btnEntendido).setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        //si el usuario cambia de pantalla mientras caen, abortamos todo y ahorramos batería
        animacionFisicaActiva = false;
        Choreographer.getInstance().removeFrameCallback(this);
    }
}