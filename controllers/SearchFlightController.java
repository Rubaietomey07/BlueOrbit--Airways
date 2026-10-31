package com.example.airlinemanagementsystem.controllers;

import com.example.airlinemanagementsystem.dao.FlightDAO;
import com.example.airlinemanagementsystem.models.Flight;
import com.example.airlinemanagementsystem.utils.SessionManager;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;

public class SearchFlightController implements Initializable {

    @FXML private ComboBox<String>   sourceCombo;
    @FXML private ComboBox<String>   destinationCombo;
    @FXML private DatePicker         datePicker;
    @FXML private Label              errorLabel;
    @FXML private TableView<Flight>  flightsTable;

    @FXML private TableColumn<Flight, String>    colFlightNumber;
    @FXML private TableColumn<Flight, String>    colSource;
    @FXML private TableColumn<Flight, String>    colDestination;
    @FXML private TableColumn<Flight, LocalDate> colDate;
    @FXML private TableColumn<Flight, Double>    colEconomy;
    @FXML private TableColumn<Flight, Double>    colBusiness;
    @FXML private TableColumn<Flight, Double>    colFirst;

    private final FlightDAO flightDAO = new FlightDAO();

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        // Set up table columns
        colFlightNumber.setCellValueFactory(new PropertyValueFactory<>("flightNumber"));
        colSource.setCellValueFactory(new PropertyValueFactory<>("source"));
        colDestination.setCellValueFactory(new PropertyValueFactory<>("destination"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        colEconomy.setCellValueFactory(new PropertyValueFactory<>("economyPrice"));
        colBusiness.setCellValueFactory(new PropertyValueFactory<>("businessPrice"));
        colFirst.setCellValueFactory(new PropertyValueFactory<>("firstPrice"));

        // Load sources and destinations into dropdowns
        sourceCombo.setItems(FXCollections.observableArrayList(flightDAO.getAllSources()));
        destinationCombo.setItems(FXCollections.observableArrayList(flightDAO.getAllDestinations()));

        // Set date picker range — today to next 20 days only
        datePicker.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                setDisabled(empty ||
                        date.isBefore(LocalDate.now()) ||
                        date.isAfter(LocalDate.now().plusDays(20)));
            }
        });
    }

    @FXML
    private void handleSearch() {
        errorLabel.setText("");

        String source      = sourceCombo.getValue();
        String destination = destinationCombo.getValue();
        LocalDate date     = datePicker.getValue();

        if (source == null || destination == null || date == null) {
            errorLabel.setText("Please select source, destination and date.");
            return;
        }
        if (source.equals(destination)) {
            errorLabel.setText("Source and destination cannot be the same.");
            return;
        }

        List<Flight> flights = flightDAO.searchFlights(source, destination, date);

        // Debug — check console for results
        System.out.println("Flights found: " + flights.size());
        for (Flight f : flights) {
            System.out.println(f.getFlightNumber() + " | " + f.getSource()
                    + " → " + f.getDestination() + " | " + f.getDate());
        }

        if (flights.isEmpty()) {
            errorLabel.setText("No flights found for the selected route and date.");
            flightsTable.setItems(FXCollections.observableArrayList());
        } else {
            errorLabel.setText("");
            flightsTable.setItems(FXCollections.observableArrayList(flights));
        }
    }

    @FXML
    private void handleSelectFlight() {
        Flight selected = flightsTable.getSelectionModel().getSelectedItem();

        if (selected == null) {
            errorLabel.setText("Please select a flight from the table first.");
            return;
        }

        // Save selected flight to session
        SessionManager.getInstance().setSelectedFlight(selected);

        loadScreen("/com/example/airlinemanagementsystem/SelectSeat.fxml");
    }

    @FXML
    private void goBack() {
        loadScreen("/com/example/airlinemanagementsystem/UserDashboard.fxml");
    }

    private void loadScreen(String fxmlPath) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(fxmlPath));
            Stage stage = (Stage) sourceCombo.getScene().getWindow();
            stage.setScene(new Scene(root, 900, 600));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            errorLabel.setText("Error loading screen.");
        }
    }
}