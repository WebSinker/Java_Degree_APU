package ui;

import models.User;
import utils.FileHandler;
import javafx.animation.FadeTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.List;

public class BankSimulationView {
    private final Stage stage;
    private final User user;
    private final double topUpAmount;
    private final Scene previousScene;

    public BankSimulationView(Stage stage, User user, double topUpAmount, Scene previousScene) {
        this.stage = stage;
        this.user = user;
        this.topUpAmount = topUpAmount;
        this.previousScene = previousScene;
    }

    public Scene createScene() {
        VBox root = new VBox(0);
        root.setStyle("-fx-background-color: #f4f7f9; -fx-font-family: 'Segoe UI', sans-serif;");
        
        // Bank Header
        HBox header = new HBox(15);
        header.setPadding(new Insets(15, 30, 15, 30));
        header.setStyle("-fx-background-color: #003366;");
        header.setAlignment(Pos.CENTER_LEFT);
        
        Circle logo = new Circle(15, Color.GOLD);
        Label bankName = new Label("APU GLOBAL BANK");
        bankName.setStyle("-fx-text-fill: white; -fx-font-size: 18px; -fx-font-weight: bold; -fx-letter-spacing: 2px;");
        header.getChildren().addAll(logo, bankName);

        // Login Section
        VBox loginBox = new VBox(20);
        loginBox.setPadding(new Insets(50, 100, 50, 100));
        loginBox.setAlignment(Pos.CENTER);
        
        Label welcomeMsg = new Label("Secure Payment Gateway");
        welcomeMsg.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #333;");
        
        VBox form = new VBox(15);
        form.setPadding(new Insets(30));
        form.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 5);");
        form.setMaxWidth(400);

        TextField userField = new TextField();
        userField.setPromptText("Online Banking Username");
        userField.setPrefHeight(40);
        
        PasswordField passField = new PasswordField();
        passField.setPromptText("Password");
        passField.setPrefHeight(40);
        
        Button loginBtn = new Button("Login to APU-Bank");
        loginBtn.setStyle("-fx-background-color: #003366; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 20; -fx-cursor: hand;");
        loginBtn.setMaxWidth(Double.MAX_VALUE);
        
        Button cancelBtn = new Button("Cancel Transaction");
        cancelBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #666; -fx-cursor: hand;");

        form.getChildren().addAll(new Label("User Login"), userField, passField, loginBtn, cancelBtn);

        loginBox.getChildren().addAll(welcomeMsg, new Label("Authorize your top-up of RM " + String.format("%.2f", topUpAmount)), form);

        root.getChildren().addAll(header, loginBox);

        // Confirmation Logic
        loginBtn.setOnAction(e -> {
            if (userField.getText().equals(user.getUsername()) && passField.getText().equals(user.getPassword())) {
                showApprovalScreen(root, loginBox);
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Bank Error");
                alert.setHeaderText("Invalid Credentials");
                alert.setContentText("Please use your APU-ASC account username and password.");
                alert.show();
            }
        });

        cancelBtn.setOnAction(e -> stage.setScene(previousScene));

        Scene scene = new Scene(root, 700, 600);
        return scene;
    }

    private void showApprovalScreen(VBox root, VBox loginBox) {
        root.getChildren().remove(loginBox);

        VBox approvalBox = new VBox(30);
        approvalBox.setPadding(new Insets(50));
        approvalBox.setAlignment(Pos.CENTER);

        Label title = new Label("Confirm Authorisation");
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold;");

        GridPane summary = new GridPane();
        summary.setHgap(20); summary.setVgap(15);
        summary.setAlignment(Pos.CENTER);
        summary.setStyle("-fx-background-color: #e3f2fd; -fx-padding: 20; -fx-border-color: #2196f3; -fx-border-radius: 5;");

        summary.add(new Label("Merchant:"), 0, 0); 
        summary.add(new Label("APU Automotive Service Centre"), 1, 0);
        summary.add(new Label("Amount:"), 0, 1);
        Label amt = new Label("RM " + String.format("%.2f", topUpAmount));
        amt.setStyle("-fx-font-weight: bold; -fx-text-fill: #c62828;");
        summary.add(amt, 1, 1);
        summary.add(new Label("Source:"), 0, 2);
        summary.add(new Label("APU Savings Account (*7829)"), 1, 2);

        Button approveBtn = new Button("Authorize Transfer");
        approveBtn.setStyle("-fx-background-color: #2e7d32; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 15 40; -fx-font-size: 16px; -fx-cursor: hand;");
        
        approveBtn.setOnAction(e -> completeTransaction());

        approvalBox.getChildren().addAll(title, summary, approveBtn);
        root.getChildren().add(approvalBox);
        
        FadeTransition ft = new FadeTransition(Duration.millis(500), approvalBox);
        ft.setFromValue(0); ft.setToValue(1); ft.play();
    }

    private void completeTransaction() {
        user.setBalance(user.getBalance() + topUpAmount);
        
        // Save to file
        List<User> all = FileHandler.loadAllUsers();
        for (int i = 0; i < all.size(); i++) {
            if (all.get(i).getId().equals(user.getId())) {
                all.set(i, user);
                break;
            }
        }
        FileHandler.saveAllUsers(all);

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Bank Transfer Success");
        alert.setHeaderText("Transaction Completed");
        alert.setContentText("RM " + String.format("%.2f", topUpAmount) + " has been transferred to your E-Wallet.\nThank you for using APU-Bank.");
        alert.showAndWait();
        
        // Return to Dashboard
        CustomerDashboardView dashboard = new CustomerDashboardView(stage, user);
        stage.setScene(dashboard.createScene());
    }
}
