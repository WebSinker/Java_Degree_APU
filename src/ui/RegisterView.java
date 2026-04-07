package ui;

import models.Customer;
import services.AuthenticationService;
import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;

public class RegisterView {
    private Stage stage;

    public RegisterView(Stage stage) {
        this.stage = stage;
    }

    public Scene createScene() {
        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(30));

        // Title and Subtitle
        Label titleLabel = new Label("Join APU ASC");
        titleLabel.getStyleClass().add("title-text");
        Label subtitleLabel = new Label("Register a new Customer Account");
        subtitleLabel.getStyleClass().add("subtitle-text");

        VBox headerBox = new VBox(5, titleLabel, subtitleLabel);
        headerBox.setAlignment(Pos.CENTER);
        headerBox.setPadding(new Insets(0, 0, 20, 0));

        // Form Container
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(15);
        grid.setVgap(15);
        grid.getStyleClass().add("form-container");

        Label nameUserLabel = new Label("Username");
        TextField userTextField = new TextField();
        userTextField.setPromptText("Choose username");

        Label pwLabel = new Label("Password");
        PasswordField pwBox = new PasswordField();
        pwBox.setPromptText("Choose password");

        Label fullNameLabel = new Label("Full Name");
        TextField fullNameField = new TextField();
        fullNameField.setPromptText("Enter full name");

        Label contactLabel = new Label("Contact Number");
        TextField contactField = new TextField();
        contactField.setPromptText("Enter contact no.");

        grid.add(nameUserLabel, 0, 0);
        grid.add(userTextField, 0, 1);
        grid.add(pwLabel, 1, 0);
        grid.add(pwBox, 1, 1);
        grid.add(fullNameLabel, 0, 2);
        grid.add(fullNameField, 0, 3);
        grid.add(contactLabel, 1, 2);
        grid.add(contactField, 1, 3);

        Label errorLabel = new Label();
        errorLabel.getStyleClass().add("error-text");
        errorLabel.setManaged(false);
        GridPane.setColumnSpan(errorLabel, 2);
        grid.add(errorLabel, 0, 4);

        // Buttons
        Button registerBtn = new Button("Register");
        registerBtn.getStyleClass().add("primary-button");
        registerBtn.setMaxWidth(Double.MAX_VALUE);
        
        Button cancelBtn = new Button("Cancel");
        cancelBtn.getStyleClass().add("secondary-button");
        cancelBtn.setMaxWidth(Double.MAX_VALUE);

        HBox btnBox = new HBox(15, registerBtn, cancelBtn);
        btnBox.setAlignment(Pos.CENTER);
        GridPane.setColumnSpan(btnBox, 2);
        grid.add(btnBox, 0, 5);

        root.getChildren().addAll(headerBox, grid);

        // Actions
        registerBtn.setOnAction(e -> {
            String username = userTextField.getText().trim();
            String password = pwBox.getText().trim();
            String name = fullNameField.getText().trim();
            String contact = contactField.getText().trim();

            if (username.isEmpty() || password.isEmpty() || name.isEmpty() || contact.isEmpty()) {
                showError(errorLabel, "Please fill in all fields.", grid);
                return;
            }

            String id = "C-" + java.util.UUID.randomUUID().toString().substring(0, 8).toUpperCase();
            Customer newCustomer = new Customer(id, username, password, name, contact, "Self");

            // Instead of registering immediately, proceed to PIN setup
            PinSetupView pinSetup = new PinSetupView(stage, newCustomer);
            stage.setScene(pinSetup.createScene());
        });

        cancelBtn.setOnAction(e -> returnToLogin());

        Scene scene = new Scene(root, 1000, 700);
        scene.getStylesheets().add(getClass().getResource("styles.css").toExternalForm());

        // Animations
        animateEntrance(root, grid);

        return scene;
    }

    private void returnToLogin() {
        LoginView loginView = new LoginView(stage);
        stage.setScene(loginView.createScene());
    }

    private void showError(Label errorLabel, String msg, GridPane grid) {
        errorLabel.setText(msg);
        errorLabel.setTextFill(Color.web("#ff4d4d")); // Error color
        errorLabel.setManaged(true);

        // Shake animation
        TranslateTransition tt = new TranslateTransition(Duration.millis(50), grid);
        tt.setByX(10f);
        tt.setCycleCount(6);
        tt.setAutoReverse(true);
        tt.play();
    }

    private void animateEntrance(VBox root, GridPane grid) {
        root.setOpacity(0);
        FadeTransition ft = new FadeTransition(Duration.millis(1200), root);
        ft.setFromValue(0);
        ft.setToValue(1);
        ft.play();

        grid.setTranslateY(30);
        TranslateTransition tt = new TranslateTransition(Duration.millis(800), grid);
        tt.setFromY(30);
        tt.setToY(0);
        tt.play();
    }
}
