package com.example.airlinemanagementsystem.controllers;

import com.example.airlinemanagementsystem.dao.BookingDAO;
import com.example.airlinemanagementsystem.dao.FlightDAO;
import com.example.airlinemanagementsystem.dao.PassengerDAO;
import com.example.airlinemanagementsystem.dao.SeatDAO;
import com.example.airlinemanagementsystem.models.Flight;
import com.example.airlinemanagementsystem.models.Seat;
import com.example.airlinemanagementsystem.models.User;
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

public class StaffBookTicketController implements Initializable {

    @FXML private TextField              passengerSearchField;
    @FXML private TableView<User>        passengerTable;
    @FXML private TableColumn<User, Integer> colPassengerId;
    @FXML private TableColumn<User, String>  colPassengerName;
    @FXML private TableColumn<User, String>  colPassengerUser;
    @FXML private Label                  selectedPassengerLabel;

    @FXML private ComboBox<String>       sourceCombo;
    @FXML private ComboBox<String>       destinationCombo;
    @FXML private DatePicker             datePicker;
    @FXML private TableView<Flight>      flightsTable;
    @FXML private TableColumn<Flight, String>    colFlightNo;
    @FXML private TableColumn<Flight, String>    colFrom;
    @FXML private TableColumn<Flight, String>    colTo;
    @FXML private TableColumn<Flight, LocalDate> colDate;
    @FXML private TableColumn<Flight, Double>    colEconomy;
    @FXML private TableColumn<Flight, Double>    colBusiness;
    @FXML private TableColumn<Flight, Double>    colFirst;

    @FXML private ComboBox<String>       classCombo;
    @FXML private ComboBox<String>       seatCombo;
    @FXML private Label                  priceLabel;
    @FXML private Label                  discountedPriceLabel;
    @FXML private Label                  bookMessageLabel;

    private final PassengerDAO passengerDAO = new PassengerDAO();
    private final FlightDAO    flightDAO    = new FlightDAO();
    private final SeatDAO      seatDAO      = new SeatDAO();
    private final BookingDAO   bookingDAO   = new BookingDAO();

    private User   selectedPassenger = null;
    private Flight selectedFlight    = null;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Passenger table
        colPassengerId.setCellValueFactory(new PropertyValueFactory<>("userId"));
        colPassengerName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colPassengerUser.setCellValueFactory(new PropertyValueFactory<>("username"));

