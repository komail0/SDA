package com.example.sda.controllers.auth;

import com.example.sda.services.AuthService;
import com.example.sda.enums.UserRole;
import com.example.sda.utils.AlertHelper; // Keep for now
import com.example.sda.utils.SceneManager;
import com.example.sda.utils.Validator;
import com.example.sda.utils.SessionManager;
import com.example.sda.models.User;
import com.example.sda.utils.ToastHelper; // NEW

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

/**
 * Controller for the Registration View. Includes advanced client-side validation,
 * password feedback, and asynchronous registration logic.
 */
public class RegistrationController implements Initializable {

    // --- FXML Injections (omitted for brevity) ---
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
        // Initialization logic omitted for brevity
        if (visiblePasswordField != null) {
            visiblePasswordField.setManaged(false);
            visiblePasswordField.setVisible(false);
        }
        if (visibleConfirmPasswordField != null) {
            visibleConfirmPasswordField.setManaged(false);
            visibleConfirmPasswordField.setVisible(false);
        }
        if (passwordVisibilityBtn != null) passwordVisibilityBtn.setText("👁");
        if (confirmPasswordVisibilityBtn != null) confirmPasswordVisibilityBtn.setText("👁");


        Platform.runLater(() -> fullNameField.requestFocus());

        setupPasswordToggle(passwordField, visiblePasswordField, passwordVisibilityBtn);
        setupPasswordToggle(confirmPasswordField, visibleConfirmPasswordField, confirmPasswordVisibilityBtn);
        setupValidationListeners();
        setupEnterKeyHandlers();

