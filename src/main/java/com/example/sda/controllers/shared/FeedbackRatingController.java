package com.example.sda.controllers.shared;

import com.example.sda.dao.RatingDAO;
import com.example.sda.models.Rating;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.HBox;

/**
 * Controller for the Rate Mentor screen
 * Handles user interactions and rating submissions
 */
public class FeedbackRatingController {

    @FXML private Label mentorNameLabel;
    @FXML private Label ratingLabel;
    @FXML private TextArea feedbackTextArea;
    @FXML private Button skipBtn;
    @FXML private Button submitRatingBtn;

    // Star buttons for overall rating
    @FXML private HBox starsContainer;

    // Category star containers
    @FXML private HBox communicationStars;
    @FXML private HBox knowledgeStars;
    @FXML private HBox responsivenessStars;
    @FXML private HBox helpfulnessStars;

    private RatingDAO ratingDAO;

    // Track current ratings
    private int overallRating = 5; // Default 5 stars
    private int communicationRating = 4;
    private int knowledgeRating = 5;
    private int responsivenessRating = 5;
    private int helpfulnessRating = 5;

    // For now, we'll use hardcoded IDs - later you'll get these from your login system
    private int currentStudentId = 1; // This should come from your logged-in user session
    private int currentMentorId = 1; // The mentor being rated (Muhammad Waqas from our sample data)

    /**
     * Initialize method - called automatically when the FXML is loaded
     */
    @FXML
    public void initialize() {
        ratingDAO = new RatingDAO();

        // Set up star button click handlers
        setupStarHandlers();

        // Set up button actions
        setupButtonHandlers();

        System.out.println("FeedbackRatingController initialized!");
    }

    /**
     * Set up click handlers for all star buttons
     */
    private void setupStarHandlers() {
        // Overall rating stars
        if (starsContainer != null) {
            for (int i = 0; i < starsContainer.getChildren().size(); i++) {
                if (starsContainer.getChildren().get(i) instanceof Button) {
                    Button starBtn = (Button) starsContainer.getChildren().get(i);
                    int rating = i + 1;
                    starBtn.setOnAction(e -> setOverallRating(rating));
                }
            }
        }

        // Communication rating stars
        setupCategoryStars(communicationStars, "communication");

        // Knowledge rating stars
        setupCategoryStars(knowledgeStars, "knowledge");

        // Responsiveness rating stars
        setupCategoryStars(responsivenessStars, "responsiveness");

        // Helpfulness rating stars
        setupCategoryStars(helpfulnessStars, "helpfulness");
    }

    /**
     * Set up click handlers for category star buttons
     */
    private void setupCategoryStars(HBox container, String category) {
        if (container != null) {
            for (int i = 0; i < container.getChildren().size(); i++) {
                if (container.getChildren().get(i) instanceof Button) {
                    Button starBtn = (Button) container.getChildren().get(i);
                    int rating = i + 1;
                    starBtn.setOnAction(e -> setCategoryRating(category, rating));
                }
            }
        }
    }

    /**
     * Set up button click handlers
     */
    private void setupButtonHandlers() {
        if (submitRatingBtn != null) {
            submitRatingBtn.setOnAction(e -> submitRating());
        }

        if (skipBtn != null) {
            skipBtn.setOnAction(e -> skipRating());
        }
    }

    /**
     * Update overall rating
     */
    private void setOverallRating(int rating) {
        this.overallRating = rating;
        updateRatingLabel(rating);
        updateStarDisplay(starsContainer, rating);
        System.out.println("Overall rating set to: " + rating);
    }

    /**
     * Update category rating
     */
    private void setCategoryRating(String category, int rating) {
        switch (category) {
            case "communication":
                this.communicationRating = rating;
                updateStarDisplay(communicationStars, rating);
                break;
            case "knowledge":
                this.knowledgeRating = rating;
                updateStarDisplay(knowledgeStars, rating);
                break;
            case "responsiveness":
                this.responsivenessRating = rating;
                updateStarDisplay(responsivenessStars, rating);
                break;
            case "helpfulness":
                this.helpfulnessRating = rating;
                updateStarDisplay(helpfulnessStars, rating);
                break;
        }
        System.out.println(category + " rating set to: " + rating);
    }

