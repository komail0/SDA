package com.example.sda.controllers.admin;

import com.example.sda.services.AdminService;
import com.example.sda.utils.ToastHelper; // NEW IMPORT
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.*;
import javafx.scene.control.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ReportsGenerationController {

    @FXML private Label totalUsersLabel;
    @FXML private Label totalProjectsLabel;
    @FXML private Label totalMentorsLabel;
    @FXML private Label avgRatingLabel;
    @FXML private Label reportDateLabel;
    @FXML private PieChart userDistributionChart;
    @FXML private BarChart<String, Number> projectStatusChart;
    @FXML private TextArea reportSummary;
    @FXML private ComboBox<String> reportTypeCombo;

    private final AdminService adminService = new AdminService();

    @FXML
    public void initialize() {
        setupReportTypes();
        loadReportData();
    }

    private void setupReportTypes() {
        reportTypeCombo.setItems(FXCollections.observableArrayList(
                "Complete Overview", "User Statistics", "Project Analysis"
        ));
        reportTypeCombo.setValue("Complete Overview");
    }

    private void loadReportData() {
        // 1. Stats
        totalUsersLabel.setText(String.valueOf(adminService.getTotalUsers()));
        totalProjectsLabel.setText(String.valueOf(adminService.getTotalProjects()));
        totalMentorsLabel.setText(String.valueOf(adminService.getActiveMentors()));
        avgRatingLabel.setText(String.format("%.1f/5.0", adminService.getAverageRating()));

        // 2. User Chart
        ObservableList<PieChart.Data> pieData = FXCollections.observableArrayList();
        adminService.getUserDistribution().forEach((k, v) -> pieData.add(new PieChart.Data(k, v)));
        userDistributionChart.setData(pieData);

        // 3. Project Chart
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Status");
        adminService.getProjectStatusDistribution().forEach((k, v) -> series.getData().add(new XYChart.Data<>(k, v)));
        projectStatusChart.getData().clear();
        projectStatusChart.getData().add(series);

        // 4. Summary
        generateSummary();

        reportDateLabel.setText("Generated: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("MMM dd, HH:mm")));
    }

    private void generateSummary() {
        StringBuilder sb = new StringBuilder();
        sb.append("SYSTEM REPORT\n================\n");
        sb.append("Total Users: ").append(totalUsersLabel.getText()).append("\n");
        sb.append("Total Projects: ").append(totalProjectsLabel.getText()).append("\n");
        sb.append("Mentors: ").append(totalMentorsLabel.getText()).append("\n");
        sb.append("Avg Rating: ").append(avgRatingLabel.getText()).append("\n\n");
        reportSummary.setText(sb.toString());
    }

    @FXML
    private void handleRefresh() {
        loadReportData();
        ToastHelper.showSuccess("Refreshed", "Data updated.");
    }

    @FXML
    private void handleGenerateReport() {
        loadReportData();
        ToastHelper.showSuccess("Report Generated", "Analysis complete.");
    }


}