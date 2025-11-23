package com.example.sda.utils;

import com.example.sda.HelloApplication;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;
import javafx.stage.Screen;


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
     * @param fxmlFile The path to the FXML file (e.g., "auth/login-view.fxml"). This path must be relative to the fxml/ folder.
     * @param title The title for the stage.
     * @param event The ActionEvent that triggered the scene change.
     */
    public void loadScene(String fxmlFile, String title, ActionEvent event) {
        String fullPath = null;
        try {
            // Get the Stage from the event source
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            // FIX: Remove hardcoded 'auth/'. fullPath is now constructed correctly from the classpath root.
            fullPath = "/com/example/sda/fxml/" + fxmlFile;

            // Load FXML
            // Use Objects.requireNonNull to catch the NullPointerException (Location not set) early
            FXMLLoader fxmlLoader = new FXMLLoader(Objects.requireNonNull(HelloApplication.class.getResource(fullPath)));
            Parent root = fxmlLoader.load();

            // Set up new scene
            Scene scene = new Scene(root);
            stage.setTitle(title);
            stage.setScene(scene);

            // Enforce maximized to usable screen area (VisualBounds)
            javafx.geometry.Rectangle2D visualBounds = Screen.getPrimary().getVisualBounds();
            stage.setX(visualBounds.getMinX());
            stage.setY(visualBounds.getMinY());
            stage.setWidth(visualBounds.getWidth());
            stage.setHeight(visualBounds.getHeight());

            stage.show();

        } catch (IOException e) {
            System.err.println("Failed to load FXML scene: " + fxmlFile);
            e.printStackTrace();
            // Since we rely on AlertHelper, this might fail if it's not compiled/available
            // AlertHelper.showErrorAlert("Navigation Error", "Could not load the required page. Check console for details.");
        } catch (NullPointerException e) {
            // Catches the error when HelloApplication.class.getResource(fullPath) returns null
            System.err.println("Failed to find FXML resource. Constructed path was: " + fullPath);
            e.printStackTrace();
            // AlertHelper.showErrorAlert("Resource Error", "The required FXML view could not be found. Check console.");
        }
    }
}