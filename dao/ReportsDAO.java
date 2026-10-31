package com.example.airlinemanagementsystem.dao;

import com.example.airlinemanagementsystem.utils.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReportsDAO {

    // Booking Report
    public List<String[]> getBookingReport() {
        List<String[]> data = new ArrayList<>();
        String sql = "SELECT b.booking_id, u.name, u.username, " +
                "f.flight_number, f.source, f.destination, f.date, " +
                "s.seat_number, b.seat_class, b.status, b.booking_date " +
                "FROM bookings b " +
                "JOIN users u ON b.user_id = u.user_id " +
                "JOIN flights f ON b.flight_id = f.flight_id " +
                "JOIN seats s ON b.seat_id = s.seat_id " +
                "ORDER BY b.booking_date DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                data.add(new String[]{
                        rs.getString("booking_id"),
                        rs.getString("name"),
                        rs.getString("username"),
                        rs.getString("flight_number"),
                        rs.getString("source") + " → " + rs.getString("destination"),
                        rs.getString("date"),
                        rs.getString("seat_number"),
                        rs.getString("seat_class"),
                        rs.getString("status"),
                        rs.getString("booking_date")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return data;
    }

    // Revenue Report
    public List<String[]> getRevenueReport() {
        List<String[]> data = new ArrayList<>();
        String sql = "SELECT f.flight_number, f.source, f.destination, f.date, " +
                "COUNT(b.booking_id) AS total_bookings, " +
                "SUM(p.amount) AS total_revenue " +
                "FROM flights f " +
                "LEFT JOIN bookings b ON f.flight_id = b.flight_id " +
                "AND b.status = 'Confirmed' " +
                "LEFT JOIN payments p ON b.booking_id = p.booking_id " +
                "GROUP BY f.flight_id " +
                "ORDER BY total_revenue DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                data.add(new String[]{
                        rs.getString("flight_number"),
                        rs.getString("source") + " → " + rs.getString("destination"),
                        rs.getString("date"),
                        rs.getString("total_bookings"),
                        "BDT " + (rs.getString("total_revenue") != null
                                ? rs.getString("total_revenue") : "0.00")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return data;
    }

    // Passenger Report
    public List<String[]> getPassengerReport() {
        List<String[]> data = new ArrayList<>();
        String sql = "SELECT u.user_id, u.name, u.username, " +
                "COUNT(b.booking_id) AS total_bookings, " +
                "SUM(CASE WHEN b.status='Confirmed' THEN 1 ELSE 0 END) AS confirmed, " +
                "SUM(CASE WHEN b.status='Cancelled' THEN 1 ELSE 0 END) AS cancelled " +
                "FROM users u " +
                "LEFT JOIN bookings b ON u.user_id = b.user_id " +
                "WHERE u.role = 'User' " +
                "GROUP BY u.user_id " +
                "ORDER BY total_bookings DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                data.add(new String[]{
                        rs.getString("user_id"),
                        rs.getString("name"),
                        rs.getString("username"),
                        rs.getString("total_bookings"),
                        rs.getString("confirmed"),
                        rs.getString("cancelled")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return data;
    }

    // Summary stats
    public String[] getSummaryStats() {
        String sql = "SELECT " +
                "(SELECT COUNT(*) FROM bookings WHERE status='Confirmed') AS confirmed, " +
                "(SELECT COUNT(*) FROM bookings WHERE status='Cancelled') AS cancelled, " +
                "(SELECT COALESCE(SUM(amount),0) FROM payments) AS total_revenue, " +
                "(SELECT COUNT(*) FROM users WHERE role='User') AS total_passengers, " +
                "(SELECT COUNT(*) FROM flights) AS total_flights";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new String[]{
                        rs.getString("confirmed"),
                        rs.getString("cancelled"),
                        "BDT " + rs.getString("total_revenue"),
                        rs.getString("total_passengers"),
                        rs.getString("total_flights")
                };
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new String[]{"0","0","BDT 0","0","0"};
    }
}