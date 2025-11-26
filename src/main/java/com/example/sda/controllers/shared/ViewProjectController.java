package com.example.sda.controllers.shared;

import com.example.sda.dao.RatingDAO; // NEW IMPORT
import com.example.sda.models.Project;
import com.example.sda.services.ProjectService;
import com.example.sda.utils.ToastHelper;
import com.example.sda.controllers.components.SidebarController;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import java.awt.Desktop;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ResourceBundle;

public class ViewProjectController implements Initializable {

    @FXML private Text projectTitle;
    @FXML private Label categoryLabel;
    @FXML private Label completionDateLabel;
    @FXML private Label universityLabel;
    @FXML private Label statusLabel;
    @FXML private Button mentorshipBtn;
    @FXML private Button githubBtn;
    @FXML private Button closeBtn;

    // NEW: Rating Label
    @FXML private Label ratingBadge;

    @FXML private Label descriptionLabel;
    @FXML private FlowPane technologiesContainer;
    @FXML private Label supervisorLabel;
    @FXML private Label uploaderLabel;
    @FXML private Label uploadDateLabel;
    @FXML private Button viewPdfBtn;

    @FXML private VBox pdfSection;

    private final ProjectService projectService = new ProjectService();
    private final RatingDAO ratingDAO = new RatingDAO(); // NEW DAO

    private Project currentProject;
    private int projectId;
    private SidebarController sidebarController;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        statusLabel.setText("Awaiting project ID...");
        closeBtn.setOnAction(this::handleClose);
        githubBtn.setOnAction(this::handleViewGitHub);
        viewPdfBtn.setOnAction(this::handleViewPdf);
        mentorshipBtn.setOnAction(this::handleMentorshipRequest);

