package com.example.airlinemanagementsystem.controllers;

import com.example.airlinemanagementsystem.dao.CouponManagementDAO;
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
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class CouponManagementController implements Initializable {

    @FXML private TextField           codeField;
    @FXML private TextField           discountField;
    @FXML private DatePicker          expiryDatePicker;
    @FXML private ComboBox<String>    statusCombo;
    @FXML private Label               formMessageLabel;
    @FXML private TableView<String[]> couponsTable;
    @FXML private TableColumn<String[], String> colId;
    @FXML private TableColumn<String[], String> colCode;
    @FXML private TableColumn<String[], String> colDiscount;
    @FXML private TableColumn<String[], String> colExpiry;
    @FXML private TableColumn<String[], String> colStatus;
    @FXML private Label               tableMessageLabel;

    private final CouponManagementDAO couponDAO = new CouponManagementDAO();
    private int selectedCouponId = -1;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        statusCombo.setItems(FXCollections.observableArrayList("Active", "Inactive"));

        colId.setCellValueFactory(       data -> new SimpleStringProperty(data.getValue()[0]));
        colCode.setCellValueFactory(     data -> new SimpleStringProperty(data.getValue()[1]));
        colDiscount.setCellValueFactory( data -> new SimpleStringProperty(data.getValue()[2]));
        colExpiry.setCellValueFactory(   data -> new SimpleStringProperty(data.getValue()[3]));
        colStatus.setCellValueFactory(   data -> new SimpleStringProperty(data.getValue()[4]));

        loadAllCoupons();

        // Fill form on row click
        couponsTable.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldVal, newVal) -> {
                    if (newVal != null) {
                        selectedCouponId = Integer.parseInt(newVal[0]);
                        codeField.setText(newVal[1]);
                        discountField.setText(newVal[2].replace("%", ""));
                        if (newVal[3] != null && !newVal[3].equals("null")) {
                            expiryDatePicker.setValue(LocalDate.parse(newVal[3]));
                        }
                        statusCombo.setValue(newVal[4]);
                    }
                }
        );
    }

    private void loadAllCoupons() {
        List<String[]> coupons = couponDAO.getAllCoupons();
        ObservableList<String[]> data = FXCollections.observableArrayList(coupons);
        couponsTable.setItems(data);
        tableMessageLabel.setText("Total: " + coupons.size() + " coupons");
    }

    @FXML
    private void handleAdd() {
        String code   = codeField.getText().trim().toUpperCase();
        String discountText = discountField.getText().trim();
        LocalDate expiry    = expiryDatePicker.getValue();
        String status       = statusCombo.getValue();

        if (code.isEmpty() || discountText.isEmpty() ||
                expiry == null || status == null) {
            showFormMessage("Please fill all fields.", "red");
            return;
        }

        try {
            int discount = Integer.parseInt(discountText);
            if (discount < 1 || discount > 100) {
                showFormMessage("Discount must be between 1 and 100.", "red");
                return;
            }
            boolean success = couponDAO.addCoupon(code, discount, expiry, status);
            if (success) {
                showFormMessage("Coupon added successfully!", "green");
                handleClear();
                loadAllCoupons();
            } else {
                showFormMessage("Coupon code already exists.", "red");
            }
        } catch (NumberFormatException e) {
            showFormMessage("Discount must be a number.", "red");
        }
    }

    @FXML
    private void handleUpdate() {
        if (selectedCouponId == -1) {
            showFormMessage("Please select a coupon first.", "red");
            return;
        }
        String discountText = discountField.getText().trim();
        LocalDate expiry    = expiryDatePicker.getValue();
        String status       = statusCombo.getValue();

        if (discountText.isEmpty() || expiry == null || status == null) {
            showFormMessage("Please fill all fields.", "red");
            return;
        }

        try {
            int discount = Integer.parseInt(discountText);
            boolean success = couponDAO.updateCoupon(
                    selectedCouponId, discount, expiry, status
            );
            if (success) {
                showFormMessage("Coupon updated successfully!", "green");
                handleClear();
                loadAllCoupons();
            } else {
                showFormMessage("Update failed.", "red");
            }
        } catch (NumberFormatException e) {
            showFormMessage("Discount must be a number.", "red");
        }
    }

    @FXML
    private void handleDelete() {
        if (selectedCouponId == -1) {
            showFormMessage("Please select a coupon first.", "red");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Coupon");
        alert.setHeaderText("Delete this coupon?");
        alert.setContentText("This action cannot be undone.");
        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            boolean success = couponDAO.deleteCoupon(selectedCouponId);
            if (success) {
                showFormMessage("Coupon deleted successfully!", "green");
                handleClear();
                loadAllCoupons();
            } else {
                showFormMessage("Delete failed.", "red");
            }
        }
    }

    @FXML
    private void handleClear() {
        codeField.clear();
        discountField.clear();
        expiryDatePicker.setValue(null);
        statusCombo.setValue(null);
        selectedCouponId = -1;
        formMessageLabel.setText("");
        couponsTable.getSelectionModel().clearSelection();
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
            Stage stage = (Stage) codeField.getScene().getWindow();
            stage.setScene(new Scene(root, 900, 600));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}