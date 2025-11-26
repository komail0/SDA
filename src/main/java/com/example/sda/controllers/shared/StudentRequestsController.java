package com.example.sda.controllers.shared;

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
import javafx.scene.layout.VBox;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;

public class StudentRequestsController implements Initializable {

    @FXML private VBox requestsContainer;

    private final MentorshipService mentorshipService = new MentorshipService();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadRequests();
    }

    private void loadRequests() {
        User user = SessionManager.getInstance().getCurrentUser();
        if (user == null) return;

        new Thread(() -> {
            List<MentorshipRequest> requests = mentorshipService.getStudentRequests(user.getId());

            // --- SORTING LOGIC: Pending -> Accepted -> Rejected ---
            requests.sort(Comparator.comparingInt(req -> {
                String status = req.getStatus().toLowerCase();
                switch (status) {
                    case "pending": return 1;
                    case "accepted": return 2;
                    case "rejected": return 3;
                    default: return 4;
                }
            }));
            // ------------------------------------------------------

            Platform.runLater(() -> {
                requestsContainer.getChildren().clear();
                if (requests.isEmpty()) {
                    VBox emptyState = new VBox(15);
                    emptyState.setStyle("-fx-alignment: center; -fx-padding: 50;");
                    Label icon = new Label("📭");
                    icon.setStyle("-fx-font-size: 64px; -fx-text-fill: #666;");
                    Label title = new Label("No Requests Yet");
                    title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: white;");
                    emptyState.getChildren().addAll(icon, title);
                    requestsContainer.getChildren().add(emptyState);
                } else {
                    for (MentorshipRequest req : requests) {
                        requestsContainer.getChildren().add(createRequestCard(req));
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

        Label avatar = new Label("👨‍💼");
        avatar.getStyleClass().add("mentor-avatar");

        VBox mentorInfo = new VBox();
        mentorInfo.getStyleClass().add("mentor-info");
        HBox.setHgrow(mentorInfo, Priority.ALWAYS);

        Label nameLabel = new Label(req.getMentorName());
        nameLabel.getStyleClass().add("mentor-name");

        Label titleLabel = new Label("Academic Mentor");
        titleLabel.getStyleClass().add("mentor-title");

        SimpleDateFormat sdf = new SimpleDateFormat("MMM d, yyyy");
        String dateStr = (req.getRequestDate() != null) ? sdf.format(req.getRequestDate()) : "N/A";
        Label dateLabel = new Label("Requested on: " + dateStr);
        dateLabel.getStyleClass().add("request-date");

        mentorInfo.getChildren().addAll(nameLabel, titleLabel, dateLabel);

        // Status Badge
        VBox statusSection = new VBox();
        statusSection.getStyleClass().add("status-section");

        Label statusBadge = new Label();
        String status = req.getStatus().toLowerCase();

        statusBadge.getStyleClass().add("status-badge");

        if (status.equals("accepted")) {
            statusBadge.setText("✅ Accepted");
            statusBadge.getStyleClass().add("status-accepted");
        } else if (status.equals("rejected")) {
            statusBadge.setText("❌ Rejected");
            statusBadge.getStyleClass().add("status-rejected");
        } else {
            statusBadge.setText("⏳ Pending");
        }
        statusSection.getChildren().add(statusBadge);

        header.getChildren().addAll(avatar, mentorInfo, statusSection);

        // --- Body ---
        VBox body = new VBox();
        body.getStyleClass().add("request-body");

        Label projectTitle = new Label("Project: " + req.getTitle());
        projectTitle.getStyleClass().add("project-title");

        Label yourMsgLabel = new Label("Your Message:");
        yourMsgLabel.getStyleClass().add("your-message-label");

        // --- Message Row ---
        HBox messageRow = new HBox(15);
        messageRow.setAlignment(Pos.TOP_LEFT);

        Label msgContent = new Label(req.getDescription());
        msgContent.getStyleClass().add("request-message");
        msgContent.setWrapText(true);
        msgContent.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(msgContent, Priority.ALWAYS);

        messageRow.getChildren().add(msgContent);

        // --- CONDITIONAL DELETE BUTTON ---
        // Only show delete button if status is NOT accepted
        if (!status.equals("accepted")) {
            Button deleteBtn = new Button("🗑️ Delete Request");
            deleteBtn.getStyleClass().add("delete-btn");
            deleteBtn.setOnAction(e -> handleDelete(req.getRequestId()));
            deleteBtn.setMinWidth(Button.USE_PREF_SIZE);

            messageRow.getChildren().add(deleteBtn);
        }
        // ---------------------------------

        body.getChildren().addAll(projectTitle, yourMsgLabel, messageRow);

        card.getChildren().addAll(header, body);
        return card;
    }

    private void handleDelete(int requestId) {
        if (mentorshipService.cancelRequest(requestId)) {
            ToastHelper.showSuccess("Deleted", "Request cancelled successfully.");
            loadRequests();
        } else {
            ToastHelper.showError("Error", "Failed to delete request.");
        }
    }
}