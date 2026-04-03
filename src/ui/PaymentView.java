package ui;

import models.Appointment;
import models.Receipt;
import models.User;
import utils.FileHandler;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class PaymentView {
    private Stage stage;
    private User staff;
    private TableView<Appointment> table;
    private ObservableList<Appointment> appList;

    public PaymentView(Stage stage, User staff) {
        this.stage = stage;
        this.staff = staff;
    }

    public Scene createScene() {
        VBox root = new VBox(20);
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.TOP_CENTER);

        Label title = new Label("Payment Collection");
        title.getStyleClass().add("title-text");

        table = new TableView<>();
        
        TableColumn<Appointment, String> idCol = new TableColumn<>("App ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        TableColumn<Appointment, String> customCol = new TableColumn<>("Customer ID");
        customCol.setCellValueFactory(new PropertyValueFactory<>("customerId"));
        TableColumn<Appointment, String> typeCol = new TableColumn<>("Type");
        typeCol.setCellValueFactory(new PropertyValueFactory<>("serviceType"));
        TableColumn<Appointment, Double> priceCol = new TableColumn<>("Price");
        priceCol.setCellValueFactory(new PropertyValueFactory<>("price"));
        TableColumn<Appointment, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));

        table.getColumns().addAll(idCol, customCol, typeCol, priceCol, statusCol);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        refreshTable();

        Button payBtn = new Button("Collect Payment & Issue Receipt");
        payBtn.getStyleClass().add("primary-button");
        
        Button backBtn = new Button("Back to Dashboard");
        backBtn.getStyleClass().add("secondary-button");

        root.getChildren().addAll(title, new Label("Unpaid Completed Appointments:"), table, payBtn, backBtn);

        // Actions
        payBtn.setOnAction(e -> {
            Appointment selected = table.getSelectionModel().getSelectedItem();
            if (selected != null) {
                // Find the customer to deduct from wallet
                List<User> allUsers = FileHandler.loadAllUsers();
                User customer = allUsers.stream()
                        .filter(u -> u.getId().equals(selected.getCustomerId()))
                        .findFirst().orElse(null);

                if (customer == null) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setContentText("Customer not found.");
                    alert.show();
                    return;
                }

                double totalDue = selected.getPrice() - 50.0; // Subtract deposit
                boolean usedWallet = false;

                if (customer.getBalance() >= totalDue) {
                    customer.setBalance(customer.getBalance() - totalDue);
                    usedWallet = true;
                    // Update user file
                    FileHandler.saveAllUsers(allUsers);
                } else {
                    // Ask staff if they collected cash
                    Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
                    confirm.setTitle("Insufficient Wallet Balance");
                    confirm.setHeaderText("Customer wallet only has RM " + String.format("%.2f", customer.getBalance()));
                    confirm.setContentText("Would you like to mark this as Paid (Manual/Cash Collection of RM " + String.format("%.2f", totalDue) + ")?");
                    
                    if (confirm.showAndWait().orElse(ButtonType.CANCEL) != ButtonType.OK) {
                        return;
                    }
                }

                // Update appointment to Paid
                selected.setPaid(true);
                List<Appointment> allApps = FileHandler.loadAllAppointments();
                for (int i = 0; i < allApps.size(); i++) {
                    if (allApps.get(i).getId().equals(selected.getId())) {
                        allApps.set(i, selected);
                        break;
                    }
                }
                FileHandler.saveAllAppointments(allApps);

                // Create Receipt
                String rId = "R-" + UUID.randomUUID().toString().substring(0, 5).toUpperCase();
                Receipt receipt = new Receipt(
                    rId, selected.getId(), selected.getCustomerId(),
                    selected.getServiceType(), "Service Fee",
                    selected.getPrice(), LocalDate.now().toString(), staff.getId()
                );
                FileHandler.saveReceipt(receipt);

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Success");
                alert.setHeaderText(usedWallet ? "E-Wallet Payment Successful" : "Manual Payment Recorded");
                alert.setContentText("Receipt ID: " + rId + "\nTotal Amount: RM " + selected.getPrice() + 
                                     (usedWallet ? "\nDeducted from Wallet: RM " + String.format("%.2f", totalDue) : ""));
                alert.showAndWait();

                refreshTable();
            }
        });

        backBtn.setOnAction(e -> {
            CounterStaffDashboardView dashboard = new CounterStaffDashboardView(stage, staff);
            stage.setScene(dashboard.createScene());
        });

        Scene scene = new Scene(root, 900, 700);
        scene.getStylesheets().add(getClass().getResource("styles.css").toExternalForm());
        return scene;
    }

    private void refreshTable() {
        appList = FXCollections.observableArrayList(
            FileHandler.loadAllAppointments().stream()
                .filter(a -> a.getStatus().equals(Appointment.STATUS_COMPLETED) && !a.isPaid())
                .collect(Collectors.toList())
        );
        table.setItems(appList);
    }
}
