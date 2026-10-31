package com.example.airlinemanagementsystem.controllers;

import com.example.airlinemanagementsystem.dao.FlightDAO;
import com.example.airlinemanagementsystem.models.Flight;
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
import java.util.Optional;
import java.util.ResourceBundle;

public class FlightManagementController implements Initializable {

    @FXML private TextField             flightNumberField;
    @FXML private TextField             sourceField;
    @FXML private TextField             destinationField;
    @FXML private DatePicker            datePicker;
    @FXML private TextField             economyField;
    @FXML private TextField             businessField;
    @FXML private TextField             firstField;
    @FXML private Label                 formMessageLabel;
    @FXML private TableView<Flight>     flightsTable;
    @FXML private TableColumn<Flight, Integer>  colId;
    @FXML private TableColumn<Flight, String>   colNumber;
    @FXML private TableColumn<Flight, String>   colSource;
    @FXML private TableColumn<Flight, String>   colDest;
    @FXML private TableColumn<Flight, LocalDate> colDate;
    @FXML private TableColumn<Flight, Double>   colEconomy;
    @FXML private TableColumn<Flight, Double>   colBusiness;
    @FXML private TableColumn<Flight, Double>   colFirst;
    @FXML private Label                 tableMessageLabel;

    private final FlightDAO flightDAO = new FlightDAO();
    private int selectedFlightId = -1;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        colId.setCellValueFactory(new PropertyValueFactory<>("flightId"));
        colNumber.setCellValueFactory(new PropertyValueFactory<>("flightNumber"));
        colSource.setCellValueFactory(new PropertyValueFactory<>("source"));
        colDest.setCellValueFactory(new PropertyValueFactory<>("destination"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        colEconomy.setCellValueFactory(new PropertyValueFactory<>("economyPrice"));
        colBusiness.setCellValueFactory(new PropertyValueFactory<>("businessPrice"));
        colFirst.setCellValueFactory(new PropertyValueFactory<>("firstPrice"));

        loadAllFlights();

        // Fill form when row clicked
        flightsTable.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldVal, newVal) -> {
                    if (newVal != null) {
                        selectedFlightId = newVal.getFlightId();
                        flightNumberField.setText(newVal.getFlightNumber());
                        sourceField.setText(newVal.getSource());
                        destinationField.setText(newVal.getDestination());
                        datePicker.setValue(newVal.getDate());
                        economyField.setText(String.valueOf(newVal.getEconomyPrice()));
                        businessField.setText(String.valueOf(newVal.getBusinessPrice()));
                        firstField.setText(String.valueOf(newVal.getFirstPrice()));
                    }
                }
        );
    }

    private void loadAllFlights() {
        List<Flight> flights = flightDAO.getAllFlights();
        flightsTable.setItems(FXCollections.observableArrayList(flights));
        tableMessageLabel.setText("Total: " + flights.size() + " flights");
    }

    @FXML
    private void handleAdd() {
        try {
            String flightNumber = flightNumberField.getText().trim();
            String source       = sourceField.getText().trim();
            String destination  = destinationField.getText().trim();
            LocalDate date      = datePicker.getValue();
            double economy  = Double.parseDouble(economyField.getText().trim());
            double business = Double.parseDouble(businessField.getText().trim());
            double first    = Double.parseDouble(firstField.getText().trim());

            if (flightNumber.isEmpty() || source.isEmpty() ||
                    destination.isEmpty() || date == null) {
                showFormMessage("Please fill all fields.", "red");
                return;
            }

            boolean success = flightDAO.addFlight(
                    flightNumber, source, destination, date, economy, business, first
            );
            if (success) {
                showFormMessage("Flight added successfully with seats!", "green");
                handleClear();
                loadAllFlights();
            } else {
                showFormMessage("Failed. Flight number may already exist.", "red");
            }
        } catch (NumberFormatException e) {
            showFormMessage("Prices must be numbers.", "red");
        }
    }

    @FXML
    private void handleUpdate() {
        if (selectedFlightId == -1) {
            showFormMessage("Please select a flight to update.", "red");
            return;
        }
        try {
            String source      = sourceField.getText().trim();
            String destination = destinationField.getText().trim();
            LocalDate date     = datePicker.getValue();
            double economy  = Double.parseDouble(economyField.getText().trim());
            double business = Double.parseDouble(businessField.getText().trim());
            double first    = Double.parseDouble(firstField.getText().trim());

            if (source.isEmpty() || destination.isEmpty() || date == null) {
                showFormMessage("Please fill all fields.", "red");
                return;
            }

            boolean success = flightDAO.updateFlight(
                    selectedFlightId, source, destination, date, economy, business, first
            );
            if (success) {
                showFormMessage("Flight updated successfully!", "green");
                handleClear();
                loadAllFlights();
            } else {
                showFormMessage("Update failed.", "red");
            }
        } catch (NumberFormatException e) {
            showFormMessage("Prices must be numbers.", "red");
        }
    }

    @FXML
    private void handleDelete() {
        if (selectedFlightId == -1) {
            showFormMessage("Please select a flight to delete.", "red");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Flight");
        alert.setHeaderText("Delete this flight?");
        alert.setContentText("All seats for this flight will also be deleted.");
        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            boolean success = flightDAO.deleteFlight(selectedFlightId);
            if (success) {
                showFormMessage("Flight deleted successfully!", "green");
                handleClear();
                loadAllFlights();
            } else {
                showFormMessage("Cannot delete — flight has existing bookings.", "red");
            }
        }
    }

    @FXML
    private void handleClear() {
        flightNumberField.clear();
        sourceField.clear();
        destinationField.clear();
        datePicker.setValue(null);
        economyField.clear();
        businessField.clear();
        firstField.clear();
        selectedFlightId = -1;
        formMessageLabel.setText("");
        flightsTable.getSelectionModel().clearSelection();
    }

    private void showFormMessage(String msg, String color) {
        formMessageLabel.setStyle("-fx-text-fill: " + color + "; -fx-font-size: 13px;");
        formMessageLabel.setText(msg);
    }

    @FXML
    private void goBack() {
        loadScreen("/com/example/airlinemanagementsystem/AdminDashboard.fxml");
    }

    private void loadScreen(String fxmlPath) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(fxmlPath));
            Stage stage = (Stage) flightNumberField.getScene().getWindow();
            stage.setScene(new Scene(root, 900, 600));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}