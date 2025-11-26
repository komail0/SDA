package com.example.sda.dao;

import com.example.sda.DB;
import com.example.sda.models.Feedback;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FeedbackDAO {


    public List<Feedback> getFeedbackByMentorId(int mentorId) {
        List<Feedback> feedbackList = new ArrayList<>();
        String sql = """
            SELECT r.*, m.name as mentor_name
            FROM ratings r
            JOIN mentors m ON r.mentor_id = m.id
            WHERE r.mentor_id = ?
            ORDER BY r.created_at DESC
            """;

        try (Connection conn = DB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, mentorId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Feedback feedback = new Feedback(
                        rs.getInt("id"),
                        rs.getInt("student_id"),
                        "Student " + rs.getInt("student_id"), // Placeholder student name
                        rs.getInt("mentor_id"),
                        rs.getString("mentor_name"),
                        rs.getInt("overall_rating"),
                        rs.getInt("communication_rating"),
                        rs.getInt("knowledge_rating"),
                        rs.getInt("responsiveness_rating"),
                        rs.getInt("helpfulness_rating"),
                        rs.getString("feedback_text"),
                        rs.getTimestamp("created_at").toLocalDateTime()
                );
                feedbackList.add(feedback);
            }

        } catch (Exception e) {
            System.err.println("Error getting feedback: " + e.getMessage());
            e.printStackTrace();
        }

        return feedbackList;
    }

    public double[] getAverageRatings(int mentorId) {
        String sql = """
            SELECT 
                AVG(overall_rating) as avg_overall,
                AVG(communication_rating) as avg_communication,
                AVG(knowledge_rating) as avg_knowledge,
                AVG(responsiveness_rating) as avg_responsiveness,
                AVG(helpfulness_rating) as avg_helpfulness
            FROM ratings 
            WHERE mentor_id = ?
            """;

        double[] averages = new double[5];

        try (Connection conn = DB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, mentorId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                averages[0] = rs.getDouble("avg_overall");
                averages[1] = rs.getDouble("avg_communication");
                averages[2] = rs.getDouble("avg_knowledge");
                averages[3] = rs.getDouble("avg_responsiveness");
                averages[4] = rs.getDouble("avg_helpfulness");
            }

        } catch (Exception e) {
            System.err.println("Error calculating averages: " + e.getMessage());
            e.printStackTrace();
        }

        return averages;
    }


    public int[] getRatingDistribution(int mentorId) {
        String sql = """
            SELECT overall_rating, COUNT(*) as count
            FROM ratings
            WHERE mentor_id = ?
            GROUP BY overall_rating
            ORDER BY overall_rating DESC
            """;

        int[] distribution = new int[5]; // [5-star, 4-star, 3-star, 2-star, 1-star]

        try (Connection conn = DB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, mentorId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                int rating = rs.getInt("overall_rating");
                int count = rs.getInt("count");
                if (rating >= 1 && rating <= 5) {
                    distribution[5 - rating] = count; // Index 0 = 5 stars, Index 4 = 1 star
                }
            }

        } catch (Exception e) {
            System.err.println("Error getting rating distribution: " + e.getMessage());
            e.printStackTrace();
        }

        return distribution;
    }


    public int getTotalFeedbackCount(int mentorId) {
        String sql = "SELECT COUNT(*) FROM ratings WHERE mentor_id = ?";

        try (Connection conn = DB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, mentorId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (Exception e) {
            System.err.println("Error getting feedback count: " + e.getMessage());
            e.printStackTrace();
        }

        return 0;
    }


    public int getStudentsMentoredCount(int mentorId) {
        String sql = "SELECT COUNT(DISTINCT student_id) FROM ratings WHERE mentor_id = ?";

        try (Connection conn = DB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, mentorId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (Exception e) {
            System.err.println("Error getting students count: " + e.getMessage());
            e.printStackTrace();
        }

        return 0;
    }
}