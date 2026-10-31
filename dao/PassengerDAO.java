package com.example.airlinemanagementsystem.dao;

import com.example.airlinemanagementsystem.models.User;
import com.example.airlinemanagementsystem.utils.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PassengerDAO {

    public List<User> getAllPassengers() {
        List<User> passengers = new ArrayList<>();
        String sql = "SELECT * FROM users WHERE role = 'User' ORDER BY name";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                User u = new User();
                u.setUserId(rs.getInt("user_id"));
                u.setName(rs.getString("name"));
                u.setUsername(rs.getString("username"));
                u.setStatus(rs.getString("status"));
                passengers.add(u);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return passengers;
    }

    public List<User> searchPassengers(String keyword) {
        List<User> passengers = new ArrayList<>();
        String sql = "SELECT * FROM users WHERE role = 'User' " +
                "AND (name LIKE ? OR username LIKE ?) ORDER BY name";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "%" + keyword + "%");
            ps.setString(2, "%" + keyword + "%");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                User u = new User();
                u.setUserId(rs.getInt("user_id"));
                u.setName(rs.getString("name"));
                u.setUsername(rs.getString("username"));
                u.setStatus(rs.getString("status"));
                passengers.add(u);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return passengers;
    }

    public boolean addPassenger(String name, String username, String password) {
        String checkSql = "SELECT user_id FROM users WHERE username = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(checkSql)) {
            ps.setString(1, username);
            if (ps.executeQuery().next()) return false;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

        String sql = "INSERT INTO users (name, username, password, role) VALUES (?,?,?,'User')";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setString(2, username);
            ps.setString(3, password);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updatePassenger(int userId, String name, String username, String password) {
        String sql = "UPDATE users SET name = ?, username = ?, password = ? WHERE user_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setString(2, username);
            ps.setString(3, password);
            ps.setInt(4, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}