package com.example.ovulitos;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.imageview.ShapeableImageView;
import java.util.List;

public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.ChatViewHolder> {

    private List<ChatListItem> chatList;
    private OnChatClickListener listener;

    public interface OnChatClickListener {
        void onChatClick(ChatListItem chat);
    }

    public ChatListAdapter(List<ChatListItem> chatList, OnChatClickListener listener) {
        this.chatList = chatList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_list, parent, false);
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        ChatListItem item = chatList.get(position);
        holder.chatName.setText(item.getName());
        holder.chatLastMessage.setText(item.getLastMessage());
        holder.chatTime.setText(item.getTime());
        
        // TODO: Cargar imagen desde URL con Glide o Picasso (item.getAvatarUrl())
        // Por ahora dejamos el placeholder
        holder.chatAvatar.setImageResource(R.drawable.imagen_usuario);
        
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onChatClick(item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return chatList != null ? chatList.size() : 0;
    }

    public static class ChatViewHolder extends RecyclerView.ViewHolder {
        ShapeableImageView chatAvatar;
        TextView chatName, chatLastMessage, chatTime;

        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            chatAvatar = itemView.findViewById(R.id.chat_avatar);
            chatName = itemView.findViewById(R.id.chat_name);
            chatLastMessage = itemView.findViewById(R.id.chat_last_message);
            chatTime = itemView.findViewById(R.id.chat_time);
        }
    }
}