    /**
     * Update the rating label text based on rating value
     */
    private void updateRatingLabel(int rating) {
        if (ratingLabel != null) {
            String text = switch (rating) {
                case 1 -> "Poor";
                case 2 -> "Fair";
                case 3 -> "Good";
                case 4 -> "Very Good";
                case 5 -> "Excellent!";
                default -> "";
            };
            ratingLabel.setText(text);
        }
    }

    /**
     * Update visual display of stars (filled/empty)
     */
    private void updateStarDisplay(HBox container, int rating) {
        if (container != null) {
            for (int i = 0; i < container.getChildren().size(); i++) {
                if (container.getChildren().get(i) instanceof Button) {
                    Button starBtn = (Button) container.getChildren().get(i);
                    if (i < rating) {
                        // Filled star
                        if (!starBtn.getStyleClass().contains("filled")) {
                            starBtn.getStyleClass().add("filled");
                        }
                        starBtn.getStyleClass().remove("empty");
                    } else {
                        // Empty star
                        if (!starBtn.getStyleClass().contains("empty")) {
                            starBtn.getStyleClass().add("empty");
                        }
                        starBtn.getStyleClass().remove("filled");
                    }
                }
            }
        }
    }

    /**
     * Submit the rating to the database
     */
    private void submitRating() {
        try {
            // Get feedback text (empty string if null or blank)
            String feedbackText = feedbackTextArea != null ? feedbackTextArea.getText() : "";
            if (feedbackText == null || feedbackText.trim().isEmpty()) {
                feedbackText = "No feedback provided";
            }

            // Create rating object
            Rating rating = new Rating(
                    currentStudentId,
                    currentMentorId,
                    overallRating,
                    communicationRating,
                    knowledgeRating,
                    responsivenessRating,
                    helpfulnessRating,
                    feedbackText
            );

            // Save to database
            boolean success = ratingDAO.insertRating(rating);

            if (success) {
                showAlert(AlertType.INFORMATION, "Success!", "Your rating has been submitted successfully! 🎉");
                System.out.println("Rating submitted successfully!");
                // TODO: Navigate to another screen or close this window
            } else {
                showAlert(AlertType.ERROR, "Error", "Failed to submit rating. Please try again.");
            }

        } catch (Exception e) {
            showAlert(AlertType.ERROR, "Error", "An error occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Skip rating
     */
    private void skipRating() {
        System.out.println("Rating skipped");
        showAlert(AlertType.INFORMATION, "Skipped", "You can rate this mentor later! ⏭️");
        // TODO: Navigate to another screen
    }

    /**
     * Show a custom styled alert dialog
     */
    private void showAlert(AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);

        // Apply custom styling to the dialog
        alert.getDialogPane().setStyle(
                "-fx-background-color: #1a1a1a;" +
                        "-fx-border-color: #00ff88;" +
                        "-fx-border-width: 2;" +
                        "-fx-border-radius: 15;" +
                        "-fx-background-radius: 15;" +
                        "-fx-padding: 20;"
        );

        // Style the content text
        alert.getDialogPane().lookup(".content.label").setStyle(
                "-fx-text-fill: #ffffff;" +
                        "-fx-font-size: 16;" +
                        "-fx-font-weight: 600;"
        );

        // Style the OK button
        alert.getDialogPane().lookupButton(alert.getButtonTypes().get(0)).setStyle(
                "-fx-background-color: linear-gradient(to right, #00ff88, #00cc6a);" +
                        "-fx-text-fill: #000000;" +
                        "-fx-font-weight: 700;" +
                        "-fx-font-size: 14;" +
                        "-fx-padding: 10 30;" +
                        "-fx-background-radius: 8;" +
                        "-fx-cursor: hand;"
        );

        alert.showAndWait();
    }
}