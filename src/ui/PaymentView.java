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
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.control.Separator;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class PaymentView {
    private Stage stage;
    private User staff;
    private TableView<Appointment> pendingTable;
    private TableView<Receipt> historyTable;
    private ObservableList<Appointment> pendingList;
    private ObservableList<Receipt> historyList;

    public PaymentView(Stage stage, User staff) {
        this.stage = stage;
        this.staff = staff;
    }

    public Scene createScene() {
        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        Tab pendingTab = new Tab("Pending Payments", createPendingTab());
        Tab historyTab = new Tab("Receipt History", createHistoryTab());

        tabPane.getTabs().addAll(pendingTab, historyTab);

        VBox root = new VBox(10, tabPane);
        root.setPadding(new Insets(10));
        
        Button backBtn = new Button("Back to Dashboard");
        backBtn.getStyleClass().add("secondary-button");
        backBtn.setOnAction(e -> {
            CounterStaffDashboardView dashboard = new CounterStaffDashboardView(stage, staff);
            stage.setScene(dashboard.createScene());
        });
        root.getChildren().add(backBtn);

        Scene scene = new Scene(root, 1000, 800);
        scene.getStylesheets().add(getClass().getResource("styles.css").toExternalForm());
        return scene;
    }

    private VBox createPendingTab() {
        VBox box = new VBox(20);
        box.setPadding(new Insets(20));
        box.setAlignment(Pos.TOP_CENTER);

        Label title = new Label("Pending Payment Collection");
        title.getStyleClass().add("title-text");

        pendingTable = new TableView<>();
        setupPendingTable();

        Button payBtn = new Button("Collect Payment & Issue Receipt");
        payBtn.getStyleClass().add("primary-button");
        payBtn.setOnAction(e -> handlePayment());

        box.getChildren().addAll(title, pendingTable, payBtn);
        refreshPendingTable();
        return box;
    }

    private void setupPendingTable() {
        TableColumn<Appointment, String> idCol = new TableColumn<>("App ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        TableColumn<Appointment, String> customCol = new TableColumn<>("Customer ID");
        customCol.setCellValueFactory(new PropertyValueFactory<>("customerId"));
        TableColumn<Appointment, String> typeCol = new TableColumn<>("Type");
        typeCol.setCellValueFactory(new PropertyValueFactory<>("serviceType"));
        TableColumn<Appointment, Double> priceCol = new TableColumn<>("Price");
        priceCol.setCellValueFactory(new PropertyValueFactory<>("price"));
        
        pendingTable.getColumns().addAll(idCol, customCol, typeCol, priceCol);
        pendingTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    private VBox createHistoryTab() {
        VBox box = new VBox(20);
        box.setPadding(new Insets(20));
        box.setAlignment(Pos.TOP_CENTER);

        Label title = new Label("Issued Receipts History");
        title.getStyleClass().add("title-text");

        historyTable = new TableView<>();
        setupHistoryTable();

        Button viewBtn = new Button("View / Send Receipt to Customer");
        viewBtn.getStyleClass().add("primary-button");
        viewBtn.setOnAction(e -> showReceiptDetails());

        box.getChildren().addAll(title, historyTable, viewBtn);
        refreshHistoryTable();
        return box;
    }

    private void setupHistoryTable() {
        TableColumn<Receipt, String> rIdCol = new TableColumn<>("Receipt ID");
        rIdCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        TableColumn<Receipt, String> appIdCol = new TableColumn<>("App ID");
        appIdCol.setCellValueFactory(new PropertyValueFactory<>("appointmentId"));
        TableColumn<Receipt, String> custIdCol = new TableColumn<>("Customer ID");
        custIdCol.setCellValueFactory(new PropertyValueFactory<>("customerId"));
        TableColumn<Receipt, Double> amtCol = new TableColumn<>("Amount");
        amtCol.setCellValueFactory(new PropertyValueFactory<>("amountPaid"));
        TableColumn<Receipt, String> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(new PropertyValueFactory<>("paymentDate"));

        historyTable.getColumns().addAll(rIdCol, appIdCol, custIdCol, amtCol, dateCol);
        historyTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    private void handlePayment() {
        Appointment selected = pendingTable.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        List<User> allUsers = FileHandler.loadAllUsers();
        User customer = allUsers.stream().filter(u -> u.getId().equals(selected.getCustomerId())).findFirst().orElse(null);

        if (customer == null) {
            showAlert("Error", "Customer not found.", Alert.AlertType.ERROR);
            return;
        }

        double totalDue = selected.getPrice() - 50.0; 
        boolean usedWallet = false;

        if (customer.getBalance() >= totalDue) {
            customer.setBalance(customer.getBalance() - totalDue);
            usedWallet = true;
            FileHandler.saveAllUsers(allUsers);
        } else {
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Manual Collection");
            confirm.setHeaderText("Insufficient Wallet: RM " + customer.getBalance());
            confirm.setContentText("Collect RM " + totalDue + " in Cash manually?");
            if (confirm.showAndWait().orElse(ButtonType.CANCEL) != ButtonType.OK) return;
        }

        selected.setPaid(true);
        updateAppointment(selected);

        String rId = "R-" + UUID.randomUUID().toString().substring(0, 5).toUpperCase();
        Receipt receipt = new Receipt(rId, selected.getId(), selected.getCustomerId(), 
            selected.getServiceType(), "Mechanical Service", selected.getPrice(), 
            LocalDate.now().toString(), staff.getId());
        FileHandler.saveReceipt(receipt);

        showAlert("Success", "Payment processed. Receipt: " + rId, Alert.AlertType.INFORMATION);
        refreshPendingTable();
        refreshHistoryTable();
    }

    private void showReceiptDetails() {
        Receipt selected = historyTable.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        Stage popup = new Stage();
        popup.setTitle("Customer Receipt: " + selected.getId());
        
        VBox content = new VBox(15);
        content.setPadding(new Insets(30));
        content.setStyle("-fx-background-color: white;");
        content.setAlignment(Pos.TOP_CENTER);

        Label header = new Label("APU AUTOMOTIVE SERVICE CENTRE");
        header.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: black;");
        
        Separator sep = new Separator();
        
        GridPane details = new GridPane();
        details.setHgap(20); details.setVgap(10);
        details.setAlignment(Pos.CENTER);
        
        addReceiptRow(details, 0, "Receipt ID:", selected.getId());
        addReceiptRow(details, 1, "Date:", selected.getPaymentDate());
        addReceiptRow(details, 2, "Customer ID:", selected.getCustomerId());
        addReceiptRow(details, 3, "Service:", selected.getServiceType());
        addReceiptRow(details, 4, "Amount Paid:", "RM " + String.format("%.2f", selected.getAmountPaid()));
        addReceiptRow(details, 5, "Staff ID:", selected.getCounterStaffId());

        Label footer = new Label("Keep this receipt for your records.\nThank you for choosing APU-ASC!");
        footer.setStyle("-fx-font-style: italic; -fx-text-fill: #666; -fx-text-alignment: center;");
        
        Button sendBtn = new Button("Send to Customer Device");
        sendBtn.getStyleClass().add("primary-button");
        sendBtn.setOnAction(e -> {
            showAlert("Sync", "Receipt has been sent to customer " + selected.getCustomerId(), Alert.AlertType.INFORMATION);
            popup.close();
        });

        content.getChildren().addAll(header, sep, details, new Separator(), footer, sendBtn);
        Scene scene = new Scene(content, 450, 500);
        popup.setScene(scene);
        popup.show();
    }

    private void addReceiptRow(GridPane gp, int row, String label, String value) {
        Label l = new Label(label);
        l.setStyle("-fx-font-weight: bold; -fx-text-fill: #333;");
        Label v = new Label(value);
        v.setStyle("-fx-text-fill: black;");
        gp.add(l, 0, row);
        gp.add(v, 1, row);
    }

    private void updateAppointment(Appointment app) {
        List<Appointment> all = FileHandler.loadAllAppointments();
        for (int i = 0; i < all.size(); i++) {
            if (all.get(i).getId().equals(app.getId())) {
                all.set(i, app);
                break;
            }
        }
        FileHandler.saveAllAppointments(all);
    }

    private void refreshPendingTable() {
        pendingList = FXCollections.observableArrayList(
            FileHandler.loadAllAppointments().stream()
                .filter(a -> a.getStatus().equals(Appointment.STATUS_COMPLETED) && !a.isPaid())
                .collect(Collectors.toList())
        );
        pendingTable.setItems(pendingList);
    }

    private void refreshHistoryTable() {
        historyList = FXCollections.observableArrayList(FileHandler.loadAllReceipts());
        historyTable.setItems(historyList);
    }

    private void showAlert(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.show();
    }
}
