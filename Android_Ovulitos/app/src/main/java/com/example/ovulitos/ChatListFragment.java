package com.example.ovulitos;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class ChatListFragment extends Fragment {

    private RecyclerView recyclerChatList;
    private ChatListAdapter adapter;
    private List<ChatListItem> chatList;
    private TextView txtEmpty;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat_list, container, false);

        recyclerChatList = view.findViewById(R.id.recycler_chat_list);
        txtEmpty = view.findViewById(R.id.chat_list_empty);

        recyclerChatList.setLayoutManager(new LinearLayoutManager(getContext()));
        
        chatList = new ArrayList<>();
        
        // --- DATOS DE PRUEBA TEMPORALES ---
        // Luego lo reemplazaremos por Firebase
        chatList.add(new ChatListItem("1", "Dra. Sofía", "¡Hola! ¿Cómo han estado tus síntomas?", "09:41", ""));
        chatList.add(new ChatListItem("2", "Comunidad de apoyo", "Me pasa igual ultimamente...", "Ayer", ""));
        chatList.add(new ChatListItem("3", "Soporte Ovulitos", "Tu reporte ha sido actualizado", "Lun.", ""));

        adapter = new ChatListAdapter(chatList);
        recyclerChatList.setAdapter(adapter);

        verificarListaVacia();

        return view;
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
}
