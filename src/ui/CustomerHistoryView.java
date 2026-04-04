package ui;

import models.Appointment;
import models.Receipt;
import models.User;
import utils.FileHandler;
import services.AppointmentService;
import javafx.animation.FadeTransition;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.List;

public class CustomerHistoryView {

    private final Stage stage;
    private final User  customer;

    public CustomerHistoryView(Stage stage, User customer) {
        this.stage    = stage;
        this.customer = customer;
    }

    @SuppressWarnings("unchecked")
    public Scene createScene() {
        VBox root = new VBox(20);
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.TOP_CENTER);

        Label titleLabel = new Label("My Service & Payment History");
        titleLabel.getStyleClass().add("title-text");

        Label subLabel = new Label("All your past appointments and receipts");
        subLabel.getStyleClass().add("subtitle-text");

        VBox headerBox = new VBox(5, titleLabel, subLabel);
        headerBox.setAlignment(Pos.CENTER);
        headerBox.setPadding(new Insets(0, 0, 10, 0));

        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        tabPane.setStyle("-fx-background-color: transparent;");

        Tab serviceTab = new Tab("Service History");

        TableView<Appointment> aptTable = new TableView<>();
        aptTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        aptTable.setPlaceholder(new Label("No service records found."));

        TableColumn<Appointment, String> aptIdCol   = new TableColumn<>("Appointment ID");
        aptIdCol.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<Appointment, String> aptDateCol = new TableColumn<>("Date");
        aptDateCol.setCellValueFactory(new PropertyValueFactory<>("date"));

        TableColumn<Appointment, String> aptTimeCol = new TableColumn<>("Time");
        aptTimeCol.setCellValueFactory(new PropertyValueFactory<>("timeSlot"));

        TableColumn<Appointment, String> aptTypeCol = new TableColumn<>("Service Type");
        aptTypeCol.setCellValueFactory(new PropertyValueFactory<>("serviceType"));

        TableColumn<Appointment, String> aptStatCol = new TableColumn<>("Status");
        aptStatCol.setCellValueFactory(new PropertyValueFactory<>("status"));

        TableColumn<Appointment, Double> aptPriceCol = new TableColumn<>("Price (RM)");
        aptPriceCol.setCellValueFactory(new PropertyValueFactory<>("price"));

        TableColumn<Appointment, Boolean> aptPaidCol = new TableColumn<>("Paid");
        aptPaidCol.setCellValueFactory(new PropertyValueFactory<>("paid"));

