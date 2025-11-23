package com.example.sda.controllers.shared;

import com.example.sda.controllers.components.SidebarController;
import com.example.sda.models.Rating;
import com.example.sda.models.User;
import com.example.sda.services.RatingService;
import com.example.sda.utils.SessionManager;
import com.example.sda.utils.ToastHelper;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.Node;

import java.util.HashMap;
import java.util.Map;

public class FeedbackRatingController {

    @FXML private Label mentorNameLabel;
    @FXML private HBox starsContainer;
    @FXML private HBox communicationStars;
    @FXML private HBox knowledgeStars;
    @FXML private HBox responsivenessStars;
    @FXML private HBox helpfulnessStars;
    @FXML private TextArea feedbackTextArea;
    @FXML private Button submitRatingBtn;
    @FXML private Button skipBtn;
    @FXML private Label ratingLabel;

    private User mentor;
    private SidebarController sidebarController;
    private final RatingService ratingService = new RatingService();

    // Store ratings: "overall", "communication", etc.
    private final Map<String, Integer> ratings = new HashMap<>();

    @FXML
    public void initialize() {
        setupStarGroup(starsContainer, "overall", ratingLabel);
        setupStarGroup(communicationStars, "communication", null);
        setupStarGroup(knowledgeStars, "knowledge", null);
        setupStarGroup(responsivenessStars, "responsiveness", null);
        setupStarGroup(helpfulnessStars, "helpfulness", null);

        submitRatingBtn.setOnAction(e -> handleSubmit());
        skipBtn.setOnAction(e -> goBack());

        // Default ratings
        ratings.put("overall", 0);
        ratings.put("communication", 0);
        ratings.put("knowledge", 0);
        ratings.put("responsiveness", 0);
        ratings.put("helpfulness", 0);
    }

    public void setMentorData(User mentor, SidebarController sidebar) {
        this.mentor = mentor;
        this.sidebarController = sidebar;
        mentorNameLabel.setText("with " + mentor.getUsername());
    }

    private void setupStarGroup(HBox container, String key, Label labelToUpdate) {
        int index = 1;
        for (Node node : container.getChildren()) {
            if (node instanceof Button) {
                Button starBtn = (Button) node;
                int value = index++;

                // Reset visual state to empty initially
                updateStarVisuals(container, 0);

                starBtn.setOnAction(e -> {
                    ratings.put(key, value);
                    updateStarVisuals(container, value);
                    if (labelToUpdate != null) {
                        updateLabelText(labelToUpdate, value);
                    }
                });
            }
        }
    }

    private void updateStarVisuals(HBox container, int value) {
        int i = 1;
        for (Node node : container.getChildren()) {
            if (node instanceof Button) {
                Button btn = (Button) node;
                btn.getStyleClass().removeAll("filled", "empty");
                if (i <= value) {
                    btn.getStyleClass().add("filled");
                } else {
                    btn.getStyleClass().add("empty");
                }
                i++;
            }
        }
    }

    private void updateLabelText(Label label, int value) {
        switch (value) {
            case 1: label.setText("Poor"); break;
            case 2: label.setText("Fair"); break;
            case 3: label.setText("Good"); break;
            case 4: label.setText("Very Good"); break;
            case 5: label.setText("Excellent!"); break;
            default: label.setText("Select a rating");
        }
    }

    private void handleSubmit() {
        if (ratings.get("overall") == 0) {
            ToastHelper.showWarning("Rating Required", "Please select at least an overall rating.");
            return;
        }

        User currentUser = SessionManager.getInstance().getCurrentUser();
        if (currentUser == null) return;

        Rating rating = new Rating(
                currentUser.getId(),
                mentor.getId(),
                ratings.get("overall"),
                ratings.get("communication"),
                ratings.get("knowledge"),
                ratings.get("responsiveness"),
                ratings.get("helpfulness"),
                feedbackTextArea.getText().trim()
        );

        if (ratingService.submitRating(rating)) {
            ToastHelper.showSuccess("Thank You!", "Your feedback has been submitted.");
            goBack();
        } else {
            ToastHelper.showError("Error", "Could not submit rating. Try again.");
        }
    }

    private void goBack() {
        if (sidebarController != null) {
            // FIX: Capture the returned controller and inject the sidebar dependency again
            Object controller = sidebarController.loadContentView("shared/mentor-rating.fxml");

            if (controller instanceof MentorRatingListController) {
                ((MentorRatingListController) controller).setSidebarController(sidebarController);
            }
        }
    }
}