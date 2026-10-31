package com.example.airlinemanagementsystem.controllers;

import com.example.airlinemanagementsystem.dao.BookingDAO;
import com.example.airlinemanagementsystem.dao.PaymentAccountDAO;
import com.example.airlinemanagementsystem.models.Flight;
import com.example.airlinemanagementsystem.utils.SessionManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.net.URL;
import java.util.ResourceBundle;

public class PaymentController implements Initializable {

    @FXML private Label        totalAmountLabel;
    @FXML private RadioButton  bankRadio;
    @FXML private RadioButton  cardRadio;
    @FXML private RadioButton  bkashRadio;
    @FXML private VBox         accountDetailsBox;
    @FXML private Label        accountTypeLabel;
    @FXML private Label        accountNumberLabel;
    @FXML private TextField    accountNumberField;
    @FXML private PasswordField pinField;
    @FXML private Label        paymentMessageLabel;

    private ToggleGroup methodGroup;
    private String selectedMethod = null;

    private final PaymentAccountDAO paymentAccountDAO = new PaymentAccountDAO();
    private final BookingDAO        bookingDAO        = new BookingDAO();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Show total amount
        double total = SessionManager.getInstance().getTotalAmount();
        totalAmountLabel.setText(String.format("BDT %.2f", total));

        // Group radio buttons
        methodGroup = new ToggleGroup();
        bankRadio.setToggleGroup(methodGroup);
        cardRadio.setToggleGroup(methodGroup);
        bkashRadio.setToggleGroup(methodGroup);

        // Hide account details until method selected
        accountDetailsBox.setVisible(false);
        accountDetailsBox.setManaged(false);
    }

    @FXML
    private void handleMethodSelection() {
        if (bankRadio.isSelected()) {
            selectedMethod = "BankAccount";
            accountTypeLabel.setText("Enter Bank Account Details");
            accountNumberLabel.setText("Account Number");
        } else if (cardRadio.isSelected()) {
            selectedMethod = "Card";
            accountTypeLabel.setText("Enter Card Details");
            accountNumberLabel.setText("Card Number");
        } else if (bkashRadio.isSelected()) {
            selectedMethod = "Bkash";
            accountTypeLabel.setText("Enter Bkash Details");
            accountNumberLabel.setText("Bkash Number");
        }

        accountDetailsBox.setVisible(true);
        accountDetailsBox.setManaged(true);
        paymentMessageLabel.setText("");
        accountNumberField.clear();
        pinField.clear();
    }

    @FXML
    private void handlePayment() {
        paymentMessageLabel.setText("");

        if (selectedMethod == null) {
            showError("Please select a payment method.");
            return;
        }

        String accountNumber = accountNumberField.getText().trim();
        String pin           = pinField.getText().trim();

        if (accountNumber.isEmpty() || pin.isEmpty()) {
            showError("Please enter account number and PIN.");
            return;
        }

        SessionManager session = SessionManager.getInstance();
        int userId = session.getUserId();

        // Check if account exists
        boolean accountExists = paymentAccountDAO.accountExists(
                userId, selectedMethod, accountNumber
        );

        if (accountExists) {
            // Verify PIN
            boolean pinValid = paymentAccountDAO.verifyPin(
                    userId, selectedMethod, accountNumber, pin
            );
            if (!pinValid) {
                showError("Incorrect PIN. Please try again.");
                return;
            }
        } else {
            // Save new account with PIN
            paymentAccountDAO.saveAccount(userId, selectedMethod, accountNumber, pin);
        }

        // Create booking
        Flight flight     = session.getSelectedFlight();
        String seatClass  = session.getSelectedClass();
        String seatNumber = session.getSelectedSeatNumber();
        double amount     = session.getTotalAmount();

        int bookingId = bookingDAO.createBooking(
                userId, flight.getFlightId(), seatClass, seatNumber, selectedMethod, amount
        );

        if (bookingId > 0) {
            session.setLastBookingId(bookingId);
            loadScreen("/com/example/airlinemanagementsystem/TicketGenerated.fxml");
        } else {
            showError("Payment failed. Please try again.");
        }
    }

    private void showError(String msg) {
        paymentMessageLabel.setStyle("-fx-text-fill: red; -fx-font-size: 13px;");
        paymentMessageLabel.setText(msg);
    }

    @FXML
    private void goBack() {
        loadScreen("/com/example/airlinemanagementsystem/PriceDetails.fxml");
    }

    private void loadScreen(String fxmlPath) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(fxmlPath));
            Stage stage = (Stage) totalAmountLabel.getScene().getWindow();
            stage.setScene(new Scene(root, 900, 600));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}