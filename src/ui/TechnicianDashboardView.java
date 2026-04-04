package ui;

import models.Technician;
import models.User;
import models.Appointment;
import services.AppointmentService;
import utils.FileHandler;
import javafx.animation.FadeTransition;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.List;

public class TechnicianDashboardView {
    private Stage stage;
    private User tech;

    public TechnicianDashboardView(Stage stage, User tech) {
        this.stage = stage;
        this.tech = tech;
    }

    public Scene createScene() {
        VBox root = new VBox(20);
        root.setAlignment(Pos.TOP_CENTER);
        root.setPadding(new Insets(30));

        Label titleLabel = new Label("Technician Dashboard");
        titleLabel.getStyleClass().add("title-text");
        
        String shift = (tech instanceof Technician) ? ((Technician)tech).getShift() : "N/A";
        Label welcomeLabel = new Label("Welcome, " + tech.getName() + " | Current Shift: " + shift);
        welcomeLabel.getStyleClass().add("subtitle-text");

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

        List<Appointment> myApps = FileHandler.loadAppointmentsByTechnician(tech.getId());
        table.setItems(FXCollections.observableArrayList(myApps));

        Button completeBtn = new Button("Mark as Completed");
        completeBtn.getStyleClass().add("primary-button");
        completeBtn.setDisable(true);

        Button chatBtn = new Button("Chat with Customer");
        chatBtn.getStyleClass().add("primary-button");
        chatBtn.setDisable(true);

        Button noteBtn = new Button("Update Service Note");
        noteBtn.getStyleClass().add("primary-button");
        noteBtn.setDisable(true);

        Button logoutBtn = new Button("Logout");
        logoutBtn.getStyleClass().add("secondary-button");

        HBox actions = new HBox(15, completeBtn, chatBtn, noteBtn, logoutBtn);
        actions.setAlignment(Pos.CENTER);

        root.getChildren().addAll(titleLabel, welcomeLabel, new Label("Your Assigned Tasks:"), table, actions);

        // Actions
        completeBtn.setOnAction(e -> {
            Appointment selected = table.getSelectionModel().getSelectedItem();
            if (selected != null) {
                selected.setStatus(Appointment.STATUS_COMPLETED);
                AppointmentService.updateAppointment(selected);
                table.setItems(FXCollections.observableArrayList(FileHandler.loadAppointmentsByTechnician(tech.getId())));
            }
        });

        chatBtn.setOnAction(e -> {
            Appointment selected = table.getSelectionModel().getSelectedItem();
            if (selected != null) {
                String customerName = "Customer (" + selected.getCustomerId() + ")"; 
                ChatView cv = new ChatView(stage, tech, selected.getId(), customerName, stage.getScene());
                stage.setScene(cv.createScene());
            }
        });

        noteBtn.setOnAction(e -> {
            Appointment selected = table.getSelectionModel().getSelectedItem();
            if (selected != null) {
                TextInputDialog dialog = new TextInputDialog(selected.getServiceReport());
                dialog.setTitle("Service Note");
                dialog.setHeaderText("Add a comment for the customer regarding this service:");
                dialog.setContentText("Note:");
                dialog.showAndWait().ifPresent(note -> {
                    selected.setServiceReport(note);
                    AppointmentService.updateAppointment(selected);
                    table.refresh();
                });
            }
        });

        table.getSelectionModel().selectedItemProperty().addListener((obs, old, val) -> {
            boolean selected = (val != null);
            completeBtn.setDisable(!selected || val.getStatus().equals(Appointment.STATUS_COMPLETED));
            chatBtn.setDisable(!selected);
            noteBtn.setDisable(!selected);
        });
        
        logoutBtn.setOnAction(e -> {
            LoginView loginView = new LoginView(stage);
            stage.setScene(loginView.createScene());
        });

        Scene scene = new Scene(root, 900, 700);
        scene.getStylesheets().add(getClass().getResource("styles.css").toExternalForm());

        root.setOpacity(0);
        FadeTransition ft = new FadeTransition(Duration.millis(800), root);
        ft.setFromValue(0);
        ft.setToValue(1);
        ft.play();

        return scene;
    }
}
