package com.carsharing.dao;

import com.carsharing.model.RideBooking;
import com.carsharing.util.DbConnectionManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class RideBookingDAO {

    /**
     * Inserts a booking inside transaction context.
     */
    public boolean insert(Connection conn, RideBooking booking) throws SQLException {
        String sql = "INSERT INTO ride_bookings (ride_id, passenger_id, seats_booked, total_price, status) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, booking.getRideId());
            ps.setInt(2, booking.getPassengerId());
            ps.setInt(3, booking.getSeatsBooked());
            ps.setBigDecimal(4, booking.getTotalPrice());
            ps.setString(5, booking.getStatus() != null ? booking.getStatus() : "CONFIRMED");
            
            int rows = ps.executeUpdate();
            if (rows > 0) {
                try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        booking.setId(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
        }
        return false;
    }

    /**
     * Finds booking by ID with joined details.
     */
    public RideBooking findById(int id) {
        String sql = "SELECT b.*, u.name AS passenger_name, u.phone AS passenger_phone, r.source, r.destination, r.ride_date, r.ride_time, r.price_per_seat, d.name AS driver_name, d.phone AS driver_phone " +
                     "FROM ride_bookings b " +
                     "JOIN users u ON b.passenger_id = u.id " +
                     "JOIN rides r ON b.ride_id = r.id " +
                     "JOIN users d ON r.driver_id = d.id " +
                     "WHERE b.id = ?";
        try (Connection conn = DbConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return extractBookingFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Finds bookings created by passenger X.
     */
    public List<RideBooking> findByPassengerId(int passengerId) {
        List<RideBooking> list = new ArrayList<>();
        String sql = "SELECT b.*, u.name AS passenger_name, u.phone AS passenger_phone, r.source, r.destination, r.ride_date, r.ride_time, r.price_per_seat, d.name AS driver_name, d.phone AS driver_phone " +
                     "FROM ride_bookings b " +
                     "JOIN users u ON b.passenger_id = u.id " +
                     "JOIN rides r ON b.ride_id = r.id " +
                     "JOIN users d ON r.driver_id = d.id " +
                     "WHERE b.passenger_id = ? " +
                     "ORDER BY b.id DESC";
        try (Connection conn = DbConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, passengerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(extractBookingFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Updates booking status inside transaction.
     */
    public boolean updateStatus(Connection conn, int id, String status) throws SQLException {
        String sql = "UPDATE ride_bookings SET status = ? WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status.toUpperCase());
            ps.setInt(2, id);
            return ps.executeUpdate() > 0;
        }
    }

    private RideBooking extractBookingFromResultSet(ResultSet rs) throws SQLException {
        RideBooking b = new RideBooking(
            rs.getInt("id"),
            rs.getInt("ride_id"),
            rs.getInt("passenger_id"),
            rs.getInt("seats_booked"),
            rs.getBigDecimal("total_price"),
            rs.getString("status"),
            rs.getTimestamp("created_at")
        );
        
        try {
            b.setPassengerName(rs.getString("passenger_name"));
            b.setPassengerPhone(rs.getString("passenger_phone"));
            b.setSource(rs.getString("source"));
            b.setDestination(rs.getString("destination"));
            b.setRideDate(rs.getDate("ride_date"));
            b.setRideTime(rs.getTime("ride_time"));
            b.setPricePerSeat(rs.getBigDecimal("price_per_seat"));
            b.setDriverName(rs.getString("driver_name"));
            b.setDriverPhone(rs.getString("driver_phone"));
        } catch (SQLException ignored) {
        }
        
        return b;
    }
}
