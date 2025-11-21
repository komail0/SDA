package com.example.sda.controllers.auth;

import com.example.sda.services.AuthService;
import com.example.sda.enums.UserRole;
import com.example.sda.utils.AlertHelper;
import com.example.sda.utils.SceneManager;
import com.example.sda.utils.Validator;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.event.ActionEvent;
import javafx.animation.PauseTransition;
import javafx.util.Duration;

import java.net.URL;
import java.util.ResourceBundle;

public class RegistrationController implements Initializable {

    // --- FXML Injections ---
    @FXML private TextField fullNameField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private TextField visiblePasswordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private TextField visibleConfirmPasswordField;
    @FXML private ToggleButton studentRole;
    @FXML private ToggleButton alumniRole;
    @FXML private ToggleGroup roleGroup;
    @FXML private VBox fullNameGroup;
    @FXML private VBox emailGroup;
    @FXML private VBox passwordGroup;
    @FXML private VBox confirmPasswordGroup;
    @FXML private HBox strengthIndicator;
    @FXML private Label feedbackLabel;
    @FXML private Label fullNameErrorLabel;
    @FXML private Label emailErrorLabel;
    @FXML private Label passwordErrorLabel;
    @FXML private Label confirmPasswordErrorLabel;
    @FXML private Button submitBtn;
    @FXML private Button passwordVisibilityBtn;
    @FXML private Button confirmPasswordVisibilityBtn;

    private final AuthService authService = new AuthService();
    private static final String CLASS_ERROR = "field-invalid";
    private static final String CLASS_SUCCESS = "field-valid";

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Removed manual maximization. SceneManager handles it.
        Platform.runLater(() -> fullNameField.requestFocus());

        setupPasswordToggle(passwordField, visiblePasswordField, passwordVisibilityBtn);
        setupPasswordToggle(confirmPasswordField, visibleConfirmPasswordField, confirmPasswordVisibilityBtn);
        setupValidationListeners();
        setupEnterKeyHandlers();

