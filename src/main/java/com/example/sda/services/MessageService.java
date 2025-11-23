package com.example.sda.services;

import com.example.sda.dao.MessageDAO;
import com.example.sda.models.Message;
import java.util.List;

public class MessageService {
    private final MessageDAO messageDAO = new MessageDAO();

    public List<Message> getChatMessages(int chatId) {
        return messageDAO.getMessagesByChatId(chatId);
    }

    public boolean sendMessage(Message message) {
        return messageDAO.saveMessage(message);
    }
}