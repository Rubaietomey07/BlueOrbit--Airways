package com.example.airlinemanagementsystem.dao;

import com.example.airlinemanagementsystem.utils.DatabaseConnection;
import java.sql.*;

public class TicketDAO {

    public String[] getBookingDetails(int bookingId) {
        String sql = "SELECT b.booking_id, u.name, f.flight_number, " +
                "f.source, f.destination, f.date, " +
                "s.seat_number, b.seat_class, " +
                "p.amount, p.payment_method " +
                "FROM bookings b " +
                "JOIN users u ON b.user_id = u.user_id " +
                "JOIN flights f ON b.flight_id = f.flight_id " +
                "JOIN seats s ON b.seat_id = s.seat_id " +
                "JOIN payments p ON p.booking_id = b.booking_id " +
                "WHERE b.booking_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, bookingId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new String[]{
                        String.valueOf(rs.getInt("booking_id")),
                        rs.getString("name"),
                        rs.getString("flight_number"),
                        rs.getString("source") + " → " + rs.getString("destination"),
                        rs.getString("date"),
                        rs.getString("seat_number"),
                        rs.getString("seat_class"),
                        "BDT " + rs.getString("amount"),
                        rs.getString("payment_method")
                };
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    public String getBookingStatus(int bookingId) {
        String sql = "SELECT status FROM bookings WHERE booking_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, bookingId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getString("status");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "Unknown";
    }
}