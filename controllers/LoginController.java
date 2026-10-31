package com.example.airlinemanagementsystem.controllers;

import com.example.airlinemanagementsystem.dao.UserDAO;
import com.example.airlinemanagementsystem.models.User;
import com.example.airlinemanagementsystem.utils.SessionManager;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class LoginController implements Initializable {

    @FXML private ComboBox<String> roleCombo;
    @FXML private TextField        usernameField;
    @FXML private PasswordField    passwordField;
    @FXML private Label            errorLabel;

    private final UserDAO userDAO = new UserDAO();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        roleCombo.setItems(FXCollections.observableArrayList(
                "User (Passenger)", "Staff", "Admin"
        ));
    }

    @FXML
    private void handleLogin() {
        String selectedRole = roleCombo.getValue();
        String username     = usernameField.getText().trim();
        String password     = passwordField.getText().trim();

        if (selectedRole == null) {
            errorLabel.setText("Please select your role.");
            return;
        }
        if (username.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Please fill in all fields.");
            return;
        }

        // Map display role to DB role
        String dbRole = switch (selectedRole) {
            case "Staff" -> "Staff";
            case "Admin" -> "Admin";
            default      -> "User";
        };

        User user = userDAO.loginWithRole(username, password, dbRole);

        if (user == null) {
            errorLabel.setText("Invalid credentials or wrong role selected.");
            return;
        }

        SessionManager.getInstance().login(
                user.getUserId(), user.getUsername(),
                user.getName(),   user.getRole()
        );

        String fxml = switch (user.getRole()) {
            case "Admin" -> "/com/example/airlinemanagementsystem/AdminDashboard.fxml";
            case "Staff" -> "/com/example/airlinemanagementsystem/StaffDashboard.fxml";
            default      -> "/com/example/airlinemanagementsystem/UserDashboard.fxml";
        };

        loadScreen(fxml);
    }

    @FXML
    private void goToRegister() {
        loadScreen("/com/example/airlinemanagementsystem/Register.fxml");
    }

    private void loadScreen(String fxmlPath) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(fxmlPath));
            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            errorLabel.setText("Error loading screen.");
        }
    }
}