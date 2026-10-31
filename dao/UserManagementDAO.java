package com.example.airlinemanagementsystem.dao;

import com.example.airlinemanagementsystem.utils.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserManagementDAO {

    public List<String[]> getAllUsers() {
        List<String[]> users = new ArrayList<>();
        String sql = "SELECT user_id, name, username, role, status, created_at " +
                "FROM users ORDER BY user_id";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                users.add(new String[]{
                        rs.getString("user_id"),
                        rs.getString("name"),
                        rs.getString("username"),
                        rs.getString("role"),
                        rs.getString("status"),
                        rs.getString("created_at")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }

    public boolean updateUserRole(int userId, String role) {
        String sql = "UPDATE users SET role = ? WHERE user_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, role);
            ps.setInt(2, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateUserStatus(int userId, String status) {
        String sql = "UPDATE users SET status = ? WHERE user_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<String[]> searchUsers(String keyword) {
        List<String[]> users = new ArrayList<>();
        String sql = "SELECT user_id, name, username, role, status, created_at " +
                "FROM users WHERE name LIKE ? OR username LIKE ? " +
                "ORDER BY user_id";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "%" + keyword + "%");
            ps.setString(2, "%" + keyword + "%");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                users.add(new String[]{
                        rs.getString("user_id"),
                        rs.getString("name"),
                        rs.getString("username"),
                        rs.getString("role"),
                        rs.getString("status"),
                        rs.getString("created_at")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }
}