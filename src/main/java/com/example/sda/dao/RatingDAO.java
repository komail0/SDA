package com.example.sda.dao;

import com.example.sda.DB;
import com.example.sda.models.Rating;
import com.example.sda.models.User;
import com.example.sda.enums.UserRole;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class RatingDAO {

    public boolean saveRating(Rating rating) {
        String query = "INSERT INTO ratings (student_id, mentor_id, overall_rating, communication_rating, " +
                "knowledge_rating, responsiveness_rating, helpfulness_rating, feedback_text) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, rating.getStudentId());
            stmt.setInt(2, rating.getMentorId());
            stmt.setInt(3, rating.getOverallRating());
            stmt.setInt(4, rating.getCommunicationRating());
            stmt.setInt(5, rating.getKnowledgeRating());
            stmt.setInt(6, rating.getResponsivenessRating());
            stmt.setInt(7, rating.getHelpfulnessRating());
            stmt.setString(8, rating.getFeedbackText());
            return stmt.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); return false; }
    }

    public boolean hasStudentRatedMentor(int studentId, int mentorId) {
        String query = "SELECT COUNT(*) FROM ratings WHERE student_id = ? AND mentor_id = ?";
        try (Connection conn = DB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, studentId);
            stmt.setInt(2, mentorId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return rs.getInt(1) > 0;
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }

    public List<User> getMentorsByStudentChat(int studentId) {
        List<User> mentors = new ArrayList<>();
        String query = "SELECT DISTINCT u.id, u.username, u.email, u.account_type FROM user u JOIN chat c ON u.id = c.mentor_id WHERE c.student_id = ?";
        try (Connection conn = DB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, studentId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                mentors.add(new User(rs.getInt("id"), rs.getString("username"), rs.getString("email"), "", UserRole.ALUMNI));
            }
        } catch (Exception e) { e.printStackTrace(); }
        return mentors;
    }

    // --- NEW METHOD FOR ALUMNI VIEW ---
    public List<Rating> getRatingsForMentor(int mentorId) {
        List<Rating> ratings = new ArrayList<>();
        String query = "SELECT r.*, u.username AS student_name " +
                "FROM ratings r " +
                "JOIN user u ON r.student_id = u.id " +
                "WHERE r.mentor_id = ? " +
                "ORDER BY r.created_at DESC";

        try (Connection conn = DB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, mentorId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                ratings.add(new Rating(
                        rs.getInt("id"),
                        rs.getInt("student_id"),
                        rs.getInt("mentor_id"),
                        rs.getInt("overall_rating"),
                        rs.getInt("communication_rating"),
                        rs.getInt("knowledge_rating"),
                        rs.getInt("responsiveness_rating"),
                        rs.getInt("helpfulness_rating"),
                        rs.getString("feedback_text"),
                        rs.getTimestamp("created_at"),
                        rs.getString("student_name")
                ));
            }
        } catch (Exception e) { e.printStackTrace(); }
        return ratings;
    }
}