        studentRole.selectedProperty().addListener((obs, wasSelected, isSelected) -> updateRoleVisuals());
        alumniRole.selectedProperty().addListener((obs, wasSelected, isSelected) -> updateRoleVisuals());
        updateRoleVisuals();
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
            if (!isVisible) textField.requestFocus();
            else passField.requestFocus();
            textField.positionCaret(textField.getText().length());
            passField.positionCaret(passField.getText().length());
        });
    }

    private void setupValidationListeners() {
        fullNameField.textProperty().addListener((obs, old, text) -> {
            boolean valid = Validator.isFullNameValid(text);
            updateFieldValidation(fullNameGroup, fullNameErrorLabel, valid, text.isEmpty(), "Name must be at least 3 characters");
        });
        emailField.textProperty().addListener((obs, old, text) -> {
            boolean valid = Validator.isEmailValid(text);
            updateFieldValidation(emailGroup, emailErrorLabel, valid, text.isEmpty(), "Invalid email format");
        });
        passwordField.textProperty().addListener((obs, old, text) -> {
            boolean valid = Validator.isPasswordValid(text);
            updateFieldValidation(passwordGroup, passwordErrorLabel, valid, text.isEmpty(), "Min 8 chars, include number & special char");
            updatePasswordStrength(text);
            validateConfirmPassword();
        });
        confirmPasswordField.textProperty().addListener((obs, old, text) -> validateConfirmPassword());
    }

    private void validateConfirmPassword() {
        String pass = passwordField.getText();
        String confirm = confirmPasswordField.getText();
        boolean match = !pass.isEmpty() && pass.equals(confirm);
        updateFieldValidation(confirmPasswordGroup, confirmPasswordErrorLabel, match, confirm.isEmpty(), "Passwords do not match");
    }

    private void updateFieldValidation(VBox group, Label errorLabel, boolean isValid, boolean isEmpty, String errorMsg) {
        group.getStyleClass().removeAll(CLASS_ERROR, CLASS_SUCCESS);
        errorLabel.setVisible(false);
        errorLabel.setManaged(false);
        if (isEmpty) return;
        if (isValid) {
            group.getStyleClass().add(CLASS_SUCCESS);
        } else {
            group.getStyleClass().add(CLASS_ERROR);
            errorLabel.setText(errorMsg);
            errorLabel.setVisible(true);
            errorLabel.setManaged(true);
        }
    }

    private void updatePasswordStrength(String password) {
        strengthIndicator.setVisible(!password.isEmpty());
        strengthIndicator.setManaged(!password.isEmpty());
        int strength = Validator.checkPasswordStrength(password);
        for (Node node : strengthIndicator.getChildren()) {
            node.setStyle("-fx-fill: #444;");
        }
        if (password.isEmpty()) return;
        String color = switch (strength) {
            case 0 -> "#ff4444";
            case 1 -> "#ffbb33";
            case 2 -> "#00C851";
            default -> "#444";
        };
        int barsToFill = strength + 1;
        for (int i = 0; i < barsToFill && i < strengthIndicator.getChildren().size(); i++) {
            strengthIndicator.getChildren().get(i).setStyle("-fx-fill: " + color + ";");
        }
    }

    private void updateRoleVisuals() {
        if (studentRole.isSelected()) {
            studentRole.getStyleClass().add("selected-role");
            alumniRole.getStyleClass().remove("selected-role");
        } else {
            alumniRole.getStyleClass().add("selected-role");
            studentRole.getStyleClass().remove("selected-role");
        }
    }

    private void setupEnterKeyHandlers() {
        fullNameField.setOnKeyPressed(e -> { if (e.getCode() == KeyCode.ENTER) emailField.requestFocus(); });
        emailField.setOnKeyPressed(e -> { if (e.getCode() == KeyCode.ENTER) passwordField.requestFocus(); });
        passwordField.setOnKeyPressed(e -> { if (e.getCode() == KeyCode.ENTER) confirmPasswordField.requestFocus(); });
        visiblePasswordField.setOnKeyPressed(e -> { if (e.getCode() == KeyCode.ENTER) confirmPasswordField.requestFocus(); });
        confirmPasswordField.setOnKeyPressed(e -> { if(e.getCode() == KeyCode.ENTER) handleRegistration(new ActionEvent()); });
        visibleConfirmPasswordField.setOnKeyPressed(e -> { if(e.getCode() == KeyCode.ENTER) handleRegistration(new ActionEvent()); });
    }

    @FXML
    private void handleRegistration(ActionEvent event) {
        feedbackLabel.setText("");
        if (!Validator.isFullNameValid(fullNameField.getText()) ||
                !Validator.isEmailValid(emailField.getText()) ||
                !Validator.isPasswordValid(passwordField.getText()) ||
                !passwordField.getText().equals(confirmPasswordField.getText())) {
            feedbackLabel.setText("Please correct the errors highlighted above.");
            feedbackLabel.setStyle("-fx-text-fill: #ff6666;");
            return;
        }

        submitBtn.setDisable(true);
        submitBtn.setText("Registering...");
        UserRole role = studentRole.isSelected() ? UserRole.STUDENT : UserRole.ALUMNI;

        new Thread(() -> {
            try {
                Thread.sleep(1000);
                boolean success = authService.registerUser(
                        fullNameField.getText(),
                        emailField.getText(),
                        passwordField.getText(),
                        role
                );

                Platform.runLater(() -> {
                    if (success) {
                        feedbackLabel.setText("Account created successfully! Redirecting...");
                        feedbackLabel.setStyle("-fx-text-fill: #00C851; -fx-font-weight: bold;");
                        submitBtn.setText("Success!");
                        try {
                            AlertHelper.showSuccessAlert("Success", "Account created successfully!");
                        } catch (Exception ignored) {}
                        PauseTransition delay = new PauseTransition(Duration.seconds(1.5));
                        delay.setOnFinished(e -> goToLogin(null));
                        delay.play();
                    } else {
                        feedbackLabel.setText("Email already registered or connection failed.");
                        feedbackLabel.setStyle("-fx-text-fill: #ff6666;");
                        resetButton();
                    }
                });
            } catch (Exception e) {
                Platform.runLater(() -> {
                    feedbackLabel.setText("Error: " + e.getMessage());
                    feedbackLabel.setStyle("-fx-text-fill: #ff6666;");
                    resetButton();
                });
            }
        }).start();
    }

    private void resetButton() {
        submitBtn.setDisable(false);
        submitBtn.setText("Create Account");
    }

    @FXML
    private void goToLogin(ActionEvent event) {
        SceneManager.getInstance().loadScene("Login.fxml", "User Login", event);
    }
}