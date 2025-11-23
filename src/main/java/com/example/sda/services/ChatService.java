package com.example.sda.services;

import com.example.sda.dao.ChatDAO;
import com.example.sda.models.Chat;
import java.util.List;

public class ChatService {
    private final ChatDAO chatDAO = new ChatDAO();

    public List<Chat> getUserChats(int userId) {
        return chatDAO.getChatsForUser(userId);
    }
    
    public boolean startChat(int mentorId, int studentId) {
        return chatDAO.createChat(mentorId, studentId);
    }
}