        // Hide badge initially until data loads
        if (ratingBadge != null) ratingBadge.setVisible(false);
    }

    public void setSidebarController(SidebarController controller) {
        this.sidebarController = controller;
    }

    public void setProjectDetails(int projectId) {
        this.projectId = projectId;
        statusLabel.setText("Loading details for Project ID: " + projectId);
        loadProjectDetails(projectId);
    }

    private void loadProjectDetails(int id) {
        new Thread(() -> {
            Project project = projectService.getProjectById(id);

            // NEW: Fetch Rating in background
            double avgRating = 0.0;
            if (project != null) {
                avgRating = ratingDAO.getMentorAverageRating(project.getUserId());
            }

            double finalAvgRating = avgRating;
            Platform.runLater(() -> {
                if (project != null) {
                    currentProject = project;
                    populateUI(project, finalAvgRating);
                } else {
                    statusLabel.setText("Error: Project not found.");
                    statusLabel.getStyleClass().add("error-message");
                }
            });
        }).start();
    }

    private void populateUI(Project project, double rating) {
        projectTitle.setText(project.getTitle());
        categoryLabel.setText(project.getCategory());
        completionDateLabel.setText(String.valueOf(project.getYear()));
        universityLabel.setText(project.getUniversity());
        descriptionLabel.setText(project.getDescription());
        supervisorLabel.setText(project.getSupervisor() != null && !project.getSupervisor().isEmpty() ? project.getSupervisor() : "N/A");
        uploaderLabel.setText(project.getAuthorName() != null ? project.getAuthorName() : "Unknown");

        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM d, yyyy");
        uploadDateLabel.setText(project.getUploadedAt() != null ? dateFormat.format(project.getUploadedAt()) : "N/A");

        // GitHub Button Logic
        if (project.getGithubLink() == null || project.getGithubLink().isEmpty()) {
            githubBtn.setDisable(true);
            githubBtn.setText("GitHub Link Unavailable");
        } else {
            githubBtn.setDisable(false);
            githubBtn.setText("💻 View on GitHub");
        }

        // NEW: Populate Rating Badge
        if (ratingBadge != null) {
            ratingBadge.setVisible(true);
            if (rating > 0) {
                ratingBadge.setText(String.format("⭐ %.1f", rating));
                ratingBadge.setStyle("-fx-background-color: #ffbb33; -fx-text-fill: black; -fx-font-weight: bold; -fx-padding: 8 15; -fx-background-radius: 5; -fx-font-size: 14px;");
            } else {
                ratingBadge.setText("No Ratings");
                ratingBadge.setStyle("-fx-background-color: #444; -fx-text-fill: #aaa; -fx-font-weight: normal; -fx-padding: 8 15; -fx-background-radius: 5; -fx-font-size: 14px;");
            }
        }

        // Technologies Logic
        technologiesContainer.getChildren().clear();
        String techString = project.getTechnologies();
        if (techString != null && !techString.isEmpty()) {
            for (String tech : techString.split(",")) {
                Label tag = new Label(tech.trim());
                tag.getStyleClass().add("tech-tag");
                technologiesContainer.getChildren().add(tag);
            }
        } else {
            Label noTech = new Label("No technologies listed.");
            noTech.getStyleClass().add("info-value");
            technologiesContainer.getChildren().add(noTech);
        }

        statusLabel.setText("Details loaded successfully.");
        statusLabel.getStyleClass().remove("status-message");
        statusLabel.setManaged(false);
    }

    @FXML
    private void handleClose(ActionEvent event) {
        if (this.sidebarController == null) {
            ToastHelper.showError("Navigation Error", "Sidebar context is missing. Cannot return to search.");
            return;
        }

        Object searchController = sidebarController.loadContentView("shared/Search.fxml");
        sidebarController.setActiveMenuItem(sidebarController.searchProjectsButton);

        if (searchController instanceof ProjectSearchController) {
            ProjectSearchController psc = (ProjectSearchController) searchController;
            psc.setSidebarController(this.sidebarController);
            psc.handleSearch(null);
        } else {
            ToastHelper.showWarning("Refresh Error", "Failed to load Project Search controller instance.");
        }
    }

    @FXML
    private void handleViewGitHub(ActionEvent event) {
        if (currentProject != null && currentProject.getGithubLink() != null) {
            String url = currentProject.getGithubLink();
            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                try {
                    Desktop.getDesktop().browse(new URI(url));
                } catch (Exception e) {
                    ToastHelper.showError("Browser Error", "Could not open GitHub link: " + url);
                }
            } else {
                ToastHelper.showWarning("System Warning", "Desktop browsing is not supported.");
            }
        }
    }

    @FXML
    private void handleViewPdf(ActionEvent event) {
        if (currentProject != null && currentProject.getPdfFileBytes() != null) {
            try {
                File tempPdf = File.createTempFile(currentProject.getTitle().replaceAll("\\s+", "_"), ".pdf");
                try (FileOutputStream fos = new FileOutputStream(tempPdf)) {
                    fos.write(currentProject.getPdfFileBytes());
                }

                if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.OPEN)) {
                    Desktop.getDesktop().open(tempPdf);
                    ToastHelper.showSuccess("PDF Viewer", "Opening " + tempPdf.getName());
                } else {
                    ToastHelper.showWarning("PDF Error", "Desktop file opening is not supported.");
                }
            } catch (IOException e) {
                ToastHelper.showError("PDF Error", "Failed to create or open temporary PDF file.");
                e.printStackTrace();
            }
        } else {
            ToastHelper.showWarning("PDF Error", "PDF file data is unavailable for this project.");
        }
    }

    @FXML
    private void handleMentorshipRequest(ActionEvent event) {
        if (currentProject == null) return;

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/sda/fxml/shared/Send-Request.fxml"));
            Parent root = loader.load();

            SendRequestController controller = loader.getController();
            controller.setRequestData(currentProject);

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initStyle(StageStyle.TRANSPARENT);

            Scene scene = new Scene(root);
            scene.setFill(Color.TRANSPARENT);
            stage.setScene(scene);
            stage.showAndWait();

        } catch (IOException e) {
            ToastHelper.showError("Error", "Could not open request form.");
            e.printStackTrace();
        }
    }
}