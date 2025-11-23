package com.example.sda.dao;

import com.example.sda.DB;
import com.example.sda.models.User;
import com.example.sda.enums.UserRole;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * Handles database operations for the User model.
 */
public class UserDAO {


    public User getUserByEmail(String identifier) {
        // Updated query to check both email and username
        String query = "SELECT id, username, email, password, account_type FROM user WHERE email = ? OR username = ?";

        try (Connection conn = DB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            // Set the identifier for both placeholders
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
        } catch (Exception e) {
            System.err.println("Error fetching user by identifier: " + e.getMessage());
        }
        return null;
    }

    /**
     * Saves a new user record to the database.
     * @param user The User object containing username, email, hashed password, and account type.
     * @return The ID of the newly created user, or -1 on failure.
     */
    public int saveUser(User user) {
        String query = "INSERT INTO user (username, email, password, account_type) VALUES (?, ?, ?, ?)";
        int generatedId = -1;

        try (Connection conn = DB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getEmail());
            stmt.setString(3, user.getPassword());
            stmt.setString(4, user.getAccountType().toString().toUpperCase()); // Store enum name

            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        generatedId = rs.getInt(1);
                        user.setId(generatedId);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error saving new user: " + e.getMessage());
        }
        return generatedId;
    }
}