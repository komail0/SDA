package com.example.sda.controllers.admin;

import com.example.sda.models.User;
import com.example.sda.services.AdminService;
import com.example.sda.utils.ToastHelper; // NEW IMPORT
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

public class UserManagementController {

    @FXML private TableView<User> usersTable;
    @FXML private TableColumn<User, Integer> colId;
    @FXML private TableColumn<User, String> colUsername;
    @FXML private TableColumn<User, String> colEmail;
    @FXML private TableColumn<User, String> colRole;
    @FXML private Label totalUsersLabel;
    @FXML private TextField searchField;

    private final AdminService adminService = new AdminService();
    private ObservableList<User> usersList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        setupTable();
        loadUsers();
    }

    private void setupTable() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colUsername.setCellValueFactory(new PropertyValueFactory<>("username"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colRole.setCellValueFactory(new PropertyValueFactory<>("accountType"));
    }

    private void loadUsers() {
        usersList.clear();
        usersList.addAll(adminService.getAllUsers());
        usersTable.setItems(usersList);
        totalUsersLabel.setText("Total Users: " + usersList.size());
    }

    @FXML
    private void handleSearch() {
        String searchText = searchField.getText().toLowerCase();
        if (searchText.isEmpty()) {
            usersTable.setItems(usersList);
        } else {
            ObservableList<User> filtered = FXCollections.observableArrayList();
            for (User user : usersList) {
                if (user.getUsername().toLowerCase().contains(searchText) ||
                        user.getEmail().toLowerCase().contains(searchText) ||
                        user.getAccountType().toString().toLowerCase().contains(searchText)) {
                    filtered.add(user);
                }
            }
            usersTable.setItems(filtered);
        }
    }

    @FXML
    private void handleRefresh() {
        loadUsers();
        searchField.clear();
        ToastHelper.showSuccess("Refreshed", "User list updated.");
    }

    @FXML
    private void handleDelete() {
        User selected = usersTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            ToastHelper.showWarning("Selection Needed", "Please select a user to delete.");
            return;
        }

        // KEEPING CONFIRMATION DIALOG (Toasts cannot do Yes/No input)
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Delete " + selected.getUsername() + "?");
        if (confirm.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            if (adminService.deleteUser(selected.getId())) {
                loadUsers();
                ToastHelper.showSuccess("Deleted", "User deleted successfully.");
            } else {
                // REPLACED ALERT WITH TOAST
                ToastHelper.showError("Error", "Failed to delete user.");
            }
        }
    }
}