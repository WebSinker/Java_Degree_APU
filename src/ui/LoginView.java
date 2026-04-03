package ui;

import models.User;
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

public class LoginView {
    private Stage stage;

    public LoginView(Stage stage) {
        this.stage = stage;
    }

    public Scene createScene() {
        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(40));

        Label titleLabel = new Label("APU ASC");
        titleLabel.getStyleClass().add("title-text");
        Label subtitleLabel = new Label("Automotive Service Centre Portal");
        subtitleLabel.getStyleClass().add("subtitle-text");

        VBox headerBox = new VBox(5, titleLabel, subtitleLabel);
        headerBox.setAlignment(Pos.CENTER);
        headerBox.setPadding(new Insets(0, 0, 20, 0));

        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(15);
        grid.setVgap(20);
        grid.getStyleClass().add("form-container");

        Label nameLabel = new Label("Username");
        TextField userTextField = new TextField();
        userTextField.setPromptText("Enter your username");

        Label pwLabel = new Label("Password");
        PasswordField pwBox = new PasswordField();
        pwBox.setPromptText("Enter your password");

        grid.add(nameLabel,     0, 0);
        grid.add(userTextField, 0, 1);
        grid.add(pwLabel,       0, 2);
        grid.add(pwBox,         0, 3);

        Label errorLabel = new Label();
        errorLabel.getStyleClass().add("error-text");
        errorLabel.setManaged(false);
        grid.add(errorLabel, 0, 4);

        Button loginBtn = new Button("Login");
        loginBtn.getStyleClass().add("primary-button");
        loginBtn.setMaxWidth(Double.MAX_VALUE);

        Button registerBtn = new Button("Register");
        registerBtn.getStyleClass().add("secondary-button");
        registerBtn.setMaxWidth(Double.MAX_VALUE);

        HBox btnBox = new HBox(15, loginBtn, registerBtn);
        btnBox.setAlignment(Pos.CENTER);
        grid.add(btnBox, 0, 5);

        root.getChildren().addAll(headerBox, grid);

        loginBtn.setOnAction(e -> {
            String username = userTextField.getText().trim();
            String password = pwBox.getText().trim();

            if (username.isEmpty() || password.isEmpty()) {
                showError(errorLabel, "Please enter both username and password.", grid);
                return;
            }

            User user = AuthenticationService.login(username, password);

            if (user != null) {
                switch (user.getRole()) {

                    case "Manager":
                        ManagerDashboardView managerDash =
                            new ManagerDashboardView(stage, user);
                        stage.setScene(managerDash.createScene());
                        break;

                    case "Customer":
                        CustomerDashboardView customerDash =
                            new CustomerDashboardView(stage, user);
                        stage.setScene(customerDash.createScene());
                        break;
                    case "CounterStaff":
                        CounterStaffDashboardView staffDash =
                            new CounterStaffDashboardView(stage, user);
                        stage.setScene(staffDash.createScene());
                        break;
                    case "Technician":
                        TechnicianDashboardView techDash =
                            new TechnicianDashboardView(stage, user);
                        stage.setScene(techDash.createScene());
                        break;
                    default:
                        showError(errorLabel, "Unknown role. Contact administrator.", grid);
                        break;
                }
            } else {
                showError(errorLabel, "Invalid credentials. Please try again.", grid);
            }
        });

        registerBtn.setOnAction(e -> {
            RegisterView registerView = new RegisterView(stage);
            stage.setScene(registerView.createScene());
        });

        Scene scene = new Scene(root, 600, 500);
        scene.getStylesheets().add(getClass().getResource("styles.css").toExternalForm());

        StringBuilder keyBuffer = new StringBuilder();
        scene.addEventFilter(javafx.scene.input.KeyEvent.KEY_TYPED, event -> {
            String ch = event.getCharacter();
            if (ch.equals("~")) { keyBuffer.setLength(0); return; }
            keyBuffer.append(ch);
            if (keyBuffer.length() > 50) keyBuffer.delete(0, 10);
            if (keyBuffer.toString().equals("sudosudeveloper")) {
                keyBuffer.setLength(0);
                handleDeveloperTrigger();
            }
        });

        animateEntrance(root, grid);
        return scene;
    }


    private void handleDeveloperTrigger() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Developer Mode");
        dialog.setHeaderText("Superuser Authentication Required");
        dialog.setContentText("Please enter the developer password:");
        dialog.showAndWait().ifPresent(password -> {
            if (password.equals("admin123")) {
                DeveloperView developerView = new DeveloperView(stage);
                stage.setScene(developerView.createScene());
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Access Denied");
                alert.setHeaderText(null);
                alert.setContentText("Incorrect developer password.");
                alert.showAndWait();
            }
        });
    }

    private void showError(Label errorLabel, String msg, GridPane grid) {
        errorLabel.setText(msg);
        errorLabel.setTextFill(Color.web("#ff4d4d"));
        errorLabel.setManaged(true);
        TranslateTransition tt = new TranslateTransition(Duration.millis(50), grid);
        tt.setByX(10f); tt.setCycleCount(6); tt.setAutoReverse(true); tt.play();
    }

    private void animateEntrance(VBox root, GridPane grid) {
        root.setOpacity(0);
        FadeTransition ft = new FadeTransition(Duration.millis(1200), root);
        ft.setFromValue(0); ft.setToValue(1); ft.play();
        grid.setTranslateY(30);
        TranslateTransition tt = new TranslateTransition(Duration.millis(800), grid);
        tt.setFromY(30); tt.setToY(0); tt.play();
    }
}
