package com.example.sda.controllers.shared;

import com.example.sda.models.Rating;
import com.example.sda.models.User;
import com.example.sda.services.RatingService;
import com.example.sda.utils.SessionManager;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.ResourceBundle;

public class ViewFeedbackController implements Initializable {

    @FXML private Label mentorNameLabel;
    @FXML private Label mentorRoleLabel;
    @FXML private Label overallRatingLabel;
    @FXML private Label totalReviewsLabel;
    @FXML private Label studentsMentoredLabel;

    @FXML private ProgressBar rating5Bar, rating4Bar, rating3Bar, rating2Bar, rating1Bar;
    @FXML private Label rating5Percent, rating4Percent, rating3Percent, rating2Percent, rating1Percent;

    @FXML private VBox reviewsContainer;

    private final RatingService ratingService = new RatingService();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadFeedbackData();
    }

    private void loadFeedbackData() {
        User user = SessionManager.getInstance().getCurrentUser();
        if (user == null) return;

        // Set Profile Header
        mentorNameLabel.setText(user.getUsername());
        mentorRoleLabel.setText("Alumni Mentor"); // Or specific role if available

        new Thread(() -> {
            List<Rating> ratings = ratingService.getMentorRatings(user.getId());
            
            Platform.runLater(() -> {
                if (ratings.isEmpty()) {
                    showEmptyState();
                } else {
                    calculateAndDisplayStats(ratings);
                    renderReviews(ratings);
                }
            });
        }).start();
    }

    private void calculateAndDisplayStats(List<Rating> ratings) {
        int total = ratings.size();
        double sum = 0;
        int[] counts = new int[6]; // Index 1-5

        for (Rating r : ratings) {
            sum += r.getOverallRating();
            int stars = Math.min(5, Math.max(1, r.getOverallRating()));
            counts[stars]++;
        }

        // 1. Top Stats
        double average = sum / total;
        overallRatingLabel.setText(String.format("⭐ %.1f", average));
        totalReviewsLabel.setText(String.valueOf(total));
        
        long uniqueStudents = ratings.stream().map(Rating::getStudentId).distinct().count();
        studentsMentoredLabel.setText(String.valueOf(uniqueStudents));

        // 2. Progress Bars
        updateBar(rating5Bar, rating5Percent, counts[5], total);
        updateBar(rating4Bar, rating4Percent, counts[4], total);
        updateBar(rating3Bar, rating3Percent, counts[3], total);
        updateBar(rating2Bar, rating2Percent, counts[2], total);
        updateBar(rating1Bar, rating1Percent, counts[1], total);
    }

    private void updateBar(ProgressBar bar, Label percentLabel, int count, int total) {
        double progress = (double) count / total;
        bar.setProgress(progress);
        percentLabel.setText(String.format("%.0f%%", progress * 100));
    }

    private void renderReviews(List<Rating> ratings) {
        reviewsContainer.getChildren().clear();
        
        // Re-add the header (It gets cleared if we clear the VBox children directly if it's inside)
        // Wait, the FXML structure has a header INSIDE reviewsContainer? No, it's in VBox.
        // Let's check FXML. If Header is inside 'reviews-section', we shouldn't clear the whole VBox.
        // The FXML provided shows 'reviewsContainer' IS the VBox. The Header is inside it.
        // So we need to be careful not to remove the sort header.
        
        // Correction: Looking at your FXML, `reviewsContainer` contains the Header HBox as the first child.
        // We should remove all children AFTER index 0.
        if (!reviewsContainer.getChildren().isEmpty()) {
            reviewsContainer.getChildren().remove(1, reviewsContainer.getChildren().size());
        }

        for (Rating r : ratings) {
            reviewsContainer.getChildren().add(createReviewCard(r));
        }
    }

    private VBox createReviewCard(Rating r) {
        VBox card = new VBox();
        card.getStyleClass().add("review-card");

        // Header
        HBox header = new HBox();
        header.getStyleClass().add("review-header");

        Label avatar = new Label("👤");
        avatar.getStyleClass().add("reviewer-avatar");

        VBox info = new VBox();
        info.getStyleClass().add("reviewer-info");
        
        Label name = new Label(r.getStudentName() != null ? r.getStudentName() : "Anonymous Student");
        name.getStyleClass().add("reviewer-name");

        // Stars string
        StringBuilder stars = new StringBuilder();
        for(int i=0; i<r.getOverallRating(); i++) stars.append("⭐");
        
        Label meta = new Label(stars.toString());
        meta.getStyleClass().add("review-stars");

        info.getChildren().addAll(name, meta);
        header.getChildren().addAll(avatar, info);

        // Text
        Label feedback = new Label(r.getFeedbackText());
        feedback.getStyleClass().add("review-text");
        feedback.setWrapText(true);

        // Date
        SimpleDateFormat sdf = new SimpleDateFormat("MMM d, yyyy");
        String dateStr = r.getCreatedAt() != null ? sdf.format(r.getCreatedAt()) : "";
        Label dateLabel = new Label(dateStr);
        dateLabel.getStyleClass().add("review-date");

        card.getChildren().addAll(header, feedback, dateLabel);
        return card;
    }

    private void showEmptyState() {
        overallRatingLabel.setText("N/A");
        totalReviewsLabel.setText("0");
        studentsMentoredLabel.setText("0");
        
        // Clear bars
        rating5Bar.setProgress(0); rating5Percent.setText("0%");
        rating4Bar.setProgress(0); rating4Percent.setText("0%");
        rating3Bar.setProgress(0); rating3Percent.setText("0%");
        rating2Bar.setProgress(0); rating2Percent.setText("0%");
        rating1Bar.setProgress(0); rating1Percent.setText("0%");

        if (reviewsContainer.getChildren().size() > 1) {
            reviewsContainer.getChildren().remove(1, reviewsContainer.getChildren().size());
        }
        
        Label empty = new Label("No reviews yet.");
        empty.setStyle("-fx-text-fill: #808080; -fx-font-size: 16px; -fx-padding: 20;");
        reviewsContainer.getChildren().add(empty);
    }
}