package com.example.ovulitos;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatFragment extends Fragment {

    private String otherUserId;
    private String otherUserName;
    private String currentUserId;
    private String chatId;

    private RecyclerView recyclerMessages;
    private EditText etMessage;
    private ImageButton btnSend;
    private TextView txtUserName;

    private MessageAdapter messageAdapter;
    private List<ChatMessage> messageList;

    private FirebaseFirestore db;
    private FirebaseAuth auth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);

        if (getArguments() != null) {
            otherUserId = getArguments().getString("otherUserId");
            otherUserName = getArguments().getString("otherUserName");
        }

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();
        
        if (currentUser != null) {
            currentUserId = currentUser.getUid();
            // Create a unique chat ID by sorting the two UIDs alphabetically
            if (currentUserId.compareTo(otherUserId) < 0) {
                chatId = currentUserId + "_" + otherUserId;
            } else {
                chatId = otherUserId + "_" + currentUserId;
            }
        } else {
            Toast.makeText(getContext(), "Por favor, inicia sesión", Toast.LENGTH_SHORT).show();
            if (getActivity() != null) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
            return view;
        }

        txtUserName = view.findViewById(R.id.chat_user_name);
        if (otherUserName != null) {
            txtUserName.setText(otherUserName);
        }

        view.findViewById(R.id.btn_back_chat).setOnClickListener(v -> {
            if (getActivity() != null) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });

        recyclerMessages = view.findViewById(R.id.recycler_chat_messages);
        etMessage = view.findViewById(R.id.et_chat_message);
        btnSend = view.findViewById(R.id.btn_send_message);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setStackFromEnd(true); // Sent messages appear at the bottom
        recyclerMessages.setLayoutManager(layoutManager);

        messageList = new ArrayList<>();
        messageAdapter = new MessageAdapter(messageList, currentUserId);
        recyclerMessages.setAdapter(messageAdapter);

        btnSend.setOnClickListener(v -> sendMessage());

        loadMessages();

        return view;
    }

    private void sendMessage() {
        String msgText = etMessage.getText().toString().trim();
        if (TextUtils.isEmpty(msgText)) return;

        etMessage.setText("");

        long timestamp = System.currentTimeMillis();
        ChatMessage msg = new ChatMessage(currentUserId, otherUserId, msgText, timestamp);

        // Save to messages subcollection
        db.collection("chats").document(chatId).collection("messages").add(msg)
            .addOnSuccessListener(documentReference -> {
                // Also update the main document with the last message
                Map<String, Object> chatData = new HashMap<>();
                chatData.put("participants", Arrays.asList(currentUserId, otherUserId));
                chatData.put("lastMessage", msgText);
                chatData.put("timestamp", timestamp);

                db.collection("chats").document(chatId).set(chatData);
            })
            .addOnFailureListener(e -> {
                Log.e("ChatFragment", "Error al enviar el mensaje", e);
                if (getContext() != null) {
                    Toast.makeText(getContext(), "Error al enviar", Toast.LENGTH_SHORT).show();
                }
            });
    }

    private void loadMessages() {
        db.collection("chats").document(chatId).collection("messages")
            .orderBy("timestamp", Query.Direction.ASCENDING)
            .addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                    if (error != null) {
                        Log.e("ChatFragment", "Listen failed.", error);
                        return;
                    }

                    if (value != null) {
                        messageList.clear();
                        for (QueryDocumentSnapshot doc : value) {
                            ChatMessage msg = doc.toObject(ChatMessage.class);
                            messageList.add(msg);
                        }
                        messageAdapter.notifyDataSetChanged();
                        if (messageList.size() > 0) {
                            recyclerMessages.scrollToPosition(messageList.size() - 1);
                        }
                    }
                }
            });
    }
}
