package com.example.sda.controllers.admin;

import com.example.sda.controllers.components.AdminSidebarController;
import com.example.sda.services.AdminService;
import javafx.application.Platform; // Required for threading
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Label;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

public class AdminDashboardController {

    @FXML private Label totalUsersValue;
    @FXML private Label totalProjectsValue;
    @FXML private Label activeMentorshipsValue;
    @FXML private Label avgRatingValue;
    @FXML private Label updateTime;
    @FXML private PieChart userDistributionChart;

    private final AdminService adminService = new AdminService();
    private AdminSidebarController sidebarController; // Reference to parent

    @FXML
    public void initialize() {
        setUpdateTime();

        // OPTIMIZATION: Run Database calls in a background thread to prevent UI freezing
        new Thread(() -> {
            // 1. Fetch all data in the background
            int users = adminService.getTotalUsers();
            int projects = adminService.getTotalProjects();
            int mentors = adminService.getActiveMentors();
            double rating = adminService.getAverageRating();
            Map<String, Integer> distribution = adminService.getUserDistribution();

            // 2. Update UI on the JavaFX Application Thread
            Platform.runLater(() -> {
                // Update Labels
                totalUsersValue.setText(String.valueOf(users));
                totalProjectsValue.setText(String.valueOf(projects));
                activeMentorshipsValue.setText(String.valueOf(mentors));
                avgRatingValue.setText(String.format("%.1f", rating));

                // Update Chart
                updateChartData(distribution);
            });
        }).start();
    }

    // Called by AdminSidebarController when loading this view
    public void setSidebarController(AdminSidebarController sidebarController) {
        this.sidebarController = sidebarController;
    }

    private void setUpdateTime() {
        updateTime.setText("📅 Last updated: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("h:mm a")));
    }

    private void updateChartData(Map<String, Integer> distribution) {
        if (userDistributionChart == null) return;
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
        for (Map.Entry<String, Integer> entry : distribution.entrySet()) {
            pieChartData.add(new PieChart.Data(entry.getKey() + " (" + entry.getValue() + ")", entry.getValue()));
        }
        userDistributionChart.setData(pieChartData);
    }

    // --- NAVIGATION HANDLERS ---
    // These use the Sidebar to switch views

    @FXML
    private void handleViewUsers() {
        if (sidebarController != null) {
            sidebarController.loadContentView("admin/user-management-view.fxml");
            sidebarController.setActiveButton(sidebarController.getUsersButton());
        }
    }

    @FXML
    private void handleViewProjects() {
        if (sidebarController != null) {
            sidebarController.loadContentView("admin/repository-management-view.fxml");
            sidebarController.setActiveButton(sidebarController.getRepoButton());
        }
    }

    @FXML
    private void handleGenerateReports() {
        if (sidebarController != null) {
            sidebarController.loadContentView("admin/reports-generation-view.fxml");
            sidebarController.setActiveButton(sidebarController.getReportsButton());
        }
    }

    // Redirect aliases for the Quick Action cards
    @FXML private void handleManageUsers() { handleViewUsers(); }
    @FXML private void handleReviewProjects() { handleViewProjects(); }
    @FXML private void handleViewMentorships() { /* Placeholder */ }
    @FXML private void handleViewRatings() { /* Placeholder */ }
}