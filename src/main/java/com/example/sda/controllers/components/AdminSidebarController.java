package com.example.sda.controllers.components;

import com.example.sda.controllers.admin.AdminDashboardController;
import com.example.sda.models.User;
import com.example.sda.utils.SceneManager;
import com.example.sda.utils.SessionManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

public class AdminSidebarController implements Initializable {

    @FXML private Text userNameText;
    @FXML private VBox mainContentArea;

    // Buttons
    @FXML private Button dashboardButton;
    @FXML private Button repoButton;
    @FXML private Button usersButton;
    @FXML private Button reportsButton;

    private List<Button> menuButtons;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        User user = SessionManager.getInstance().getCurrentUser();
        if(user != null) userNameText.setText(user.getUsername());

        menuButtons = new ArrayList<>();
        menuButtons.add(dashboardButton);
        menuButtons.add(repoButton);
        menuButtons.add(usersButton);
        menuButtons.add(reportsButton);

        loadDashboard();
    }


    @FXML
    private void handleMenuNavigation(ActionEvent event) {
        Button source = (Button) event.getSource();

        if (source == dashboardButton) {
            loadDashboard();
        } else if (source == repoButton) {
            loadContentView("admin/repository-management-view.fxml");
        } else if (source == usersButton) {
            loadContentView("admin/user-management-view.fxml");
        } else if (source == reportsButton) {
            loadContentView("admin/reports-generation-view.fxml");
        }

        setActiveButton(source);
    }

    // Specific method to load dashboard and pass 'this' controller to it
    public void loadDashboard() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/sda/fxml/admin/admin-dashboard-view.fxml"));
            Parent view = loader.load();

            // Give Dashboard access to Sidebar so "Quick Actions" work
            AdminDashboardController dashboardController = loader.getController();
            dashboardController.setSidebarController(this);

            displayView(view);
            setActiveButton(dashboardButton);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Generic loader for other views
    public void loadContentView(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/sda/fxml/" + fxmlPath));
            Parent view = loader.load();
            displayView(view);
        } catch (IOException e) {
            System.err.println("Failed to load: " + fxmlPath);
            e.printStackTrace();
        }
    }

    // Helper to actually put the node in the scene
    private void displayView(Parent view) {
        mainContentArea.getChildren().clear();
        mainContentArea.getChildren().add(view);
        VBox.setVgrow(view, javafx.scene.layout.Priority.ALWAYS);
    }

    // Visual helper
    public void setActiveButton(Button activeBtn) {
        for (Button btn : menuButtons) btn.getStyleClass().remove("active");
        if(activeBtn != null) activeBtn.getStyleClass().add("active");
    }

    // Getters for external access (e.g. from Dashboard)
    public Button getRepoButton() { return repoButton; }
    public Button getUsersButton() { return usersButton; }
    public Button getReportsButton() { return reportsButton; }

    @FXML
    private void handleLogout(ActionEvent event) {
        SessionManager.getInstance().logout();
        SceneManager.getInstance().loadScene("auth/Login.fxml", "Login", event);
    }
}