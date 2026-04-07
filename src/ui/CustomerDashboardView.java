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

public class CustomerDashboardView {

    private final Stage stage;
    private final User  customer;

    public CustomerDashboardView(Stage stage, User customer) {
        this.stage    = stage;
        this.customer = customer;
    }

    public Scene createScene() {
        VBox root = new VBox(30);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(40));

        Label titleLabel = new Label("Customer Portal");
        titleLabel.getStyleClass().add("title-text");

        Label welcomeLabel = new Label("Welcome, " + customer.getName());
        welcomeLabel.getStyleClass().add("subtitle-text");

        VBox headerBox = new VBox(8, titleLabel, welcomeLabel);
        Label balanceLabel = new Label("E-Wallet Balance: RM " + String.format("%.2f", customer.getBalance()));
        balanceLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #00E676; -fx-font-weight: bold;");
        headerBox.getChildren().add(balanceLabel);
        headerBox.setAlignment(Pos.CENTER);

        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(20);
        grid.setVgap(20);

        Button profileBtn = createMenuButton(
            "Edit Profile",
            "Update name, contact & password");

        Button historyBtn = createMenuButton(
            "Service History",
            "View your past appointments & receipts");

        Button feedbackBtn = createMenuButton(
            "My Feedback",
            "See technician feedback on your jobs");

        Button commentBtn = createMenuButton(
            "Leave a Comment",
            "Rate staff & technicians");

        Button bookBtn = createMenuButton(
            "Book Appointment",
            "Schedule a new service (Deposit RM 50)");

        Button topUpBtn = createMenuButton(
            "Top Up E-Wallet",
            "Add funds to your digital wallet");

        grid.add(profileBtn,  0, 0);
        grid.add(historyBtn,  1, 0);
        grid.add(feedbackBtn, 0, 1);
        grid.add(commentBtn,  1, 1);
        grid.add(bookBtn,     0, 2);
        grid.add(topUpBtn,    1, 2);

        Button logoutBtn = new Button("Logout");
        logoutBtn.getStyleClass().add("secondary-button");
        logoutBtn.setPrefWidth(200);

        root.getChildren().addAll(headerBox, grid, logoutBtn);

        profileBtn.setOnAction(e -> {
            UserProfileView view = new UserProfileView(stage, customer);
            stage.setScene(view.createScene());
        });

        historyBtn.setOnAction(e -> {
            CustomerHistoryView view = new CustomerHistoryView(stage, customer);
            stage.setScene(view.createScene());
        });

        feedbackBtn.setOnAction(e -> {
            CustomerFeedbackView view = new CustomerFeedbackView(stage, customer);
            stage.setScene(view.createScene());
        });

        commentBtn.setOnAction(e -> {
            CustomerCommentView view = new CustomerCommentView(stage, customer);
            stage.setScene(view.createScene());
        });

        bookBtn.setOnAction(e -> {
            MakeAppointmentView view = new MakeAppointmentView(stage, customer);
            stage.setScene(view.createScene());
        });

        topUpBtn.setOnAction(e -> {
            TopUpView view = new TopUpView(stage, customer);
            stage.setScene(view.createScene());
        });

        logoutBtn.setOnAction(e -> {
            LoginView loginView = new LoginView(stage);
            stage.setScene(loginView.createScene());
        });

        Scene scene = new Scene(root, 1000, 700);
        scene.getStylesheets().add(getClass().getResource("styles.css").toExternalForm());

        root.setOpacity(0);
        FadeTransition ft = new FadeTransition(Duration.millis(900), root);
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
        s.setWrapText(true);
        s.setMaxWidth(220);
        s.setAlignment(Pos.CENTER);

        vbox.getChildren().addAll(t, s);

        Button btn = new Button();
        btn.setGraphic(vbox);
        btn.getStyleClass().add("form-container");
        btn.setPrefSize(250, 120);
        btn.setCursor(javafx.scene.Cursor.HAND);

        btn.setOnMouseEntered(e -> btn.setStyle(
            "-fx-background-color: rgba(255,255,255,0.15); " +
            "-fx-scale-x: 1.05; -fx-scale-y: 1.05;"));
        btn.setOnMouseExited(e -> btn.setStyle(
            "-fx-background-color: rgba(255,255,255,0.05); " +
            "-fx-scale-x: 1; -fx-scale-y: 1;"));

        return btn;
    }
}
