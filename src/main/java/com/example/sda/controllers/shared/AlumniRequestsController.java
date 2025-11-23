package com.example.sda.controllers.shared;

import com.example.sda.controllers.components.SidebarController;
import com.example.sda.models.MentorshipRequest;
import com.example.sda.models.User;
import com.example.sda.services.MentorshipService;
import com.example.sda.utils.SessionManager;
import com.example.sda.utils.ToastHelper;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.ResourceBundle;

public class AlumniRequestsController implements Initializable {

    @FXML private VBox requestsContainer;

    private final MentorshipService mentorshipService = new MentorshipService();

    // Needed for navigation to chat
    private SidebarController sidebarController;

    public void setSidebarController(SidebarController sidebarController) {
        this.sidebarController = sidebarController;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadIncomingRequests();
    }

    private void loadIncomingRequests() {
        User user = SessionManager.getInstance().getCurrentUser();
        if (user == null) return;

        new Thread(() -> {
            List<MentorshipRequest> requests = mentorshipService.getRequestsForMentor(user.getId());
            Platform.runLater(() -> {
                requestsContainer.getChildren().clear();
                if (requests.isEmpty()) {
                    VBox emptyState = new VBox(15);
                    emptyState.setStyle("-fx-alignment: center; -fx-padding: 50;");
                    Label icon = new Label("📭");
                    icon.setStyle("-fx-font-size: 64px; -fx-text-fill: #666;");
                    Label title = new Label("No Incoming Requests");
                    title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: white;");
                    emptyState.getChildren().addAll(icon, title);
                    requestsContainer.getChildren().add(emptyState);
                } else {
                    for (MentorshipRequest req : requests) {
                        // Filter: Only show Pending requests
                        if(req.getStatus().equalsIgnoreCase("pending")) {
                            requestsContainer.getChildren().add(createRequestCard(req));
                        }
                    }
                }
            });
        }).start();
    }

    private VBox createRequestCard(MentorshipRequest req) {
        VBox card = new VBox();
        card.getStyleClass().add("request-card");

        // --- Header ---
        HBox header = new HBox();
        header.getStyleClass().add("request-header");

        Label avatar = new Label("🎓");
        avatar.getStyleClass().add("mentor-avatar");

        VBox infoBox = new VBox();
        infoBox.getStyleClass().add("mentor-info");
        HBox.setHgrow(infoBox, Priority.ALWAYS);

        Label nameLabel = new Label("Student: " + req.getStudentName());
        nameLabel.getStyleClass().add("mentor-name");

        Label uniLabel = new Label(req.getUniversity() + " • " + req.getAcademicYear());
        uniLabel.getStyleClass().add("mentor-title");

        SimpleDateFormat sdf = new SimpleDateFormat("MMM d");
        String dateStr = (req.getRequestDate() != null) ? sdf.format(req.getRequestDate()) : "N/A";
        Label dateLabel = new Label("Received: " + dateStr);
        dateLabel.getStyleClass().add("request-date");

        infoBox.getChildren().addAll(nameLabel, uniLabel, dateLabel);

        VBox statusSection = new VBox();
        statusSection.getStyleClass().add("status-section");
        Label statusBadge = new Label("⏳ Pending");
        statusBadge.getStyleClass().addAll("status-badge");
        statusSection.getChildren().add(statusBadge);

        header.getChildren().addAll(avatar, infoBox, statusSection);

        // --- Body ---
        VBox body = new VBox();
        body.getStyleClass().add("request-body");

        Label projectTitle = new Label("Project: " + req.getTitle());
        projectTitle.getStyleClass().add("project-title");

        Label msgLabel = new Label("Message:");
        msgLabel.getStyleClass().add("your-message-label");

        // --- Content Row (Message + Horizontal Buttons) ---
        HBox contentRow = new HBox(15);
        contentRow.setAlignment(Pos.TOP_LEFT);

        Label msgContent = new Label(req.getDescription());
        msgContent.getStyleClass().add("request-message");
        msgContent.setWrapText(true);
        msgContent.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(msgContent, Priority.ALWAYS);

        // Buttons Container - HORIZONTAL (HBox)
        HBox buttonsBox = new HBox(10);
        buttonsBox.setAlignment(Pos.TOP_RIGHT);
        buttonsBox.setMinWidth(Region.USE_PREF_SIZE);

        // Move Upward
        buttonsBox.setTranslateY(-25);

        double BUTTON_WIDTH = 90.0;

        Button acceptBtn = new Button("Accept");
        acceptBtn.setPrefWidth(BUTTON_WIDTH);
        acceptBtn.setStyle("-fx-background-color: rgba(0, 255, 136, 0.15); -fx-text-fill: #00ff88; -fx-border-color: #00ff88; -fx-border-radius: 8; -fx-background-radius: 8; -fx-font-weight: bold; -fx-cursor: hand; -fx-padding: 6 10;");
        acceptBtn.setOnAction(e -> handleStatusUpdate(req.getRequestId(), "accepted"));

        Button rejectBtn = new Button("Reject");
        rejectBtn.setPrefWidth(BUTTON_WIDTH);
        rejectBtn.getStyleClass().add("delete-btn");
        rejectBtn.setOnAction(e -> handleStatusUpdate(req.getRequestId(), "rejected"));

        buttonsBox.getChildren().addAll(acceptBtn, rejectBtn);

        contentRow.getChildren().addAll(msgContent, buttonsBox);

        body.getChildren().addAll(projectTitle, msgLabel, contentRow);

        card.getChildren().addAll(header, body);
        return card;
    }

    private void handleStatusUpdate(int requestId, String newStatus) {
        if (mentorshipService.updateRequestStatus(requestId, newStatus)) {
            String msg = newStatus.equals("accepted") ? "Request Accepted!" : "Request Rejected.";
            ToastHelper.showSuccess("Success", msg);

            if (newStatus.equals("accepted") && sidebarController != null) {
                // NAVIGATE TO CHAT
                sidebarController.loadContentView("shared/chat.fxml");
            }

            loadIncomingRequests();
        } else {
            ToastHelper.showError("Error", "Failed to update status.");
        }
    }
}