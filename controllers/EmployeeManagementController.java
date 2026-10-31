package com.example.airlinemanagementsystem.controllers;

import com.example.airlinemanagementsystem.dao.EmployeeManagementDAO;
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
import java.util.Optional;
import java.util.ResourceBundle;

public class EmployeeManagementController implements Initializable {

    @FXML private TextField              nameField;
    @FXML private TextField              usernameField;
    @FXML private TextField              passwordField;
    @FXML private ComboBox<String>       roleCombo;
    @FXML private TextField              departmentField;
    @FXML private ComboBox<String>       statusCombo;
    @FXML private Label                  formMessageLabel;
    @FXML private TableView<String[]>    employeesTable;
    @FXML private TableColumn<String[], String> colEmpId;
    @FXML private TableColumn<String[], String> colName;
    @FXML private TableColumn<String[], String> colUsername;
    @FXML private TableColumn<String[], String> colRole;
    @FXML private TableColumn<String[], String> colDept;
    @FXML private TableColumn<String[], String> colStatus;
    @FXML private Label                  tableMessageLabel;

    private final EmployeeManagementDAO employeeDAO = new EmployeeManagementDAO();
    private int selectedEmployeeId = -1;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        roleCombo.setItems(FXCollections.observableArrayList("Staff", "Admin"));
        statusCombo.setItems(FXCollections.observableArrayList("Active", "Inactive"));

        colEmpId.setCellValueFactory(  data -> new SimpleStringProperty(data.getValue()[0]));
        colName.setCellValueFactory(   data -> new SimpleStringProperty(data.getValue()[2]));
        colUsername.setCellValueFactory(data -> new SimpleStringProperty(data.getValue()[3]));
        colRole.setCellValueFactory(   data -> new SimpleStringProperty(data.getValue()[4]));
        colDept.setCellValueFactory(   data -> new SimpleStringProperty(data.getValue()[5]));
        colStatus.setCellValueFactory( data -> new SimpleStringProperty(data.getValue()[6]));

        loadAllEmployees();

        // Fill form on row click
        employeesTable.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldVal, newVal) -> {
                    if (newVal != null) {
                        selectedEmployeeId = Integer.parseInt(newVal[0]);
                        nameField.setText(newVal[2]);
                        usernameField.setText(newVal[3]);
                        passwordField.clear();
                        roleCombo.setValue(newVal[4]);
                        departmentField.setText(newVal[5]);
                        statusCombo.setValue(newVal[6]);
                    }
                }
        );
    }

    private void loadAllEmployees() {
        List<String[]> employees = employeeDAO.getAllEmployees();
        ObservableList<String[]> data = FXCollections.observableArrayList(employees);
        employeesTable.setItems(data);
        tableMessageLabel.setText("Total: " + employees.size() + " employees");
    }

    @FXML
    private void handleAdd() {
        String name       = nameField.getText().trim();
        String username   = usernameField.getText().trim();
        String password   = passwordField.getText().trim();
        String role       = roleCombo.getValue();
        String department = departmentField.getText().trim();

        if (name.isEmpty() || username.isEmpty() || password.isEmpty()
                || role == null || department.isEmpty()) {
            showFormMessage("Please fill all fields.", "red");
            return;
        }

        boolean success = employeeDAO.addEmployee(
                name, username, password, role, department
        );
        if (success) {
            showFormMessage("Employee added successfully!", "green");
            handleClear();
            loadAllEmployees();
        } else {
            showFormMessage("Username already exists.", "red");
        }
    }

    @FXML
    private void handleUpdate() {
        if (selectedEmployeeId == -1) {
            showFormMessage("Please select an employee first.", "red");
            return;
        }
        String name       = nameField.getText().trim();
        String department = departmentField.getText().trim();
        String status     = statusCombo.getValue();

        if (name.isEmpty() || department.isEmpty() || status == null) {
            showFormMessage("Please fill all fields.", "red");
            return;
        }

        boolean success = employeeDAO.updateEmployee(
                selectedEmployeeId, name, department, status
        );
        if (success) {
            showFormMessage("Employee updated successfully!", "green");
            handleClear();
            loadAllEmployees();
        } else {
            showFormMessage("Update failed.", "red");
        }
    }

    @FXML
    private void handleDelete() {
        if (selectedEmployeeId == -1) {
            showFormMessage("Please select an employee first.", "red");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Employee");
        alert.setHeaderText("Delete this employee?");
        alert.setContentText("This will also delete their user account.");
        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            boolean success = employeeDAO.deleteEmployee(selectedEmployeeId);
            if (success) {
                showFormMessage("Employee deleted successfully!", "green");
                handleClear();
                loadAllEmployees();
            } else {
                showFormMessage("Delete failed.", "red");
            }
        }
    }

    @FXML
    private void handleClear() {
        nameField.clear();
        usernameField.clear();
        passwordField.clear();
        roleCombo.setValue(null);
        departmentField.clear();
        statusCombo.setValue(null);
        selectedEmployeeId = -1;
        formMessageLabel.setText("");
        employeesTable.getSelectionModel().clearSelection();
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
            Stage stage = (Stage) nameField.getScene().getWindow();
            stage.setScene(new Scene(root, 900, 600));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}