package ui;

import models.Appointment;
import models.Feedback;
import models.User;
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
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;

public class CustomerFeedbackView {

    private final Stage stage;
    private final User  customer;

    public CustomerFeedbackView(Stage stage, User customer) {
        this.stage    = stage;
        this.customer = customer;
    }

    @SuppressWarnings("unchecked")
    public Scene createScene() {
        VBox root = new VBox(20);
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.TOP_CENTER);


        Label titleLabel = new Label("Technician Feedback on My Appointments");
        titleLabel.getStyleClass().add("title-text");

        Label subLabel = new Label(
            "Feedback written by technicians for your completed services");
        subLabel.getStyleClass().add("subtitle-text");

        VBox headerBox = new VBox(5, titleLabel, subLabel);
        headerBox.setAlignment(Pos.CENTER);
        headerBox.setPadding(new Insets(0, 0, 10, 0));

        TableView<Feedback> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setPlaceholder(new Label("No feedback available for your appointments yet."));

        TableColumn<Feedback, String> idCol = new TableColumn<>("Feedback ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        idCol.setPrefWidth(120);

        TableColumn<Feedback, String> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(new PropertyValueFactory<>("date"));
        dateCol.setPrefWidth(100);

        TableColumn<Feedback, Integer> ratingCol = new TableColumn<>("Rating");
        ratingCol.setCellValueFactory(new PropertyValueFactory<>("rating"));
        ratingCol.setPrefWidth(70);
        ratingCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Integer rating, boolean empty) {
                super.updateItem(rating, empty);
                if (empty || rating == null) {
                    setText(null);
                } else {
                    setText("★".repeat(rating) + "☆".repeat(5 - rating));
                }
            }
        });

        TableColumn<Feedback, String> commentCol = new TableColumn<>("Technician's Comment");
        commentCol.setCellValueFactory(new PropertyValueFactory<>("comment"));
        commentCol.setCellFactory(col -> {
            TableCell<Feedback, String> cell = new TableCell<>();
            Label label = new Label();
            label.setWrapText(true);
            cell.setGraphic(label);
            cell.prefHeightProperty().bind(label.heightProperty());
            cell.itemProperty().addListener((obs, oldVal, newVal) -> label.setText(newVal));
            return cell;
        });

        table.getColumns().addAll(idCol, dateCol, ratingCol, commentCol);

        List<Appointment> myAppointments =
            FileHandler.loadAppointmentsByCustomer(customer.getId());

        List<Feedback> allFeedback = FileHandler.loadAllFeedback();
        List<Feedback> myFeedback  = new ArrayList<>();
        for (Feedback f : allFeedback) {
            if (f.getCustomerId().equals(customer.getId()) && !f.isHidden()) {
                myFeedback.add(f);
            }
        }

        table.setItems(FXCollections.observableArrayList(myFeedback));


        double avgRating = myFeedback.stream()
            .mapToInt(Feedback::getRating)
            .average()
            .orElse(0.0);

        Label summaryLabel = new Label(
            "Total feedback: " + myFeedback.size() +
            "  |  Average rating: " + String.format("%.1f / 5.0", avgRating));
        summaryLabel.setStyle("-fx-text-fill: #b3c5d5; -fx-font-size: 12px;");

        TextArea detailArea = new TextArea();
        detailArea.setEditable(false);
        detailArea.setWrapText(true);
        detailArea.setPrefRowCount(4);
        detailArea.setPromptText("Select a row to see the full comment here.");
        detailArea.setStyle("-fx-control-inner-background: rgba(255,255,255,0.05); " +
                            "-fx-text-fill: #e0e0e0;");

        table.getSelectionModel().selectedItemProperty().addListener((obs, old, sel) -> {
            if (sel != null) {
                detailArea.setText(
                    "Date:    " + sel.getDate()    + "\n" +
                    "Rating:  " + sel.getRating() + " / 5\n\n" +
                    sel.getComment()
                );
            }
        });

        Button backBtn = new Button("Back to Dashboard");
        backBtn.getStyleClass().add("secondary-button");
        backBtn.setOnAction(e -> {
            CustomerDashboardView dashboard = new CustomerDashboardView(stage, customer);
            stage.setScene(dashboard.createScene());
        });

        HBox btnBox = new HBox(backBtn);
        btnBox.setAlignment(Pos.CENTER);

        root.getChildren().addAll(
            headerBox, table, summaryLabel,
            new Label("Full Comment:"), detailArea,
            btnBox);

        Scene scene = new Scene(root, 900, 620);
        scene.getStylesheets().add(getClass().getResource("styles.css").toExternalForm());

        root.setOpacity(0);
        FadeTransition ft = new FadeTransition(Duration.millis(700), root);
        ft.setFromValue(0); ft.setToValue(1); ft.play();

        return scene;
    }
}
