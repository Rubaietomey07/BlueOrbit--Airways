package com.example.airlinemanagementsystem.controllers;

import com.example.airlinemanagementsystem.dao.UserManagementDAO;
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

public class UserManagementController implements Initializable {

    @FXML private TextField              searchField;
    @FXML private TableView<String[]>    usersTable;
    @FXML private TableColumn<String[], String> colId;
    @FXML private TableColumn<String[], String> colName;
    @FXML private TableColumn<String[], String> colUsername;
    @FXML private TableColumn<String[], String> colRole;
    @FXML private TableColumn<String[], String> colStatus;
    @FXML private TableColumn<String[], String> colCreated;
    @FXML private Label                  tableMessageLabel;
    @FXML private Label                  selectedUserLabel;
    @FXML private ComboBox<String>       roleCombo;
    @FXML private Label                  actionMessageLabel;

    private final UserManagementDAO userManagementDAO = new UserManagementDAO();
    private int selectedUserId = -1;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        roleCombo.setItems(FXCollections.observableArrayList(
                "User", "Staff", "Admin"
        ));

        colId.setCellValueFactory(      d -> new SimpleStringProperty(d.getValue()[0]));
        colName.setCellValueFactory(    d -> new SimpleStringProperty(d.getValue()[1]));
        colUsername.setCellValueFactory(d -> new SimpleStringProperty(d.getValue()[2]));
        colRole.setCellValueFactory(    d -> new SimpleStringProperty(d.getValue()[3]));
        colStatus.setCellValueFactory(  d -> new SimpleStringProperty(d.getValue()[4]));
        colCreated.setCellValueFactory( d -> new SimpleStringProperty(d.getValue()[5]));

        loadAllUsers();

        // Fill selected user info on row click
        usersTable.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldVal, newVal) -> {
                    if (newVal != null) {
                        selectedUserId = Integer.parseInt(newVal[0]);
                        selectedUserLabel.setStyle(
                                "-fx-font-size: 13px; -fx-text-fill: #1a73e8;"
                        );
                        selectedUserLabel.setText(
                                "Selected: " + newVal[1] +
                                        " (" + newVal[2] + ")" +
                                        " | Role: " + newVal[3] +
                                        " | Status: " + newVal[4]
                        );
                        roleCombo.setValue(newVal[3]);
                        actionMessageLabel.setText("");
                    }
                }
        );
    }

    private void loadAllUsers() {
        List<String[]> users = userManagementDAO.getAllUsers();
        ObservableList<String[]> data = FXCollections.observableArrayList(users);
        usersTable.setItems(data);
        tableMessageLabel.setText("Total: " + users.size() + " users");
    }

    @FXML
    private void handleSearch() {
        String keyword = searchField.getText().trim();
        if (keyword.isEmpty()) {
            loadAllUsers();
            return;
        }
        List<String[]> results = userManagementDAO.searchUsers(keyword);
        usersTable.setItems(FXCollections.observableArrayList(results));
        tableMessageLabel.setText("Found: " + results.size() + " users");
    }

    @FXML
    private void handleShowAll() {
        searchField.clear();
        loadAllUsers();
    }

    @FXML
    private void handleAssignRole() {
        if (selectedUserId == -1) {
            showActionMessage("Please select a user first.", "red");
            return;
        }
        String role = roleCombo.getValue();
        if (role == null) {
            showActionMessage("Please select a role.", "red");
            return;
        }

        boolean success = userManagementDAO.updateUserRole(selectedUserId, role);
        if (success) {
            showActionMessage("Role updated to " + role + " successfully!", "green");
            loadAllUsers();
        } else {
            showActionMessage("Failed to update role.", "red");
        }
    }

    @FXML
    private void handleActivate() {
        if (selectedUserId == -1) {
            showActionMessage("Please select a user first.", "red");
            return;
        }
        boolean success = userManagementDAO.updateUserStatus(selectedUserId, "Active");
        if (success) {
            showActionMessage("User activated successfully!", "green");
            loadAllUsers();
        } else {
            showActionMessage("Failed to activate user.", "red");
        }
    }

    @FXML
    private void handleDeactivate() {
        if (selectedUserId == -1) {
            showActionMessage("Please select a user first.", "red");
            return;
        }
        boolean success = userManagementDAO.updateUserStatus(selectedUserId, "Inactive");
        if (success) {
            showActionMessage("User deactivated successfully!", "green");
            loadAllUsers();
        } else {
            showActionMessage("Failed to deactivate user.", "red");
        }
    }

    private void showActionMessage(String msg, String color) {
        actionMessageLabel.setStyle(
                "-fx-text-fill: " + color + "; -fx-font-size: 13px;"
        );
        actionMessageLabel.setText(msg);
    }

    @FXML
    private void goBack() {
        loadScreen("/com/example/airlinemanagementsystem/AdminDashboard.fxml");
    }

    private void loadScreen(String fxmlPath) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(fxmlPath));
            Stage stage = (Stage) searchField.getScene().getWindow();
            stage.setScene(new Scene(root, 900, 600));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}