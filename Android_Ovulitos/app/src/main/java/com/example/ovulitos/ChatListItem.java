package com.example.ovulitos;

public class ChatListItem {
    private String chatId;
    private String otherUserId;
    private String name;
    private String lastMessage;
    private String time;
    private String avatarUrl;

    public ChatListItem(String chatId, String otherUserId, String name, String lastMessage, String time, String avatarUrl) {
        this.chatId = chatId;
        this.otherUserId = otherUserId;
        this.name = name;
        this.lastMessage = lastMessage;
        this.time = time;
        this.avatarUrl = avatarUrl;
    }

    public String getChatId() { return chatId; }
    public String getOtherUserId() { return otherUserId; }
    public String getName() { return name; }
    public String getLastMessage() { return lastMessage; }
    public String getTime() { return time; }
    public String getAvatarUrl() { return avatarUrl; }
}
