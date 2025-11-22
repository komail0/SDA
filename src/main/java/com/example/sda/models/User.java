package com.example.sda.models;

import com.example.sda.enums.UserRole;

/**
 * Represents a user in the system, mapping directly to the 'users' table.
 */
public class User {
    private int id;
    private String username; // Mapped to fullNameField
    private String email;
    private String password; // Should store the HASHED password
    private UserRole accountType; // Mapped to the role selection

    // Default constructor for creating new users
    public User(String username, String email, String password, UserRole accountType) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.accountType = accountType;
    }

    // Constructor including ID for fetching from DB
    public User(int id, String username, String email, String password, UserRole accountType) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
        this.accountType = accountType;
    }

    // --- Getters and Setters ---

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public UserRole getAccountType() {
        return accountType;
    }

    public void setAccountType(UserRole accountType) {
        this.accountType = accountType;
    }
}