        if (studentRole != null && alumniRole != null) {
            studentRole.selectedProperty().addListener((obs, wasSelected, isSelected) -> updateRoleVisuals());
            alumniRole.selectedProperty().addListener((obs, wasSelected, isSelected) -> updateRoleVisuals());
            updateRoleVisuals();
        }
    }

    // Toggle, Validation, Visuals, and Enter Key methods omitted for brevity

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
        Platform.runLater(() -> {
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
        });
    }

    private void updatePasswordStrength(String password) {
        Platform.runLater(() -> {
            if (strengthIndicator != null) {
                strengthIndicator.setVisible(!password.isEmpty());
                strengthIndicator.setManaged(!password.isEmpty());

                int strength = Validator.checkPasswordStrength(password);

                for (Node node : strengthIndicator.getChildren()) {
                    node.setStyle("-fx-fill: #444;");
                }

                if (password.isEmpty()) return;

                String color = switch (strength) {
                    case 0 -> "#ff4444"; // Weak (Red)
                    case 1 -> "#ffbb33"; // Medium (Yellow)
                    case 2 -> "#00C851"; // Strong (Green)
                    default -> "#444";
                };

                int barsToFill = strength + 1;
                for (int i = 0; i < barsToFill && i < strengthIndicator.getChildren().size(); i++) {
                    strengthIndicator.getChildren().get(i).setStyle("-fx-fill: " + color + ";");
                }
            }
        });
    }

    private void updateRoleVisuals() {
        Platform.runLater(() -> {
            if (studentRole.isSelected()) {
                studentRole.getStyleClass().add("selected-role");
                alumniRole.getStyleClass().remove("selected-role");
            } else {
                alumniRole.getStyleClass().add("selected-role");
                studentRole.getStyleClass().remove("selected-role");
            }
        });
    }

    private void setupEnterKeyHandlers() {
        // Key handler logic omitted for brevity
        fullNameField.setOnKeyPressed(e -> { if (e.getCode() == KeyCode.ENTER) emailField.requestFocus(); });
        emailField.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                if (visiblePasswordField.isVisible()) visiblePasswordField.requestFocus();
                else passwordField.requestFocus();
            }
        });
        passwordField.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                if (visibleConfirmPasswordField.isVisible()) visibleConfirmPasswordField.requestFocus();
                else confirmPasswordField.requestFocus();
            }
        });
        visiblePasswordField.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                if (visibleConfirmPasswordField.isVisible()) visibleConfirmPasswordField.requestFocus();
                else confirmPasswordField.requestFocus();
            }
        });
        confirmPasswordField.setOnKeyPressed(e -> { if(e.getCode() == KeyCode.ENTER) handleRegistration(new ActionEvent()); });
        visibleConfirmPasswordField.setOnKeyPressed(e -> { if(e.getCode() == KeyCode.ENTER) handleRegistration(new ActionEvent()); });
    }


    @FXML
    private void handleRegistration(ActionEvent event) {
        feedbackLabel.setText("");
        feedbackLabel.setStyle("");

        // Final client-side validation check (omitted for brevity)
        if (!Validator.isFullNameValid(fullNameField.getText()) ||
                !Validator.isEmailValid(emailField.getText()) ||
                !Validator.isPasswordValid(passwordField.getText()) ||
                !passwordField.getText().equals(confirmPasswordField.getText()) ||
                passwordField.getText().isEmpty()) {

            // NEW: Use Toast for immediate validation failure
            ToastHelper.showError("Validation Failed", "Please correct the errors.");

            // Manually trigger the visual update for all fields (omitted for brevity)
            updateFieldValidation(fullNameGroup, fullNameErrorLabel, Validator.isFullNameValid(fullNameField.getText()), fullNameField.getText().isEmpty(), "Name must be at least 3 characters");
            updateFieldValidation(emailGroup, emailErrorLabel, Validator.isEmailValid(emailField.getText()), emailField.getText().isEmpty(), "Invalid email format");
            updateFieldValidation(passwordGroup, passwordErrorLabel, Validator.isPasswordValid(passwordField.getText()), passwordField.getText().isEmpty(), "Min 8 chars, include number & special char");
            validateConfirmPassword();

            return;
        }

        submitBtn.setDisable(true);
        submitBtn.setText("Registering...");
        UserRole role = studentRole.isSelected() ? UserRole.STUDENT : UserRole.ALUMNI;

        final String email = emailField.getText();
        final String password = passwordField.getText();

        // Perform registration and immediate login on a background thread
        new Thread(() -> {
            try {
                // 1. Attempt Registration
                boolean success = authService.registerUser(
                        fullNameField.getText(),
                        email,
                        password,
                        role
                );

                User loggedInUser;
                if (success) {
                    // 2. Immediately Log In the newly created user
                    loggedInUser = authService.authenticate(email, password);
                } else {
                    loggedInUser = null;
                }

                Platform.runLater(() -> {
                    if (loggedInUser != null) {
                        // Registration and Login Successful
                        SessionManager.getInstance().setCurrentUser(loggedInUser);

                        // NEW: Use Toast for success and remove feedback label updates
                        ToastHelper.showSuccess("Account Created", "Welcome to AcadBridge, " + loggedInUser.getUsername() + "!");
                        submitBtn.setText("Success!");

                        // --- NAVIGATION TO SIDEBAR ---
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

                        PauseTransition delay = new PauseTransition(Duration.seconds(0.5)); // Reduced delay
                        delay.setOnFinished(e -> {
                            ActionEvent dummyEvent = new ActionEvent(submitBtn, null);
                            SceneManager.getInstance().loadScene(fxmlFile, title, dummyEvent);
                        });
                        delay.play();

                    } else if (success) {
                        // Fallback if registration succeeded but immediate authentication failed
                        ToastHelper.showWarning("Login Required", "Account created, but automatic login failed. Please log in manually.");
                        resetButton();
                        PauseTransition delay = new PauseTransition(Duration.seconds(1.0)); // Reduced delay
                        delay.setOnFinished(e -> {
                            ActionEvent dummyEvent = new ActionEvent(submitBtn, null);
                            goToLogin(dummyEvent);
                        });
                        delay.play();

                    } else {
                        // Registration failed (email already exists or DB connection failure)
                        ToastHelper.showError("Registration Failed", "Email already registered or connection failed.");
                        resetButton();
                    }
                });
            } catch (IllegalArgumentException e) {
                // Catches the specific email already exists error from AuthService
                Platform.runLater(() -> {
                    ToastHelper.showWarning("Registration Failed", e.getMessage());
                    resetButton();
                });
            } catch (Exception e) {
                Platform.runLater(() -> {
                    ToastHelper.showError("System Error", "An unexpected error occurred during registration.");
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
        SceneManager.getInstance().loadScene("auth/Login.fxml", "User Login", event);
    }
}