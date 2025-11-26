package com.example.sda.controllers.shared;

import com.example.sda.models.Project;
import com.example.sda.services.ProjectService;
import com.example.sda.utils.SessionManager;
import com.example.sda.models.User;
import com.example.sda.utils.ToastHelper; // NEW

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

public class UploadProjectController implements Initializable {


    @FXML private TextField titleField;
    @FXML private TextField githubField;
    @FXML private TextArea descriptionArea;
    @FXML private TextField categoryField; // Custom Category (TextField)
    @FXML private DatePicker yearPicker; // Project Completion Year
    @FXML private TextField universityField; // University Name
    @FXML private TextField supervisorField;
    @FXML private TextField technologiesField;
    @FXML private Label statusLabel; // Keep for showing status (e.g., uploading...)
    @FXML private VBox uploadForm;

    @FXML private Button browseBtn;
    @FXML private Button submitBtn;
    @FXML private Button removeFileBtn;
    @FXML private VBox fileListContainer;
    @FXML private Label fileNameLabel;
    @FXML private Label fileSizeLabel;
    @FXML private Label fileErrorLabel;

    // --- Services and State ---
    private final ProjectService projectService = new ProjectService();
    private File selectedFile;
    private static final long MAX_FILE_SIZE_BYTES = 5 * 1024 * 1024; // 5 MB

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        statusLabel.setText("");
        fileErrorLabel.setText("");
        fileListContainer.setVisible(false);
        fileListContainer.setManaged(false);


        yearPicker.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                setDisable(empty || date.isAfter(LocalDate.now()));
            }
        });
    }


    private boolean validateUrl(String url) {
        if (url == null || url.isEmpty()) return true; // Optional field
        return Pattern.compile("^https?://.*").matcher(url).matches();
    }

    private boolean validateForm(User user) {
        statusLabel.setText("");
        fileErrorLabel.setText("");

        if (user == null) {
            ToastHelper.showError("Session Error", "Session expired. Please log in again.");
            return false;
        }


        if (titleField.getText().length() > 255 || titleField.getText().isEmpty()) {
            ToastHelper.showWarning("Input Error", "Project Title is required and cannot exceed 255 characters.");
            return false;
        }

        if (descriptionArea.getText().length() < 50) {
            ToastHelper.showWarning("Input Error", "Description is required and must be at least 50 characters.");
            return false;
        }

        if (categoryField.getText().isEmpty()) {
            ToastHelper.showWarning("Input Error", "Category is required.");
            return false;
        }

        if (yearPicker.getValue() == null) {
            ToastHelper.showWarning("Input Error", "Completion Year is required.");
            return false;
        }

        if (universityField.getText().isEmpty()) {
            ToastHelper.showWarning("Input Error", "University is required.");
            return false;
        }

        if (!validateUrl(githubField.getText())) {
            ToastHelper.showWarning("Input Error", "GitHub Link must be a valid URL.");
            return false;
        }

        if (selectedFile == null) {
            fileErrorLabel.setText("A PDF file is required.");
            ToastHelper.showWarning("File Required", "Please select a PDF file for upload.");
            return false;
        }

        return true;
    }

    // --- UI Handlers ---

    @FXML
    private void handleFileBrowse(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Project PDF");

        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("PDF Documents (*.pdf)", "*.pdf")
        );

        // Determine the current Stage
        Stage stage = (Stage) (event.getSource() instanceof Node ? ((Node) event.getSource()).getScene().getWindow() : browseBtn.getScene().getWindow());

        File file = fileChooser.showOpenDialog(stage);

        if (file != null) {
            if (!file.getName().toLowerCase().endsWith(".pdf")) {
                fileErrorLabel.setText("Error: Only PDF files are allowed.");
                ToastHelper.showError("File Error", "Only PDF files are allowed.");
                selectedFile = null;
            } else if (file.length() > MAX_FILE_SIZE_BYTES) {
                fileErrorLabel.setText("Error: File size must not exceed 5 MB.");
                ToastHelper.showError("File Error", "File size must not exceed 5 MB.");
                selectedFile = null;
            } else {
                selectedFile = file;
                fileNameLabel.setText(file.getName());
                fileSizeLabel.setText(String.format("%.2f MB", (double) file.length() / (1024 * 1024)));
                fileListContainer.setVisible(true);
                fileListContainer.setManaged(true);
                fileErrorLabel.setText(""); // Clear file error
            }
        }
    }

    @FXML
    private void handleFileRemove(ActionEvent event) {
        selectedFile = null;
        fileNameLabel.setText("[No file selected]");
        fileSizeLabel.setText("");
        fileListContainer.setVisible(false);
        fileListContainer.setManaged(false);
        fileErrorLabel.setText("");
    }

    @FXML
    private void handleSubmit(ActionEvent event) {
        User currentUser = SessionManager.getInstance().getCurrentUser();
        if (!validateForm(currentUser)) {
            // Toast helper already called in validateForm
            return;
        }

        submitBtn.setDisable(true);
        submitBtn.setText("Uploading...");
        statusLabel.setStyle("-fx-text-fill: #00ff88;");
        statusLabel.setText("Preparing file and submitting data...");

        // Execute submission on a separate thread
        new Thread(() -> {
            try (InputStream pdfStream = new FileInputStream(selectedFile)) {

                // 1. Create Project Model (omitted for brevity)
                Project project = new Project(
                        currentUser.getId(),
                        titleField.getText().trim(),
                        descriptionArea.getText().trim(),
                        categoryField.getText().trim(),
                        yearPicker.getValue().getYear(),
                        universityField.getText().trim(),
                        supervisorField.getText().trim(),
                        githubField.getText().trim(),
                        technologiesField.getText().trim(),
                        pdfStream
                );

                // 2. Call Service to Save Data
                boolean success = projectService.uploadNewProject(project, selectedFile.length());

                Platform.runLater(() -> {
                    if (success) {
                        // NEW: Use Toast for SUCCESS
                        ToastHelper.showSuccess("Upload Complete", "Project '" + project.getTitle() + "' submitted successfully Waiting Admin Approval!");
                        // Clear the status label and form
                        statusLabel.setText("");
                        resetForm();
                    } else {
                        // NEW: Use Toast for DB failure
                        ToastHelper.showError("Submission Failed", "Database connection or write error. Project could not be saved.");
                        statusLabel.setText("Submission failed."); // Keep minimal status update
                        statusLabel.setStyle("-fx-text-fill: #ff6666;");
                    }
                    submitBtn.setDisable(false);
                    submitBtn.setText("Submit Project");
                });

            } catch (IOException e) {
                Platform.runLater(() -> {
                    ToastHelper.showError("File Error", "Could not read PDF file during upload process.");
                    submitBtn.setDisable(false);
                    submitBtn.setText("Submit Project");
                });
            } catch (Exception e) {
                Platform.runLater(() -> {
                    ToastHelper.showError("System Error", "An unexpected error occurred during submission.");
                    submitBtn.setDisable(false);
                    submitBtn.setText("Submit Project");
                    e.printStackTrace();
                });
            }
        }).start();
    }

    @FXML
    private void handleCancel(ActionEvent event) {
        resetForm();
        ToastHelper.showWarning("Form Cleared", "Project upload cancelled.");
    }

    private void resetForm() {
        titleField.clear();
        githubField.clear();
        descriptionArea.clear();
        categoryField.clear();
        yearPicker.setValue(null);
        universityField.clear();
        supervisorField.clear();
        technologiesField.clear();
        handleFileRemove(null); // Clear selected file display
        statusLabel.setText("");
    }
}