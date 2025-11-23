package com.example.sda.dao;

import com.example.sda.DB;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

public class AdminDAO {

    public int getTotalUsers() {
        return getCount("SELECT COUNT(*) FROM user");
    }

    public int getTotalProjects() {
        return getCount("SELECT COUNT(*) FROM project");
    }

    public int getActiveMentors() {
        // Mentors are users with ALUMNI account type
        return getCount("SELECT COUNT(*) FROM user WHERE account_type = 'ALUMNI'");
    }

    public double getAverageRating() {
        String query = "SELECT AVG(overall_rating) FROM ratings";
        try (Connection conn = DB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) return rs.getDouble(1);
        } catch (Exception e) { e.printStackTrace(); }
        return 0.0;
    }

    public Map<String, Integer> getUserDistribution() {
        Map<String, Integer> distribution = new HashMap<>();
        String query = "SELECT account_type, COUNT(*) FROM user GROUP BY account_type";
        try (Connection conn = DB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                distribution.put(rs.getString(1), rs.getInt(2));
            }
        } catch (Exception e) { e.printStackTrace(); }
        return distribution;
    }

    public Map<String, Integer> getProjectStatusDistribution() {
        Map<String, Integer> distribution = new HashMap<>();
        String query = "SELECT status, COUNT(*) FROM project GROUP BY status";
        try (Connection conn = DB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                String status = rs.getString(1);
                if(status == null) status = "pending";
                distribution.put(status, rs.getInt(2));
            }
        } catch (Exception e) { e.printStackTrace(); }
        return distribution;
    }

    private int getCount(String query) {
        try (Connection conn = DB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        } catch (Exception e) { e.printStackTrace(); }
        return 0;
    }
}