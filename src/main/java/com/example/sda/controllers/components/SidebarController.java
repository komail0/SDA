package com.example.sda.controllers.components;

import com.example.sda.controllers.shared.AlumniRequestsController;
import com.example.sda.controllers.shared.MentorRatingListController;
import com.example.sda.controllers.shared.ProjectSearchController;
import com.example.sda.models.User;
import com.example.sda.utils.SessionManager;
import com.example.sda.utils.SceneManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.event.ActionEvent;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

public class SidebarController implements Initializable {


    @FXML private Text userNameText;
    @FXML private Text userRoleText;
    @FXML private Text portalTitleText;


    @FXML private VBox mainContentArea;


    @FXML public Button searchProjectsButton;
    @FXML private Button uploadProjectButton;
    @FXML private Button viewProjectsButton;
    @FXML private Button chatButton;
    @FXML private Button viewRequestsButton;
    @FXML private Button mentorshipRequestsButton;
    @FXML private Button viewFeedbackButton;

    private List<Button> menuButtons;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadUserData();

        menuButtons = new ArrayList<>();
        if (searchProjectsButton != null) menuButtons.add(searchProjectsButton);
        if (uploadProjectButton != null) menuButtons.add(uploadProjectButton);
        if (viewProjectsButton != null) menuButtons.add(viewProjectsButton);
        if (chatButton != null) menuButtons.add(chatButton);
        if (viewRequestsButton != null) menuButtons.add(viewRequestsButton);
        if (mentorshipRequestsButton != null) menuButtons.add(mentorshipRequestsButton);
        if (viewFeedbackButton != null) menuButtons.add(viewFeedbackButton);

        showInitialWelcomeScreen();
    }

    private void showInitialWelcomeScreen() {
        User user = SessionManager.getInstance().getCurrentUser();
        String role = (user != null) ? user.getAccountType().toString() : "User";
        String welcomeMessage;

        if (role.equals("ALUMNI")) {
            welcomeMessage = "Welcome to the Alumni Portal. Please use the navigation menu on the left to view and upload your own work and give mentorship to desired candidates";
        } else if (role.equals("STUDENT")) {
            welcomeMessage = "Welcome to the Student Portal. Use the navigation menu to search for projects, chat with mentors, or manage your requests.";
        } else {
            welcomeMessage = "Welcome to the Dashboard. Please select an option from the menu.";
        }

        mainContentArea.getChildren().clear();

        VBox welcomeBox = new VBox(20);
        welcomeBox.getStyleClass().add("content-area");
        welcomeBox.setPadding(new javafx.geometry.Insets(40));
        welcomeBox.setAlignment(javafx.geometry.Pos.CENTER);

        Text title = new Text("Portal Overview");
        title.getStyleClass().add("content-title");

        Text subtitle = new Text(welcomeMessage);
        subtitle.getStyleClass().add("content-subtitle");
        subtitle.setWrappingWidth(600);

        welcomeBox.getChildren().addAll(title, subtitle);
        mainContentArea.getChildren().add(welcomeBox);
        VBox.setVgrow(welcomeBox, javafx.scene.layout.Priority.ALWAYS);
    }

    public void setActiveMenuItem(Button clickedButton) {
        for (Button btn : menuButtons) {
            btn.getStyleClass().remove("active");
        }
        if (clickedButton != null) {
            clickedButton.getStyleClass().add("active");
        }
    }

    private void loadUserData() {
        User user = SessionManager.getInstance().getCurrentUser();
        if (user != null) {
            userNameText.setText(user.getUsername());
            String roleStr = user.getAccountType().toString();
            userRoleText.setText(roleStr);
            portalTitleText.setText(roleStr + " Portal");
        } else {
            userNameText.setText("Guest");
            userRoleText.setText("N/A");
            portalTitleText.setText("Error");
        }
    }

    /**
     * Loads a new FXML view into the central content area.
     */
    public Object loadContentView(String fxmlPath) {
        try {
            String fullPath = "/com/example/sda/fxml/" + fxmlPath;
            FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource(fullPath)));
            Parent view = loader.load();

            mainContentArea.getChildren().clear();
            mainContentArea.getChildren().add(view);
            VBox.setVgrow(view, javafx.scene.layout.Priority.ALWAYS);

            return loader.getController();

        } catch (IOException e) {
            System.err.println("Error loading content view: " + fxmlPath);
            e.printStackTrace();
            mainContentArea.getChildren().clear();
            mainContentArea.getChildren().add(new Text("Error loading view: " + fxmlPath));
        } catch (NullPointerException e) {
            System.err.println("Resource not found: " + fxmlPath);
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Helper to set view directly without reloading via FXML path.
     * This is used by nested controllers (like MentorRatingListController) to navigate.
     */
    public void loadViewDirectly(Parent view) {
        mainContentArea.getChildren().clear();
        mainContentArea.getChildren().add(view);
        VBox.setVgrow(view, javafx.scene.layout.Priority.ALWAYS);
    }

    @FXML
    private void handleProjectsMenu(ActionEvent event) {
        Button source = (Button) event.getSource();
        User user = SessionManager.getInstance().getCurrentUser();

        if (source.getId().equals("uploadProjectButton")) {
            loadContentView("shared/Upload-Project.fxml");
        }
        else if (source.getId().equals("viewProjectsButton")) {
            if (user != null && user.getAccountType().toString().equals("ALUMNI")) {
                loadContentView("shared/Alumni-Repository.fxml");
            } else {
                Object controller = loadContentView("shared/Search.fxml");
                if (controller instanceof ProjectSearchController) {
                    ((ProjectSearchController) controller).setSidebarController(this);
                }
            }
        }
        else if (source.getId().equals("searchProjectsButton")) {
            Object controller = loadContentView("shared/Search.fxml");
            if (controller instanceof ProjectSearchController) {
                ((ProjectSearchController) controller).setSidebarController(this);
            }
        }
        else if (source.getId().equals("mentorshipRequestsButton") || source.getId().equals("viewRequestsButton")) {
            if (user != null && user.getAccountType().toString().equals("ALUMNI")) {
                Object controller = loadContentView("shared/Alumni-Requests.fxml");
                if (controller instanceof AlumniRequestsController) {
                    ((AlumniRequestsController) controller).setSidebarController(this);
                }
            } else {
                loadContentView("shared/Pending-Requests.fxml");
            }
        }
        else if (source.getId().equals("chatButton")) {
            loadContentView("shared/chat.fxml");
        }
        // --- NEW: FEEDBACK BUTTON HANDLER ---
        else if (source.getId().equals("viewFeedbackButton")) {
            if (user != null && user.getAccountType().toString().equals("ALUMNI")) {
                // ALUMNI: View their own feedback dashboard
                loadContentView("shared/view-feedback.fxml");
            } else {
                // STUDENT: View list of mentors to rate
                Object controller = loadContentView("shared/mentor-rating.fxml");
                if (controller instanceof MentorRatingListController) {
                    ((MentorRatingListController) controller).setSidebarController(this);
                }
            }
        }

        setActiveMenuItem(source);
    }

    @FXML
    private void handleLogout(ActionEvent event) {
        SessionManager.getInstance().logout();
        SceneManager.getInstance().loadScene("auth/Login.fxml", "User Login", event);
    }
}