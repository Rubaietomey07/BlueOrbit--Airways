package com.example.airlinemanagementsystem.controllers;

import com.example.airlinemanagementsystem.dao.TicketDAO;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class VerifyTicketController implements Initializable {

    @FXML private TextField bookingIdField;
    @FXML private Label     errorLabel;
    @FXML private VBox      ticketDetailsBox;
    @FXML private Label     resultBookingId;
    @FXML private Label     resultPassenger;
    @FXML private Label     resultFlight;
    @FXML private Label     resultRoute;
    @FXML private Label     resultDate;
    @FXML private Label     resultSeat;
    @FXML private Label     resultClass;
    @FXML private Label     resultStatus;

    private final TicketDAO ticketDAO = new TicketDAO();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        ticketDetailsBox.setVisible(false);
        ticketDetailsBox.setManaged(false);
    }

    @FXML
    private void handleVerify() {
        errorLabel.setText("");
        String input = bookingIdField.getText().trim();

        if (input.isEmpty()) {
            errorLabel.setText("Please enter a Booking ID.");
            return;
        }

        // Allow BKG-1 or just 1
        input = input.replace("BKG-", "").replace("bkg-", "").trim();

        int bookingId;
        try {
            bookingId = Integer.parseInt(input);
        } catch (NumberFormatException e) {
            errorLabel.setText("Invalid Booking ID format.");
            return;
        }

        String[] details = ticketDAO.getBookingDetails(bookingId);

        if (details == null) {
            ticketDetailsBox.setVisible(false);
            ticketDetailsBox.setManaged(false);
            errorLabel.setText("No ticket found for Booking ID: BKG-" + bookingId);
            return;
        }

        resultBookingId.setText("BKG-" + details[0]);
        resultPassenger.setText(details[1]);
        resultFlight.setText(details[2]);
        resultRoute.setText(details[3]);
        resultDate.setText(details[4]);
        resultSeat.setText(details[5]);
        resultClass.setText(details[6]);

        // Also get status
        String status = ticketDAO.getBookingStatus(bookingId);
        if ("Cancelled".equals(status)) {
            resultStatus.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: red;");
            resultStatus.setText("CANCELLED");
        } else {
            resultStatus.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #27ae60;");
            resultStatus.setText("VALID ✓");
        }

        ticketDetailsBox.setVisible(true);
        ticketDetailsBox.setManaged(true);
    }

    @FXML
    private void goBack() {
        loadScreen("/com/example/airlinemanagementsystem/UserDashboard.fxml");
    }

    private void loadScreen(String fxmlPath) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(fxmlPath));
            Stage stage = (Stage) bookingIdField.getScene().getWindow();
            stage.setScene(new Scene(root, 900, 600));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}