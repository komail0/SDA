package com.example.sda.utils;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.DialogPane;

/**
 * Utility class to display standardized JavaFX alerts.
 */
public class AlertHelper {

    public static void showSuccessAlert(String title, String message) {
        showAlert(AlertType.INFORMATION, title, message);
    }

    public static void showErrorAlert(String title, String message) {
        showAlert(AlertType.ERROR, title, message);
    }

    public static void showWarningAlert(String title, String message) {
        showAlert(AlertType.WARNING, title, message);
    }

    private static void showAlert(AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        
        // Optional: Apply CSS styling to the dialog
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add(
            AlertHelper.class.getResource("/com/example/sda/css/components/alert-style.css").toExternalForm());
        dialogPane.getStyleClass().add("custom-alert");

        alert.showAndWait();
    }
}