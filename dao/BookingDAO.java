package com.example.airlinemanagementsystem.dao;

import com.example.airlinemanagementsystem.utils.DatabaseConnection;
import java.sql.*;
import com.example.airlinemanagementsystem.models.Booking;
import java.util.ArrayList;
import java.util.List;

public class BookingDAO {

    public int createBooking(int userId, int flightId, String seatClass,
                             String seatNumber, String paymentMethod, double amount) {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            // Get seat id
            int seatId = getSeatId(conn, flightId, seatNumber);
            if (seatId == -1) return -1;

            // Create booking
            String bookingSql = "INSERT INTO bookings " +
                    "(user_id, flight_id, seat_id, seat_class) VALUES (?,?,?,?)";
            int bookingId = -1;
            try (PreparedStatement ps = conn.prepareStatement(
                    bookingSql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, userId);
                ps.setInt(2, flightId);
                ps.setInt(3, seatId);
                ps.setString(4, seatClass);
                ps.executeUpdate();
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) bookingId = rs.getInt(1);
            }

            if (bookingId == -1) { conn.rollback(); return -1; }

            // Create payment
            String paymentSql = "INSERT INTO payments " +
                    "(booking_id, payment_method, amount) VALUES (?,?,?)";
            try (PreparedStatement ps = conn.prepareStatement(paymentSql)) {
                ps.setInt(1, bookingId);
                ps.setString(2, paymentMethod);
                ps.setDouble(3, amount);
                ps.executeUpdate();
            }

            // Mark seat as booked
            String seatSql = "UPDATE seats SET status = 'Booked' WHERE seat_id = ?";
            try (PreparedStatement ps = conn.prepareStatement(seatSql)) {
                ps.setInt(1, seatId);
                ps.executeUpdate();
            }

