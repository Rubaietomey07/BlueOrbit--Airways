package com.example.airlinemanagementsystem.dao;

import com.example.airlinemanagementsystem.utils.DatabaseConnection;
import java.sql.*;

public class PaymentAccountDAO {

    public boolean accountExists(int userId, String paymentType, String accountNumber) {
        String sql = "SELECT account_id FROM payment_accounts " +
                "WHERE user_id = ? AND payment_type = ? AND account_number = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setString(2, paymentType);
            ps.setString(3, accountNumber);
            return ps.executeQuery().next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean verifyPin(int userId, String paymentType,
                             String accountNumber, String pin) {
        String sql = "SELECT account_id FROM payment_accounts " +
                "WHERE user_id = ? AND payment_type = ? " +
                "AND account_number = ? AND pin = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setString(2, paymentType);
            ps.setString(3, accountNumber);
            ps.setString(4, pin);
            return ps.executeQuery().next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void saveAccount(int userId, String paymentType,
                            String accountNumber, String pin) {
        String sql = "INSERT INTO payment_accounts " +
                "(user_id, payment_type, account_number, pin) VALUES (?,?,?,?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setString(2, paymentType);
            ps.setString(3, accountNumber);
            ps.setString(4, pin);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}