package com.example.airlinemanagementsystem.controllers;

import com.example.airlinemanagementsystem.utils.SessionManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class AdminDashboardController implements Initializable {

    @FXML private Label welcomeLabel;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        String name = SessionManager.getInstance().getName();
        int userId  = SessionManager.getInstance().getUserId();
        welcomeLabel.setText("Welcome, " + name +
                " | Employee ID: " + userId);
    }

    @FXML private void goToFlightManagement(MouseEvent e) {
        loadScreen("/com/example/airlinemanagementsystem/FlightManagement.fxml");
    }
    @FXML private void goToEmployeeManagement(MouseEvent e) {
        loadScreen("/com/example/airlinemanagementsystem/EmployeeManagement.fxml");
    }
    @FXML private void goToCouponManagement(MouseEvent e) {
        loadScreen("/com/example/airlinemanagementsystem/CouponManagement.fxml");
    }
    @FXML private void goToReports(MouseEvent e) {
        loadScreen("/com/example/airlinemanagementsystem/Reports.fxml");
    }
    @FXML private void goToUserManagement(MouseEvent e) {
        loadScreen("/com/example/airlinemanagementsystem/UserManagement.fxml");
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