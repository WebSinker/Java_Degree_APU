package ui;

import models.User;
import models.CounterStaff;
import models.Technician;
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
import java.util.UUID;
import java.util.stream.Collectors;

public class StaffManagementView {
    private Stage stage;
    private User manager;
    private TableView<User> table;
    private ObservableList<User> staffList;

    public StaffManagementView(Stage stage, User manager) {
        this.stage = stage;
        this.manager = manager;
    }

    public Scene createScene() {
        VBox root = new VBox(20);
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.TOP_CENTER);

        Label title = new Label("Staff Management");
        title.getStyleClass().add("title-text");

        table = new TableView<>();
        
        TableColumn<User, String> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        
        TableColumn<User, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        
        TableColumn<User, String> roleCol = new TableColumn<>("Role");
        roleCol.setCellValueFactory(new PropertyValueFactory<>("role"));
        
        TableColumn<User, String> userCol = new TableColumn<>("Username");
        userCol.setCellValueFactory(new PropertyValueFactory<>("username"));

        table.getColumns().addAll(idCol, nameCol, roleCol, userCol);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        refreshTable();

        // Form for adding new staff
        HBox form = new HBox(10);
        form.setAlignment(Pos.CENTER);
        TextField nameField = new TextField(); nameField.setPromptText("Name");
        TextField userField = new TextField(); userField.setPromptText("Username");
        PasswordField passField = new PasswordField(); passField.setPromptText("Password");
        ComboBox<String> roleCombo = new ComboBox<>(FXCollections.observableArrayList("CounterStaff", "Technician"));
        roleCombo.setPromptText("Role");
        
        ComboBox<String> shiftCombo = new ComboBox<>(FXCollections.observableArrayList("Morning", "Night"));
        shiftCombo.setPromptText("Shift (Tech only)");
        shiftCombo.setVisible(false); // Only show for Tech
        
        roleCombo.setOnAction(e -> shiftCombo.setVisible("Technician".equals(roleCombo.getValue())));

        Button addBtn = new Button("Add Staff");
        addBtn.getStyleClass().add("primary-button");
        
        form.getChildren().addAll(nameField, userField, passField, roleCombo, shiftCombo, addBtn);

        Button deleteBtn = new Button("Delete Selected");
        deleteBtn.setStyle("-fx-background-color: #ff4d4d; -fx-text-fill: white;");
        
        Button backBtn = new Button("Back to Dashboard");
        backBtn.getStyleClass().add("secondary-button");

        root.getChildren().addAll(title, table, form, deleteBtn, backBtn);

        // Actions
        addBtn.setOnAction(e -> {
            String name = nameField.getText();
            String user = userField.getText();
            String pass = passField.getText();
            String role = roleCombo.getValue();
            
            String shift = ("Technician".equals(role)) ? shiftCombo.getValue() : "N/A";
            if (role == null || (role.equals("Technician") && shift == null)) return;

            String id = (role.equals("Technician") ? "T-" : "S-") + UUID.randomUUID().toString().substring(0, 5).toUpperCase();
            User newUser = role.equals("Technician") ? 
                        new Technician(id, user, pass, name, "N/A", shift, manager.getId()) : 
                        new CounterStaff(id, user, pass, name, "N/A", manager.getId());
            
            if (FileHandler.saveUser(newUser)) {
                refreshTable();
                nameField.clear(); userField.clear(); passField.clear();
                roleCombo.setValue(null); shiftCombo.setValue(null);
            }
        });

        deleteBtn.setOnAction(e -> {
            User selected = table.getSelectionModel().getSelectedItem();
            if (selected != null) {
                List<User> all = FileHandler.loadAllUsers();
                all.removeIf(u -> u.getId().equals(selected.getId()));
                FileHandler.saveAllUsers(all);
                refreshTable();
            }
        });

        backBtn.setOnAction(e -> {
            ManagerDashboardView dashboard = new ManagerDashboardView(stage, manager);
            stage.setScene(dashboard.createScene());
        });

        Scene scene = new Scene(root, 1000, 700);
        scene.getStylesheets().add(getClass().getResource("styles.css").toExternalForm());
        return scene;
    }

    private void refreshTable() {
        List<User> all = FileHandler.loadAllUsers();
        staffList = FXCollections.observableArrayList(
            all.stream()
               .filter(u -> (u instanceof CounterStaff || u instanceof Technician) && 
                            u.getCreatorId() != null && u.getCreatorId().equals(manager.getId()))
               .collect(Collectors.toList())
        );
        table.setItems(staffList);
    }
}
