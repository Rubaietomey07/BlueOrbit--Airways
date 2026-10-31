package com.example.airlinemanagementsystem.dao;

import com.example.airlinemanagementsystem.utils.DatabaseConnection;

import java.sql.*;

public class EmployeeDAO {

    // Returns discount percent: 100 for Admin, 70 for Staff, 0 if invalid
    public int getEmployeeDiscount(int employeeId) {
        String sql = "SELECT e.role FROM employees e " +
                "WHERE e.employee_id = ? AND e.status = 'Active'";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, employeeId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String role = rs.getString("role");
                return role.equals("Admin") ? 100 : 70;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0; // invalid employee id
    }
}