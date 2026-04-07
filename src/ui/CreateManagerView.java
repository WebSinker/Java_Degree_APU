package ui;

import models.Manager;
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
import javafx.stage.Stage;
import javafx.util.Duration;

public class CreateManagerView {
    private Stage stage;

    public CreateManagerView(Stage stage) {
        this.stage = stage;
    }

    public Scene createScene() {
        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(30));

        Label titleLabel = new Label("Create Manager");
        titleLabel.getStyleClass().add("title-text");

        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(15);
        grid.setVgap(15);
        grid.getStyleClass().add("form-container");

        Label userLabel = new Label("Username");
        TextField userField = new TextField();

        Label pwLabel = new Label("Password");
        PasswordField pwField = new PasswordField();

        Label nameLabel = new Label("Full Name");
        TextField nameField = new TextField();

        Label contactLabel = new Label("Contact Number");
        TextField contactField = new TextField();

        grid.add(userLabel, 0, 0);
        grid.add(userField, 0, 1);
        grid.add(pwLabel, 1, 0);
        grid.add(pwField, 1, 1);
        grid.add(nameLabel, 0, 2);
        grid.add(nameField, 0, 3);
        grid.add(contactLabel, 1, 2);
        grid.add(contactField, 1, 3);

        Label errorLabel = new Label();
        errorLabel.getStyleClass().add("error-text");
        errorLabel.setManaged(false);
        grid.add(errorLabel, 0, 4, 2, 1);

        Button createBtn = new Button("Create Manager");
        createBtn.getStyleClass().add("primary-button");
        createBtn.setMaxWidth(Double.MAX_VALUE);

        Button backBtn = new Button("Back");
        backBtn.getStyleClass().add("secondary-button");
        backBtn.setMaxWidth(Double.MAX_VALUE);

        HBox btnBox = new HBox(15, createBtn, backBtn);
        btnBox.setAlignment(Pos.CENTER);
        grid.add(btnBox, 0, 5, 2, 1);

        root.getChildren().addAll(titleLabel, grid);

        // Actions
        createBtn.setOnAction(e -> {
            String username = userField.getText().trim();
            String password = pwField.getText().trim();
            String name = nameField.getText().trim();
            String contact = contactField.getText().trim();

            if (username.isEmpty() || password.isEmpty() || name.isEmpty() || contact.isEmpty()) {
                showError(errorLabel, "All fields are required.", grid);
                return;
            }

            String id = "M-" + java.util.UUID.randomUUID().toString().substring(0, 5).toUpperCase();
            Manager newManager = new Manager(id, username, password, name, contact, "Developer");

            String result = AuthenticationService.registerUser(newManager);

            if ("Success".equals(result)) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Success");
                alert.setHeaderText(null);
                alert.setContentText("Manager account created successfully.");
                alert.showAndWait();
                goBack();
            }
        });

        backBtn.setOnAction(ev -> goBack());

        Scene scene = new Scene(root, 1000, 700);
        scene.getStylesheets().add(getClass().getResource("styles.css").toExternalForm());

        // Animations
        root.setOpacity(0);
        FadeTransition ft = new FadeTransition(Duration.millis(800), root);
        ft.setFromValue(0);
        ft.setToValue(1);
        ft.play();

        return scene;
    }

    private void goBack() {
        DeveloperView developerView = new DeveloperView(stage);
        stage.setScene(developerView.createScene());
    }

    private void showError(Label errorLabel, String msg, GridPane grid) {
        errorLabel.setText(msg);
        errorLabel.setManaged(true);
        TranslateTransition tt = new TranslateTransition(Duration.millis(50), grid);
        tt.setByX(10f);
        tt.setCycleCount(6);
        tt.setAutoReverse(true);
        tt.play();
    }
}
