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
            projectsGrid.add(card, col, row);

            col++;
            if (col >= COLUMNS) {
                col = 0;
                row++;
            }
        }
    }

    private VBox createRepositoryCard(Project project) {
        // Title
        Text titleText = new Text(project.getTitle());
        titleText.getStyleClass().add("project-title");

        // Author & Year
        Text authorText = new Text("Completed: " + project.getYear());
        authorText.getStyleClass().add("author-text");
        HBox authorBox = new HBox(authorText);
        authorBox.getStyleClass().add("project-author");

        // Header Box
        VBox headerBox = new VBox(10, titleText, authorBox);
        headerBox.getStyleClass().add("project-header");
        headerBox.setPadding(new javafx.geometry.Insets(15));

        // Description
        Text descriptionText = new Text(project.getDescription());
        descriptionText.getStyleClass().add("project-description");
        descriptionText.setWrappingWidth(400);

        // Category Tag
        Label categoryTag = new Label(project.getCategory());
        categoryTag.getStyleClass().add("tag");
        HBox tagsBox = new HBox(8, categoryTag);
        tagsBox.getStyleClass().add("project-tags");

        // Delete Button
        Button deleteBtn = new Button("🗑 Delete Project");
        deleteBtn.getStyleClass().add("delete-btn"); // Reusing styling or new styling
        deleteBtn.setOnAction(e -> handleDelete(project.getProjectId()));

        HBox actionBox = new HBox(deleteBtn);
        actionBox.setAlignment(Pos.CENTER_RIGHT);

        // Body Content
        VBox bodyContent = new VBox(15, descriptionText, tagsBox, actionBox);
        bodyContent.getStyleClass().add("project-body");
        bodyContent.setPadding(new javafx.geometry.Insets(15));
        VBox.setVgrow(bodyContent, Priority.ALWAYS);

        // Main Card
        VBox card = new VBox(headerBox, bodyContent);
        card.getStyleClass().add("project-card");

        return card;
    }

    private void handleDelete(int projectId) {
        // In a real app, you might want a confirmation dialog here
        if (projectService.deleteProject(projectId)) {
            ToastHelper.showSuccess("Deleted", "Project removed successfully.");
            loadRepositories(); // Refresh the list
        } else {
            ToastHelper.showError("Error", "Failed to delete project.");
        }
    }
}