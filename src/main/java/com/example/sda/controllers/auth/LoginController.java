package com.example.sda.controllers.auth;

import com.example.sda.services.AuthService;
import com.example.sda.utils.AlertHelper;
import com.example.sda.utils.SceneManager;
import com.example.sda.utils.SessionManager;
import com.example.sda.models.User;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.event.ActionEvent;
import javafx.animation.PauseTransition;
import javafx.util.Duration;
import javafx.scene.input.KeyCode;

import java.net.URL;
import java.util.ResourceBundle;

public class LoginController implements Initializable {

    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private TextField visiblePasswordField;
    @FXML private Button passwordVisibilityBtn;
    @FXML private Button loginBtn;
    @FXML private Label feedbackLabel;
    @FXML private CheckBox rememberCheckbox;

    private AuthService authService;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.authService = new AuthService();

        if (feedbackLabel != null) feedbackLabel.setText("");

        // Removed manual Stage sizing logic.
        // SceneManager handles maximizing now.
        // Just setting focus to email field.
        Platform.runLater(() -> emailField.requestFocus());

        setupPasswordToggle(passwordField, visiblePasswordField, passwordVisibilityBtn);
        setupEnterKeyHandlers();
    }

    private void setupPasswordToggle(PasswordField passField, TextField textField, Button toggleBtn) {
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
            if (feedbackLabel != null) {
                feedbackLabel.setText("Please enter both email and password.");
                feedbackLabel.setStyle("-fx-text-fill: #ff6666;");
            }
            return;
        }

        loginBtn.setDisable(true);
        loginBtn.setText("Logging in...");

        new Thread(() -> {
            try {
                Thread.sleep(500);
                User loggedInUser = authService.authenticate(email, password);

                Platform.runLater(() -> {
                    if (loggedInUser != null) {
                        SessionManager.getInstance().setCurrentUser(loggedInUser);
                        if (feedbackLabel != null) {
                            feedbackLabel.setText("Login Successful! Redirecting...");
                            feedbackLabel.setStyle("-fx-text-fill: #00C851; -fx-font-weight: bold;");
                        }
                        loginBtn.setText("Success!");
                        PauseTransition delay = new PauseTransition(Duration.seconds(1.0));
                        delay.setOnFinished(e -> {
                            SceneManager.getInstance().loadScene("shared/project-repository-view.fxml", "Dashboard", event);
                        });
                        delay.play();
                    } else {
                        if (feedbackLabel != null) {
                            feedbackLabel.setText("Invalid email or password.");
                            feedbackLabel.setStyle("-fx-text-fill: #ff6666;");
                        }
                        resetButton();
                    }
                });
            } catch (Exception e) {
                Platform.runLater(() -> {
                    if (feedbackLabel != null) {
                        feedbackLabel.setText("Login Error: " + e.getMessage());
                        feedbackLabel.setStyle("-fx-text-fill: #ff6666;");
                    }
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
        SceneManager.getInstance().loadScene("Registration.fxml", "Create Account", event);
    }
}