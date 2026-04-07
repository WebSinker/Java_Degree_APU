package ui;

import models.User;
import services.AuthenticationService;
import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;

public class PinSetupView {
    private Stage stage;
    private User pendingUser;

    public PinSetupView(Stage stage, User pendingUser) {
        this.stage = stage;
        this.pendingUser = pendingUser;
    }

    public Scene createScene() {
        VBox root = new VBox(25);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(40));
        root.getStyleClass().add("root");

        Label titleLabel = new Label("Set Payment PIN");
        titleLabel.getStyleClass().add("title-text");
        
        Label instructionLabel = new Label("Secure your account with a 6-digit payment PIN.\nThis PIN will be required for all E-Wallet transactions.");
        instructionLabel.getStyleClass().add("subtitle-text");
        instructionLabel.setStyle("-fx-text-alignment: center;");

        VBox headerBox = new VBox(10, titleLabel, instructionLabel);
        headerBox.setAlignment(Pos.CENTER);

        VBox formBox = new VBox(15);
        formBox.setAlignment(Pos.CENTER);
        formBox.getStyleClass().add("form-container");
        formBox.setMaxWidth(400);

        Label pinLabel = new Label("6-Digit PIN");
        PasswordField pinField = new PasswordField();
        pinField.setPromptText("Enter 6 numbers");
        pinField.setMaxWidth(200);
        pinField.setStyle("-fx-alignment: center; -fx-font-size: 18px; -fx-letter-spacing: 0.5em;");

        // Force numeric and length limit
        pinField.textProperty().addListener((obs, old, val) -> {
            if (!val.matches("\\d*")) {
                pinField.setText(val.replaceAll("[^\\d]", ""));
            }
            if (pinField.getText().length() > 6) {
                pinField.setText(pinField.getText().substring(0, 6));
            }
        });

        Label confirmLabel = new Label("Confirm PIN");
        PasswordField confirmField = new PasswordField();
        confirmField.setPromptText("Re-enter PIN");
        confirmField.setMaxWidth(200);
        confirmField.setStyle("-fx-alignment: center; -fx-font-size: 18px; -fx-letter-spacing: 0.5em;");

        Label errorLabel = new Label();
        errorLabel.getStyleClass().add("error-text");
        errorLabel.setManaged(false);

        Button finalizeBtn = new Button("Finish Registration");
        finalizeBtn.getStyleClass().add("primary-button");
        finalizeBtn.setMaxWidth(200);

        formBox.getChildren().addAll(pinLabel, pinField, confirmLabel, confirmField, errorLabel, finalizeBtn);

        root.getChildren().addAll(headerBox, formBox);

        // Actions
        finalizeBtn.setOnAction(e -> {
            String pin1 = pinField.getText();
            String pin2 = confirmField.getText();

            if (pin1.length() != 6) {
                showError(errorLabel, "PIN must be exactly 6 digits.", formBox);
                return;
            }
            if (!pin1.equals(pin2)) {
                showError(errorLabel, "PINs do not match.", formBox);
                return;
            }

            // Set PIN and save user
            pendingUser.setPaymentPin(pin1);
            String result = AuthenticationService.registerUser(pendingUser);

            if ("Success".equals(result)) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Account Created");
                alert.setHeaderText(null);
                alert.setContentText("Registration Successful! Redirecting to Login...");
                alert.showAndWait();
                
                LoginView loginView = new LoginView(stage);
                stage.setScene(loginView.createScene());
            } else {
                showError(errorLabel, result, formBox);
            }
        });

        Scene scene = new Scene(root, 1000, 700);
        scene.getStylesheets().add(getClass().getResource("styles.css").toExternalForm());

        // Entrance Animation
        root.setOpacity(0);
        FadeTransition ft = new FadeTransition(Duration.millis(1000), root);
        ft.setToValue(1);
        ft.play();

        return scene;
    }

    private void showError(Label errorLabel, String msg, VBox box) {
        errorLabel.setText(msg);
        errorLabel.setManaged(true);
        
        TranslateTransition tt = new TranslateTransition(Duration.millis(50), box);
        tt.setByX(10f);
        tt.setCycleCount(6);
        tt.setAutoReverse(true);
        tt.play();
    }
}
