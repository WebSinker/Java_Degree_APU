package ui;

import models.User;
import models.ServiceItem;
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

public class ServicePricingView {
    private Stage stage;
    private User manager;
    private TableView<ServiceItem> table;

    public ServicePricingView(Stage stage, User manager) {
        this.stage = stage;
        this.manager = manager;
    }

    public Scene createScene() {
        VBox root = new VBox(20);
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.TOP_CENTER);

        Label title = new Label("Service Pricing Management");
        title.getStyleClass().add("title-text");

        table = new TableView<>();
        
        TableColumn<ServiceItem, String> nameCol = new TableColumn<>("Service Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        
        TableColumn<ServiceItem, String> catCol = new TableColumn<>("Category");
        catCol.setCellValueFactory(new PropertyValueFactory<>("category"));
        
        TableColumn<ServiceItem, Double> priceCol = new TableColumn<>("Price (RM)");
        priceCol.setCellValueFactory(new PropertyValueFactory<>("price"));

        table.getColumns().addAll(nameCol, catCol, priceCol);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        refreshTable();

        // Add form
        HBox form = new HBox(10);
        form.setAlignment(Pos.CENTER);
        TextField nameField = new TextField(); nameField.setPromptText("Service Name");
        ComboBox<String> catCombo = new ComboBox<>(FXCollections.observableArrayList("Normal", "Major"));
        catCombo.setPromptText("Category");
        TextField priceField = new TextField(); priceField.setPromptText("Price");
        
        Button addBtn = new Button("Add Service");
        addBtn.getStyleClass().add("primary-button");
        form.getChildren().addAll(nameField, catCombo, priceField, addBtn);

        Button deleteBtn = new Button("Delete Selected");
        deleteBtn.setStyle("-fx-background-color: #ff4d4d; -fx-text-fill: white;");
        
        Button backBtn = new Button("Back to Dashboard");
        backBtn.getStyleClass().add("secondary-button");

        root.getChildren().addAll(title, table, form, deleteBtn, backBtn);

        addBtn.setOnAction(e -> {
            try {
                String name = nameField.getText();
                String cat = catCombo.getValue();
                double price = Double.parseDouble(priceField.getText());
                
                if (name.isEmpty() || cat == null) return;

                ServiceItem item = new ServiceItem(UUID.randomUUID().toString().substring(0, 5), name, cat, price);
                List<ServiceItem> all = FileHandler.loadAllServices();
                all.add(item);
                FileHandler.saveAllServices(all);
                refreshTable();
            } catch (NumberFormatException ex) {}
        });

        deleteBtn.setOnAction(e -> {
            ServiceItem selected = table.getSelectionModel().getSelectedItem();
            if (selected != null) {
                List<ServiceItem> all = FileHandler.loadAllServices();
                all.removeIf(s -> s.getId().equals(selected.getId()));
                FileHandler.saveAllServices(all);
                refreshTable();
            }
        });

        backBtn.setOnAction(e -> {
            ManagerDashboardView dashboard = new ManagerDashboardView(stage, manager);
            stage.setScene(dashboard.createScene());
        });

        Scene scene = new Scene(root, 900, 700);
        scene.getStylesheets().add(getClass().getResource("styles.css").toExternalForm());
        return scene;
    }

    private void refreshTable() {
        table.setItems(FXCollections.observableArrayList(FileHandler.loadAllServices()));
    }
}
