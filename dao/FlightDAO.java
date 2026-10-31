package com.example.airlinemanagementsystem.dao;

import com.example.airlinemanagementsystem.models.Flight;
import com.example.airlinemanagementsystem.utils.DatabaseConnection;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.sql.Date;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class FlightDAO {

    public List<Flight> searchFlights(String source, String destination, LocalDate date) {
        List<Flight> flights = new ArrayList<>();

        String sql = "SELECT * FROM flights WHERE " +
                "LOWER(source) = LOWER(?) AND " +
                "LOWER(destination) = LOWER(?) AND " +
                "date = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, source);
            ps.setString(2, destination);
            ps.setDate(3, Date.valueOf(date));

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Flight flight = new Flight();
                flight.setFlightId(rs.getInt("flight_id"));
                flight.setFlightNumber(rs.getString("flight_number"));
                flight.setSource(rs.getString("source"));
                flight.setDestination(rs.getString("destination"));
                flight.setDate(rs.getDate("date").toLocalDate());
                flight.setEconomyPrice(rs.getDouble("economy_price"));
                flight.setBusinessPrice(rs.getDouble("business_price"));
                flight.setFirstPrice(rs.getDouble("first_price"));
                flights.add(flight);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return flights;
    }

    public List<String> getAllSources() {
        List<String> sources = new ArrayList<>();
        String sql = "SELECT DISTINCT source FROM flights ORDER BY source";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) sources.add(rs.getString("source"));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return sources;
    }

    public List<String> getAllDestinations() {
        List<String> destinations = new ArrayList<>();
        String sql = "SELECT DISTINCT destination FROM flights ORDER BY destination";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) destinations.add(rs.getString("destination"));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return destinations;
    }
    public List<Flight> getAllFlights() {
        List<Flight> flights = new ArrayList<>();
        String sql = "SELECT * FROM flights ORDER BY date, source";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Flight f = new Flight();
                f.setFlightId(rs.getInt("flight_id"));
                f.setFlightNumber(rs.getString("flight_number"));
                f.setSource(rs.getString("source"));
                f.setDestination(rs.getString("destination"));
                f.setDate(rs.getDate("date").toLocalDate());
                f.setEconomyPrice(rs.getDouble("economy_price"));
                f.setBusinessPrice(rs.getDouble("business_price"));
                f.setFirstPrice(rs.getDouble("first_price"));
                flights.add(f);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return flights;
    }

    public boolean addFlight(String flightNumber, String source, String destination,
                             LocalDate date, double economy, double business, double first) {
        String sql = "INSERT INTO flights (flight_number, source, destination, date, " +
                "economy_price, business_price, first_price) VALUES (?,?,?,?,?,?,?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql,
                     Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, flightNumber);
            ps.setString(2, source);
            ps.setString(3, destination);
            ps.setDate(4, Date.valueOf(date));
            ps.setDouble(5, economy);
            ps.setDouble(6, business);
            ps.setDouble(7, first);
            ps.executeUpdate();

            // Auto generate seats
            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) {
                int flightId = keys.getInt(1);
                generateSeats(conn, flightId, economy, business, first);
            }
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void generateSeats(Connection conn, int flightId,
                               double economy, double business, double first)
            throws SQLException {
        String sql = "INSERT INTO seats (flight_id, seat_number, seat_class) VALUES (?,?,?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            String[] economySeats  = {"3A","3B","3C","4A","4B","4C","5A","5B","5C"};
            String[] businessSeats = {"2A","2B","2C"};
            String[] firstSeats    = {"1A","1B","1C"};

            if (economy > 0) {
                for (String s : economySeats) {
                    ps.setInt(1, flightId);
                    ps.setString(2, s);
                    ps.setString(3, "Economy");
                    ps.addBatch();
                }
            }
            if (business > 0) {
                for (String s : businessSeats) {
                    ps.setInt(1, flightId);
                    ps.setString(2, s);
                    ps.setString(3, "Business");
                    ps.addBatch();
                }
            }
            if (first > 0) {
                for (String s : firstSeats) {
                    ps.setInt(1, flightId);
                    ps.setString(2, s);
                    ps.setString(3, "First");
                    ps.addBatch();
                }
            }
            ps.executeBatch();
        }
    }

    public boolean updateFlight(int flightId, String source, String destination,
                                LocalDate date, double economy, double business, double first) {
        String sql = "UPDATE flights SET source=?, destination=?, date=?, " +
                "economy_price=?, business_price=?, first_price=? " +
                "WHERE flight_id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, source);
            ps.setString(2, destination);
            ps.setDate(3, Date.valueOf(date));
            ps.setDouble(4, economy);
            ps.setDouble(5, business);
            ps.setDouble(6, first);
            ps.setInt(7, flightId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean deleteFlight(int flightId) {
        String sql = "DELETE FROM flights WHERE flight_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, flightId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}