            conn.commit();
            return bookingId;

        } catch (SQLException e) {
            try { if (conn != null) conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            e.printStackTrace();
        } finally {
            try { if (conn != null) conn.setAutoCommit(true); } catch (SQLException e) { e.printStackTrace(); }
        }
        return -1;
    }

    private int getSeatId(Connection conn, int flightId, String seatNumber) {
        String sql = "SELECT seat_id FROM seats WHERE flight_id = ? " +
                "AND seat_number = ? AND status = 'Available'";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, flightId);
            ps.setString(2, seatNumber);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt("seat_id");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }
    public List<Booking> getUserBookings(int userId) {
        List<Booking> bookings = new ArrayList<>();
        String sql = "SELECT b.booking_id, f.flight_number, f.source, f.destination, " +
                "f.date, s.seat_number, b.seat_class, p.amount, " +
                "p.payment_method, b.status, b.booking_date " +
                "FROM bookings b " +
                "JOIN flights f ON b.flight_id = f.flight_id " +
                "JOIN seats s ON b.seat_id = s.seat_id " +
                "JOIN payments p ON p.booking_id = b.booking_id " +
                "WHERE b.user_id = ? ORDER BY b.booking_date DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Booking b = new Booking();
                b.setBookingId(rs.getInt("booking_id"));
                b.setFlightNumber(rs.getString("flight_number"));
                b.setSource(rs.getString("source"));
                b.setDestination(rs.getString("destination"));
                b.setDate(rs.getString("date"));
                b.setSeatNumber(rs.getString("seat_number"));
                b.setSeatClass(rs.getString("seat_class"));
                b.setAmount(rs.getDouble("amount"));
                b.setPaymentMethod(rs.getString("payment_method"));
                b.setStatus(rs.getString("status"));
                b.setBookingDate(rs.getString("booking_date"));
                bookings.add(b);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return bookings;
    }

    public boolean cancelBooking(int bookingId, int userId) {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            // Get seat_id first
            int seatId = -1;
            String getSeatSql = "SELECT seat_id FROM bookings " +
                    "WHERE booking_id = ? AND user_id = ? AND status = 'Confirmed'";
            try (PreparedStatement ps = conn.prepareStatement(getSeatSql)) {
                ps.setInt(1, bookingId);
                ps.setInt(2, userId);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) seatId = rs.getInt("seat_id");
            }

            if (seatId == -1) { conn.rollback(); return false; }

            // Cancel booking
            String cancelSql = "UPDATE bookings SET status = 'Cancelled' " +
                    "WHERE booking_id = ? AND user_id = ?";
            try (PreparedStatement ps = conn.prepareStatement(cancelSql)) {
                ps.setInt(1, bookingId);
                ps.setInt(2, userId);
                ps.executeUpdate();
            }

            // Free up seat
            String freeSeatSql = "UPDATE seats SET status = 'Available' WHERE seat_id = ?";
            try (PreparedStatement ps = conn.prepareStatement(freeSeatSql)) {
                ps.setInt(1, seatId);
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
    public boolean deleteBooking(int bookingId, int userId) {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            // Verify it's cancelled and belongs to this user
            String checkSql = "SELECT booking_id FROM bookings " +
                    "WHERE booking_id = ? AND user_id = ? AND status = 'Cancelled'";
            try (PreparedStatement ps = conn.prepareStatement(checkSql)) {
                ps.setInt(1, bookingId);
                ps.setInt(2, userId);
                if (!ps.executeQuery().next()) {
                    conn.rollback();
                    return false;
                }
            }

            // Delete payment first (foreign key constraint)
            String deletePaymentSql = "DELETE FROM payments WHERE booking_id = ?";
            try (PreparedStatement ps = conn.prepareStatement(deletePaymentSql)) {
                ps.setInt(1, bookingId);
                ps.executeUpdate();
            }

            // Then delete booking
            String deleteBookingSql = "DELETE FROM bookings WHERE booking_id = ? AND user_id = ?";
            try (PreparedStatement ps = conn.prepareStatement(deleteBookingSql)) {
                ps.setInt(1, bookingId);
                ps.setInt(2, userId);
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
    public int getFlightIdByBooking(int bookingId) {
        String sql = "SELECT flight_id FROM bookings WHERE booking_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, bookingId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt("flight_id");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public boolean changeSeat(int bookingId, String newClass, String newSeatNumber) {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            // Get old seat_id
            int oldSeatId = -1;
            String getOldSql = "SELECT seat_id FROM bookings WHERE booking_id = ?";
            try (PreparedStatement ps = conn.prepareStatement(getOldSql)) {
                ps.setInt(1, bookingId);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) oldSeatId = rs.getInt("seat_id");
            }
            if (oldSeatId == -1) { conn.rollback(); return false; }

            // Get flight_id
            int flightId = getFlightIdByBooking(bookingId);

            // Get new seat_id
            int newSeatId = -1;
            String getNewSql = "SELECT seat_id FROM seats WHERE flight_id = ? " +
                    "AND seat_number = ? AND status = 'Available'";
            try (PreparedStatement ps = conn.prepareStatement(getNewSql)) {
                ps.setInt(1, flightId);
                ps.setString(2, newSeatNumber);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) newSeatId = rs.getInt("seat_id");
            }
            if (newSeatId == -1) { conn.rollback(); return false; }

            // Update booking
            String updateBooking = "UPDATE bookings SET seat_id = ?, seat_class = ? " +
                    "WHERE booking_id = ?";
            try (PreparedStatement ps = conn.prepareStatement(updateBooking)) {
                ps.setInt(1, newSeatId);
                ps.setString(2, newClass);
                ps.setInt(3, bookingId);
                ps.executeUpdate();
            }

            // Free old seat
            String freeOld = "UPDATE seats SET status = 'Available' WHERE seat_id = ?";
            try (PreparedStatement ps = conn.prepareStatement(freeOld)) {
                ps.setInt(1, oldSeatId);
                ps.executeUpdate();
            }

            // Book new seat
            String bookNew = "UPDATE seats SET status = 'Booked' WHERE seat_id = ?";
            try (PreparedStatement ps = conn.prepareStatement(bookNew)) {
                ps.setInt(1, newSeatId);
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