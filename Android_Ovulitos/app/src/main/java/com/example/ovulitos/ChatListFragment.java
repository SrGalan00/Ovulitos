package com.example.ovulitos;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.ListenerRegistration;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ChatListFragment extends Fragment {

    private RecyclerView recyclerChatList;
    private ChatListAdapter adapter;
    private List<ChatListItem> chatList;
    private TextView txtEmpty;
    private FloatingActionButton fabNewChat;

    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private String currentUserId;
    private ListenerRegistration chatListListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat_list, container, false);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            currentUserId = currentUser.getUid();
        }

        recyclerChatList = view.findViewById(R.id.recycler_chat_list);
        txtEmpty = view.findViewById(R.id.chat_list_empty);
        fabNewChat = view.findViewById(R.id.fab_new_chat);

        recyclerChatList.setLayoutManager(new LinearLayoutManager(getContext()));
        chatList = new ArrayList<>();
        
        adapter = new ChatListAdapter(chatList, chat -> {
            ChatFragment chatFragment = new ChatFragment();
            Bundle args = new Bundle();
            args.putString("otherUserId", chat.getOtherUserId());
            args.putString("otherUserName", chat.getName());
            chatFragment.setArguments(args);

            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).reemplazarFragmento(chatFragment);
            }
        });
        
        recyclerChatList.setAdapter(adapter);

        fabNewChat.setOnClickListener(v -> {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).reemplazarFragmento(new UsersListFragment());
            }
        });

        if (currentUserId != null) {
            loadRecentChats();
        } else {
            verificarListaVacia();
        }

        return view;
    }

    private void loadRecentChats() {
        if (currentUserId == null) return;
        chatListListener = db.collection("chats")
            .whereArrayContains("participants", currentUserId)
            .addSnapshotListener((value, error) -> {
                if (error != null) {
                    Log.e("ChatListFragment", "Listen failed.", error);
                    return;
                }

                if (value != null) {
                    chatList.clear();
                    for (QueryDocumentSnapshot doc : value) {
                        List<String> participants = (List<String>) doc.get("participants");
                        if (participants != null && participants.size() == 2) {
                            String otherUserId = participants.get(0).equals(currentUserId) ? participants.get(1) : participants.get(0);
                            String lastMessage = doc.getString("lastMessage");
                            Long timestamp = doc.getLong("timestamp");
                            
                            String formattedTime = "";
                            if (timestamp != null) {
                                formattedTime = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date(timestamp));
                            }
                            
                            // Fetch other user's name
                            String finalFormattedTime = formattedTime;
                            db.collection("usuarios").whereEqualTo("uid", otherUserId)
                                .get()
                                .addOnSuccessListener(queryDocumentSnapshots -> {
                                    if (!queryDocumentSnapshots.isEmpty()) {
                                        String userName = queryDocumentSnapshots.getDocuments().get(0).getString("nombre");
                                        
                                        // Update or add chat list item
                                        ChatListItem item = new ChatListItem(doc.getId(), otherUserId, userName, lastMessage, finalFormattedTime, "");
                                        chatList.add(item);
                                        // Sort by timestamp if needed. For now, just notify
                                        adapter.notifyDataSetChanged();
                                        verificarListaVacia();
                                    }
                                });
                        }
                    }
                    verificarListaVacia();
                }
            });
    }

    private void verificarListaVacia() {
        if (chatList.isEmpty()) {
            recyclerChatList.setVisibility(View.GONE);
            txtEmpty.setVisibility(View.VISIBLE);
        } else {
            recyclerChatList.setVisibility(View.VISIBLE);
            txtEmpty.setVisibility(View.GONE);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (chatListListener != null) {
            chatListListener.remove();
        }
    }
}
