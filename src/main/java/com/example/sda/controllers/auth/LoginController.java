package com.example.sda.controllers.auth;

import com.example.sda.services.AuthService;
import com.example.sda.utils.AlertHelper; // Keep for now, but not used for toast
import com.example.sda.utils.SceneManager;
import com.example.sda.utils.SessionManager;
import com.example.sda.models.User;
import com.example.sda.enums.UserRole;
import com.example.sda.utils.ToastHelper; // NEW

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.event.ActionEvent;
import javafx.animation.PauseTransition;
import javafx.util.Duration;
import javafx.scene.input.KeyCode;
import javafx.scene.Node;

import java.net.URL;
import java.util.ResourceBundle;


public class LoginController implements Initializable {

    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private TextField visiblePasswordField;
    @FXML private Button passwordVisibilityBtn;
    @FXML private Button loginBtn; // CRITICAL: Used as the event source
    @FXML private Label feedbackLabel; // Retain only for immediate input errors
    @FXML private CheckBox rememberCheckbox;

    private AuthService authService;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.authService = new AuthService();

        if (feedbackLabel != null) feedbackLabel.setText("");

        Platform.runLater(() -> emailField.requestFocus());

        setupPasswordToggle(passwordField, visiblePasswordField, passwordVisibilityBtn);
        setupEnterKeyHandlers();
    }

    private void setupPasswordToggle(PasswordField passField, TextField textField, Button toggleBtn) {
        // Implementation omitted for brevity
        textField.setManaged(false);
        textField.setVisible(false);
        toggleBtn.setText("👁");

        passField.textProperty().bindBidirectional(textField.textProperty());
        toggleBtn.setOnAction(e -> {
            boolean isVisible = textField.isVisible();

            textField.setVisible(!isVisible);
            textField.setManaged(!isVisible);
            passField.setVisible(isVisible);
            passField.setManaged(isVisible);

            toggleBtn.setText(isVisible ? "👁" : "✖");

            if (!isVisible) {
                textField.requestFocus();
                textField.positionCaret(textField.getText().length());
            } else {
                passField.requestFocus();
                passField.positionCaret(passField.getText().length());
            }
        });
    }

    private void setupEnterKeyHandlers() {
        // Implementation omitted for brevity
        emailField.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                if (visiblePasswordField.isVisible()) visiblePasswordField.requestFocus();
                else passwordField.requestFocus();
            }
        });
        passwordField.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) handleLogin(new ActionEvent());
        });
        visiblePasswordField.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) handleLogin(new ActionEvent());
        });
    }

    @FXML
    private void handleLogin(ActionEvent event) {
        if (feedbackLabel != null) {
            feedbackLabel.setText("");
            feedbackLabel.setStyle("");
        }

        String email = emailField.getText().trim();
        String password = passwordField.getText();

        if (email.isEmpty() || password.isEmpty()) {
            ToastHelper.showError("Error", "Please enter both email and password.");
            resetButton();
            return;
        }

        loginBtn.setDisable(true);
        loginBtn.setText("Logging in...");

        // Perform login attempt on a background thread
        new Thread(() -> {
            try {
                User loggedInUser = authService.authenticate(email, password);

                Platform.runLater(() -> {
                    if (loggedInUser != null) {
                        SessionManager.getInstance().setCurrentUser(loggedInUser);

                        // NEW: Use Toast for SUCCESS
                        ToastHelper.showSuccess("Login Successful", "Welcome back, " + loggedInUser.getUsername() + "!");

                        loginBtn.setText("Success!");

                        // --- NAVIGATION LOGIC ---
                        String fxmlFile;
                        String title;

                        if (loggedInUser.getAccountType() == UserRole.STUDENT) {
                            fxmlFile = "components/student_sidebar.fxml";
                            title = "Student Portal - Dashboard";
                        } else if (loggedInUser.getAccountType() == UserRole.ALUMNI) {
                            fxmlFile = "components/alumni_sidebar.fxml";
                            title = "Alumni Portal - Dashboard";
                        } else {
                            fxmlFile = "components/admin_sidebar.fxml";
                            title = "Admin Portal - Dashboard";
                        }

                        // Delay for 1.0 seconds, then navigate
                        PauseTransition delay = new PauseTransition(Duration.seconds(0.5)); // Reduced delay
                        delay.setOnFinished(e -> {
                            ActionEvent dummyEvent = new ActionEvent(loginBtn, null);
                            SceneManager.getInstance().loadScene(fxmlFile, title, dummyEvent);
                        });
                        delay.play();
                    } else {
                        // NEW: Use Toast for FAILURE
                        ToastHelper.showError("Login Failed", "Invalid email or password.");
                        resetButton();
                    }
                });
            } catch (Exception e) {
                Platform.runLater(() -> {
                    // NEW: Use Toast for exception
                    ToastHelper.showError("System Error", "Could not connect to service. Try again.");
                    System.err.println("Login Exception: " + e.getMessage());
                    resetButton();
                });
            }
        }).start();
    }

    private void resetButton() {
        loginBtn.setDisable(false);
        loginBtn.setText("Login to Account");
    }

    @FXML
    private void goToRegistration(ActionEvent event) {
        SceneManager.getInstance().loadScene("auth/Registration.fxml", "Create Account", event);
    }
}