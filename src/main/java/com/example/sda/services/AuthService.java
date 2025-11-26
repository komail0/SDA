package com.example.sda.services;

import com.example.sda.dao.UserDAO;
import com.example.sda.models.User;
import com.example.sda.enums.UserRole;
import org.mindrot.jbcrypt.BCrypt; // Import the jBCrypt library


public class AuthService {
    private final UserDAO userDAO;

    public AuthService() {
        this.userDAO = new UserDAO();
    }


    private String hashPassword(String password) {
        // We use BCrypt.hashpw(password, BCrypt.gensalt()) for production-grade security.
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }


    public boolean checkPassword(String candidatePassword, String hashedPassword) {
        // BCrypt handles the extraction of the salt and the comparison safely.
        return BCrypt.checkpw(candidatePassword, hashedPassword);
    }


    public User authenticate(String email, String password) {
        // 1. Fetch user by email
        User user = userDAO.getUserByEmail(email);


        // 2. Check if user exists and if password matches the stored hash
        if (user != null && checkPassword(password, user.getPassword())) {
            // Password matches, authentication successful
            return user;
        }

        // Authentication failed
        return null;
    }


    public boolean registerUser(String username, String email, String password, UserRole role) throws IllegalArgumentException {

        // 1. Check if user already exists
        if (userDAO.getUserByEmail(email) != null) {
            throw new IllegalArgumentException("Error: An account with this email already exists.");
        }

        // 2. Hash the password using BCrypt
        String hashedPassword = hashPassword(password);

        // 3. Create the User model
        User newUser = new User(username, email, hashedPassword, role);

        // 4. Save to the database
        int newId = userDAO.saveUser(newUser);

        // Check if the save operation was successful
        return newId != -1;
    }
}