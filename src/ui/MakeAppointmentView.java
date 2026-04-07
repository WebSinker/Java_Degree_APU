package ui;

import models.Appointment;
import models.User;
import services.AppointmentService;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class MakeAppointmentView {
    private Stage stage;
    private User customer;
    private DatePicker datePicker;
    private ComboBox<String> serviceTypeCombo;
    private GridPane timeSlotGrid;
    private String selectedTime = null;

    public MakeAppointmentView(Stage stage, User customer) {
        this.stage = stage;
        this.customer = customer;
    }

    public Scene createScene() {
        VBox root = new VBox(20);
        root.setPadding(new Insets(30));
        root.setAlignment(Pos.TOP_CENTER);

        Label title = new Label("Book a Service Appointment");
        title.getStyleClass().add("title-text");

        VBox form = new VBox(15);
        form.setAlignment(Pos.CENTER);
        form.setMaxWidth(500);
        form.getStyleClass().add("form-container");

        datePicker = new DatePicker(LocalDate.now().plusDays(1));
        datePicker.setPromptText("Select Date");
        
        serviceTypeCombo = new ComboBox<>();
        serviceTypeCombo.getItems().addAll("Normal Service (1h)", "Major Service (3h)");
        serviceTypeCombo.setPromptText("Select Service Type");
        
        Label instruction = new Label("Select a time slot (08:00 - 22:00):");
        
        timeSlotGrid = new GridPane();
        timeSlotGrid.setHgap(10);
        timeSlotGrid.setVgap(10);
        timeSlotGrid.setAlignment(Pos.CENTER);
        
        refreshTimeSlots();

        serviceTypeCombo.setOnAction(e -> refreshTimeSlots());
        datePicker.setOnAction(e -> refreshTimeSlots());

        Button bookBtn = new Button("Confirm Booking");
        bookBtn.getStyleClass().add("primary-button");
        bookBtn.setDisable(true);

        Button backBtn = new Button("Back to Dashboard");
        backBtn.getStyleClass().add("secondary-button");

        form.getChildren().addAll(new Label("1. Pick Date"), datePicker, 
                                  new Label("2. Pick Service"), serviceTypeCombo, 
                                  instruction, timeSlotGrid, bookBtn);
        
        root.getChildren().addAll(title, form, backBtn);

        // Actions
        bookBtn.setOnAction(e -> {
            if (selectedTime != null && datePicker.getValue() != null && serviceTypeCombo.getValue() != null) {
                // 1. Debt Check
                List<Appointment> myApps = AppointmentService.getAllAppointments().stream()
                        .filter(a -> a.getCustomerId().equals(customer.getId()))
                        .collect(Collectors.toList());
                boolean hasDebt = myApps.stream().anyMatch(a -> a.getStatus().equals(Appointment.STATUS_COMPLETED) && !a.isPaid());
                
                if (hasDebt) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Booking Blocked");
                    alert.setHeaderText("Unpaid Completed Services Found");
                    alert.setContentText("You must pay for your previous completed services before making a new booking.");
                    alert.showAndWait();
                    return;
                }

                // 2. Deposit Check
                double deposit = 50.0;
                if (customer.getBalance() < deposit) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Insufficient Funds");
                    alert.setHeaderText("Low E-Wallet Balance");
                    alert.setContentText("A mandatory deposit of RM 50.00 is required to book. Please top up your wallet.");
                    alert.showAndWait();
                    return;
                }

                String type = serviceTypeCombo.getValue().contains("Major") ? "Major" : "Normal";
                double price = type.equals("Major") ? 300.0 : 100.0; 
                
                String id = "A-" + UUID.randomUUID().toString().substring(0, 5).toUpperCase();
                Appointment app = new Appointment(
                    id, customer.getId(), "N/A", "N/A", type, "S-001",
                    datePicker.getValue().toString(), selectedTime,
                    Appointment.STATUS_PENDING, price, false, "N/A"
                );
                
                // 3. Deduct Deposit
                customer.setBalance(customer.getBalance() - deposit);
                List<User> users = utils.FileHandler.loadAllUsers();
                for (int i = 0; i < users.size(); i++) {
                    if (users.get(i).getId().equals(customer.getId())) {
                        users.set(i, customer);
                        break;
                    }
                }
                utils.FileHandler.saveAllUsers(users);

                if (AppointmentService.createAppointment(app)) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Success");
                    alert.setHeaderText("Appointment Booked");
                    alert.setContentText("Appointment booked successfully! RM 50.00 deposit has been deducted from your wallet.");
                    alert.showAndWait();
                    backBtn.fire();
                }
            }
        });

        backBtn.setOnAction(e -> {
            CustomerDashboardView dashboard = new CustomerDashboardView(stage, customer);
            stage.setScene(dashboard.createScene());
        });

        // Enable book button when slot is selected
        // (Handled in refreshTimeSlots)

        Scene scene = new Scene(root, 1000, 700);
        scene.getStylesheets().add(getClass().getResource("styles.css").toExternalForm());
        return scene;
    }

    private void refreshTimeSlots() {
        timeSlotGrid.getChildren().clear();
        selectedTime = null;
        
        // Final button ref for inner access
        Button bookBtn = null;
        if (timeSlotGrid.getParent() != null) {
            for (javafx.scene.Node node : ((VBox)timeSlotGrid.getParent()).getChildren()) {
                if (node instanceof Button && ((Button)node).getText().equals("Confirm Booking")) {
                    bookBtn = (Button)node;
                    bookBtn.setDisable(true);
                }
            }
        }

        int duration = (serviceTypeCombo.getValue() != null && serviceTypeCombo.getValue().contains("Major")) ? 3 : 1;
        
        for (int hour = 8; hour < 22; hour++) {
            if (hour + duration > 22) break;
            
            String slotStr = String.format("%02d:00", hour);
            Button slotBtn = new Button(slotStr);
            slotBtn.setPrefWidth(80);
            
            final Button finalBookBtn = bookBtn;
            slotBtn.setOnAction(e -> {
                selectedTime = slotStr;
                // Highlight selection
                timeSlotGrid.getChildren().forEach(n -> n.setStyle(""));
                slotBtn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
                if (finalBookBtn != null) finalBookBtn.setDisable(false);
            });
            
            timeSlotGrid.add(slotBtn, (hour - 8) % 4, (hour - 8) / 4);
        }
    }
}