        TableColumn<Appointment, Void> aptActionCol = new TableColumn<>("Actions");
        aptActionCol.setCellFactory(col -> new TableCell<>() {
            private final Button payBtn = new Button("Pay");
            private final Button chatBtn = new Button("Chat");
            private final HBox btnBox = new HBox(8, payBtn, chatBtn);
            {
                payBtn.getStyleClass().add("primary-button");
                payBtn.setStyle("-fx-font-size: 11px; -fx-padding: 3 8 3 8;");
                payBtn.setOnAction(e -> {
                    Appointment apt = getTableView().getItems().get(getIndex());
                    handlePayment(apt);
                });
                
                chatBtn.getStyleClass().add("secondary-button");
                chatBtn.setStyle("-fx-font-size: 11px; -fx-padding: 3 8 3 8;");
                chatBtn.setOnAction(e -> {
                    Appointment apt = getTableView().getItems().get(getIndex());
                    String techName = "Technician (" + apt.getTechnicianId() + ")";
                    ChatView cv = new ChatView(stage, customer, apt.getId(), techName, stage.getScene());
                    stage.setScene(cv.createScene());
                });
                btnBox.setAlignment(Pos.CENTER_LEFT);
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    Appointment apt = getTableView().getItems().get(getIndex());
                    payBtn.setVisible(Appointment.STATUS_COMPLETED.equals(apt.getStatus()) && !apt.isPaid());
                    payBtn.setManaged(payBtn.isVisible());
                    if (payBtn.isVisible()) {
                        payBtn.setText("Pay RM " + String.format("%.2f", apt.getPrice() - 50.0));
                    }
                    
                    boolean hasTech = !apt.getTechnicianId().equals("N/A");
                    chatBtn.setVisible(hasTech);
                    chatBtn.setManaged(chatBtn.isVisible());

                    boolean hasNote = apt.getServiceReport() != null && !apt.getServiceReport().equals("N/A");
                    Button noteBtn = new Button("View Note");
                    noteBtn.getStyleClass().add("secondary-button");
                    noteBtn.setStyle("-fx-font-size: 11px; -fx-padding: 3 8 3 8;");
                    noteBtn.setOnAction(e -> {
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Technician's Service Note");
                        alert.setHeaderText("Note for Appointment: " + apt.getId());
                        alert.setContentText(apt.getServiceReport());
                        alert.showAndWait();
                    });

                    btnBox.getChildren().clear();
                    btnBox.getChildren().addAll(payBtn, chatBtn);
                    if (hasNote) btnBox.getChildren().add(noteBtn);
                    
                    setGraphic(btnBox);
                }
            }
        });

        aptTable.getColumns().addAll(
            aptIdCol, aptDateCol, aptTimeCol, aptTypeCol,
            aptStatCol, aptPriceCol, aptPaidCol, aptActionCol);

        List<Appointment> myAppointments =
            FileHandler.loadAppointmentsByCustomer(customer.getId());
        aptTable.setItems(FXCollections.observableArrayList(myAppointments));

        // Summary label
        long completedCount = myAppointments.stream()
            .filter(a -> Appointment.STATUS_COMPLETED.equals(a.getStatus()))
            .count();
        Label aptSummary = new Label(
            "Total: " + myAppointments.size() +
            " appointment(s)  |  Completed: " + completedCount);
        aptSummary.setStyle("-fx-text-fill: #b3c5d5; -fx-font-size: 12px;");

        VBox serviceContent = new VBox(10, aptTable, aptSummary);
        serviceContent.setPadding(new Insets(12));
        serviceTab.setContent(serviceContent);

        Tab paymentTab = new Tab("Payment History");

        TableView<Receipt> rcptTable = new TableView<>();
        rcptTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        rcptTable.setPlaceholder(new Label("No payment records found."));

        TableColumn<Receipt, String> rcptIdCol   = new TableColumn<>("Receipt ID");
        rcptIdCol.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<Receipt, String> rcptAptCol  = new TableColumn<>("Appointment");
        rcptAptCol.setCellValueFactory(new PropertyValueFactory<>("appointmentId"));

        TableColumn<Receipt, String> rcptDateCol = new TableColumn<>("Payment Date");
        rcptDateCol.setCellValueFactory(new PropertyValueFactory<>("paymentDate"));

        TableColumn<Receipt, String> rcptNameCol = new TableColumn<>("Service");
        rcptNameCol.setCellValueFactory(new PropertyValueFactory<>("serviceName"));

        TableColumn<Receipt, String> rcptTypeCol = new TableColumn<>("Type");
        rcptTypeCol.setCellValueFactory(new PropertyValueFactory<>("serviceType"));

        TableColumn<Receipt, Double> rcptAmtCol  = new TableColumn<>("Amount Paid (RM)");
        rcptAmtCol.setCellValueFactory(new PropertyValueFactory<>("amountPaid"));

        rcptTable.getColumns().addAll(
            rcptIdCol, rcptAptCol, rcptDateCol,
            rcptNameCol, rcptTypeCol, rcptAmtCol);

        List<Receipt> myReceipts =
            FileHandler.loadReceiptsByCustomer(customer.getId());
        rcptTable.setItems(FXCollections.observableArrayList(myReceipts));

        // Total amount summary
        double totalPaid = myReceipts.stream()
            .mapToDouble(Receipt::getAmountPaid)
            .sum();
        Label rcptSummary = new Label(
            "Total receipts: " + myReceipts.size() +
            "  |  Total paid: RM " + String.format("%.2f", totalPaid));
        rcptSummary.setStyle("-fx-text-fill: #b3c5d5; -fx-font-size: 12px;");

        VBox paymentContent = new VBox(10, rcptTable, rcptSummary);
        paymentContent.setPadding(new Insets(12));
        paymentTab.setContent(paymentContent);

        tabPane.getTabs().addAll(serviceTab, paymentTab);

        Button backBtn = new Button("Back to Dashboard");
        backBtn.getStyleClass().add("secondary-button");
        backBtn.setOnAction(e -> {
            CustomerDashboardView dashboard = new CustomerDashboardView(stage, customer);
            stage.setScene(dashboard.createScene());
        });

        HBox btnBox = new HBox(backBtn);
        btnBox.setAlignment(Pos.CENTER);

        root.getChildren().addAll(headerBox, tabPane, btnBox);

        Scene scene = new Scene(root, 900, 580);
        scene.getStylesheets().add(getClass().getResource("styles.css").toExternalForm());

        root.setOpacity(0);
        FadeTransition ft = new FadeTransition(Duration.millis(700), root);
        ft.setFromValue(0); ft.setToValue(1); ft.play();

        return scene;
    }

    private void handlePayment(Appointment apt) {
        double due = apt.getPrice() - 50.0;
        if (customer.getBalance() < due) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Insufficient Funds");
            alert.setHeaderText(null);
            alert.setContentText("Your balance is RM " + String.format("%.2f", customer.getBalance()) + 
                                ". You need RM " + String.format("%.2f", due) + " to complete payment.");
            alert.showAndWait();
            return;
        }

        apt.setPaid(true);
        customer.setBalance(customer.getBalance() - due);

        // Save everything
        AppointmentService.updateAppointment(apt);
        List<User> users = FileHandler.loadAllUsers();
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getId().equals(customer.getId())) {
                users.set(i, customer);
                break;
            }
        }
        FileHandler.saveAllUsers(users);

        // Generate Receipt
        // Constructor: id, appointmentId, customerId, serviceType, serviceName, amountPaid, paymentDate, counterStaffId
        Receipt r = new Receipt(
            "R-" + java.util.UUID.randomUUID().toString().substring(0, 5).toUpperCase(),
            apt.getId(), customer.getId(), apt.getServiceType(), 
            apt.getServiceType() + " Service", apt.getPrice(), 
            java.time.LocalDate.now().toString(), "Wallet"
        );
        FileHandler.saveReceipt(r);

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Payment Success");
        alert.setHeaderText("Thank you for your payment!");
        alert.setContentText("RM " + String.format("%.2f", due) + " deducted from your wallet. Your new balance: RM " + 
                            String.format("%.2f", customer.getBalance()));
        alert.show();

        // Refresh view
        stage.setScene(createScene());
    }
}
