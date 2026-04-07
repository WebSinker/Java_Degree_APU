package ui;

import models.Appointment;
import models.Technician;
import models.User;
import services.AppointmentService;
import utils.FileHandler;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.List;
import java.util.stream.Collectors;

public class AssignAppointmentView {
    private Stage stage;
    private User staff;
    private TableView<Appointment> appTable;
    private TableView<Technician> techTable;
    private ObservableList<Appointment> appList;
    private ObservableList<Technician> techList;

    public AssignAppointmentView(Stage stage, User staff) {
        this.stage = stage;
        this.staff = staff;
    }

    public Scene createScene() {
        VBox root = new VBox(20);
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.TOP_CENTER);

        Label title = new Label("Appointment Assignment & Shift Management");
        title.getStyleClass().add("title-text");

        HBox tables = new HBox(20);
        tables.setAlignment(Pos.CENTER);

        // Appointments Table
        VBox appBox = new VBox(10);
        appBox.setAlignment(Pos.CENTER);
        appTable = new TableView<>();
        
        TableColumn<Appointment, String> appIdCol = new TableColumn<>("App ID");
        appIdCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        TableColumn<Appointment, String> appDateCol = new TableColumn<>("Date");
        appDateCol.setCellValueFactory(new PropertyValueFactory<>("date"));
        TableColumn<Appointment, String> appTimeCol = new TableColumn<>("Time");
        appTimeCol.setCellValueFactory(new PropertyValueFactory<>("timeSlot"));
        TableColumn<Appointment, String> appTypeCol = new TableColumn<>("Type");
        appTypeCol.setCellValueFactory(new PropertyValueFactory<>("serviceType"));
        
        appTable.getColumns().addAll(appIdCol, appDateCol, appTimeCol, appTypeCol);
        appTable.setPrefWidth(400);

        appBox.getChildren().addAll(new Label("Appointments (Unassigned)"), appTable);

        // Technicians Table
        VBox techBox = new VBox(10);
        techBox.setAlignment(Pos.CENTER);
        techTable = new TableView<>();
        
        TableColumn<Technician, String> techIdCol = new TableColumn<>("Tech ID");
        techIdCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        TableColumn<Technician, String> techNameCol = new TableColumn<>("Name");
        techNameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        
        // Custom cell for shift management
        TableColumn<Technician, String> shiftCol = new TableColumn<>("Shift");
        shiftCol.setCellValueFactory(new PropertyValueFactory<>("shift"));

        techTable.getColumns().addAll(techIdCol, techNameCol, shiftCol);
        techTable.setPrefWidth(400);

        techBox.getChildren().addAll(new Label("Technicians & Shifts"), techTable);

        tables.getChildren().addAll(appBox, techBox);

        HBox controls = new HBox(15);
        controls.setAlignment(Pos.CENTER);
        Button assignBtn = new Button("Assign Selected Technician");
        assignBtn.getStyleClass().add("primary-button");
        
        ComboBox<String> shiftCombo = new ComboBox<>(FXCollections.observableArrayList("Morning", "Night"));
        shiftCombo.setPromptText("Change Shift");
        Button updateShiftBtn = new Button("Update Shift");
        updateShiftBtn.getStyleClass().add("secondary-button");

        controls.getChildren().addAll(assignBtn, new Label(" | "), shiftCombo, updateShiftBtn);

        Button backBtn = new Button("Back to Dashboard");
        backBtn.getStyleClass().add("secondary-button");

        root.getChildren().addAll(title, tables, controls, backBtn);

        refreshTables();

        // Actions
        assignBtn.setOnAction(e -> {
            Appointment selectedApp = appTable.getSelectionModel().getSelectedItem();
            Technician selectedTech = techTable.getSelectionModel().getSelectedItem();
            
            if (selectedApp != null && selectedTech != null) {
                if (AppointmentService.assignTechnician(selectedApp.getId(), selectedTech.getId(), staff.getId())) {
                    refreshTables();
                    showInfo("Assignment Successful!");
                } else {
                    showError("Technician is not available for this slot or shift.");
                }
            }
        });

        updateShiftBtn.setOnAction(e -> {
            User selectedTech = techTable.getSelectionModel().getSelectedItem();
            String newShift = shiftCombo.getValue();
            if (selectedTech != null && newShift != null && selectedTech instanceof Technician) {
                ((Technician)selectedTech).setShift(newShift);
                List<User> users = FileHandler.loadAllUsers();
                for (int i = 0; i < users.size(); i++) {
                    if (users.get(i).getId().equals(selectedTech.getId())) {
                        users.set(i, selectedTech);
                        break;
                    }
                }
                FileHandler.saveAllUsers(users);
                refreshTables();
            }
        });

        backBtn.setOnAction(e -> {
            CounterStaffDashboardView dashboard = new CounterStaffDashboardView(stage, staff);
            stage.setScene(dashboard.createScene());
        });

        Scene scene = new Scene(root, 1000, 700);
        scene.getStylesheets().add(getClass().getResource("styles.css").toExternalForm());
        return scene;
    }

    private void refreshTables() {
        appList = FXCollections.observableArrayList(
            FileHandler.loadAllAppointments().stream()
                .filter(a -> a.getTechnicianId().equals("N/A") || a.getTechnicianId().isEmpty())
                .collect(Collectors.toList())
        );
        appTable.setItems(appList);

        techList = FXCollections.observableArrayList(
            FileHandler.loadAllUsers().stream()
                .filter(u -> u instanceof Technician)
                .map(u -> (Technician)u)
                .collect(Collectors.toList())
        );
        techTable.setItems(techList);
    }

    private void showInfo(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    private void showError(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
