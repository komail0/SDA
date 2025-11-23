package com.example.sda.controllers.admin;

import com.example.sda.models.Project;
import com.example.sda.models.ProjectDTO;
import com.example.sda.services.AdminService;
import com.example.sda.utils.ToastHelper; // NEW IMPORT
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

public class RepositoryManagementController {

    @FXML private TableView<ProjectDTO> projectsTable;
    @FXML private TableColumn<ProjectDTO, Integer> colId;
    @FXML private TableColumn<ProjectDTO, String> colTitle;
    @FXML private TableColumn<ProjectDTO, String> colDescription;
    @FXML private TableColumn<ProjectDTO, String> colOwner;
    @FXML private TableColumn<ProjectDTO, String> colStatus;
    @FXML private Label totalProjectsLabel;
    @FXML private TextField searchField;
    @FXML private ComboBox<String> statusFilter;

    private final AdminService adminService = new AdminService();
    private ObservableList<ProjectDTO> projectsList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        setupTable();
        // Match status strings to database values
        statusFilter.setItems(FXCollections.observableArrayList("All", "pending", "approved", "rejected"));
        statusFilter.setValue("All");
        loadProjects();
    }

    private void setupTable() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        colDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        colOwner.setCellValueFactory(new PropertyValueFactory<>("owner"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
    }

    private void loadProjects() {
        projectsList.clear();
        for (Project p : adminService.getAllProjects()) {
            String currentStatus = (p.getStatus() == null) ? "pending" : p.getStatus();
            String author = (p.getAuthorName() == null) ? "Unknown" : p.getAuthorName();

            projectsList.add(new ProjectDTO(
                    p.getProjectId(),
                    p.getTitle(),
                    p.getDescription(),
                    author,
                    currentStatus
            ));
        }
        projectsTable.setItems(projectsList);
        totalProjectsLabel.setText("Total Projects: " + projectsList.size());
    }

    @FXML
    private void handleSearch() {
        String searchText = searchField.getText().toLowerCase();
        String statusValue = statusFilter.getValue();

        ObservableList<ProjectDTO> filtered = FXCollections.observableArrayList();
        for (ProjectDTO p : projectsList) {
            boolean matchesSearch = p.getTitle().toLowerCase().contains(searchText) ||
                    p.getOwner().toLowerCase().contains(searchText);
            boolean matchesStatus = statusValue.equals("All") || p.getStatus().equalsIgnoreCase(statusValue);

            if (matchesSearch && matchesStatus) filtered.add(p);
        }
        projectsTable.setItems(filtered);
    }

    @FXML
    private void handleRefresh() {
        loadProjects();
        searchField.clear();
        statusFilter.setValue("All");
        ToastHelper.showSuccess("Refreshed", "Project list updated."); // Added feedback
    }

    @FXML
    private void handleApprove() {
        ProjectDTO selected = projectsTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            if(adminService.approveProject(selected.getId())) {
                handleRefresh();
                // REPLACED ALERT WITH TOAST
                ToastHelper.showSuccess("Success", "Project approved successfully.");
            } else {
                ToastHelper.showError("Error", "Could not approve project.");
            }
        } else {
            ToastHelper.showWarning("Selection Needed", "Please select a project to approve.");
        }
    }

    @FXML
    private void handleReject() {
        ProjectDTO selected = projectsTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            if(adminService.rejectProject(selected.getId())) {
                handleRefresh();
                // REPLACED ALERT WITH TOAST
                ToastHelper.showSuccess("Success", "Project rejected.");
            } else {
                ToastHelper.showError("Error", "Could not reject project.");
            }
        } else {
            ToastHelper.showWarning("Selection Needed", "Please select a project to reject.");
        }
    }

    @FXML
    private void handleDelete() {
        ProjectDTO selected = projectsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            ToastHelper.showWarning("Selection Needed", "Please select a project to delete.");
            return;
        }

        // KEEPING CONFIRMATION DIALOG (Toasts cannot do Yes/No input)
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to delete this project?");
        alert.setHeaderText("Delete Project");
        if (alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            if (adminService.deleteProject(selected.getId())) {
                handleRefresh();
                ToastHelper.showSuccess("Deleted", "Project deleted successfully.");
            } else {
                ToastHelper.showError("Error", "Failed to delete project.");
            }
        }
    }
}