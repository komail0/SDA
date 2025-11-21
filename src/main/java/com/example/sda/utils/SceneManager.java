package com.example.sda.utils;

import com.example.sda.HelloApplication;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Manages the navigation and loading of different FXML scenes.
 * Enforces maximized window state globally.
 */
public class SceneManager {
    private static SceneManager instance;

    private SceneManager() {
        // Private constructor for Singleton pattern
    }

    public static SceneManager getInstance() {
        if (instance == null) {
            instance = new SceneManager();
        }
        return instance;
    }

    /**
     * Loads a new FXML scene into the current window and maximizes it.
     * @param fxmlFile The path to the FXML file (e.g., "auth/login-view.fxml").
     * @param title The title for the stage.
     * @param event The ActionEvent that triggered the scene change.
     */
    public void loadScene(String fxmlFile, String title, ActionEvent event) {
        try {
            // Get the Stage from the event source
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            // Construct the full resource path
            String fullPath = "/com/example/sda/fxml/auth/" + fxmlFile;

            // Load FXML
            FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource(fullPath));
            Parent root = fxmlLoader.load();

            // Set up new scene
            Scene scene = new Scene(root);
            stage.setTitle(title);
            stage.setScene(scene);

            // Enforce maximized to usable screen area (VisualBounds)
            javafx.geometry.Rectangle2D visualBounds = javafx.stage.Screen.getPrimary().getVisualBounds();
            stage.setX(visualBounds.getMinX());
            stage.setY(visualBounds.getMinY());
            stage.setWidth(visualBounds.getWidth());
            stage.setHeight(visualBounds.getHeight());

            stage.show();

        } catch (IOException e) {
            System.err.println("Failed to load FXML scene: " + fxmlFile);
            e.printStackTrace();
            AlertHelper.showErrorAlert("Navigation Error", "Could not load the required page. Check console for details.");
        }
    }

}