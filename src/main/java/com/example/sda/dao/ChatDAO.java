package com.example.sda.dao;

import com.example.sda.DB;
import com.example.sda.models.Chat;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class ChatDAO {

    public boolean createChat(int mentorId, int studentId) {
        // Check if chat already exists to avoid duplicates
        if (chatExists(mentorId, studentId)) return true;

        String query = "INSERT INTO chat (mentor_id, student_id) VALUES (?, ?)";
        try (Connection conn = DB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, mentorId);
            stmt.setInt(2, studentId);
            return stmt.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean chatExists(int mentorId, int studentId) {
        String query = "SELECT COUNT(*) FROM chat WHERE mentor_id = ? AND student_id = ?";
        try (Connection conn = DB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, mentorId);
            stmt.setInt(2, studentId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return rs.getInt(1) > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<Chat> getChatsForUser(int userId) {
        List<Chat> chats = new ArrayList<>();
        // This query figures out the "Other Person" based on whether the current user is the mentor or student
        String query = "SELECT c.*, u.username AS other_name " +
                       "FROM chat c " +
                       "JOIN user u ON u.id = (CASE WHEN c.mentor_id = ? THEN c.student_id ELSE c.mentor_id END) " +
                       "WHERE c.mentor_id = ? OR c.student_id = ? " +
                       "ORDER BY c.created_at DESC";

        try (Connection conn = DB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, userId);
            stmt.setInt(2, userId);
            stmt.setInt(3, userId);
            
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                chats.add(new Chat(
                    rs.getInt("chat_id"),
                    rs.getInt("mentor_id"),
                    rs.getInt("student_id"),
                    rs.getTimestamp("created_at"),
                    rs.getString("other_name")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return chats;
    }
}