        // Flight table
        colFlightNo.setCellValueFactory(new PropertyValueFactory<>("flightNumber"));
        colFrom.setCellValueFactory(new PropertyValueFactory<>("source"));
        colTo.setCellValueFactory(new PropertyValueFactory<>("destination"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        colEconomy.setCellValueFactory(new PropertyValueFactory<>("economyPrice"));
        colBusiness.setCellValueFactory(new PropertyValueFactory<>("businessPrice"));
        colFirst.setCellValueFactory(new PropertyValueFactory<>("firstPrice"));

        // Load all passengers initially
        passengerTable.setItems(FXCollections.observableArrayList(
                passengerDAO.getAllPassengers()
        ));

        // Load sources and destinations
        sourceCombo.setItems(FXCollections.observableArrayList(flightDAO.getAllSources()));
        destinationCombo.setItems(FXCollections.observableArrayList(flightDAO.getAllDestinations()));

        // Date picker range
        datePicker.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                setDisabled(empty ||
                        date.isBefore(LocalDate.now()) ||
                        date.isAfter(LocalDate.now().plusDays(20)));
            }
        });

        // Passenger selection listener
        passengerTable.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldVal, newVal) -> {
                    if (newVal != null) {
                        selectedPassenger = newVal;
                        selectedPassengerLabel.setStyle("-fx-text-fill: #27ae60; -fx-font-size: 13px;");
                        selectedPassengerLabel.setText("Selected: " + newVal.getName() +
                                " (" + newVal.getUsername() + ")");
                    }
                }
        );

        // Flight selection listener
        flightsTable.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldVal, newVal) -> {
                    if (newVal != null) {
                        selectedFlight = newVal;
                        classCombo.getItems().clear();
                        if (newVal.getEconomyPrice() > 0)  classCombo.getItems().add("Economy");
                        if (newVal.getBusinessPrice() > 0) classCombo.getItems().add("Business");
                        if (newVal.getFirstPrice() > 0)    classCombo.getItems().add("First");
                    }
                }
        );

        // Class selection listener
        classCombo.setOnAction(e -> {
            if (selectedFlight == null || classCombo.getValue() == null) return;
            String cls = classCombo.getValue();
            double basePrice = switch (cls) {
                case "Business" -> selectedFlight.getBusinessPrice();
                case "First"    -> selectedFlight.getFirstPrice();
                default         -> selectedFlight.getEconomyPrice();
            };
            double discounted = basePrice * 0.30; // 70% off = pay 30%
            priceLabel.setText(String.format("Base Price: BDT %.2f", basePrice));
            discountedPriceLabel.setText(String.format(
                    "After 70%% Staff Discount: BDT %.2f", discounted));

            // Load available seats
            List<Seat> seats = seatDAO.getAvailableSeats(selectedFlight.getFlightId(), cls);
            seatCombo.setItems(FXCollections.observableArrayList(
                    seats.stream().map(Seat::getSeatNumber).toList()
            ));
        });
    }

    @FXML
    private void handlePassengerSearch() {
        String keyword = passengerSearchField.getText().trim();
        List<User> results = keyword.isEmpty()
                ? passengerDAO.getAllPassengers()
                : passengerDAO.searchPassengers(keyword);
        passengerTable.setItems(FXCollections.observableArrayList(results));
    }

    @FXML
    private void handleFlightSearch() {
        String source      = sourceCombo.getValue();
        String destination = destinationCombo.getValue();
        LocalDate date     = datePicker.getValue();

        if (source == null || destination == null || date == null) {
            bookMessageLabel.setStyle("-fx-text-fill: red;");
            bookMessageLabel.setText("Please select source, destination and date.");
            return;
        }

        List<Flight> flights = flightDAO.searchFlights(source, destination, date);
        flightsTable.setItems(FXCollections.observableArrayList(flights));
    }

    @FXML
    private void handleConfirmBooking() {
        bookMessageLabel.setText("");

        if (selectedPassenger == null) {
            showBookMessage("Please select a passenger first.", "red");
            return;
        }
        if (selectedFlight == null) {
            showBookMessage("Please select a flight first.", "red");
            return;
        }
        if (classCombo.getValue() == null) {
            showBookMessage("Please select a seat class.", "red");
            return;
        }
        if (seatCombo.getValue() == null) {
            showBookMessage("Please select a seat number.", "red");
            return;
        }

        String seatClass  = classCombo.getValue();
        String seatNumber = seatCombo.getValue();

        double basePrice = switch (seatClass) {
            case "Business" -> selectedFlight.getBusinessPrice();
            case "First"    -> selectedFlight.getFirstPrice();
            default         -> selectedFlight.getEconomyPrice();
        };

        double discountedPrice = basePrice * 0.30; // 70% staff discount

        int bookingId = bookingDAO.createBooking(
                selectedPassenger.getUserId(),
                selectedFlight.getFlightId(),
                seatClass,
                seatNumber,
                "BankAccount",
                discountedPrice
        );

        if (bookingId > 0) {
            showBookMessage("Booking confirmed! Booking ID: BKG-" + bookingId +
                    " for " + selectedPassenger.getName(), "green");
            seatCombo.getItems().clear();
            classCombo.getItems().clear();
            priceLabel.setText("Price: Select class to see price");
            discountedPriceLabel.setText("");
        } else {
            showBookMessage("Booking failed. Please try again.", "red");
        }
    }

    private void showBookMessage(String msg, String color) {
        bookMessageLabel.setStyle("-fx-text-fill: " + color + "; -fx-font-size: 13px;");
        bookMessageLabel.setText(msg);
    }

    @FXML
    private void goBack() {
        loadScreen("/com/example/airlinemanagementsystem/StaffDashboard.fxml");
    }

    private void loadScreen(String fxmlPath) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(fxmlPath));
            Stage stage = (Stage) passengerSearchField.getScene().getWindow();
            stage.setScene(new Scene(root, 900, 600));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}