package com.example.sda.controllers.shared;

import com.example.sda.controllers.components.SidebarController;
import com.example.sda.models.User;
import com.example.sda.services.RatingService;
import com.example.sda.utils.SessionManager;
import com.example.sda.utils.ToastHelper;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class MentorRatingListController implements Initializable {

    @FXML private GridPane mentorsGrid;
    @FXML private VBox emptyState;

    private final RatingService ratingService = new RatingService();
    private SidebarController sidebarController;

    // Dependency Injection
    public void setSidebarController(SidebarController sidebarController) {
        this.sidebarController = sidebarController;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadMentors();
    }

    private void loadMentors() {
        User currentUser = SessionManager.getInstance().getCurrentUser();
        if (currentUser == null) return;

        new Thread(() -> {
            List<User> mentors = ratingService.getMentorsForStudent(currentUser.getId());
            
            Platform.runLater(() -> {
                mentorsGrid.getChildren().clear();
                if (mentors.isEmpty()) {
                    mentorsGrid.setVisible(false);
                    emptyState.setVisible(true);
                    emptyState.setManaged(true);
                } else {
                    mentorsGrid.setVisible(true);
                    emptyState.setVisible(false);
                    emptyState.setManaged(false);
                    
                    int col = 0;
                    int row = 0;
                    for (User mentor : mentors) {
                        VBox card = createMentorCard(mentor, currentUser.getId());
                        mentorsGrid.add(card, col, row);
                        col++;
                        if (col == 2) {
                            col = 0;
                            row++;
                        }
                    }
                }
            });
        }).start();
    }

    private VBox createMentorCard(User mentor, int studentId) {
        VBox card = new VBox();
        card.getStyleClass().add("mentor-card");

        Label avatar = new Label("👨‍💼");
        avatar.getStyleClass().add("mentor-avatar");

        VBox info = new VBox();
        info.getStyleClass().add("mentor-info");
        
        Label name = new Label(mentor.getUsername());
        name.getStyleClass().add("mentor-name");
        
        Label role = new Label("Alumni Mentor");
        role.getStyleClass().add("mentor-title");

        info.getChildren().addAll(name, role);

        Region spacer = new Region();
        spacer.getStyleClass().add("separator");

        Button actionBtn = new Button();
        
        // CHECK IF ALREADY RATED
        boolean isRated = ratingService.hasRated(studentId, mentor.getId());
        
        if (isRated) {
            actionBtn.setText("✓ Already Rated");
            actionBtn.getStyleClass().add("rated-btn");
            actionBtn.setDisable(true); // Disable visually
            
            // Even though disabled, we add a handler just in case or for tooltips
            actionBtn.setOnAction(e -> {
               ToastHelper.showWarning("Already Rated", "You have already rated " + mentor.getUsername());
            });
        } else {
            actionBtn.setText("⭐ Rate Mentor");
            actionBtn.getStyleClass().add("rate-btn");
            actionBtn.setOnAction(e -> openRatingView(mentor));
        }

        card.getChildren().addAll(avatar, info, spacer, actionBtn);
        return card;
    }

    private void openRatingView(User mentor) {
        if (sidebarController == null) return;

        try {
            // We have to manually load the view to get the controller and pass data
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/sda/fxml/shared/feedback-rating-view.fxml"));
            Parent view = loader.load();
            
            FeedbackRatingController controller = loader.getController();
            controller.setMentorData(mentor, sidebarController);
            
            // Now manually inject into sidebar (bypass SidebarController.loadContentView() to avoid reloading FXML)
            sidebarController.loadViewDirectly(view);
            
        } catch (IOException e) {
            e.printStackTrace();
            ToastHelper.showError("Error", "Could not load rating form.");
        }
    }
}