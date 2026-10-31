package com.example.airlinemanagementsystem.controllers;

import com.example.airlinemanagementsystem.dao.TicketDAO;
import com.example.airlinemanagementsystem.utils.SessionManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class TicketGeneratedController implements Initializable {

    @FXML private Label bookingIdLabel;
    @FXML private Label passengerLabel;
    @FXML private Label flightNumberLabel;
    @FXML private Label routeLabel;
    @FXML private Label dateLabel;
    @FXML private Label seatLabel;
    @FXML private Label classLabel;
    @FXML private Label amountLabel;
    @FXML private Label paymentMethodLabel;

    private final TicketDAO ticketDAO = new TicketDAO();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        int bookingId = SessionManager.getInstance().getLastBookingId();
        String[] details = ticketDAO.getBookingDetails(bookingId);

        if (details != null) {
            bookingIdLabel.setText("BKG-" + details[0]);
            passengerLabel.setText(details[1]);
            flightNumberLabel.setText(details[2]);
            routeLabel.setText(details[3]);
            dateLabel.setText(details[4]);
            seatLabel.setText(details[5]);
            classLabel.setText(details[6]);
            amountLabel.setText(details[7]);
            paymentMethodLabel.setText(details[8]);
        }
    }

    @FXML
    private void goToDashboard() {
        loadScreen("/com/example/airlinemanagementsystem/UserDashboard.fxml");
    }

    @FXML
    private void goToMyBookings() {
        loadScreen("/com/example/airlinemanagementsystem/MyBookings.fxml");
    }

    private void loadScreen(String fxmlPath) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(fxmlPath));
            Stage stage = (Stage) bookingIdLabel.getScene().getWindow();
            stage.setScene(new Scene(root, 900, 600));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}