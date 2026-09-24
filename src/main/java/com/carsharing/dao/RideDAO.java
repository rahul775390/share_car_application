package com.carsharing.dao;

import com.carsharing.model.Ride;
import com.carsharing.util.DbConnectionManager;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

public class RideDAO {

    /**
     * Inserts a new ride.
     */
    public boolean insert(Ride ride) {
        String sql = "INSERT INTO rides (driver_id, vehicle_id, source, destination, ride_date, ride_time, total_seats, available_seats, price_per_seat, description, status) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DbConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            ps.setInt(1, ride.getDriverId());
            ps.setInt(2, ride.getVehicleId());
            ps.setString(3, ride.getSource());
            ps.setString(4, ride.getDestination());
            ps.setDate(5, ride.getRideDate());
            ps.setTime(6, ride.getRideTime());
            ps.setInt(7, ride.getTotalSeats());
            ps.setInt(8, ride.getAvailableSeats());
            ps.setBigDecimal(9, ride.getPricePerSeat());
            ps.setString(10, ride.getDescription());
            ps.setString(11, ride.getStatus() != null ? ride.getStatus() : "ACTIVE");
            
            int rows = ps.executeUpdate();
            if (rows > 0) {
                try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        ride.setId(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Finds a ride by ID with joined driver and vehicle details.
     */
    public Ride findById(int id) {
        String sql = "SELECT r.*, u.name AS driver_name, u.phone AS driver_phone, v.make AS vehicle_make, v.model AS vehicle_model, v.license_plate AS vehicle_plate " +
                     "FROM rides r " +
                     "JOIN users u ON r.driver_id = u.id " +
                     "JOIN vehicles v ON r.vehicle_id = v.id " +
                     "WHERE r.id = ?";
        try (Connection conn = DbConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return extractRideFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Finds active rides created by driver X.
     */
    public List<Ride> findByDriverId(int driverId) {
        List<Ride> list = new ArrayList<>();
        String sql = "SELECT r.*, u.name AS driver_name, u.phone AS driver_phone, v.make AS vehicle_make, v.model AS vehicle_model, v.license_plate AS vehicle_plate " +
                     "FROM rides r " +
                     "JOIN users u ON r.driver_id = u.id " +
                     "JOIN vehicles v ON r.vehicle_id = v.id " +
                     "WHERE r.driver_id = ? " +
                     "ORDER BY r.ride_date DESC, r.ride_time DESC";
        try (Connection conn = DbConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, driverId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(extractRideFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Searches active rides based on strict criteria and optional filters.
     */
    public List<Ride> searchRides(String source, String destination, Date date, 
                                  BigDecimal maxPrice, Integer minSeats, Time maxTime) {
        List<Ride> list = new ArrayList<>();
        
        StringBuilder sb = new StringBuilder(
            "SELECT r.*, u.name AS driver_name, u.phone AS driver_phone, v.make AS vehicle_make, v.model AS vehicle_model, v.license_plate AS vehicle_plate " +
            "FROM rides r " +
            "JOIN users u ON r.driver_id = u.id " +
            "JOIN vehicles v ON r.vehicle_id = v.id " +
            "WHERE r.status IN ('ACTIVE', 'FULL') "
        );
        
        List<Object> params = new ArrayList<>();

        if (source != null && !source.trim().isEmpty()) {
            sb.append("AND LOWER(r.source) LIKE ? ");
            params.add("%" + source.trim().toLowerCase() + "%");
        }
        if (destination != null && !destination.trim().isEmpty()) {
            sb.append("AND LOWER(r.destination) LIKE ? ");
            params.add("%" + destination.trim().toLowerCase() + "%");
        }
        if (date != null) {
            sb.append("AND r.ride_date = ? ");
            params.add(date);
        }
        if (maxPrice != null) {
            sb.append("AND r.price_per_seat <= ? ");
            params.add(maxPrice);
        }
        if (minSeats != null && minSeats > 0) {
            sb.append("AND r.available_seats >= ? ");
            params.add(minSeats);
        }
        if (maxTime != null) {
            sb.append("AND r.ride_time <= ? ");
            params.add(maxTime);
        }

        sb.append("ORDER BY r.ride_date ASC, r.ride_time ASC");

        try (Connection conn = DbConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sb.toString())) {
            
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(extractRideFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return list;
    }

    /**
     * Updates editable details of a ride.
     */
    public boolean update(Ride ride) {
        String sql = "UPDATE rides SET source = ?, destination = ?, ride_date = ?, ride_time = ?, description = ? WHERE id = ?";
        try (Connection conn = DbConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, ride.getSource());
            ps.setString(2, ride.getDestination());
            ps.setDate(3, ride.getRideDate());
            ps.setTime(4, ride.getRideTime());
            ps.setString(5, ride.getDescription());
            ps.setInt(6, ride.getId());
            
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Updates ride status.
     */
    public boolean updateStatus(int id, String status) {
        String sql = "UPDATE rides SET status = ? WHERE id = ?";
        try (Connection conn = DbConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, status.toUpperCase());
            ps.setInt(2, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Atomically books seats in a ride, checking availability inside database.
     */
    public boolean deductSeatsAtomic(Connection conn, int id, int seatsToBook) throws SQLException {
        String sql = "UPDATE rides SET " +
                     "available_seats = available_seats - ?, " +
                     "status = CASE WHEN available_seats - ? = 0 THEN 'FULL' ELSE 'ACTIVE' END " +
                     "WHERE id = ? AND status = 'ACTIVE' AND available_seats >= ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, seatsToBook);
            ps.setInt(2, seatsToBook);
            ps.setInt(3, id);
            ps.setInt(4, seatsToBook);
            
            int rowsUpdated = ps.executeUpdate();
            return rowsUpdated > 0;
        }
    }

    /**
     * Atomically restores cancelled seats to a ride.
     */
    public boolean restoreSeatsAtomic(Connection conn, int id, int seatsToRestore) throws SQLException {
        String sql = "UPDATE rides SET " +
                     "available_seats = available_seats + ?, " +
                     "status = 'ACTIVE' " +
                     "WHERE id = ? AND available_seats + ? <= total_seats";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, seatsToRestore);
            ps.setInt(2, id);
            ps.setInt(3, seatsToRestore);
            
            int rowsUpdated = ps.executeUpdate();
            return rowsUpdated > 0;
        }
    }

    private Ride extractRideFromResultSet(ResultSet rs) throws SQLException {
        Ride r = new Ride(
            rs.getInt("id"),
            rs.getInt("driver_id"),
            rs.getInt("vehicle_id"),
            rs.getString("source"),
            rs.getString("destination"),
            rs.getDate("ride_date"),
            rs.getTime("ride_time"),
            rs.getInt("total_seats"),
            rs.getInt("available_seats"),
            rs.getBigDecimal("price_per_seat"),
            rs.getString("description"),
            rs.getString("status"),
            rs.getTimestamp("created_at")
        );
        
        try {
            r.setDriverName(rs.getString("driver_name"));
            r.setDriverPhone(rs.getString("driver_phone"));
            r.setVehicleMake(rs.getString("vehicle_make"));
            r.setVehicleModel(rs.getString("vehicle_model"));
            r.setVehiclePlate(rs.getString("vehicle_plate"));
        } catch (SQLException ignored) {
            // Join fields might not exist in simple queries
        }
        
        return r;
    }
}
