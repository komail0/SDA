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
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class ChatController implements Initializable {

    @FXML private VBox chatListContainer;
    @FXML private VBox chatContentArea; // This must match the fx:id in chat.fxml

    private final ChatService chatService = new ChatService();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadChats();
    }

    private void loadChats() {
        User user = SessionManager.getInstance().getCurrentUser();
        if (user == null) return;

        new Thread(() -> {
            List<Chat> chats = chatService.getUserChats(user.getId());
            Platform.runLater(() -> {
                chatListContainer.getChildren().clear();
                if(chats.isEmpty()) {
                    Label empty = new Label("No active chats");
                    empty.setStyle("-fx-text-fill: #666; -fx-padding: 10;");
                    chatListContainer.getChildren().add(empty);
                } else {
                    for (Chat chat : chats) {
                        chatListContainer.getChildren().add(createChatItem(chat));
                    }
                }
            });
        }).start();
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