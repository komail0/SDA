package com.example.sda.utils;

/**
 * Utility class for performing client-side input validation and checks.
 */
public class Validator {

    /** Regex for a standard email format. */
    private static final String EMAIL_REGEX = "^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$";

    /**
     * Determines the strength of a given password.
     * @param password The password string to evaluate.
     * @return An integer representing strength: 0 (Weak), 1 (Medium), 2 (Strong).
     */
    public static int checkPasswordStrength(String password) {
        if (password == null || password.length() < 8) {
            return 0; // Weak (too short or empty)
        }

        int score = 0;

        // Score 1: Contains both upper and lower case letters
        if (password.matches("(?=.*[a-z])(?=.*[A-Z]).*")) {
            score++;
        }

        // Score 2: Contains a number
        if (password.matches("(?=.*[0-9]).*")) {
            score++;
        }

        // Score 3: Contains a special character and is long enough
        if (password.matches("(?=.*[^a-zA-Z0-9]).*") && password.length() >= 12) {
            score++;
        }

        // Mapping score (max 3) to strength levels:
        // 0-1 -> Weak (0)
        // 2   -> Medium (1)
        // 3   -> Strong (2)

        if (score >= 3) {
            return 2; // Strong
        } else if (score == 2) {
            return 1; // Medium
        } else {
            return 0; // Weak
        }
    }

    /**
     * Validates if the full name is not empty and is reasonably sized.
     * @param fullName The full name string.
     * @return true if valid, false otherwise.
     */
    public static boolean isFullNameValid(String fullName) {
        return fullName != null && fullName.trim().length() >= 3;
    }

    /**
     * Validates if the email matches the standard regex pattern.
     * @param email The email string.
     * @return true if valid, false otherwise.
     */
    public static boolean isEmailValid(String email) {
        return email != null && email.matches(EMAIL_REGEX);
    }

    /**
     * Validates if the password meets minimum length requirements (8 characters).
     * @param password The password string.
     * @return true if valid, false otherwise.
     */
    public static boolean isPasswordValid(String password) {
        return password != null && password.length() >= 8;
    }
}