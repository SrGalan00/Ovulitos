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

    // TODO: Reemplaza con la IP de tu PC en la red local si pruebas en dispositivo físico
    // 10.0.2.2 es para acceder a localhost desde el emulador de Android.
    private static final String BASE_URL = "http://10.206.9.22:3000/";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ai_assistant, container, false);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            currentUserId = currentUser.getUid();
        }

        view.findViewById(R.id.btn_back_ai).setOnClickListener(v -> {
            if (getActivity() != null) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });

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

        // Inicializar Retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
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
                    addAiMessage("Lo siento, tuve un problema analizando tu consulta. Las políticas de seguridad son estrictas para proteger tu bienestar.");
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
