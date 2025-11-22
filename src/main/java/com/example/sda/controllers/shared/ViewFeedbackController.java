package com.example.sda.controllers.shared;

import com.example.sda.dao.FeedbackDAO;
import com.example.sda.models.Feedback;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.FlowPane;
import javafx.geometry.Insets;

import java.util.List;

/**
 * Controller for the View Feedback screen
 * Displays mentor ratings and student reviews
 */
public class ViewFeedbackController {

    @FXML private Label mentorNameLabel;
    @FXML private Label mentorRoleLabel;
    @FXML private Label overallRatingLabel;
    @FXML private Label totalReviewsLabel;
    @FXML private Label studentsMentoredLabel;

    @FXML private ProgressBar rating5Bar;
    @FXML private ProgressBar rating4Bar;
    @FXML private ProgressBar rating3Bar;
    @FXML private ProgressBar rating2Bar;
    @FXML private ProgressBar rating1Bar;

    @FXML private Label rating5Percent;
    @FXML private Label rating4Percent;
    @FXML private Label rating3Percent;
    @FXML private Label rating2Percent;
    @FXML private Label rating1Percent;

    @FXML private VBox reviewsContainer;

    private FeedbackDAO feedbackDAO;
    private int currentMentorId = 1; // Default to mentor ID 1 (Muhammad Waqas)

    /**
     * Initialize method - called automatically when FXML is loaded
     */
    @FXML
    public void initialize() {
        feedbackDAO = new FeedbackDAO();
        loadFeedbackData();
        System.out.println("ViewFeedbackController initialized!");
    }

    /**
     * Load all feedback data for the current mentor
     */
    private void loadFeedbackData() {
        try {
            // Get statistics
            double[] averages = feedbackDAO.getAverageRatings(currentMentorId);
            int totalReviews = feedbackDAO.getTotalFeedbackCount(currentMentorId);
            int studentsMentored = feedbackDAO.getStudentsMentoredCount(currentMentorId);
            int[] distribution = feedbackDAO.getRatingDistribution(currentMentorId);

            // Update profile stats
            updateProfileStats(averages[0], totalReviews, studentsMentored);

            // Update rating breakdown
            updateRatingBreakdown(distribution, totalReviews);

            // Load and display reviews
            loadReviews();

        } catch (Exception e) {
            System.err.println("Error loading feedback data: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Update profile statistics section
     */
    private void updateProfileStats(double avgRating, int totalReviews, int studentsMentored) {
        if (overallRatingLabel != null) {
            overallRatingLabel.setText(String.format("⭐ %.1f", avgRating));
        }
        if (totalReviewsLabel != null) {
            totalReviewsLabel.setText(String.valueOf(totalReviews));
        }
        if (studentsMentoredLabel != null) {
            studentsMentoredLabel.setText(String.valueOf(studentsMentored));
        }
    }

    /**
     * Update rating breakdown bars
     */
    private void updateRatingBreakdown(int[] distribution, int totalReviews) {
        if (totalReviews == 0) return;

        ProgressBar[] bars = {rating5Bar, rating4Bar, rating3Bar, rating2Bar, rating1Bar};
        Label[] labels = {rating5Percent, rating4Percent, rating3Percent, rating2Percent, rating1Percent};

        for (int i = 0; i < 5; i++) {
            double percentage = (double) distribution[i] / totalReviews;
            if (bars[i] != null) {
                bars[i].setProgress(percentage);
            }
            if (labels[i] != null) {
                labels[i].setText(String.format("%d%%", (int)(percentage * 100)));
            }
        }
    }

    /**
     * Load and display all reviews
     */
    private void loadReviews() {
        if (reviewsContainer == null) return;

        // Clear existing reviews
        reviewsContainer.getChildren().clear();

        // Get all feedback
        List<Feedback> feedbackList = feedbackDAO.getFeedbackByMentorId(currentMentorId);

        // Create review cards
        for (Feedback feedback : feedbackList) {
            VBox reviewCard = createReviewCard(feedback);
            reviewsContainer.getChildren().add(reviewCard);
        }

        System.out.println("Loaded " + feedbackList.size() + " reviews");
    }

    /**
     * Create a review card UI element
     */
    private VBox createReviewCard(Feedback feedback) {
        VBox card = new VBox(15);
        card.getStyleClass().add("review-card");

        // Header with avatar and student info
        HBox header = new HBox(15);
        header.getStyleClass().add("review-header");

        Label avatar = new Label(getInitials(feedback.getStudentName()));
        avatar.getStyleClass().add("reviewer-avatar");

        VBox info = new VBox(5);
        info.getStyleClass().add("reviewer-info");

        Label name = new Label(feedback.getStudentName());
        name.getStyleClass().add("reviewer-name");

        HBox meta = new HBox(8);
        meta.getStyleClass().add("review-meta");

        Label stars = new Label(feedback.getStarsString(feedback.getOverallRating()));
        stars.getStyleClass().add("review-stars");

        Label date = new Label("• " + feedback.getFormattedDate());
        date.getStyleClass().add("review-date");

        meta.getChildren().addAll(stars, date);
        info.getChildren().addAll(name, meta);
        header.getChildren().addAll(avatar, info);

        // Feedback text
        Label feedbackText = new Label(feedback.getFeedbackText());
        feedbackText.getStyleClass().add("review-text");
        feedbackText.setWrapText(true);

        // Category ratings
        FlowPane categories = new FlowPane(15, 15);
        categories.getStyleClass().add("review-categories");

        if (feedback.getCommunicationRating() > 0) {
            Label comm = new Label("Communication: " + feedback.getStarsString(feedback.getCommunicationRating()));
            comm.getStyleClass().add("review-category");
            categories.getChildren().add(comm);
        }

        if (feedback.getKnowledgeRating() > 0) {
            Label know = new Label("Knowledge: " + feedback.getStarsString(feedback.getKnowledgeRating()));
            know.getStyleClass().add("review-category");
            categories.getChildren().add(know);
        }

        if (feedback.getResponsivenessRating() > 0) {
            Label resp = new Label("Responsiveness: " + feedback.getStarsString(feedback.getResponsivenessRating()));
            resp.getStyleClass().add("review-category");
            categories.getChildren().add(resp);
        }

        if (feedback.getHelpfulnessRating() > 0) {
            Label help = new Label("Helpfulness: " + feedback.getStarsString(feedback.getHelpfulnessRating()));
            help.getStyleClass().add("review-category");
            categories.getChildren().add(help);
        }

        card.getChildren().addAll(header, feedbackText, categories);
        return card;
    }

    /**
     * Get initials from a name
     */
    private String getInitials(String name) {
        if (name == null || name.trim().isEmpty()) return "?";

        String[] parts = name.trim().split("\\s+");
        if (parts.length == 1) {
            return parts[0].substring(0, Math.min(2, parts[0].length())).toUpperCase();
        } else {
            return (parts[0].charAt(0) + "" + parts[parts.length - 1].charAt(0)).toUpperCase();
        }
    }
}