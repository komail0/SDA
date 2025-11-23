package com.example.sda.controllers.shared;

import com.example.sda.models.Project;
import com.example.sda.models.User;
import com.example.sda.services.ProjectService;
import com.example.sda.utils.SessionManager;
import com.example.sda.utils.ToastHelper;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class AlumniRepositoryController implements Initializable {

    @FXML private Text totalProjectsText;
    @FXML private GridPane projectsGrid;
    @FXML private Label statusLabel;

    private final ProjectService projectService = new ProjectService();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadRepositories();
    }

    private void loadRepositories() {
        User user = SessionManager.getInstance().getCurrentUser();
        if (user == null) return;

        new Thread(() -> {
            List<Project> projects = projectService.getAlumniProjects(user.getId());

            Platform.runLater(() -> {
                totalProjectsText.setText(String.valueOf(projects.size()));
                projectsGrid.getChildren().clear();

                if (projects.isEmpty()) {
                    statusLabel.setText("You haven't uploaded any projects yet.");
                } else {
                    statusLabel.setText("");
                    renderProjects(projects);
                }
            });
        }).start();
    }

    private void renderProjects(List<Project> projects) {
        int row = 0;
        int col = 0;
        final int COLUMNS = 2;

        for (Project project : projects) {
            VBox card = createRepositoryCard(project);
            // Important: Allow card to grow to fill the grid cell
            GridPane.setHgrow(card, Priority.ALWAYS);
            projectsGrid.add(card, col, row);

            col++;
            if (col >= COLUMNS) {
                col = 0;
                row++;
            }
        }
    }

    private VBox createRepositoryCard(Project project) {
        // --- Title (Using Label for auto-wrapping) ---
        Label titleLabel = new Label(project.getTitle());
        titleLabel.getStyleClass().add("project-title");
        titleLabel.setWrapText(true);
        titleLabel.setMaxWidth(Double.MAX_VALUE);
        // Force white color to match dark theme (Label uses -fx-text-fill)
        titleLabel.setStyle("-fx-text-fill: #ffffff; -fx-font-size: 20px; -fx-font-weight: 700;");

        // --- Author & Year ---
        Label authorLabel = new Label("Completed: " + project.getYear());
        authorLabel.getStyleClass().add("author-text");
        authorLabel.setStyle("-fx-text-fill: #808080; -fx-font-size: 14px;");

        // --- Status Tag ---
        Label statusTag = new Label(project.getStatus().toUpperCase());
        statusTag.getStyleClass().add("status-tag");

        String status = project.getStatus().toLowerCase();
        if (status.equals("approved")) statusTag.getStyleClass().add("status-approved");
        else if (status.equals("rejected")) statusTag.getStyleClass().add("status-rejected");
        else statusTag.getStyleClass().add("status-pending");

        // --- Header Row (Title + Spacer + Status) ---
        HBox topRow = new HBox(10);
        topRow.setAlignment(Pos.CENTER_LEFT);
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        topRow.getChildren().addAll(titleLabel, spacer, statusTag);

        VBox headerBox = new VBox(8, topRow, authorLabel);
        headerBox.getStyleClass().add("project-header");
        headerBox.setPadding(new javafx.geometry.Insets(15));

        // --- Description (Using Label for auto-wrapping) ---
        Label descriptionLabel = new Label(project.getDescription());
        descriptionLabel.getStyleClass().add("project-description");
        descriptionLabel.setWrapText(true);
        descriptionLabel.setMaxWidth(Double.MAX_VALUE);
        // Force grey color
        descriptionLabel.setStyle("-fx-text-fill: #c0c0c0; -fx-font-size: 14px; -fx-line-spacing: 0.6;");

        // --- Category Tag ---
        Label categoryTag = new Label(project.getCategory());
        categoryTag.getStyleClass().add("tag");
        // Ensure tag text is visible
        categoryTag.setStyle("-fx-text-fill: #00ff88;");
        HBox tagsBox = new HBox(8, categoryTag);
        tagsBox.getStyleClass().add("project-tags");

        // --- Delete Button ---
        Button deleteBtn = new Button("🗑 Delete Project");
        deleteBtn.getStyleClass().add("delete-btn");
        deleteBtn.setOnAction(e -> handleDelete(project.getProjectId()));

        HBox actionBox = new HBox(deleteBtn);
        actionBox.setAlignment(Pos.CENTER_RIGHT);

        // --- Body Content ---
        VBox bodyContent = new VBox(15, descriptionLabel, tagsBox, actionBox);
        bodyContent.getStyleClass().add("project-body");
        bodyContent.setPadding(new javafx.geometry.Insets(15));
        VBox.setVgrow(bodyContent, Priority.ALWAYS);

        // --- Main Card ---
        VBox card = new VBox(headerBox, bodyContent);
        card.getStyleClass().add("project-card");
        // Ensure the card background is dark
        card.setStyle("-fx-background-color: #1a1a1a; -fx-background-radius: 20; -fx-border-color: #333; -fx-border-radius: 20; -fx-border-width: 1;");

        return card;
    }

    private void handleDelete(int projectId) {
        if (projectService.deleteProject(projectId)) {
            ToastHelper.showSuccess("Deleted", "Project removed successfully.");
            loadRepositories();
        } else {
            ToastHelper.showError("Error", "Failed to delete project.");
        }
    }
}