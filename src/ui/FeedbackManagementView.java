package ui;

import models.User;
import models.Feedback;
import utils.FileHandler;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.List;

public class FeedbackManagementView {
    private Stage stage;
    private User manager;
    private TableView<Feedback> table;

    public FeedbackManagementView(Stage stage, User manager) {
        this.stage = stage;
        this.manager = manager;
    }

    public Scene createScene() {
        VBox root = new VBox(20);
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.TOP_CENTER);

        Label title = new Label("Customer Feedback Moderation");
        title.getStyleClass().add("title-text");

        table = new TableView<>();
        
        TableColumn<Feedback, String> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(new PropertyValueFactory<>("date"));
        
        TableColumn<Feedback, Integer> rateCol = new TableColumn<>("Rating");
        rateCol.setCellValueFactory(new PropertyValueFactory<>("rating"));
        
        TableColumn<Feedback, String> commentCol = new TableColumn<>("Comment");
        commentCol.setCellValueFactory(new PropertyValueFactory<>("comment"));
        
        TableColumn<Feedback, Boolean> hiddenCol = new TableColumn<>("Hidden");
        hiddenCol.setCellValueFactory(new PropertyValueFactory<>("hidden"));

        table.getColumns().addAll(dateCol, rateCol, commentCol, hiddenCol);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        refreshTable();

        HBox actions = new HBox(15);
        actions.setAlignment(Pos.CENTER);
        
        Button hideBtn = new Button("Toggle Hide/Show");
        hideBtn.getStyleClass().add("primary-button");
        
        Button deleteBtn = new Button("Delete Feedback");
        deleteBtn.setStyle("-fx-background-color: #ff4d4d; -fx-text-fill: white;");
        
        Button backBtn = new Button("Back to Dashboard");
        backBtn.getStyleClass().add("secondary-button");

        actions.getChildren().addAll(hideBtn, deleteBtn, backBtn);

        root.getChildren().addAll(title, table, actions);

        hideBtn.setOnAction(e -> {
            Feedback selected = table.getSelectionModel().getSelectedItem();
            if (selected != null) {
                List<Feedback> all = FileHandler.loadAllFeedback();
                for (Feedback f : all) {
                    if (f.getId().equals(selected.getId())) {
                        f.setHidden(!f.isHidden());
                    }
                }
                FileHandler.saveAllFeedback(all);
                refreshTable();
            }
        });

        deleteBtn.setOnAction(e -> {
            Feedback selected = table.getSelectionModel().getSelectedItem();
            if (selected != null) {
                List<Feedback> all = FileHandler.loadAllFeedback();
                all.removeIf(f -> f.getId().equals(selected.getId()));
                FileHandler.saveAllFeedback(all);
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
        table.setItems(FXCollections.observableArrayList(FileHandler.loadAllFeedback()));
    }
}
