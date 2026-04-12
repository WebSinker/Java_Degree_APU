package ui;

import java.util.List;

import javafx.animation.FadeTransition;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import models.Appointment;
import models.Technician;
import models.User;
import services.AppointmentService;
import utils.FileHandler;

public class TechnicianDashboardView {
    private Stage stage;
    private User tech;

    public TechnicianDashboardView(Stage stage, User tech) {
        this.stage = stage;
        this.tech  = tech;
    }

    public Scene createScene() {
        VBox root = new VBox(20);
        root.setAlignment(Pos.TOP_CENTER);
        root.setPadding(new Insets(30));

        // ── Header ────────────────────────────────────────────
        Label titleLabel = new Label("Technician Dashboard");
        titleLabel.getStyleClass().add("title-text");

        String shift = (tech instanceof Technician)
                ? ((Technician) tech).getShift() : "N/A";
        Label welcomeLabel = new Label(
                "Welcome, " + tech.getName() + " | Current Shift: " + shift);
        welcomeLabel.getStyleClass().add("subtitle-text");

        // ── Appointments Table ────────────────────────────────
        TableView<Appointment> table = new TableView<>();

        TableColumn<Appointment, String> idCol = new TableColumn<>("App ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<Appointment, String> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(new PropertyValueFactory<>("date"));

        TableColumn<Appointment, String> timeCol = new TableColumn<>("Time");
        timeCol.setCellValueFactory(new PropertyValueFactory<>("timeSlot"));

        TableColumn<Appointment, String> typeCol = new TableColumn<>("Service Type");
        typeCol.setCellValueFactory(new PropertyValueFactory<>("serviceType"));

        TableColumn<Appointment, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));

        table.getColumns().addAll(idCol, dateCol, timeCol, typeCol, statusCol);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        VBox.setVgrow(table, Priority.ALWAYS);

        List<Appointment> myApps =
                FileHandler.loadAppointmentsByTechnician(tech.getId());
        table.setItems(FXCollections.observableArrayList(myApps));

        // ── [ADDED] Appointment detail panel ─────────────────
        // Assignment: "Check comments and details of individual assigned appointments"
        Label detailHeader = new Label("Appointment Details:");
        detailHeader.setStyle("-fx-font-weight: bold; -fx-font-size: 13px;");

        TextArea detailArea = new TextArea(
                "Select a row above to view its full details.");
        detailArea.setEditable(false);
        detailArea.setWrapText(true);
        detailArea.setPrefHeight(110);

        // Populate detail panel whenever a row is clicked
        table.getSelectionModel().selectedItemProperty()
                .addListener((obs, old, sel) -> {
                    if (sel != null) {
                        detailArea.setText(
                            "Appointment ID  : " + sel.getId()             + "\n" +
                            "Customer ID     : " + sel.getCustomerId()      + "\n" +
                            "Service Type    : " + sel.getServiceType()     + "\n" +
                            "Date            : " + sel.getDate()            + "\n" +
                            "Time Slot       : " + sel.getTimeSlot()        + "\n" +
                            "Status          : " + sel.getStatus()          + "\n" +
                            "Price           : RM " + String.format("%.2f", sel.getPrice()) + "\n" +
                            "Paid            : " + (sel.isPaid() ? "Yes" : "No") + "\n" +
                            "Service Note    : " + sel.getServiceReport()
                        );
                    }
                });

        // ── Buttons ───────────────────────────────────────────
        Button completeBtn = new Button("Mark as Completed");
        completeBtn.getStyleClass().add("primary-button");
        completeBtn.setDisable(true);

        Button chatBtn = new Button("Chat with Customer");
        chatBtn.getStyleClass().add("primary-button");
        chatBtn.setDisable(true);

        Button noteBtn = new Button("Update Service Note");
        noteBtn.getStyleClass().add("primary-button");
        noteBtn.setDisable(true);

        // [ADDED] Edit Profile button
        // Assignment: "Edit personal / individual profile"
        Button editProfileBtn = new Button("Edit Profile");
        editProfileBtn.getStyleClass().add("secondary-button");

        Button logoutBtn = new Button("Logout");
        logoutBtn.getStyleClass().add("secondary-button");

        HBox actions = new HBox(15,
                completeBtn, chatBtn, noteBtn, editProfileBtn, logoutBtn);
        actions.setAlignment(Pos.CENTER);

        root.getChildren().addAll(
                titleLabel, welcomeLabel,
                new Label("Your Assigned Tasks:"),
                table,
                detailHeader, detailArea,
                actions
        );

        // ── Enable/disable buttons based on row selection ─────
        table.getSelectionModel().selectedItemProperty()
                .addListener((obs, old, val) -> {
                    boolean has = (val != null);
                    completeBtn.setDisable(!has
                            || val.getStatus().equals(Appointment.STATUS_COMPLETED));
                    chatBtn.setDisable(!has);
                    noteBtn.setDisable(!has);
                });

        // ── Mark as Completed ─────────────────────────────────
        completeBtn.setOnAction(e -> {
            Appointment sel = table.getSelectionModel().getSelectedItem();
            if (sel != null) {
                sel.setStatus(Appointment.STATUS_COMPLETED);
                AppointmentService.updateAppointment(sel);
                table.setItems(FXCollections.observableArrayList(
                        FileHandler.loadAppointmentsByTechnician(tech.getId())));
                detailArea.setText("Appointment " + sel.getId()
                        + " has been marked as Completed.");
            }
        });

