package ui;

import models.Customer;
import models.User;
import services.CustomerService;
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

import java.util.UUID;

public class CustomerManagementView {
    private Stage stage;
    private User staff;
    private TableView<Customer> table;
    private ObservableList<Customer> customerList;

    public CustomerManagementView(Stage stage, User staff) {
        this.stage = stage;
        this.staff = staff;
    }

    public Scene createScene() {
        VBox root = new VBox(20);
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.TOP_CENTER);

        Label title = new Label("Customer Management");
        title.getStyleClass().add("title-text");

        table = new TableView<>();
        
        TableColumn<Customer, String> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        
        TableColumn<Customer, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        
        TableColumn<Customer, String> userCol = new TableColumn<>("Username");
        userCol.setCellValueFactory(new PropertyValueFactory<>("username"));

        TableColumn<Customer, String> phoneCol = new TableColumn<>("Phone");
        phoneCol.setCellValueFactory(new PropertyValueFactory<>("contactNumber"));

        table.getColumns().addAll(idCol, nameCol, userCol, phoneCol);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        refreshTable();

        // Form for adding new customer
        VBox form = new VBox(10);
        form.setAlignment(Pos.CENTER);
        form.setPadding(new Insets(10));
        form.getStyleClass().add("form-container");

        HBox inputs = new HBox(10);
        inputs.setAlignment(Pos.CENTER);
        TextField nameField = new TextField(); nameField.setPromptText("Full Name");
        TextField userField = new TextField(); userField.setPromptText("Username");
        PasswordField passField = new PasswordField(); passField.setPromptText("Password");
        TextField phoneField = new TextField(); phoneField.setPromptText("Phone Number");
        
        Button addBtn = new Button("Register New Customer");
        addBtn.getStyleClass().add("primary-button");
        
        inputs.getChildren().addAll(nameField, userField, passField, phoneField);
        form.getChildren().addAll(new Label("Quick Registration"), inputs, addBtn);

        HBox actions = new HBox(20);
        actions.setAlignment(Pos.CENTER);
        Button deleteBtn = new Button("Delete Selected");
        deleteBtn.setStyle("-fx-background-color: #ff4d4d; -fx-text-fill: white;");
        
        Button backBtn = new Button("Back to Dashboard");
        backBtn.getStyleClass().add("secondary-button");
        actions.getChildren().addAll(deleteBtn, backBtn);

        root.getChildren().addAll(title, table, form, actions);

        // Actions
        addBtn.setOnAction(e -> {
            String name = nameField.getText();
            String user = userField.getText();
            String pass = passField.getText();
            String phone = phoneField.getText();
            
            if (name.isEmpty() || user.isEmpty() || pass.isEmpty()) return;

            String id = "C-" + UUID.randomUUID().toString().substring(0, 5).toUpperCase();
            Customer newCustomer = new Customer(id, user, pass, name, phone, staff.getId());
            
            if (CustomerService.saveCustomer(newCustomer)) {
                refreshTable();
                nameField.clear(); userField.clear(); passField.clear(); phoneField.clear();
            }
        });

        deleteBtn.setOnAction(e -> {
            Customer selected = table.getSelectionModel().getSelectedItem();
            if (selected != null) {
                CustomerService.deleteCustomer(selected.getId());
                refreshTable();
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

    private void refreshTable() {
        customerList = FXCollections.observableArrayList(CustomerService.getAllCustomers());
        table.setItems(customerList);
    }
}
