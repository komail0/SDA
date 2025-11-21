package com.example.sda.dao;

import com.example.sda.DB;
import com.example.sda.models.Rating;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Rating
 * Handles all database operations related to ratings
 */
public class RatingDAO {

    /**
     * Insert a new rating into the database
     * @param rating The rating object to insert
     * @return true if successful, false otherwise
     */
    public boolean insertRating(Rating rating) {
        String sql = """
            INSERT INTO ratings 
            (student_id, mentor_id, overall_rating, communication_rating, 
             knowledge_rating, responsiveness_rating, helpfulness_rating, feedback_text)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
            """;

        try (Connection conn = DB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, rating.getStudentId());
            ps.setInt(2, rating.getMentorId());
            ps.setInt(3, rating.getOverallRating());
            ps.setInt(4, rating.getCommunicationRating());
            ps.setInt(5, rating.getKnowledgeRating());
            ps.setInt(6, rating.getResponsivenessRating());
            ps.setInt(7, rating.getHelpfulnessRating());
            ps.setString(8, rating.getFeedbackText());

            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;

        } catch (Exception e) {
            System.err.println("Error inserting rating: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Get all ratings for a specific mentor
     * @param mentorId The ID of the mentor
     * @return List of ratings for that mentor
     */
    public List<Rating> getRatingsByMentorId(int mentorId) {
        List<Rating> ratings = new ArrayList<>();
        String sql = "SELECT * FROM ratings WHERE mentor_id = ? ORDER BY created_at DESC";

        try (Connection conn = DB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, mentorId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Rating rating = new Rating(
                        rs.getInt("id"),
                        rs.getInt("student_id"),
                        rs.getInt("mentor_id"),
                        rs.getInt("overall_rating"),
                        rs.getInt("communication_rating"),
                        rs.getInt("knowledge_rating"),
                        rs.getInt("responsiveness_rating"),
                        rs.getInt("helpfulness_rating"),
                        rs.getString("feedback_text"),
                        rs.getTimestamp("created_at").toLocalDateTime()
                );
                ratings.add(rating);
            }

        } catch (Exception e) {
            System.err.println("Error getting ratings: " + e.getMessage());
            e.printStackTrace();
        }

        return ratings;
    }

    /**
     * Get average ratings for a specific mentor
     * @param mentorId The ID of the mentor
     * @return Array of doubles: [overall, communication, knowledge, responsiveness, helpfulness]
     */
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

    /**
     * Check if a student has already rated a specific mentor
     * @param studentId The student's ID
     * @param mentorId The mentor's ID
     * @return true if rating exists, false otherwise
     */
    public boolean hasStudentRatedMentor(int studentId, int mentorId) {
        String sql = "SELECT COUNT(*) FROM ratings WHERE student_id = ? AND mentor_id = ?";

        try (Connection conn = DB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, studentId);
            ps.setInt(2, mentorId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }

        } catch (Exception e) {
            System.err.println("Error checking rating existence: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Get total count of ratings for a mentor
     * @param mentorId The mentor's ID
     * @return Number of ratings
     */
    public int getRatingCount(int mentorId) {
        String sql = "SELECT COUNT(*) FROM ratings WHERE mentor_id = ?";

        try (Connection conn = DB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, mentorId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (Exception e) {
            System.err.println("Error getting rating count: " + e.getMessage());
            e.printStackTrace();
        }

        return 0;
    }
}