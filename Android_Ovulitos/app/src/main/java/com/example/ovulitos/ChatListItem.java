package com.example.ovulitos;

public class ChatListItem {
    private String chatId;
    private String name;
    private String lastMessage;
    private String time;
    private String avatarUrl;

    public ChatListItem(String chatId, String name, String lastMessage, String time, String avatarUrl) {
        this.chatId = chatId;
        this.name = name;
        this.lastMessage = lastMessage;
        this.time = time;
        this.avatarUrl = avatarUrl;
    }

    public String getChatId() { return chatId; }
    public String getName() { return name; }
    public String getLastMessage() { return lastMessage; }
    public String getTime() { return time; }
    public String getAvatarUrl() { return avatarUrl; }
}
