package ui;

import models.User;
import javafx.animation.FadeTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

public class ManagerDashboardView {
    private Stage stage;
    private User manager;

    public ManagerDashboardView(Stage stage, User manager) {
        this.stage = stage;
        this.manager = manager;
    }

    public Scene createScene() {
        VBox root = new VBox(30);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(40));

        Label titleLabel = new Label("Manager Dashboard");
        titleLabel.getStyleClass().add("title-text");
        Label welcomeLabel = new Label("Welcome, " + manager.getName());
        welcomeLabel.getStyleClass().add("subtitle-text");

        VBox headerBox = new VBox(10, titleLabel, welcomeLabel);
        headerBox.setAlignment(Pos.CENTER);

        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(20);
        grid.setVgap(20);

        Button staffBtn = createMenuButton("Staff Management", "Manage Counter Staff & Technicians");
        Button pricingBtn = createMenuButton("Service Pricing", "Set Normal/Major Service prices");
        Button feedbackBtn = createMenuButton("Feedback Moderation", "View and manage comments");
        Button reportsBtn = createMenuButton("Analyzed Reports", "View business performance charts");
        Button logoutBtn = new Button("Logout");
        logoutBtn.getStyleClass().add("secondary-button");
        logoutBtn.setPrefWidth(200);

        grid.add(staffBtn, 0, 0);
        grid.add(pricingBtn, 1, 0);
        grid.add(feedbackBtn, 0, 1);
        grid.add(reportsBtn, 1, 1);

        root.getChildren().addAll(headerBox, grid, logoutBtn);

        // Actions
        staffBtn.setOnAction(e -> {
            StaffManagementView view = new StaffManagementView(stage, manager);
            stage.setScene(view.createScene());
        });

        pricingBtn.setOnAction(e -> {
            ServicePricingView view = new ServicePricingView(stage, manager);
            stage.setScene(view.createScene());
        });

        feedbackBtn.setOnAction(e -> {
            FeedbackManagementView view = new FeedbackManagementView(stage, manager);
            stage.setScene(view.createScene());
        });

        reportsBtn.setOnAction(e -> {
            ReportView view = new ReportView(stage, manager);
            stage.setScene(view.createScene());
        });

        logoutBtn.setOnAction(e -> {
            LoginView loginView = new LoginView(stage);
            stage.setScene(loginView.createScene());
        });

        Scene scene = new Scene(root, 1000, 700);
        scene.getStylesheets().add(getClass().getResource("styles.css").toExternalForm());

        root.setOpacity(0);
        FadeTransition ft = new FadeTransition(Duration.millis(1000), root);
        ft.setFromValue(0);
        ft.setToValue(1);
        ft.play();

        return scene;
    }

    private Button createMenuButton(String title, String subtitle) {
        VBox vbox = new VBox(5);
        vbox.setAlignment(Pos.CENTER);
        Label t = new Label(title);
        t.setStyle("-fx-font-weight: bold; -fx-font-size: 16px; -fx-text-fill: white;");
        Label s = new Label(subtitle);
        s.setStyle("-fx-font-size: 11px; -fx-text-fill: #b3c5d5;");
        vbox.getChildren().addAll(t, s);

        Button btn = new Button();
        btn.setGraphic(vbox);
        btn.getStyleClass().add("form-container"); // Reuse container style for card look
        btn.setPrefSize(250, 120);
        btn.setCursor(javafx.scene.Cursor.HAND);
        
        // Add hover effect manually if CSS isn't enough for the custom graphic
        btn.setOnMouseEntered(e -> btn.setStyle("-fx-background-color: rgba(255,255,255,0.15); -fx-scale-x: 1.05; -fx-scale-y: 1.05;"));
        btn.setOnMouseExited(e -> btn.setStyle("-fx-background-color: rgba(255,255,255,0.05); -fx-scale-x: 1; -fx-scale-y: 1;"));
        
        return btn;
    }
}
