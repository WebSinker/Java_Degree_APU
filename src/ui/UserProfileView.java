package ui;

import models.User;
import utils.FileHandler;
import javafx.animation.FadeTransition;
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

import java.util.List;

public class UserProfileView {

    private final Stage stage;
    private final User  user;

    public UserProfileView(Stage stage, User user) {
        this.stage = stage;
        this.user  = user;
    }

    public Scene createScene() {
        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(30));

        Label titleLabel = new Label("Edit Profile");
        titleLabel.getStyleClass().add("title-text");

        Label subLabel = new Label("Update your personal information");
        subLabel.getStyleClass().add("subtitle-text");

        VBox headerBox = new VBox(5, titleLabel, subLabel);
        headerBox.setAlignment(Pos.CENTER);
        headerBox.setPadding(new Insets(0, 0, 10, 0));

        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(15);
        grid.setVgap(15);
        grid.getStyleClass().add("form-container");

        Label nameLabel = new Label("Full Name");
        TextField nameField = new TextField(user.getName());
        nameField.setPromptText("Your full name");

        Label contactLabel = new Label("Contact Number");
        TextField contactField = new TextField(user.getContactNumber());
        contactField.setPromptText("e.g. 0123456789");

        Label currentPwLabel = new Label("Current Password");
        PasswordField currentPwField = new PasswordField();
        currentPwField.setPromptText("Required to save changes");

        Label newPwLabel = new Label("New Password");
        PasswordField newPwField = new PasswordField();
        newPwField.setPromptText("Leave blank to keep current");

        Label confirmPwLabel = new Label("Confirm New Password");
        PasswordField confirmPwField = new PasswordField();
        confirmPwField.setPromptText("Repeat new password");

        grid.add(nameLabel,      0, 0); grid.add(nameField,      0, 1);
        grid.add(contactLabel,   1, 0); grid.add(contactField,   1, 1);
        grid.add(currentPwLabel, 0, 2); grid.add(currentPwField, 0, 3);
        grid.add(newPwLabel,     0, 4); grid.add(newPwField,     0, 5);
        grid.add(confirmPwLabel, 1, 4); grid.add(confirmPwField, 1, 5);

        Label msgLabel = new Label();
        msgLabel.setManaged(false);
        msgLabel.getStyleClass().add("error-text");
        GridPane.setColumnSpan(msgLabel, 2);
        grid.add(msgLabel, 0, 6);

        Button saveBtn = new Button("Save Changes");
        saveBtn.getStyleClass().add("primary-button");
        saveBtn.setMaxWidth(Double.MAX_VALUE);

        Button backBtn = new Button("Back to Dashboard");
        backBtn.getStyleClass().add("secondary-button");
        backBtn.setMaxWidth(Double.MAX_VALUE);

        HBox btnBox = new HBox(15, saveBtn, backBtn);
        btnBox.setAlignment(Pos.CENTER);
        GridPane.setColumnSpan(btnBox, 2);
        grid.add(btnBox, 0, 7);

        root.getChildren().addAll(headerBox, grid);

        saveBtn.setOnAction(e -> {
            String currentPw = currentPwField.getText().trim();
            String newName   = nameField.getText().trim();
            String contact   = contactField.getText().trim();
            String newPw     = newPwField.getText().trim();
            String confirmPw = confirmPwField.getText().trim();

            if (currentPw.isEmpty()) {
                showMsg(msgLabel, "Please enter your current password to save changes.", false);
                return;
            }
            if (!currentPw.equals(user.getPassword())) {
                showMsg(msgLabel, "Current password is incorrect.", false);
                return;
            }
            if (newName.isEmpty() || contact.isEmpty()) {
                showMsg(msgLabel, "Name and contact number cannot be empty.", false);
                return;
            }
            if (!contact.matches("\\d{10,11}")) {
                showMsg(msgLabel, "Contact must be 10–11 digits (numbers only).", false);
                return;
            }
            if (!newPw.isEmpty()) {
                if (newPw.length() < 6) {
                    showMsg(msgLabel, "New password must be at least 6 characters.", false);
                    return;
                }
                if (!newPw.equals(confirmPw)) {
                    showMsg(msgLabel, "New passwords do not match.", false);
                    return;
                }
            }

            user.setName(newName);
            user.setContactNumber(contact);
            if (!newPw.isEmpty()) {
                user.setPassword(newPw);
            }

            List<User> allUsers = FileHandler.loadAllUsers();
            for (int i = 0; i < allUsers.size(); i++) {
                if (allUsers.get(i).getId().equals(user.getId())) {
                    allUsers.set(i, user);
                    break;
                }
            }
            FileHandler.saveAllUsers(allUsers);

            showMsg(msgLabel, "Profile updated successfully!", true);
            currentPwField.clear();
            newPwField.clear();
            confirmPwField.clear();
        });

        backBtn.setOnAction(e -> {
            goToDashboard(user.getRole());
        });

        Scene scene = new Scene(root, 1000, 700);
        scene.getStylesheets().add(getClass().getResource("styles.css").toExternalForm());

        root.setOpacity(0);
        FadeTransition ft = new FadeTransition(Duration.millis(700), root);
        ft.setFromValue(0); ft.setToValue(1); ft.play();

        return scene;
    }

    private void goToDashboard(String role) {
        Scene nextScene = null;
        switch (role) {
            case "Manager":
                nextScene = new ManagerDashboardView(stage, user).createScene();
                break;
            case "CounterStaff":
                nextScene = new CounterStaffDashboardView(stage, user).createScene();
                break;
            case "Technician":
                nextScene = new TechnicianDashboardView(stage, user).createScene();
                break;
            case "Customer":
                nextScene = new CustomerDashboardView(stage, user).createScene();
                break;
        }
        if (nextScene != null) stage.setScene(nextScene);
    }

    private void showMsg(Label label, String msg, boolean success) {
        label.setText(msg);
        label.setTextFill(success ? Color.web("#00E676") : Color.web("#ff4d4d"));
        label.setManaged(true);
    }
}
