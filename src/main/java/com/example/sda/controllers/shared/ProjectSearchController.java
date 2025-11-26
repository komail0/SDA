package com.example.sda.controllers.shared;

import com.example.sda.models.Project;
import com.example.sda.services.ProjectService;
import com.example.sda.utils.ToastHelper;
import com.example.sda.controllers.components.SidebarController;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Duration;
import javafx.util.StringConverter;
import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;

public class ProjectSearchController implements Initializable {

    @FXML private TextField searchField;
    @FXML private DatePicker yearFilterPicker;
    @FXML private TextField categoryFilterField;
    @FXML private Button applyFilterBtn;

    @FXML private Text totalProjectsText;
    @FXML private Text totalContributorsText;
    @FXML private GridPane projectsGrid;
    @FXML private Label projectStatusLabel;

    private final ProjectService projectService = new ProjectService();
    private Timeline autoRefreshTimeline;

    private SidebarController sidebarController;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setupYearPicker();
        handleSearch(null);
    }

    public void setSidebarController(SidebarController sidebarController) {
        this.sidebarController = sidebarController;
    }

    public void stopAutoRefresh() {
        if (autoRefreshTimeline != null) {
            autoRefreshTimeline.stop();
        }
    }

    private void setupYearPicker() {
        yearFilterPicker.setConverter(new StringConverter<LocalDate>() {
            @Override
            public String toString(LocalDate date) {
                return (date != null) ? String.valueOf(date.getYear()) : "";
            }

            @Override
            public LocalDate fromString(String string) {
                if (string != null && !string.isEmpty()) {
                    try {
                        int year = Integer.parseInt(string);
                        return LocalDate.of(year, 1, 1);
                    } catch (NumberFormatException e) { return null; }
                }
                return null;
            }
        });
        yearFilterPicker.setPromptText("Select year");
        yearFilterPicker.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                setDisable(empty || date.isAfter(LocalDate.now()));
            }
        });
    }

    private void setupAutoRefresh() {
        if (autoRefreshTimeline != null) autoRefreshTimeline.stop();
        autoRefreshTimeline = new Timeline(new KeyFrame(Duration.seconds(10), this::handleSearch));
        autoRefreshTimeline.setCycleCount(Timeline.INDEFINITE);
        autoRefreshTimeline.play();
    }

    @FXML
    public void handleSearch(ActionEvent event) {
        projectsGrid.getChildren().clear();
        projectStatusLabel.setText("Loading projects...");

        String keyword = searchField.getText().trim();
        Integer year = (yearFilterPicker.getValue() != null) ? yearFilterPicker.getValue().getYear() : null;
        String category = categoryFilterField.getText().trim();

        new Thread(() -> {
            try {
                List<Project> projects = projectService.searchProjects(keyword, year, category);
                int totalProjects = projectService.countTotalProjects();
                int totalContributors = projectService.countUniqueContributors();

                Platform.runLater(() -> {
                    updateStats(totalProjects, totalContributors);
                    if (projects.isEmpty()) {
                        projectStatusLabel.setText("No projects found matching your criteria.");
                    } else {
                        projectStatusLabel.setText("");
                        renderProjects(projects);
                    }
                });

            } catch (Exception e) {
                Platform.runLater(() -> {
                    ToastHelper.showError("Database Error", "Failed to fetch projects.");
                    projectStatusLabel.setText("Failed to load projects: " + e.getMessage());
                });
            }
        }).start();
    }

    private void updateStats(int totalProjects, int totalContributors) {
        totalProjectsText.setText(String.valueOf(totalProjects));
        totalContributorsText.setText(String.valueOf(totalContributors));
    }

    private void renderProjects(List<Project> projects) {
        projectsGrid.getChildren().clear();
        int row = 0;
        int col = 0;
        final int COLUMNS = 2;

        for (Project project : projects) {
            VBox projectCard = createProjectCard(project);
            projectsGrid.add(projectCard, col, row);

            col++;
            if (col >= COLUMNS) {
                col = 0;
                row++;
            }
        }
    }

    /**
     * Updated Card Design:
     * 1. Reduced padding to decrease height.
     * 2. Removed footer container (and its border line).
     * 3. Moved View Details button into the main body, centered.
     */
    private VBox createProjectCard(Project project) {
        Text titleText = new Text(project.getTitle());
        titleText.getStyleClass().add("project-title");

        String authorName = project.getAuthorName() != null ? project.getAuthorName() : "N/A";
        Text authorText = new Text(authorName + " • " + project.getYear());
        authorText.getStyleClass().add("author-text");
        HBox authorBox = new HBox(authorText);
        authorBox.getStyleClass().add("project-author");

        VBox headerBox = new VBox(10, titleText, authorBox);
        headerBox.getStyleClass().add("project-header");
        // REDUCED PADDING (was 25)
        headerBox.setPadding(new javafx.geometry.Insets(15));

        Text descriptionText = new Text(project.getDescription());
        descriptionText.getStyleClass().add("project-description");
        descriptionText.setWrappingWidth(400);

        Label categoryTag = new Label(project.getCategory());
        categoryTag.getStyleClass().add("tag");

        HBox tagsBox = new HBox(8, categoryTag);
        tagsBox.getStyleClass().add("project-tags");

        // --- Button Moved Here ---
        Button viewBtn = new Button("View Details");
        viewBtn.getStyleClass().add("view-btn");
        viewBtn.setOnAction(e -> loadDetailsView(project.getProjectId()));

        HBox buttonContainer = new HBox(viewBtn);
        buttonContainer.setAlignment(Pos.CENTER); // Center in card
        // -------------------------

        // Add button directly to body, remove footer box
        VBox bodyContent = new VBox(15, descriptionText, tagsBox, buttonContainer);
        bodyContent.getStyleClass().add("project-body");
        // REDUCED PADDING (was 25)
        bodyContent.setPadding(new javafx.geometry.Insets(15));
        VBox.setVgrow(bodyContent, javafx.scene.layout.Priority.ALWAYS);

        VBox card = new VBox(headerBox, bodyContent);
        card.getStyleClass().add("project-card");

        return card;
    }

    private void loadDetailsView(int projectId) {
        stopAutoRefresh();

        if (this.sidebarController == null) {
            ToastHelper.showError("UI Error", "Sidebar reference is missing. Cannot navigate.");
            setupAutoRefresh();
            return;
        }

        try {
            Object loadedController = sidebarController.loadContentView("shared/View-Project.fxml");

            if (loadedController instanceof ViewProjectController) {
                ViewProjectController detailController = (ViewProjectController) loadedController;
                detailController.setSidebarController(this.sidebarController);
                detailController.setProjectDetails(projectId);
            } else {
                ToastHelper.showError("Load Error", "Failed to load project details view.");
                setupAutoRefresh();
            }

        } catch (Exception e) {
            ToastHelper.showError("Navigation Error", "An unexpected error occurred.");
            e.printStackTrace();
            setupAutoRefresh();
        }
    }
}