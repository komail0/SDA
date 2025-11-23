package com.example.sda.dao;

import com.example.sda.DB;
import com.example.sda.models.User;
import com.example.sda.enums.UserRole;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {

    public User getUserByEmail(String identifier) {
        String query = "SELECT id, username, email, password, account_type FROM user WHERE email = ? OR username = ?";
        try (Connection conn = DB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, identifier);
            stmt.setString(2, identifier);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new User(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("email"),
                        rs.getString("password"),
                        UserRole.valueOf(rs.getString("account_type").toUpperCase())
                );
            }
        } catch (Exception e) { e.printStackTrace(); }
        return null;
    }

    public int saveUser(User user) {
        String query = "INSERT INTO user (username, email, password, account_type) VALUES (?, ?, ?, ?)";
        int generatedId = -1;
        try (Connection conn = DB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getEmail());
            stmt.setString(3, user.getPassword());
            stmt.setString(4, user.getAccountType().toString().toUpperCase());
            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        generatedId = rs.getInt(1);
                        user.setId(generatedId);
                    }
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
        return generatedId;
    }

    // --- ADMIN METHODS ---

    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String query = "SELECT id, username, email, account_type FROM user ORDER BY id";
        try (Connection conn = DB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                users.add(new User(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("email"),
                        "",
                        UserRole.valueOf(rs.getString("account_type").toUpperCase())
                ));
            }
        } catch (Exception e) { e.printStackTrace(); }
        return users;
    }

    public boolean deleteUser(int userId) {
        String query = "DELETE FROM user WHERE id = ?";
        try (Connection conn = DB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, userId);
            return stmt.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); return false; }
    }
}