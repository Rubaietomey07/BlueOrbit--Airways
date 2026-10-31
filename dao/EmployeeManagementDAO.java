package com.example.airlinemanagementsystem.dao;

import com.example.airlinemanagementsystem.utils.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EmployeeManagementDAO {

    public List<String[]> getAllEmployees() {
        List<String[]> employees = new ArrayList<>();
        String sql = "SELECT e.employee_id, u.user_id, u.name, u.username, " +
                "e.role, e.department, e.status " +
                "FROM employees e JOIN users u ON e.user_id = u.user_id " +
                "ORDER BY e.employee_id";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                employees.add(new String[]{
                        rs.getString("employee_id"),
                        rs.getString("user_id"),
                        rs.getString("name"),
                        rs.getString("username"),
                        rs.getString("role"),
                        rs.getString("department"),
                        rs.getString("status")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return employees;
    }

    public boolean addEmployee(String name, String username, String password,
                               String role, String department) {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            // Check username exists
            String checkSql = "SELECT user_id FROM users WHERE username = ?";
            try (PreparedStatement ps = conn.prepareStatement(checkSql)) {
                ps.setString(1, username);
                if (ps.executeQuery().next()) {
                    conn.rollback();
                    return false;
                }
            }

            // Insert user
            int userId = -1;
            String userSql = "INSERT INTO users (name, username, password, role) " +
                    "VALUES (?, ?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(
                    userSql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, name);
                ps.setString(2, username);
                ps.setString(3, password);
                ps.setString(4, role);
                ps.executeUpdate();
                ResultSet keys = ps.getGeneratedKeys();
                if (keys.next()) userId = keys.getInt(1);
            }

            if (userId == -1) { conn.rollback(); return false; }

            // Insert employee
            String empSql = "INSERT INTO employees (user_id, role, department) " +
                    "VALUES (?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(empSql)) {
                ps.setInt(1, userId);
                ps.setString(2, role);
                ps.setString(3, department);
                ps.executeUpdate();
            }

            conn.commit();
            return true;

        } catch (SQLException e) {
            try { if (conn != null) conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            e.printStackTrace();
        } finally {
            try { if (conn != null) conn.setAutoCommit(true); } catch (SQLException e) { e.printStackTrace(); }
        }
        return false;
    }

    public boolean updateEmployee(int employeeId, String name, String department,
                                  String status) {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            // Get user_id
            int userId = -1;
            String getSql = "SELECT user_id FROM employees WHERE employee_id = ?";
            try (PreparedStatement ps = conn.prepareStatement(getSql)) {
                ps.setInt(1, employeeId);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) userId = rs.getInt("user_id");
            }
            if (userId == -1) { conn.rollback(); return false; }

            // Update user name
            String userSql = "UPDATE users SET name = ? WHERE user_id = ?";
            try (PreparedStatement ps = conn.prepareStatement(userSql)) {
                ps.setString(1, name);
                ps.setInt(2, userId);
                ps.executeUpdate();
            }

            // Update employee
            String empSql = "UPDATE employees SET department = ?, status = ? " +
                    "WHERE employee_id = ?";
            try (PreparedStatement ps = conn.prepareStatement(empSql)) {
                ps.setString(1, department);
                ps.setString(2, status);
                ps.setInt(3, employeeId);
                ps.executeUpdate();
            }

            conn.commit();
            return true;

        } catch (SQLException e) {
            try { if (conn != null) conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            e.printStackTrace();
        } finally {
            try { if (conn != null) conn.setAutoCommit(true); } catch (SQLException e) { e.printStackTrace(); }
        }
        return false;
    }

    public boolean deleteEmployee(int employeeId) {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            // Get user_id
            int userId = -1;
            String getSql = "SELECT user_id FROM employees WHERE employee_id = ?";
            try (PreparedStatement ps = conn.prepareStatement(getSql)) {
                ps.setInt(1, employeeId);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) userId = rs.getInt("user_id");
            }
            if (userId == -1) { conn.rollback(); return false; }

            // Delete employee first
            String delEmpSql = "DELETE FROM employees WHERE employee_id = ?";
            try (PreparedStatement ps = conn.prepareStatement(delEmpSql)) {
                ps.setInt(1, employeeId);
                ps.executeUpdate();
            }

            // Delete user
            String delUserSql = "DELETE FROM users WHERE user_id = ?";
            try (PreparedStatement ps = conn.prepareStatement(delUserSql)) {
                ps.setInt(1, userId);
                ps.executeUpdate();
            }

            conn.commit();
            return true;

        } catch (SQLException e) {
            try { if (conn != null) conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            e.printStackTrace();
        } finally {
            try { if (conn != null) conn.setAutoCommit(true); } catch (SQLException e) { e.printStackTrace(); }
        }
        return false;
    }
}