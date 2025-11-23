package com.example.sda.controllers.shared;

import com.example.sda.models.MentorshipRequest;
import com.example.sda.models.Project;
import com.example.sda.models.User;
import com.example.sda.services.MentorshipService;
import com.example.sda.utils.SessionManager;
import com.example.sda.utils.ToastHelper; // Import ToastHelper
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class SendRequestController {

    @FXML private TextField studentNameField;
    @FXML private TextField universityNameField;
    @FXML private TextField academicYearField;
    @FXML private TextField emailField; // Optional
    @FXML private TextArea requestMessageArea;
    @FXML private Button sendRequestBtn;
    @FXML private Button cancelBtn;
    @FXML private Label statusLabel; // We will hide this since we use Toasts now

    private Project targetProject;
    private User currentUser;
    private final MentorshipService mentorshipService = new MentorshipService();

    @FXML
    public void initialize() {
        sendRequestBtn.setOnAction(e -> handleSendRequest());
        cancelBtn.setOnAction(e -> closePopup());

        // Hide the old status label as we are using Toasts
        if(statusLabel != null) {
            statusLabel.setVisible(false);
            statusLabel.setManaged(false);
        }
    }

    public void setRequestData(Project project) {
        this.targetProject = project;
        this.currentUser = SessionManager.getInstance().getCurrentUser();

        if (currentUser != null) {
            studentNameField.setText(currentUser.getUsername());
        }
    }

    private void handleSendRequest() {
        // 1. Validate Form (Using Toasts)
        if (!validateForm()) return;

        // 2. Check for Duplicates
        if (mentorshipService.hasExistingRequest(currentUser.getId(), targetProject.getUserId(), targetProject.getTitle())) {
            ToastHelper.showWarning("Duplicate Request", "You have already sent a request for this project.");
            return;
        }

        // 3. Prepare Data
        MentorshipRequest request = new MentorshipRequest(
                currentUser.getId(),
                targetProject.getUserId(),
                targetProject.getTitle(),
                requestMessageArea.getText().trim(),
                universityNameField.getText().trim(),
                academicYearField.getText().trim()
        );

        // 4. Send to Service
        boolean success = mentorshipService.sendRequest(request);

        // 5. Handle Result
        if (success) {
            ToastHelper.showSuccess("Request Sent", "Your mentorship request has been sent to " + targetProject.getAuthorName());
            closePopup();
        } else {
            ToastHelper.showError("Submission Failed", "Could not send request due to a system error.");
        }
    }

    private boolean validateForm() {
        // Check Student Name
        if (studentNameField.getText().trim().isEmpty()) {
            ToastHelper.showWarning("Missing Input", "Please enter your full name.");
            studentNameField.requestFocus();
            return false;
        }

        // Check University
        if (universityNameField.getText().trim().isEmpty()) {
            ToastHelper.showWarning("Missing Input", "Please enter your university name.");
            universityNameField.requestFocus();
            return false;
        }

        // Check Year
        if (academicYearField.getText().trim().isEmpty()) {
            ToastHelper.showWarning("Missing Input", "Please enter your academic year.");
            academicYearField.requestFocus();
            return false;
        }

        // Check Message
        if (requestMessageArea.getText().trim().isEmpty()) {
            ToastHelper.showWarning("Missing Input", "Please provide a message for the mentor.");
            requestMessageArea.requestFocus();
            return false;
        }

        return true;
    }

    private void closePopup() {
        Stage stage = (Stage) cancelBtn.getScene().getWindow();
        stage.close();
    }
}