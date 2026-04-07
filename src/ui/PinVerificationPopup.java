package ui;

import models.User;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.util.function.Consumer;

public class PinVerificationPopup {
    private final User user;
    private final Consumer<Boolean> callback;
    private String currentInput = "";
    private final Label[] dots = new Label[6];

    public PinVerificationPopup(User user, Consumer<Boolean> callback) {
        this.user = user;
        this.callback = callback;
    }

    public void show() {
        Stage popup = new Stage();
        popup.initModality(Modality.APPLICATION_MODAL);
        popup.initStyle(StageStyle.TRANSPARENT);
        popup.setTitle("Authorize Transaction");

        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(30));
        root.getStyleClass().add("form-container"); // Reuse glassmorphism
        root.setStyle("-fx-background-color: rgba(15, 32, 39, 0.95); -fx-border-color: #4facfe; -fx-border-width: 2; -fx-background-radius: 20; -fx-border-radius: 20;");

        Label title = new Label("Enter Payment PIN");
        title.getStyleClass().add("title-text");
        title.setStyle("-fx-font-size: 20px;");

        HBox dotBox = new HBox(15);
        dotBox.setAlignment(Pos.CENTER);
        for (int i = 0; i < 6; i++) {
            dots[i] = new Label("○");
            dots[i].setStyle("-fx-font-size: 24px; -fx-text-fill: #4facfe;");
            dotBox.getChildren().add(dots[i]);
        }

        GridPane keypad = new GridPane();
        keypad.setAlignment(Pos.CENTER);
        keypad.setHgap(15);
        keypad.setVgap(15);

        String[] keys = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "C", "0", "OK"};
        for (int i = 0; i < keys.length; i++) {
            String key = keys[i];
            Button btn = new Button(key);
            btn.setPrefSize(65, 65);
            btn.getStyleClass().add(key.equals("OK") ? "primary-button" : "secondary-button");
            btn.setStyle(btn.getStyle() + "-fx-font-size: 18px; -fx-background-radius: 50; -fx-border-radius: 50;");
            
            btn.setOnAction(e -> handleKeyPress(key, popup));
            keypad.add(btn, i % 3, i / 3);
        }

        Button cancelBtn = new Button("Cancel");
        cancelBtn.getStyleClass().add("secondary-button");
        cancelBtn.setPrefWidth(225);
        cancelBtn.setOnAction(e -> {
            callback.accept(false);
            popup.close();
        });

        root.getChildren().addAll(title, dotBox, keypad, cancelBtn);

        Scene scene = new Scene(root);
        scene.setFill(null);
        scene.getStylesheets().add(getClass().getResource("styles.css").toExternalForm());
        popup.setScene(scene);
        popup.show();
    }

    private void handleKeyPress(String key, Stage stage) {
        if (key.equals("C")) {
            currentInput = "";
        } else if (key.equals("OK")) {
            if (currentInput.equals(user.getPaymentPin())) {
                callback.accept(true);
                stage.close();
            } else {
                currentInput = "";
                // Shake effect or error feedback could go here
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setContentText("Incorrect PIN. Please try again.");
                alert.show();
            }
        } else {
            if (currentInput.length() < 6) {
                currentInput += key;
            }
        }
        updateDots();
    }

    private void updateDots() {
        for (int i = 0; i < 6; i++) {
            if (i < currentInput.length()) {
                dots[i].setText("●");
            } else {
                dots[i].setText("○");
            }
        }
    }
}
