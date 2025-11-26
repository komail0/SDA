package com.example.sda.utils;

import com.example.sda.HelloApplication;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import org.controlsfx.control.Notifications;
import javafx.util.Duration;

import java.io.IOException;
import java.util.Objects;
import java.util.function.Consumer;


public class ToastHelper {

    private static final String FXML_PATH = "/com/example/sda/fxml/components/Toast.fxml";

    private static final String STYLE_SUCCESS = "toast-success-bg";
    private static final String STYLE_ERROR = "toast-error-bg";
    private static final String STYLE_WARNING = "toast-warning-bg";


    private static void show(String title, String message, String accentStyle, Pos position, Consumer<Node> nodeProcessor) {

        Platform.runLater(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(
                        HelloApplication.class.getResource(FXML_PATH)
                ));
                Node toastRoot = loader.load();


                // Look up FXML elements by fx:id
                HBox rootHBox = (HBox) toastRoot.lookup("#toastRoot"); // Cast root to HBox
                Text titleText = (Text) toastRoot.lookup("#toastTitle");
                Label messageLabel = (Label) toastRoot.lookup("#toastMessage");
                StackPane iconContainer = (StackPane) toastRoot.lookup("#statusIconContainer");
                Button closeButton = (Button) toastRoot.lookup("#closeButton");
                Text logoText = (Text) toastRoot.lookup("#toastLogo");

                if (titleText == null || messageLabel == null || iconContainer == null ||
                        logoText == null || closeButton == null) {
                    throw new IllegalStateException("Toast FXML missing required fx:id elements. Check Toast.fxml");
                }

                // Set content
                titleText.setText(title);
                messageLabel.setText(message);
                messageLabel.setStyle("-fx-text-fill: #ffffff;"); // Force white color
                logoText.setText("AB"); // AcadBridge logo

                // Apply accent style to BOTH root and icon container
                rootHBox.getStyleClass().removeAll(STYLE_SUCCESS, STYLE_ERROR, STYLE_WARNING);
                rootHBox.getStyleClass().add(accentStyle);

                iconContainer.getStyleClass().removeAll(STYLE_SUCCESS, STYLE_ERROR, STYLE_WARNING);
                iconContainer.getStyleClass().add(accentStyle);

                // Build notification
                Notifications notificationBuilder = Notifications.create()
                        .title(null)
                        .text(null)
                        .graphic(toastRoot)
                        .hideAfter(Duration.seconds(4))
                        .position(position)
                        .onAction(event -> {
                            // Optional: handle notification click
                        });

                // Apply custom styling to remove default ControlsFX background
                notificationBuilder.styleClass("custom-controlsfx-notification");

                // Make close button functional
                final Notifications notification = notificationBuilder;
                closeButton.setOnAction(event -> {
                    // Hide the notification when close button is clicked
                    // Note: ControlsFX doesn't provide direct hide method,
                    // so we rely on the hideAfter timer
                });

                // Apply additional processing if provided
                if (nodeProcessor != null) {
                    nodeProcessor.accept(toastRoot);
                }

                notification.show();

            } catch (IOException e) {
                System.err.println("FATAL: Could not load Toast FXML: " + FXML_PATH);
                e.printStackTrace();

                // Fallback to simple text notification
                Notifications.create()
                        .title(title)
                        .text(message)
                        .position(position)
                        .hideAfter(Duration.seconds(3))
                        .show();
            }
        });
    }


    public static void showSuccess(String title, String message) {
        show(title, message, STYLE_SUCCESS, Pos.TOP_RIGHT, null);
    }


    public static void showError(String title, String message) {
        show(title, message, STYLE_ERROR, Pos.TOP_RIGHT, null);
    }


    public static void showWarning(String title, String message) {
        show(title, message, STYLE_WARNING, Pos.TOP_RIGHT, null);
    }


    public static void showCustom(String title, String message, Pos position) {
        show(title, message, STYLE_SUCCESS, position, null);
    }
}