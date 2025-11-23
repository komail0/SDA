package com.example.sda;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {

    private static final String APP_TITLE = "AcadBridge - User Login";
    private static final String FXML_PATH = "fxml/auth/Login.fxml"; // Updated path

    @Override
    public void start(Stage stage) throws IOException {
        try {
            // 1. Load the FXML file
            FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource(FXML_PATH));

            // For maximized windows, it's best to start with a standard size
            Scene scene = new Scene(fxmlLoader.load(), 1200, 800);

            stage.setTitle(APP_TITLE);
            stage.setScene(scene);

            // 2. Set the stage to be maximized
            stage.setMaximized(true);

            stage.show();
        } catch (Exception e) {
            System.err.println("Failed to start the application. Could not load FXML: " + FXML_PATH);
            e.printStackTrace();
            // You may want to add an Alert box here to inform the user
        }
    }

    public static void main(String[] args) {
        launch();
    }
}