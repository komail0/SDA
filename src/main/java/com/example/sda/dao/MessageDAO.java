package com.example.sda.dao;

import com.example.sda.DB;
import com.example.sda.models.Message;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class MessageDAO {

    public boolean saveMessage(Message msg) {
        String query = "INSERT INTO messages (chat_id, mentor_id, student_id, sender_id, message_text) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, msg.getChatId());
            stmt.setInt(2, msg.getMentorId());
            stmt.setInt(3, msg.getStudentId());
            stmt.setInt(4, msg.getSenderId());
            stmt.setString(5, msg.getMessageText());

            return stmt.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Message> getMessagesByChatId(int chatId) {
        List<Message> messages = new ArrayList<>();
        String query = "SELECT * FROM messages WHERE chat_id = ? ORDER BY sent_at ASC";

        try (Connection conn = DB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, chatId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                messages.add(new Message(
                    rs.getInt("message_id"),
                    rs.getInt("chat_id"),
                    rs.getInt("mentor_id"),
                    rs.getInt("student_id"),
                    rs.getInt("sender_id"),
                    rs.getString("message_text"),
                    rs.getTimestamp("sent_at")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return messages;
    }
}