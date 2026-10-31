package com.example.airlinemanagementsystem.controllers;

import com.example.airlinemanagementsystem.dao.CouponDAO;
import com.example.airlinemanagementsystem.dao.EmployeeDAO;
import com.example.airlinemanagementsystem.models.Flight;
import com.example.airlinemanagementsystem.utils.SessionManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class PriceDetailsController {

    @FXML private Label     flightNumberLabel;
    @FXML private Label     routeLabel;
    @FXML private Label     dateLabel;
    @FXML private Label     seatLabel;
    @FXML private Label     classLabel;
    @FXML private Label     basePriceLabel;
    @FXML private Label     taxLabel;
    @FXML private Label     discountLabel;
    @FXML private Label     totalLabel;
    @FXML private TextField employeeIdField;
    @FXML private TextField couponField;
    @FXML private Label     discountMessageLabel;

    private final CouponDAO   couponDAO   = new CouponDAO();
    private final EmployeeDAO employeeDAO = new EmployeeDAO();

    private double basePrice      = 0;
    private double tax            = 0;
    private double discountAmount = 0;
    private double total          = 0;

    // Called manually after FXML loads
    public void initData() {
        SessionManager session = SessionManager.getInstance();
        Flight flight          = session.getSelectedFlight();
        String seatClass       = session.getSelectedClass();
        String seatNumber      = session.getSelectedSeatNumber();

        System.out.println("initData called");
        System.out.println("Flight: " + (flight != null ? flight.getFlightNumber() : "NULL"));
        System.out.println("Class: " + seatClass);
        System.out.println("Seat: " + seatNumber);

        if (flight == null) return;

        flightNumberLabel.setText("Flight: " + flight.getFlightNumber());
        routeLabel.setText("Route: " + flight.getSource() + " → " + flight.getDestination());
        dateLabel.setText("Date: " + flight.getDate().toString());
        seatLabel.setText("Seat: " + seatNumber);
        classLabel.setText("Class: " + seatClass);

        basePrice = switch (seatClass) {
            case "Business" -> flight.getBusinessPrice();
            case "First"    -> flight.getFirstPrice();
            default         -> flight.getEconomyPrice();
        };

        tax   = basePrice * 0.15;
        total = basePrice + tax;

        updatePriceDisplay();
    }

    @FXML
    private void applyEmployeeDiscount() {
        discountMessageLabel.setText("");
        String empIdText = employeeIdField.getText().trim();

        if (empIdText.isEmpty()) {
            discountMessageLabel.setStyle("-fx-text-fill: red;");
            discountMessageLabel.setText("Please enter an Employee ID.");
            return;
        }

        try {
            int empId           = Integer.parseInt(empIdText);
            int discountPercent = employeeDAO.getEmployeeDiscount(empId);

            if (discountPercent == 0) {
                discountMessageLabel.setStyle("-fx-text-fill: red;");
                discountMessageLabel.setText("Invalid Employee ID.");
            } else {
                discountAmount = (basePrice + tax) * discountPercent / 100.0;
                total          = (basePrice + tax) - discountAmount;
                SessionManager.getInstance().setDiscountAmount(discountAmount);
                SessionManager.getInstance().setTotalAmount(total);
                updatePriceDisplay();
                discountMessageLabel.setStyle("-fx-text-fill: green;");
                discountMessageLabel.setText("Employee discount of " + discountPercent + "% applied!");
            }
        } catch (NumberFormatException e) {
            discountMessageLabel.setStyle("-fx-text-fill: red;");
            discountMessageLabel.setText("Employee ID must be a number.");
        }
    }

    @FXML
    private void applyCouponDiscount() {
        discountMessageLabel.setText("");
        String code = couponField.getText().trim();

        if (code.isEmpty()) {
            discountMessageLabel.setStyle("-fx-text-fill: red;");
            discountMessageLabel.setText("Please enter a coupon code.");
            return;
        }

        int discountPercent = couponDAO.getCouponDiscount(code);

        if (discountPercent == 0) {
            discountMessageLabel.setStyle("-fx-text-fill: red;");
            discountMessageLabel.setText("Invalid or expired coupon code.");
        } else {
            discountAmount = (basePrice + tax) * discountPercent / 100.0;
            total          = (basePrice + tax) - discountAmount;
            SessionManager.getInstance().setDiscountAmount(discountAmount);
            SessionManager.getInstance().setTotalAmount(total);
            updatePriceDisplay();
            discountMessageLabel.setStyle("-fx-text-fill: green;");
            discountMessageLabel.setText("Coupon discount of " + discountPercent + "% applied!");
        }
    }

    private void updatePriceDisplay() {
        basePriceLabel.setText(String.format("BDT %.2f", basePrice));
        taxLabel.setText(String.format("BDT %.2f", tax));
        discountLabel.setText(String.format("- BDT %.2f", discountAmount));
        totalLabel.setText(String.format("BDT %.2f", total));
        SessionManager.getInstance().setTotalAmount(total);
    }

    @FXML
    private void handleProceed() {
        loadScreen("/com/example/airlinemanagementsystem/Payment.fxml");
    }

    @FXML
    private void goBack() {
        loadScreen("/com/example/airlinemanagementsystem/SelectSeat.fxml");
    }

    private void loadScreen(String fxmlPath) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(fxmlPath));
            Stage stage = (Stage) totalLabel.getScene().getWindow();
            stage.setScene(new Scene(root, 900, 600));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}