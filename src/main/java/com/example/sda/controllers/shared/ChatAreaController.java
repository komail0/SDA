package com.example.sda.controllers.shared;

import com.example.sda.models.Chat;
import com.example.sda.models.Message;
import com.example.sda.models.User;
import com.example.sda.services.MessageService;
import com.example.sda.utils.SessionManager;
import com.example.sda.utils.ToastHelper;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import java.text.SimpleDateFormat;
import java.util.List;

public class ChatAreaController {

    @FXML private Label chatUserName;
    @FXML private VBox messagesContainer;
    @FXML private ScrollPane messagesScrollPane;
    @FXML private TextArea messageInput;
    @FXML private Button sendBtn;

    private Chat currentChat;
    private User currentUser;
    private final MessageService messageService = new MessageService();

    public void setChatData(Chat chat) {
        this.currentChat = chat;
        this.currentUser = SessionManager.getInstance().getCurrentUser();

        // Setup Header
        chatUserName.setText(chat.getOtherParticipantName());

        loadMessages();

        // Auto-scroll to bottom
        messagesContainer.heightProperty().addListener((observable, oldValue, newValue) ->
                messagesScrollPane.setVvalue(1.0));

        // Setup Send Button
        sendBtn.setOnAction(e -> handleSendMessage());

        // NEW: Setup Enter Key handler
        setupEnterKeyHandler();
    }

    // NEW METHOD: Setup Enter key to send message
    private void setupEnterKeyHandler() {
        messageInput.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.ENTER) {
                // If Shift is pressed, allow new line (default behavior)
                if (event.isShiftDown()) {
                    // Do nothing, allow default JavaFX TextArea behavior (new line)
                    return;
                }

                // If only Enter is pressed, send the message and consume the event
                handleSendMessage();
                event.consume(); // Prevents the default behavior (new line)
            }
        });
    }

    private void loadMessages() {
        new Thread(() -> {
            List<Message> messages = messageService.getChatMessages(currentChat.getChatId());
            Platform.runLater(() -> {
                messagesContainer.getChildren().clear();

                for (Message msg : messages) {
                    boolean isSelf = (msg.getSenderId() == currentUser.getId());
                    messagesContainer.getChildren().add(createMessageBubble(msg, isSelf));
                }

                // Scroll to bottom after loading
                messagesScrollPane.setVvalue(1.0);
            });
        }).start();
    }

    private void handleSendMessage() {
        String text = messageInput.getText().trim();

        // FIX: Show Toast if message is empty
        if (text.isEmpty()) {
            ToastHelper.showWarning("Empty Message", "Text cannot be empty.");
            return;
        }

        Message msg = new Message(
                currentChat.getChatId(),
                currentChat.getMentorId(),
                currentChat.getStudentId(),
                currentUser.getId(),
                text
        );

        if (messageService.sendMessage(msg)) {
            // Add to UI immediately
            messagesContainer.getChildren().add(createMessageBubble(msg, true));
            messageInput.clear();
            messagesScrollPane.setVvalue(1.0);
        } else {
            ToastHelper.showError("Error", "Failed to send message.");
        }
    }

    private HBox createMessageBubble(Message msg, boolean isSelf) {
        HBox row = new HBox();
        row.getStyleClass().add("message-row");

        VBox content = new VBox();
        content.getStyleClass().add("message-content");

        VBox bubble = new VBox();
        bubble.getStyleClass().add("message-bubble");

        Label textLabel = new Label(msg.getMessageText());
        textLabel.getStyleClass().add("message-text");
        textLabel.setWrapText(true);

        bubble.getChildren().add(textLabel);

        // Timestamp
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");
        String timeStr = (msg.getSentAt() != null) ? sdf.format(msg.getSentAt()) : "Just now";
        Label timeLabel = new Label(timeStr);
        timeLabel.getStyleClass().add("message-meta");

        content.getChildren().addAll(bubble, timeLabel);

        // Avatar
        Label avatar = new Label(isSelf ? "👤" : "👨‍💼");
        avatar.getStyleClass().add("message-avatar");

        if (isSelf) {
            row.getStyleClass().add("message-row-sent");
            content.getStyleClass().add("message-content-sent");
            bubble.getStyleClass().add("message-bubble-sent");
            textLabel.getStyleClass().add("message-text-sent");
            timeLabel.getStyleClass().add("message-meta-sent");
            avatar.getStyleClass().add("message-avatar-sent");

            // For self: Content then Avatar
            row.getChildren().addAll(content, avatar);
        } else {
            // For others: Avatar then Content
            row.getChildren().addAll(avatar, content);
        }

        return row;
    }
}