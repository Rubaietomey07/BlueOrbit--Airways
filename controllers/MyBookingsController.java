package com.example.airlinemanagementsystem.controllers;

import com.example.airlinemanagementsystem.dao.BookingDAO;
import com.example.airlinemanagementsystem.models.Booking;
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
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class MyBookingsController implements Initializable {

    @FXML private TableView<Booking>             bookingsTable;
    @FXML private TableColumn<Booking, Integer>  colBookingId;
    @FXML private TableColumn<Booking, String>   colFlight;
    @FXML private TableColumn<Booking, String>   colRoute;
    @FXML private TableColumn<Booking, String>   colDate;
    @FXML private TableColumn<Booking, String>   colSeat;
    @FXML private TableColumn<Booking, String>   colClass;
    @FXML private TableColumn<Booking, Double>   colAmount;
    @FXML private TableColumn<Booking, String>   colStatus;
    @FXML private Label                          messageLabel;

    private final BookingDAO bookingDAO = new BookingDAO();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        colBookingId.setCellValueFactory(new PropertyValueFactory<>("bookingId"));
        colFlight.setCellValueFactory(new PropertyValueFactory<>("flightNumber"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        colSeat.setCellValueFactory(new PropertyValueFactory<>("seatNumber"));
        colClass.setCellValueFactory(new PropertyValueFactory<>("seatClass"));
        colAmount.setCellValueFactory(new PropertyValueFactory<>("amount"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        // Route = source + destination combined
        colRoute.setCellValueFactory(cellData -> {
            Booking b = cellData.getValue();
            return new javafx.beans.property.SimpleStringProperty(
                    b.getSource() + " → " + b.getDestination()
            );
        });

        loadBookings();
    }

    private void loadBookings() {
        int userId = SessionManager.getInstance().getUserId();
        List<Booking> bookings = bookingDAO.getUserBookings(userId);
        if (bookings.isEmpty()) {
            messageLabel.setStyle("-fx-text-fill: #888;");
            messageLabel.setText("No bookings found.");
        } else {
            bookingsTable.setItems(FXCollections.observableArrayList(bookings));
        }
    }

    @FXML
    private void handleCancel() {
        Booking selected = bookingsTable.getSelectionModel().getSelectedItem();

        if (selected == null) {
            messageLabel.setStyle("-fx-text-fill: red;");
            messageLabel.setText("Please select a booking to cancel.");
            return;
        }

        if (selected.getStatus().equals("Cancelled")) {
            messageLabel.setStyle("-fx-text-fill: red;");
            messageLabel.setText("This booking is already cancelled.");
            return;
        }

        // Confirm dialog
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Cancel Booking");
        alert.setHeaderText("Cancel Booking BKG-" + selected.getBookingId() + "?");
        alert.setContentText("This action cannot be undone. The seat will be released.");
        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            int userId = SessionManager.getInstance().getUserId();
            boolean success = bookingDAO.cancelBooking(selected.getBookingId(), userId);
            if (success) {
                messageLabel.setStyle("-fx-text-fill: green;");
                messageLabel.setText("Booking cancelled successfully!");
                loadBookings(); // refresh table
            } else {
                messageLabel.setStyle("-fx-text-fill: red;");
                messageLabel.setText("Failed to cancel booking.");
            }
        }
    }

    @FXML
    private void goBack() {
        loadScreen("/com/example/airlinemanagementsystem/UserDashboard.fxml");
    }

    private void loadScreen(String fxmlPath) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(fxmlPath));
            Stage stage = (Stage) bookingsTable.getScene().getWindow();
            stage.setScene(new Scene(root, 900, 600));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @FXML
    private void handleDelete() {
        Booking selected = bookingsTable.getSelectionModel().getSelectedItem();

        if (selected == null) {
            messageLabel.setStyle("-fx-text-fill: red;");
            messageLabel.setText("Please select a booking to delete.");
            return;
        }

        if (!selected.getStatus().equals("Cancelled")) {
            messageLabel.setStyle("-fx-text-fill: red;");
            messageLabel.setText("Only cancelled bookings can be deleted. Cancel it first.");
            return;
        }

        // Confirm dialog
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Booking");
        alert.setHeaderText("Permanently delete Booking BKG-" + selected.getBookingId() + "?");
        alert.setContentText("This will permanently remove this booking record from the database.");
        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            int userId = SessionManager.getInstance().getUserId();
            boolean success = bookingDAO.deleteBooking(selected.getBookingId(), userId);
            if (success) {
                messageLabel.setStyle("-fx-text-fill: green;");
                messageLabel.setText("Booking deleted successfully!");
                loadBookings(); // refresh table
            } else {
                messageLabel.setStyle("-fx-text-fill: red;");
                messageLabel.setText("Failed to delete. Only cancelled bookings can be deleted.");
            }
        }
    }
}