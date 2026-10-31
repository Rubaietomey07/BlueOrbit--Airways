package com.example.airlinemanagementsystem.controllers;

import com.example.airlinemanagementsystem.dao.SeatDAO;
import com.example.airlinemanagementsystem.models.Flight;
import com.example.airlinemanagementsystem.models.Seat;
import com.example.airlinemanagementsystem.utils.SessionManager;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class SelectSeatController implements Initializable {

    @FXML private Label      flightNumberLabel;
    @FXML private Label      routeLabel;
    @FXML private Label      dateLabel;
    @FXML private Label      economyPriceLabel;
    @FXML private Label      businessPriceLabel;
    @FXML private Label      firstPriceLabel;
    @FXML private RadioButton economyRadio;
    @FXML private RadioButton businessRadio;
    @FXML private RadioButton firstRadio;
    @FXML private ComboBox<String> seatCombo;
    @FXML private Label      errorLabel;

    private final SeatDAO seatDAO = new SeatDAO();
    private ToggleGroup classGroup;
    private String selectedClass = null;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Flight flight = SessionManager.getInstance().getSelectedFlight();

        // Show flight info
        flightNumberLabel.setText(flight.getFlightNumber());
        routeLabel.setText(flight.getSource() + " → " + flight.getDestination());
        dateLabel.setText(flight.getDate().toString());

        // Show prices
        economyPriceLabel.setText("BDT " + flight.getEconomyPrice());
        businessPriceLabel.setText("BDT " + flight.getBusinessPrice());
        firstPriceLabel.setText("BDT " + flight.getFirstPrice());

        // Group radio buttons
        classGroup = new ToggleGroup();
        economyRadio.setToggleGroup(classGroup);
        businessRadio.setToggleGroup(classGroup);
        firstRadio.setToggleGroup(classGroup);
    }

    @FXML
    private void handleClassSelection() {
        Flight flight = SessionManager.getInstance().getSelectedFlight();

        if (economyRadio.isSelected())      selectedClass = "Economy";
        else if (businessRadio.isSelected()) selectedClass = "Business";
        else if (firstRadio.isSelected())    selectedClass = "First";

        // Load available seats for selected class
        List<Seat> seats = seatDAO.getAvailableSeats(
                flight.getFlightId(), selectedClass
        );

        if (seats.isEmpty()) {
            seatCombo.setItems(FXCollections.observableArrayList());
            seatCombo.setPromptText("No seats available");
        } else {
            seatCombo.setItems(FXCollections.observableArrayList(
                    seats.stream().map(Seat::getSeatNumber).toList()
            ));
            seatCombo.setPromptText("Select seat");
        }
    }

    @FXML
    private void handleContinue() {
        errorLabel.setText("");

        if (selectedClass == null) {
            errorLabel.setText("Please select a seat class.");
            return;
        }
        if (seatCombo.getValue() == null) {
            errorLabel.setText("Please select a seat number.");
            return;
        }

        SessionManager.getInstance().setSelectedClass(selectedClass);
        SessionManager.getInstance().setSelectedSeatNumber(seatCombo.getValue());

        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/example/airlinemanagementsystem/PriceDetails.fxml")
            );
            Parent root = loader.load();

            // Get controller and call initData manually
            PriceDetailsController controller = loader.getController();
            controller.initData();

            Stage stage = (Stage) seatCombo.getScene().getWindow();
            stage.setScene(new Scene(root, 900, 600));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            errorLabel.setText("Error loading screen.");
        }
    }
    @FXML
    private void goBack() {
        loadScreen("/com/example/airlinemanagementsystem/SearchFlight.fxml");
    }

    private void loadScreen(String fxmlPath) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(fxmlPath));
            Stage stage = (Stage) seatCombo.getScene().getWindow();
            stage.setScene(new Scene(root, 900, 600));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            errorLabel.setText("Error loading screen.");
        }
    }
}