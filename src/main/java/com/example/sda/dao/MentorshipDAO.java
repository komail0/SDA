package com.example.sda.dao;

import com.example.sda.DB;
import com.example.sda.models.MentorshipRequest;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class MentorshipDAO {

    // Create a new request
    public boolean createRequest(MentorshipRequest request) {
        String query = "INSERT INTO mentorship_request (student_id, mentor_id, title, request_description, university, academic_year, status) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, request.getStudentId());
            stmt.setInt(2, request.getMentorId());
            stmt.setString(3, request.getTitle());
            stmt.setString(4, request.getDescription());
            stmt.setString(5, request.getUniversity());
            stmt.setString(6, request.getAcademicYear());
            stmt.setString(7, request.getStatus());

            return stmt.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Check for duplicates
    public boolean checkRequestExists(int studentId, int mentorId, String projectTitle) {
        String query = "SELECT COUNT(*) FROM mentorship_request WHERE student_id = ? AND mentor_id = ? AND title = ?";
        try (Connection conn = DB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, studentId);
            stmt.setInt(2, mentorId);
            stmt.setString(3, projectTitle);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return rs.getInt(1) > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // Get requests sent BY a Student (For Student View)
    public List<MentorshipRequest> getRequestsByStudentId(int studentId) {
        List<MentorshipRequest> requests = new ArrayList<>();
        String query = "SELECT m.*, u.username AS mentor_name " +
                "FROM mentorship_request m " +
                "JOIN user u ON m.mentor_id = u.id " +
                "WHERE m.student_id = ? " +
                "ORDER BY m.request_date DESC";

        try (Connection conn = DB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, studentId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                requests.add(new MentorshipRequest(
                        rs.getInt("request_id"),
                        rs.getInt("student_id"),
                        rs.getInt("mentor_id"),
                        rs.getString("title"),
                        rs.getString("request_description"),
                        rs.getString("university"),
                        rs.getString("academic_year"),
                        rs.getString("status"),
                        rs.getTimestamp("request_date"),
                        rs.getString("mentor_name")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return requests;
    }

    // Get requests sent TO a Mentor (For Alumni View)
    public List<MentorshipRequest> getRequestsByMentorId(int mentorId) {
        List<MentorshipRequest> requests = new ArrayList<>();
        String query = "SELECT m.*, u.username AS student_name " +
                "FROM mentorship_request m " +
                "JOIN user u ON m.student_id = u.id " +
                "WHERE m.mentor_id = ? AND m.status = 'pending' " + // Only show pending
                "ORDER BY m.request_date DESC";

        try (Connection conn = DB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, mentorId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                requests.add(new MentorshipRequest(
                        rs.getInt("request_id"),
                        rs.getInt("student_id"),
                        rs.getInt("mentor_id"),
                        rs.getString("title"),
                        rs.getString("request_description"),
                        rs.getString("university"),
                        rs.getString("academic_year"),
                        rs.getString("status"),
                        rs.getTimestamp("request_date"),
                        rs.getString("student_name") // Pass student name
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return requests;
    }

    // Update Request Status (Accept/Reject)
    public boolean updateStatus(int requestId, String status) {
        String query = "UPDATE mentorship_request SET status = ? WHERE request_id = ?";
        try (Connection conn = DB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, status);
            stmt.setInt(2, requestId);

            return stmt.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Delete a request
    public boolean deleteRequest(int requestId) {
        String query = "DELETE FROM mentorship_request WHERE request_id = ?";
        try (Connection conn = DB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, requestId);
            return stmt.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // --- NEW METHOD ---
    public MentorshipRequest getRequestById(int requestId) {
        String query = "SELECT m.*, u.username AS student_name FROM mentorship_request m JOIN user u ON m.student_id = u.id WHERE m.request_id = ?";
        try (Connection conn = DB.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, requestId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return mapResultSetToRequest(rs, "student_name");
        } catch (Exception e) { e.printStackTrace(); }
        return null;
    }

    private MentorshipRequest mapResultSetToRequest(ResultSet rs, String nameColumn) throws java.sql.SQLException {
        return new MentorshipRequest(
                rs.getInt("request_id"),
                rs.getInt("student_id"),
                rs.getInt("mentor_id"),
                rs.getString("title"),
                rs.getString("request_description"),
                rs.getString("university"),
                rs.getString("academic_year"),
                rs.getString("status"),
                rs.getTimestamp("request_date"),
                rs.getString(nameColumn)
        );
    }
}