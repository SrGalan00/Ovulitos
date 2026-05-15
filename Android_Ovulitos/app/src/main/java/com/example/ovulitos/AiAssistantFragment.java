package com.example.ovulitos;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AiAssistantFragment extends Fragment {

    private String currentUserId = "anonymous";
    private RecyclerView recyclerMessages;
    private EditText etMessage;
    private ImageButton btnSend;
    private ProgressBar loadingIndicator;

    private MessageAdapter messageAdapter;
    private List<ChatMessage> messageList;
    private AiApiService apiService;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ai_assistant, container, false);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            currentUserId = currentUser.getUid();
        }



        recyclerMessages = view.findViewById(R.id.recycler_ai_messages);
        etMessage = view.findViewById(R.id.et_ai_message);
        btnSend = view.findViewById(R.id.btn_send_ai);
        loadingIndicator = view.findViewById(R.id.ai_loading_indicator);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setStackFromEnd(true);
        recyclerMessages.setLayoutManager(layoutManager);

        messageList = new ArrayList<>();
        // Asignamos "user" como el ID local para los mensajes enviados
        messageAdapter = new MessageAdapter(messageList, "user");
        recyclerMessages.setAdapter(messageAdapter);

        // Mensaje de bienvenida de la IA
        addAiMessage("Hola. Soy tu Asistente Virtual de Salud Íntima. ¿En qué te puedo ayudar hoy? Recuerda que este es un espacio seguro.");

        // Inicializar Retrofit con timeout elevado para dar tiempo a la IA
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BuildConfig.BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiService = retrofit.create(AiApiService.class);

        btnSend.setOnClickListener(v -> sendMessage());

        return view;
    }

    private void sendMessage() {
        String msgText = etMessage.getText().toString().trim();
        if (TextUtils.isEmpty(msgText)) return;

        etMessage.setText("");
        
        // Agregar mensaje del usuario a la vista
        long timestamp = System.currentTimeMillis();
        ChatMessage userMsg = new ChatMessage("user", "ai", msgText, timestamp);
        messageList.add(userMsg);
        messageAdapter.notifyItemInserted(messageList.size() - 1);
        recyclerMessages.scrollToPosition(messageList.size() - 1);

        // Mostrar indicador de carga
        loadingIndicator.setVisibility(View.VISIBLE);
        btnSend.setEnabled(false);

        // Llamada a la API de Node.js
        AiRequest request = new AiRequest(msgText, currentUserId);
        apiService.askAssistant(request).enqueue(new Callback<AiResponse>() {
            @Override
            public void onResponse(Call<AiResponse> call, Response<AiResponse> response) {
                if (!isAdded() || getView() == null) return;
                loadingIndicator.setVisibility(View.GONE);
                btnSend.setEnabled(true);
                
                if (response.isSuccessful() && response.body() != null) {
                    addAiMessage(response.body().getReply());
                } else {
                    String errorMessage = "Lo siento, tuve un problema analizando tu consulta.";
                    try {
                        if (response.errorBody() != null) {
                            String errorJson = response.errorBody().string();
                            // Intentar extraer el campo "error" del JSON
                            if (errorJson.contains("\"error\":")) {
                                // Una forma simple de extraer el mensaje sin añadir dependencias pesadas de parsing
                                errorMessage = errorJson.split("\"error\":")[1]
                                        .split("\"")[1];
                            }
                        }
                    } catch (Exception e) {
                        Log.e("AiAssistant", "Error parsing error body", e);
                    }
                    
                    addAiMessage(errorMessage + " (Error " + response.code() + ")");
                    Log.e("AiAssistant", "Error response: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<AiResponse> call, Throwable t) {
                if (!isAdded() || getView() == null) return;
                loadingIndicator.setVisibility(View.GONE);
                btnSend.setEnabled(true);
                addAiMessage("Hubo un error de conexión con el servidor. Revisa tu internet o intenta más tarde.");
                Log.e("AiAssistant", "Network error: ", t);
            }
        });
    }

    private void addAiMessage(String text) {
        long timestamp = System.currentTimeMillis();
        ChatMessage aiMsg = new ChatMessage("ai", "user", text, timestamp);
        messageList.add(aiMsg);
        messageAdapter.notifyItemInserted(messageList.size() - 1);
        recyclerMessages.scrollToPosition(messageList.size() - 1);
    }
}
