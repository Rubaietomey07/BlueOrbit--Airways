package com.example.airlinemanagementsystem.dao;

import com.example.airlinemanagementsystem.models.Seat;
import com.example.airlinemanagementsystem.utils.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SeatDAO {

    public List<Seat> getAvailableSeats(int flightId, String seatClass) {
        List<Seat> seats = new ArrayList<>();
        String sql = "SELECT * FROM seats WHERE flight_id = ? " +
                "AND seat_class = ? AND status = 'Available'";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, flightId);
            ps.setString(2, seatClass);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Seat seat = new Seat();
                seat.setSeatId(rs.getInt("seat_id"));
                seat.setFlightId(rs.getInt("flight_id"));
                seat.setSeatNumber(rs.getString("seat_number"));
                seat.setSeatClass(rs.getString("seat_class"));
                seat.setStatus(rs.getString("status"));
                seats.add(seat);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return seats;
    }

    public Seat getSeatByNumber(int flightId, String seatNumber) {
        String sql = "SELECT * FROM seats WHERE flight_id = ? AND seat_number = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, flightId);
            ps.setString(2, seatNumber);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                Seat seat = new Seat();
                seat.setSeatId(rs.getInt("seat_id"));
                seat.setFlightId(rs.getInt("flight_id"));
                seat.setSeatNumber(rs.getString("seat_number"));
                seat.setSeatClass(rs.getString("seat_class"));
                seat.setStatus(rs.getString("status"));
                return seat;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}