        // ── Chat with Customer ────────────────────────────────
        chatBtn.setOnAction(e -> {
            Appointment sel = table.getSelectionModel().getSelectedItem();
            if (sel != null) {
                String customerName = "Customer (" + sel.getCustomerId() + ")";
                ChatView cv = new ChatView(stage, tech, sel.getId(),
                        customerName, stage.getScene());
                stage.setScene(cv.createScene());
            }
        });

        // ── Update Service Note ───────────────────────────────
        noteBtn.setOnAction(e -> {
            Appointment sel = table.getSelectionModel().getSelectedItem();
            if (sel != null) {
                TextInputDialog dialog = new TextInputDialog(sel.getServiceReport());
                dialog.setTitle("Service Note");
                dialog.setHeaderText(
                        "Add a comment for the customer regarding this service:");
                dialog.setContentText("Note:");
                dialog.showAndWait().ifPresent(note -> {
                    sel.setServiceReport(note);
                    AppointmentService.updateAppointment(sel);
                    table.refresh();
                    detailArea.setText(detailArea.getText().replaceAll("Service Note    : .*", "Service Note    : " + note));
                });
            }
        });

        // ── [ADDED] Edit Profile ──────────────────────────────
        // Assignment: "Edit personal / individual profile"
        editProfileBtn.setOnAction(e -> {
            Dialog<Boolean> dialog = new Dialog<>();
            dialog.setTitle("Edit My Profile");
            dialog.setHeaderText("Update your personal information");

            ButtonType saveType =
                    new ButtonType("Save Changes", ButtonBar.ButtonData.OK_DONE);
            dialog.getDialogPane().getButtonTypes().addAll(
                    saveType, ButtonType.CANCEL);

            GridPane grid = new GridPane();
            grid.setHgap(10);
            grid.setVgap(12);
            grid.setPadding(new Insets(20));

            // Pre-fill with current technician data
            TextField     nameField    = new TextField(tech.getName());
            TextField     contactField = new TextField(tech.getContactNumber());
            PasswordField passField    = new PasswordField();
            PasswordField confirmField = new PasswordField();

            passField.setPromptText("Leave blank to keep current password");
            confirmField.setPromptText("Confirm new password");

            Label idLabel = new Label("User ID: " + tech.getId());
            idLabel.setStyle("-fx-text-fill: grey; -fx-font-size: 11px;");

            grid.add(new Label("Full Name:"),        0, 0); grid.add(nameField,    1, 0);
            grid.add(new Label("Contact Number:"),   0, 1); grid.add(contactField, 1, 1);
            grid.add(new Label("New Password:"),     0, 2); grid.add(passField,    1, 2);
            grid.add(new Label("Confirm Password:"), 0, 3); grid.add(confirmField, 1, 3);
            grid.add(idLabel,                        1, 4);

            dialog.getDialogPane().setContent(grid);

            dialog.setResultConverter(btn -> {
                if (btn != saveType) return false;

                String name    = nameField.getText().trim();
                String contact = contactField.getText().trim();
                String pass    = passField.getText();
                String confirm = confirmField.getText();

                // Input validation
                if (name.isEmpty()) {
                    showAlert(Alert.AlertType.ERROR, "Name cannot be empty.");
                    return false;
                }
                if (!contact.isEmpty() &&
                        !contact.matches("^[0-9\\-+]{8,15}$")) {
                    showAlert(Alert.AlertType.ERROR,
                            "Contact number must be 8-15 digits (digits, +, - only).");
                    return false;
                }
                if (!pass.isEmpty()) {
                    if (pass.length() < 6) {
                        showAlert(Alert.AlertType.ERROR,
                                "Password must be at least 6 characters.");
                        return false;
                    }
                    if (!pass.equals(confirm)) {
                        showAlert(Alert.AlertType.ERROR,
                                "Passwords do not match.");
                        return false;
                    }
                    tech.setPassword(pass);
                }

                // Apply changes to the technician object in memory
                tech.setName(name);
                if (!contact.isEmpty()) tech.setContactNumber(contact);

                // Persist to users.csv — load all, replace this one, save all
                List<models.User> allUsers = FileHandler.loadAllUsers();
                for (int i = 0; i < allUsers.size(); i++) {
                    if (allUsers.get(i).getId().equals(tech.getId())) {
                        allUsers.set(i, tech);
                        break;
                    }
                }
                FileHandler.saveAllUsers(allUsers);

                // Refresh the welcome label to show the updated name
                String updatedShift = (tech instanceof Technician)
                        ? ((Technician) tech).getShift() : "N/A";
                welcomeLabel.setText("Welcome, " + tech.getName()
                        + " | Current Shift: " + updatedShift);

                return true;
            });

            dialog.showAndWait().ifPresent(saved -> {
                if (saved) showAlert(Alert.AlertType.INFORMATION,
                        "Profile updated successfully!");
            });
        });

        // ── Logout ────────────────────────────────────────────
        logoutBtn.setOnAction(e -> {
            LoginView loginView = new LoginView(stage);
            stage.setScene(loginView.createScene());
        });

        // ── Scene ─────────────────────────────────────────────
        Scene scene = new Scene(root, 1000, 780);
        scene.getStylesheets().add(
                getClass().getResource("styles.css").toExternalForm());

        root.setOpacity(0);
        FadeTransition ft = new FadeTransition(Duration.millis(800), root);
        ft.setFromValue(0);
        ft.setToValue(1);
        ft.play();

        return scene;
    }

    // ── Helper: popup dialog ──────────────────────────────────
    private void showAlert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type, message, ButtonType.OK);
        alert.setHeaderText(null);
        alert.showAndWait();
    }
}