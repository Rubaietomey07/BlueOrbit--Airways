package com.example.airlinemanagementsystem.controllers;

import com.example.airlinemanagementsystem.dao.BookingDAO;
import com.example.airlinemanagementsystem.dao.PassengerDAO;
import com.example.airlinemanagementsystem.dao.SeatDAO;
import com.example.airlinemanagementsystem.models.Seat;
import com.example.airlinemanagementsystem.models.User;
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
import java.util.List;
import java.util.ResourceBundle;

public class PassengerManagementController implements Initializable {

    @FXML private TextField              nameField;
    @FXML private TextField              usernameField;
    @FXML private TextField              passwordField;
    @FXML private Label                  formMessageLabel;
    @FXML private TextField              searchField;
    @FXML private TableView<User>        passengersTable;
    @FXML private TableColumn<User, Integer> colUserId;
    @FXML private TableColumn<User, String>  colName;
    @FXML private TableColumn<User, String>  colUsername;
    @FXML private TableColumn<User, String>  colStatus;
    @FXML private Label                  tableMessageLabel;

    @FXML private TextField        changeBookingIdField;
    @FXML private ComboBox<String> newClassCombo;
    @FXML private ComboBox<String> newSeatCombo;
    @FXML private Label            changeSeatMessageLabel;

    private final PassengerDAO passengerDAO = new PassengerDAO();
    private final BookingDAO   bookingDAO   = new BookingDAO();
    private final SeatDAO      seatDAO      = new SeatDAO();

    private int selectedUserId = -1;
    private int currentFlightId = -1;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        colUserId.setCellValueFactory(new PropertyValueFactory<>("userId"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colUsername.setCellValueFactory(new PropertyValueFactory<>("username"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        loadAllPassengers();

        // When row clicked fill form
        passengersTable.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldVal, newVal) -> {
                    if (newVal != null) {
                        selectedUserId = newVal.getUserId();
                        nameField.setText(newVal.getName());
                        usernameField.setText(newVal.getUsername());
                        passwordField.clear();
                    }
                }
        );

        // When class selected load seats
        newClassCombo.setOnAction(e -> {
            String cls = newClassCombo.getValue();
            if (cls == null || currentFlightId == -1) return;
            List<Seat> seats = seatDAO.getAvailableSeats(currentFlightId, cls);
            newSeatCombo.setItems(FXCollections.observableArrayList(
                    seats.stream().map(Seat::getSeatNumber).toList()
            ));
        });
    }

    private void loadAllPassengers() {
        List<User> passengers = passengerDAO.getAllPassengers();
        passengersTable.setItems(FXCollections.observableArrayList(passengers));
        tableMessageLabel.setText("Total: " + passengers.size() + " passengers");
    }

    @FXML
    private void handleAdd() {
        String name     = nameField.getText().trim();
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        if (name.isEmpty() || username.isEmpty() || password.isEmpty()) {
            showFormMessage("Please fill in all fields.", "red");
            return;
        }

        boolean success = passengerDAO.addPassenger(name, username, password);
        if (success) {
            showFormMessage("Passenger added successfully!", "green");
            handleClear();
            loadAllPassengers();
        } else {
            showFormMessage("Username already exists. Try another.", "red");
        }
    }

    @FXML
    private void handleUpdate() {
        if (selectedUserId == -1) {
            showFormMessage("Please select a passenger from the table first.", "red");
            return;
        }

        String name     = nameField.getText().trim();
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        if (name.isEmpty() || username.isEmpty() || password.isEmpty()) {
            showFormMessage("Please fill in all fields.", "red");
            return;
        }

        boolean success = passengerDAO.updatePassenger(
                selectedUserId, name, username, password
        );
        if (success) {
            showFormMessage("Passenger updated successfully!", "green");
            handleClear();
            loadAllPassengers();
        } else {
            showFormMessage("Update failed.", "red");
        }
    }

    @FXML
    private void handleSearch() {
        String keyword = searchField.getText().trim();
        if (keyword.isEmpty()) {
            loadAllPassengers();
            return;
        }
        List<User> results = passengerDAO.searchPassengers(keyword);
        passengersTable.setItems(FXCollections.observableArrayList(results));
        tableMessageLabel.setText("Found: " + results.size() + " passengers");
    }

    @FXML
    private void handleShowAll() {
        searchField.clear();
        loadAllPassengers();
    }

    @FXML
    private void handleClear() {
        nameField.clear();
        usernameField.clear();
        passwordField.clear();
        selectedUserId = -1;
        formMessageLabel.setText("");
        passengersTable.getSelectionModel().clearSelection();
    }

    @FXML
    private void handleLoadSeats() {
        changeSeatMessageLabel.setText("");
        String bookingIdText = changeBookingIdField.getText().trim();

        if (bookingIdText.isEmpty()) {
            showChangeSeatMessage("Please enter a Booking ID.", "red");
            return;
        }

        int bookingId;
        try {
            bookingId = Integer.parseInt(bookingIdText.replace("BKG-", "").trim());
        } catch (NumberFormatException e) {
            showChangeSeatMessage("Invalid Booking ID format.", "red");
            return;
        }

        currentFlightId = bookingDAO.getFlightIdByBooking(bookingId);

        if (currentFlightId == -1) {
            showChangeSeatMessage("Booking not found.", "red");
            return;
        }

        newClassCombo.setItems(FXCollections.observableArrayList(
                "Economy", "Business", "First"
        ));
        newSeatCombo.getItems().clear();

        showChangeSeatMessage(
                "Seats loaded! Select class then seat number.", "green"
        );
    }

    @FXML
    private void handleChangeSeat() {
        String bookingIdText = changeBookingIdField.getText().trim();
        String newClass      = newClassCombo.getValue();
        String newSeat       = newSeatCombo.getValue();

        if (bookingIdText.isEmpty() || newClass == null || newSeat == null) {
            showChangeSeatMessage("Please fill all fields.", "red");
            return;
        }

        int bookingId = Integer.parseInt(
                bookingIdText.replace("BKG-", "").trim()
        );

        boolean success = bookingDAO.changeSeat(bookingId, newClass, newSeat);

        if (success) {
            showChangeSeatMessage(
                    "Seat changed successfully to " + newClass + " - " + newSeat + "!",
                    "green"
            );
            newClassCombo.getItems().clear();
            newSeatCombo.getItems().clear();
            changeBookingIdField.clear();
            currentFlightId = -1;
        } else {
            showChangeSeatMessage("Failed to change seat. Seat may not be available.", "red");
        }
    }

    private void showFormMessage(String msg, String color) {
        formMessageLabel.setStyle("-fx-text-fill: " + color + "; -fx-font-size: 13px;");
        formMessageLabel.setText(msg);
    }

    private void showChangeSeatMessage(String msg, String color) {
        changeSeatMessageLabel.setStyle(
                "-fx-text-fill: " + color + "; -fx-font-size: 13px;"
        );
        changeSeatMessageLabel.setText(msg);
    }

    @FXML
    private void goBack() {
        loadScreen("/com/example/airlinemanagementsystem/StaffDashboard.fxml");
    }

    private void loadScreen(String fxmlPath) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(fxmlPath));
            Stage stage = (Stage) nameField.getScene().getWindow();
            stage.setScene(new Scene(root, 900, 600));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}