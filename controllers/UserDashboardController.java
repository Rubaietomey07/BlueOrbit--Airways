package com.example.airlinemanagementsystem.controllers;

import com.example.airlinemanagementsystem.utils.SessionManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.scene.input.MouseEvent;
import java.net.URL;
import java.util.ResourceBundle;

public class UserDashboardController implements Initializable {

    @FXML private Label welcomeLabel;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Show logged in user's name
        String name = SessionManager.getInstance().getName();
        welcomeLabel.setText("Welcome back, " + name + "!");
    }

    @FXML
    private void goToSearchFlight(MouseEvent event) {
        loadScreen("/com/example/airlinemanagementsystem/SearchFlight.fxml");
    }

    @FXML
    private void goToMyBookings(MouseEvent event) {
        loadScreen("/com/example/airlinemanagementsystem/MyBookings.fxml");
    }

    @FXML
    private void goToVerifyTicket(MouseEvent event) {
        loadScreen("/com/example/airlinemanagementsystem/VerifyTicket.fxml");
    }

    @FXML
    private void handleLogout() {
        SessionManager.getInstance().logout();
        loadScreen("/com/example/airlinemanagementsystem/Login.fxml");
    }

    private void loadScreen(String fxmlPath) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(fxmlPath));
            Stage stage = (Stage) welcomeLabel.getScene().getWindow();
            stage.setScene(new Scene(root, 900, 600));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}