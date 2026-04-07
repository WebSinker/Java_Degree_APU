package ui;

import models.User;
import utils.FileHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.List;

public class TopUpView {
    private Stage stage;
    private User customer;

    public TopUpView(Stage stage, User customer) {
        this.stage = stage;
        this.customer = customer;
    }

    public Scene createScene() {
        VBox root = new VBox(20);
        root.setPadding(new Insets(40));
        root.setAlignment(Pos.CENTER);

        Label title = new Label("Top Up E-Wallet");
        title.getStyleClass().add("title-text");

        Label currentBalanceLabel = new Label("Current Balance: RM " + String.format("%.2f", customer.getBalance()));
        currentBalanceLabel.getStyleClass().add("subtitle-text");

        TextField amountField = new TextField();
        amountField.setPromptText("Enter amount (RM)");
        amountField.setMaxWidth(200);

        Button topUpBtn = new Button("Confirm Top Up");
        topUpBtn.getStyleClass().add("primary-button");

        Button backBtn = new Button("Back to Dashboard");
        backBtn.getStyleClass().add("secondary-button");

        root.getChildren().addAll(title, currentBalanceLabel, new Label("Enter Top Up Amount:"), amountField, topUpBtn, backBtn);

        // Actions
        topUpBtn.setOnAction(e -> {
            try {
                double amount = Double.parseDouble(amountField.getText().trim());
                if (amount <= 0) {
                    showError("Amount must be greater than zero.");
                    return;
                }

                // Redirect to Bank Simulation instead of immediate balance update
                BankSimulationView bank = new BankSimulationView(stage, customer, amount, stage.getScene());
                stage.setScene(bank.createScene());

            } catch (NumberFormatException ex) {
                showError("Invalid amount format.");
            }
        });

        backBtn.setOnAction(e -> {
            CustomerDashboardView dashboard = new CustomerDashboardView(stage, customer);
            stage.setScene(dashboard.createScene());
        });

        Scene scene = new Scene(root, 1000, 700);
        scene.getStylesheets().add(getClass().getResource("styles.css").toExternalForm());
        return scene;
    }

    private void showError(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setContentText(msg);
        alert.show();
    }
}
