package ui;

import models.ChatMessage;
import models.User;
import utils.FileHandler;
import javafx.collections.FXCollections;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

public class ChatView {
    private Stage stage;
    private User currentUser;
    private String appointmentId;
    private String otherPartyName;
    private Scene previousScene;

    private VBox chatContent;
    private ScrollPane scrollPane;
    private TextArea messageArea;
    private Timeline autoRefresh;

    public ChatView(Stage stage, User currentUser, String appointmentId, String otherPartyName, Scene previousScene) {
        this.stage = stage;
        this.currentUser = currentUser;
        this.appointmentId = appointmentId;
        this.otherPartyName = otherPartyName;
        this.previousScene = previousScene;
    }

    public Scene createScene() {
        VBox root = new VBox(15);
        root.setPadding(new Insets(20));
        root.getStyleClass().add("root");

        HBox header = new HBox(20);
        header.setAlignment(Pos.CENTER_LEFT);
        Button backBtn = new Button("← Back");
        backBtn.getStyleClass().add("secondary-button");
        Label title = new Label("Chat: " + otherPartyName + " (App: " + appointmentId + ")");
        title.getStyleClass().add("title-text");
        title.setStyle("-fx-font-size: 20px;");
        header.getChildren().addAll(backBtn, title);

        chatContent = new VBox(10);
        chatContent.setPadding(new Insets(10));
        
        scrollPane = new ScrollPane(chatContent);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefHeight(500);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: transparent;");
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        HBox inputArea = new HBox(10);
        inputArea.setAlignment(Pos.CENTER);
        messageArea = new TextArea();
        messageArea.setPromptText("Type your message here...");
        messageArea.setPrefRowCount(2);
        messageArea.setWrapText(true);
        messageArea.getStyleClass().add("text-area");
        
        Button sendBtn = new Button("Send");
        sendBtn.getStyleClass().add("primary-button");
        sendBtn.setPrefHeight(60);
        
        inputArea.getChildren().addAll(messageArea, sendBtn);

        root.getChildren().addAll(header, scrollPane, inputArea);

        loadMessages();

        // Auto-refresh every 3 seconds to simulate real-time chat
        autoRefresh = new Timeline(new KeyFrame(Duration.seconds(3), e -> refreshMessages()));
        autoRefresh.setCycleCount(Timeline.INDEFINITE);
        autoRefresh.play();

        // Actions
        sendBtn.setOnAction(e -> sendMessage());
        backBtn.setOnAction(e -> {
            autoRefresh.stop();
            stage.setScene(previousScene);
        });

        Scene scene = new Scene(root, 600, 700);
        scene.getStylesheets().add(getClass().getResource("styles.css").toExternalForm());
        return scene;
    }

    private void sendMessage() {
        String text = messageArea.getText().trim();
        if (text.isEmpty()) return;

        String id = "MSG-" + UUID.randomUUID().toString().substring(0, 8);
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        
        ChatMessage msg = new ChatMessage(id, appointmentId, currentUser.getId(), text, timestamp);
        if (FileHandler.saveChatMessage(msg)) {
            messageArea.clear();
            refreshMessages();
        }
    }

    private void refreshMessages() {
        int previousCount = chatContent.getChildren().size();
        loadMessages();
        if (chatContent.getChildren().size() > previousCount) {
            scrollPane.setVvalue(1.0); // Scroll to bottom
        }
    }

    private void loadMessages() {
        chatContent.getChildren().clear();
        List<ChatMessage> messages = FileHandler.loadChatsForAppointment(appointmentId);
        
        for (ChatMessage m : messages) {
            boolean isMe = m.getSenderId().equals(currentUser.getId());
            VBox bubbleWrapper = new VBox(2);
            
            Label msgLabel = new Label(m.getMessage());
            msgLabel.setWrapText(true);
            msgLabel.setMaxWidth(350);
            msgLabel.setPadding(new Insets(10, 15, 10, 15));
            msgLabel.setStyle("-fx-font-weight: 500;");
            
            Label timeLabel = new Label(m.getTimestamp());
            timeLabel.setStyle("-fx-font-size: 9px; -fx-text-fill: #cccccc;");

            if (isMe) {
                bubbleWrapper.setAlignment(Pos.CENTER_RIGHT);
                msgLabel.setStyle(msgLabel.getStyle() + "-fx-background-color: #0078d4; -fx-text-fill: white; -fx-background-radius: 15 15 2 15;");
            } else {
                bubbleWrapper.setAlignment(Pos.CENTER_LEFT);
                msgLabel.setStyle(msgLabel.getStyle() + "-fx-background-color: #e1e1e1; -fx-text-fill: black; -fx-background-radius: 15 15 15 2;");
            }

            bubbleWrapper.getChildren().addAll(msgLabel, timeLabel);
            chatContent.getChildren().add(bubbleWrapper);
        }
    }
}
