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
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class CustomerCommentView {

    private final Stage stage;
    private final User  customer;

    public CustomerCommentView(Stage stage, User customer) {
        this.stage    = stage;
        this.customer = customer;
    }

    public Scene createScene() {
        VBox root = new VBox(20);
        root.setAlignment(Pos.TOP_CENTER);
        root.setPadding(new Insets(30));

        Label titleLabel = new Label("Leave a Comment");
        titleLabel.getStyleClass().add("title-text");

        Label subLabel = new Label(
            "Rate and comment on your completed appointments");
        subLabel.getStyleClass().add("subtitle-text");

        VBox headerBox = new VBox(5, titleLabel, subLabel);
        headerBox.setAlignment(Pos.CENTER);
        headerBox.setPadding(new Insets(0, 0, 10, 0));

        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(15);
        grid.setVgap(14);
        grid.getStyleClass().add("form-container");

        Label aptLabel = new Label("Select Appointment");

        List<Appointment> completedApts = FileHandler
            .loadAppointmentsByCustomer(customer.getId())
            .stream()
            .filter(a -> Appointment.STATUS_COMPLETED.equals(a.getStatus()))
            .collect(Collectors.toList());

        List<Feedback> existingFeedback = FileHandler.loadAllFeedback();

        List<Appointment> eligibleApts = completedApts.stream()
            .filter(a -> existingFeedback.stream()
                .noneMatch(f -> f.getCustomerId().equals(customer.getId())
                             && f.getId().startsWith("C-CMT-" + a.getId())))
            .collect(Collectors.toList());

        ComboBox<String> aptCombo = new ComboBox<>();
        if (eligibleApts.isEmpty()) {
            aptCombo.setPromptText("No eligible appointments");
            aptCombo.setDisable(true);
        } else {
            aptCombo.setItems(FXCollections.observableArrayList(
                eligibleApts.stream()
                    .map(a -> a.getId() + "  –  " + a.getDate() + "  [" + a.getServiceType() + "]")
                    .collect(Collectors.toList())
            ));
            aptCombo.setPromptText("Select a completed appointment");
        }
        aptCombo.setMaxWidth(Double.MAX_VALUE);

        Label ratingLabel = new Label("Rating  (1 = poor, 5 = excellent)");

        ToggleGroup starGroup = new ToggleGroup();
        HBox starBox = new HBox(12);
        starBox.setAlignment(Pos.CENTER_LEFT);
        for (int i = 1; i <= 5; i++) {
            RadioButton rb = new RadioButton("★ " + i);
            rb.setToggleGroup(starGroup);
            rb.setUserData(i);
            rb.setStyle("-fx-text-fill: #f9a825; -fx-font-size: 15px;");
            starBox.getChildren().add(rb);
        }

        Label commentLabel = new Label("Your Comment");
        TextArea commentArea = new TextArea();
        commentArea.setPromptText(
            "Describe your experience with the counter staff and technician...");
        commentArea.setWrapText(true);
        commentArea.setPrefRowCount(5);
        commentArea.setMaxWidth(Double.MAX_VALUE);
        commentArea.getStyleClass().add("text-area");

        Label charCount = new Label("0 / 500 characters");
        charCount.setStyle("-fx-text-fill: #b3c5d5; -fx-font-size: 11px;");
        commentArea.textProperty().addListener((obs, old, val) -> {
            if (val.length() > 500) {
                commentArea.setText(val.substring(0, 500));
            }
            charCount.setText(commentArea.getText().length() + " / 500 characters");
        });

        Label msgLabel = new Label();
        msgLabel.setManaged(false);
        GridPane.setColumnSpan(msgLabel, 2);

        grid.add(aptLabel,     0, 0); grid.add(aptCombo,    0, 1);
        grid.add(ratingLabel,  0, 2); grid.add(starBox,     0, 3);
        grid.add(commentLabel, 0, 4); grid.add(commentArea, 0, 5);
        grid.add(charCount,    0, 6); grid.add(msgLabel,    0, 7);

        Button submitBtn = new Button("Submit Comment");
        submitBtn.getStyleClass().add("primary-button");
        submitBtn.setDisable(eligibleApts.isEmpty());

        Button backBtn = new Button("Back to Dashboard");
        backBtn.getStyleClass().add("secondary-button");

        HBox btnBox = new HBox(15, submitBtn, backBtn);
        btnBox.setAlignment(Pos.CENTER);
        GridPane.setColumnSpan(btnBox, 2);
        grid.add(btnBox, 0, 8);

        root.getChildren().addAll(headerBox, grid);

        submitBtn.setOnAction(e -> {
            int selectedIndex = aptCombo.getSelectionModel().getSelectedIndex();
            if (selectedIndex < 0) {
                showMsg(msgLabel, "Please select an appointment.", false);
                return;
            }
            Appointment selectedApt = eligibleApts.get(selectedIndex);

            Toggle selectedStar = starGroup.getSelectedToggle();
            if (selectedStar == null) {
                showMsg(msgLabel, "Please select a star rating.", false);
                return;
            }
            int rating = (int) selectedStar.getUserData();

            String comment = commentArea.getText().trim();
            if (comment.isEmpty()) {
                showMsg(msgLabel, "Please enter a comment.", false);
                return;
            }
            if (comment.length() < 10) {
                showMsg(msgLabel, "Comment must be at least 10 characters.", false);
                return;
            }

            String feedbackId = "C-CMT-" + selectedApt.getId() + "-"
                              + UUID.randomUUID().toString().substring(0, 6).toUpperCase();

            String today = LocalDate.now().toString();

            String safeComment = comment.replace(",", ";");

            Feedback newFeedback = new Feedback(
                feedbackId,
                customer.getId(), 
                selectedApt.getId(),
                selectedApt.getTechnicianId(),
                safeComment,
                rating,
                today,
                false
            );

            List<Feedback> allFeedbackList = FileHandler.loadAllFeedback();
            allFeedbackList.add(newFeedback);
            FileHandler.saveAllFeedback(allFeedbackList);

            showMsg(msgLabel,
                "Comment submitted successfully! Thank you for your feedback.", true);
            commentArea.clear();
            starGroup.selectToggle(null);
            eligibleApts.remove(selectedIndex);
            aptCombo.setItems(FXCollections.observableArrayList(
                eligibleApts.stream()
                    .map(a -> a.getId() + "  –  " + a.getDate() +
                              "  [" + a.getServiceType() + "]")
                    .collect(Collectors.toList())
            ));
            aptCombo.getSelectionModel().clearSelection();
            if (eligibleApts.isEmpty()) {
                aptCombo.setPromptText("No more appointments to comment on");
                aptCombo.setDisable(true);
                submitBtn.setDisable(true);
            }
        });

        backBtn.setOnAction(e -> {
            CustomerDashboardView dashboard = new CustomerDashboardView(stage, customer);
            stage.setScene(dashboard.createScene());
        });

        Scene scene = new Scene(root, 1000, 700);
        scene.getStylesheets().add(getClass().getResource("styles.css").toExternalForm());

        root.setOpacity(0);
        FadeTransition ft = new FadeTransition(Duration.millis(700), root);
        ft.setFromValue(0); ft.setToValue(1); ft.play();

        return scene;
    }

    private void showMsg(Label label, String msg, boolean success) {
        label.setText(msg);
        label.setTextFill(success ? Color.web("#00E676") : Color.web("#ff4d4d"));
        label.setManaged(true);
    }
}
