package com.example.sda.controllers.shared;

import com.example.sda.models.Chat;
import com.example.sda.models.User;
import com.example.sda.services.ChatService;
import com.example.sda.utils.SessionManager;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class ChatController implements Initializable {

    @FXML private VBox chatListContainer;
    @FXML private VBox chatContentArea;
    @FXML private TextField searchField;

    private final ChatService chatService = new ChatService();

    // Master list to store all fetched chats for filtering
    private List<Chat> allChats = new ArrayList<>();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadChats();

        // Add Search Listener
        if (searchField != null) {
            searchField.textProperty().addListener((observable, oldValue, newValue) -> {
                filterChats(newValue);
            });
        }
    }

    private void loadChats() {
        User user = SessionManager.getInstance().getCurrentUser();
        if (user == null) return;

        new Thread(() -> {
            // Fetch from DB
            List<Chat> chats = chatService.getUserChats(user.getId());

            // Store in master list
            allChats = chats;

            Platform.runLater(() -> {
                // Initial display of all chats
                displayChats(allChats);
            });
        }).start();
    }

    // New helper method to display a specific list of chats
    private void displayChats(List<Chat> chatsToDisplay) {
        chatListContainer.getChildren().clear();

        if(chatsToDisplay.isEmpty()) {
            Label empty = new Label("No active chats found");
            empty.setStyle("-fx-text-fill: #666; -fx-padding: 10;");
            chatListContainer.getChildren().add(empty);
        } else {
            for (Chat chat : chatsToDisplay) {
                chatListContainer.getChildren().add(createChatItem(chat));
            }
        }
    }

    // New logic to filter the list based on input
    private void filterChats(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            displayChats(allChats); // Show all if search is empty
            return;
        }

        String lowerCaseFilter = keyword.toLowerCase();
        List<Chat> filteredList = new ArrayList<>();

        for (Chat chat : allChats) {
            String participantName = chat.getOtherParticipantName();
            if (participantName != null && participantName.toLowerCase().contains(lowerCaseFilter)) {
                filteredList.add(chat);
            }
        }

        displayChats(filteredList);
    }

    private HBox createChatItem(Chat chat) {
        HBox item = new HBox();
        item.getStyleClass().add("chat-item");

        String name = chat.getOtherParticipantName();
        String initials = (name != null && !name.isEmpty()) ? name.substring(0, 1).toUpperCase() : "?";

        Label avatar = new Label(initials);
        avatar.getStyleClass().add("chat-avatar");

        VBox info = new VBox();
        info.getStyleClass().add("chat-info");
        HBox.setHgrow(info, Priority.ALWAYS);

        Label nameLabel = new Label(name);
        nameLabel.getStyleClass().add("chat-name");

        Label previewLabel = new Label("Click to open chat");
        previewLabel.getStyleClass().add("chat-preview");

        info.getChildren().addAll(nameLabel, previewLabel);
        item.getChildren().addAll(avatar, info);

        // Click Handler
        item.setOnMouseClicked(e -> {
            // Visual selection
            chatListContainer.getChildren().forEach(node -> node.getStyleClass().remove("active"));
            item.getStyleClass().add("active");

            // Load Chat Area
            loadChatArea(chat);
        });

        return item;
    }

    private void loadChatArea(Chat chat) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/sda/fxml/shared/message.fxml"));
            Parent chatView = loader.load();

            ChatAreaController controller = loader.getController();
            controller.setChatData(chat);

            chatContentArea.getChildren().clear();
            chatContentArea.getChildren().add(chatView);
            VBox.setVgrow(chatView, Priority.ALWAYS);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}