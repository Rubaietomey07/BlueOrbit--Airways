package com.example.airlinemanagementsystem.controllers;

import com.example.airlinemanagementsystem.dao.ReportsDAO;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class ReportsController implements Initializable {

    // Summary labels
    @FXML private Label confirmedLabel;
    @FXML private Label cancelledLabel;
    @FXML private Label revenueLabel;
    @FXML private Label passengersLabel;
    @FXML private Label flightsLabel;

    // Booking table
    @FXML private TableView<String[]>           bookingTable;
    @FXML private TableColumn<String[], String> bColId;
    @FXML private TableColumn<String[], String> bColName;
    @FXML private TableColumn<String[], String> bColFlight;
    @FXML private TableColumn<String[], String> bColRoute;
    @FXML private TableColumn<String[], String> bColDate;
    @FXML private TableColumn<String[], String> bColSeat;
    @FXML private TableColumn<String[], String> bColClass;
    @FXML private TableColumn<String[], String> bColStatus;
    @FXML private Label                         bookingCountLabel;

    // Revenue table
    @FXML private TableView<String[]>           revenueTable;
    @FXML private TableColumn<String[], String> rColFlight;
    @FXML private TableColumn<String[], String> rColRoute;
    @FXML private TableColumn<String[], String> rColDate;
    @FXML private TableColumn<String[], String> rColBookings;
    @FXML private TableColumn<String[], String> rColRevenue;

    // Passenger table
    @FXML private TableView<String[]>           passengerTable;
    @FXML private TableColumn<String[], String> pColId;
    @FXML private TableColumn<String[], String> pColName;
    @FXML private TableColumn<String[], String> pColUsername;
    @FXML private TableColumn<String[], String> pColTotal;
    @FXML private TableColumn<String[], String> pColConfirmed;
    @FXML private TableColumn<String[], String> pColCancelled;

    private final ReportsDAO reportsDAO = new ReportsDAO();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setupColumns();
        loadSummaryStats();
        loadBookingReport();
        loadRevenueReport();
        loadPassengerReport();
    }

    private void setupColumns() {
        // Booking columns
        bColId.setCellValueFactory(     d -> new SimpleStringProperty(d.getValue()[0]));
        bColName.setCellValueFactory(   d -> new SimpleStringProperty(d.getValue()[1]));
        bColFlight.setCellValueFactory( d -> new SimpleStringProperty(d.getValue()[3]));
        bColRoute.setCellValueFactory(  d -> new SimpleStringProperty(d.getValue()[4]));
        bColDate.setCellValueFactory(   d -> new SimpleStringProperty(d.getValue()[5]));
        bColSeat.setCellValueFactory(   d -> new SimpleStringProperty(d.getValue()[6]));
        bColClass.setCellValueFactory(  d -> new SimpleStringProperty(d.getValue()[7]));
        bColStatus.setCellValueFactory( d -> new SimpleStringProperty(d.getValue()[8]));

        // Revenue columns
        rColFlight.setCellValueFactory(   d -> new SimpleStringProperty(d.getValue()[0]));
        rColRoute.setCellValueFactory(    d -> new SimpleStringProperty(d.getValue()[1]));
        rColDate.setCellValueFactory(     d -> new SimpleStringProperty(d.getValue()[2]));
        rColBookings.setCellValueFactory( d -> new SimpleStringProperty(d.getValue()[3]));
        rColRevenue.setCellValueFactory(  d -> new SimpleStringProperty(d.getValue()[4]));

        // Passenger columns
        pColId.setCellValueFactory(       d -> new SimpleStringProperty(d.getValue()[0]));
        pColName.setCellValueFactory(     d -> new SimpleStringProperty(d.getValue()[1]));
        pColUsername.setCellValueFactory( d -> new SimpleStringProperty(d.getValue()[2]));
        pColTotal.setCellValueFactory(    d -> new SimpleStringProperty(d.getValue()[3]));
        pColConfirmed.setCellValueFactory(d -> new SimpleStringProperty(d.getValue()[4]));
        pColCancelled.setCellValueFactory(d -> new SimpleStringProperty(d.getValue()[5]));
    }

    private void loadSummaryStats() {
        String[] stats = reportsDAO.getSummaryStats();
        confirmedLabel.setText(stats[0]);
        cancelledLabel.setText(stats[1]);
        revenueLabel.setText(stats[2]);
        passengersLabel.setText(stats[3]);
        flightsLabel.setText(stats[4]);
    }

    private void loadBookingReport() {
        List<String[]> data = reportsDAO.getBookingReport();
        ObservableList<String[]> items = FXCollections.observableArrayList(data);
        bookingTable.setItems(items);
        bookingCountLabel.setText("Total bookings: " + data.size());
    }

    private void loadRevenueReport() {
        List<String[]> data = reportsDAO.getRevenueReport();
        revenueTable.setItems(FXCollections.observableArrayList(data));
    }

    private void loadPassengerReport() {
        List<String[]> data = reportsDAO.getPassengerReport();
        passengerTable.setItems(FXCollections.observableArrayList(data));
    }

    @FXML
    private void goBack() {
        loadScreen("/com/example/airlinemanagementsystem/AdminDashboard.fxml");
    }

    private void loadScreen(String fxmlPath) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(fxmlPath));
            Stage stage = (Stage) confirmedLabel.getScene().getWindow();
            stage.setScene(new Scene(root, 900, 600));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}