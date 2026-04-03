package ui;

import javafx.animation.FadeTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

public class DeveloperView {
    private Stage stage;

    public DeveloperView(Stage stage) {
        this.stage = stage;
    }

    public Scene createScene() {
        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(40));

        Label titleLabel = new Label("Developer Mode");
        titleLabel.getStyleClass().add("title-text");
        Label subtitleLabel = new Label("Superuser Dashboard");
        subtitleLabel.getStyleClass().add("subtitle-text");

        VBox headerBox = new VBox(5, titleLabel, subtitleLabel);
        headerBox.setAlignment(Pos.CENTER);

        Button createManagerBtn = new Button("Create Manager Account");
        createManagerBtn.getStyleClass().add("primary-button");
        createManagerBtn.setPrefWidth(300);

        Button logoutBtn = new Button("Exit Developer Mode");
        logoutBtn.getStyleClass().add("secondary-button");
        logoutBtn.setPrefWidth(300);

        root.getChildren().addAll(headerBox, createManagerBtn, logoutBtn);

        // Actions
        createManagerBtn.setOnAction(e -> {
            CreateManagerView createManagerView = new CreateManagerView(stage);
            stage.setScene(createManagerView.createScene());
        });

        logoutBtn.setOnAction(e -> {
            LoginView loginView = new LoginView(stage);
            stage.setScene(loginView.createScene());
        });

        Scene scene = new Scene(root, 600, 500);
        scene.getStylesheets().add(getClass().getResource("styles.css").toExternalForm());

        // Animations
        root.setOpacity(0);
        FadeTransition ft = new FadeTransition(Duration.millis(1000), root);
        ft.setFromValue(0);
        ft.setToValue(1);
        ft.play();

        return scene;
    }
}
