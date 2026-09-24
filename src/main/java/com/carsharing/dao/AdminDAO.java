package com.carsharing.dao;

import com.carsharing.model.Ride;
import com.carsharing.model.User;
import com.carsharing.model.Vehicle;
import com.carsharing.util.DbConnectionManager;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdminDAO {

    /**
     * Aggregates platform-wide statistical metrics.
     */
    public Map<String, Object> getSystemStatistics() {
        Map<String, Object> stats = new HashMap<>();

        try (Connection conn = DbConnectionManager.getConnection()) {
            stats.put("totalUsers", querySingleInt(conn, "SELECT COUNT(*) FROM users"));
            stats.put("activeUsers", querySingleInt(conn, "SELECT COUNT(*) FROM users WHERE status = 'ACTIVE'"));
            stats.put("suspendedUsers", querySingleInt(conn, "SELECT COUNT(*) FROM users WHERE status = 'SUSPENDED'"));
            stats.put("totalVehicles", querySingleInt(conn, "SELECT COUNT(*) FROM vehicles WHERE status != 'REMOVED'"));
            stats.put("totalRides", querySingleInt(conn, "SELECT COUNT(*) FROM rides"));
            stats.put("activeRides", querySingleInt(conn, "SELECT COUNT(*) FROM rides WHERE status = 'ACTIVE'"));
            stats.put("totalRideBookings", querySingleInt(conn, "SELECT COUNT(*) FROM ride_bookings"));
            stats.put("totalRideRevenue", querySingleDecimal(conn, "SELECT COALESCE(SUM(total_price), 0) FROM ride_bookings WHERE status = 'CONFIRMED'"));
            
            // Rental stats (gracefully handled even before listings exist)
            stats.put("totalRentalListings", querySingleInt(conn, "SELECT COUNT(*) FROM rental_listings"));
            stats.put("totalRentalBookings", querySingleInt(conn, "SELECT COUNT(*) FROM rental_bookings"));
            stats.put("totalRentalRevenue", querySingleDecimal(conn, "SELECT COALESCE(SUM(total_price), 0) FROM rental_bookings WHERE status = 'CONFIRMED'"));

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return stats;
    }

    /**
     * Retrieves all registered users.
     */
    public List<User> getAllUsers() {
        List<User> list = new ArrayList<>();
        String sql = "SELECT * FROM users ORDER BY id DESC";
        try (Connection conn = DbConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(new User(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("email"),
                    rs.getString("password_hash"),
                    rs.getString("role"),
                    rs.getString("phone"),
                    rs.getString("city"),
                    rs.getString("status"),
                    rs.getTimestamp("created_at")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Updates user active/suspended status.
     */
    public boolean updateUserStatus(int userId, String status) {
        String sql = "UPDATE users SET status = ? WHERE id = ?";
        try (Connection conn = DbConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, status.toUpperCase());
            ps.setInt(2, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Updates user role (e.g. USER to ADMIN).
     */
    public boolean updateUserRole(int userId, String role) {
        String sql = "UPDATE users SET role = ? WHERE id = ?";
        try (Connection conn = DbConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, role.toUpperCase());
            ps.setInt(2, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Retrieves all vehicles with owner information.
     */
    public List<Vehicle> getAllVehiclesWithOwners() {
        List<Vehicle> list = new ArrayList<>();
        String sql = "SELECT v.*, u.name AS owner_name FROM vehicles v " +
                     "JOIN users u ON v.owner_id = u.id " +
                     "ORDER BY v.id DESC";
        try (Connection conn = DbConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Vehicle v = new Vehicle(
                    rs.getInt("id"),
                    rs.getInt("owner_id"),
                    rs.getString("make"),
                    rs.getString("model"),
                    rs.getInt("year"),
                    rs.getString("license_plate"),
                    rs.getString("type"),
                    rs.getString("fuel_type"),
                    rs.getString("transmission"),
                    rs.getString("color"),
                    rs.getInt("seating_capacity"),
                    rs.getString("status"),
                    rs.getTimestamp("created_at")
                );
                list.add(v);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Retrieves all recent rides with driver and vehicle information.
     */
    public List<Ride> getAllRidesWithDetails() {
        List<Ride> list = new ArrayList<>();
        String sql = "SELECT r.*, u.name AS driver_name, u.phone AS driver_phone, " +
                     "v.make AS vehicle_make, v.model AS vehicle_model, v.license_plate AS vehicle_plate " +
                     "FROM rides r " +
                     "JOIN users u ON r.driver_id = u.id " +
                     "JOIN vehicles v ON r.vehicle_id = v.id " +
                     "ORDER BY r.id DESC LIMIT 50";
        try (Connection conn = DbConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
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
                r.setDriverName(rs.getString("driver_name"));
                r.setDriverPhone(rs.getString("driver_phone"));
                r.setVehicleMake(rs.getString("vehicle_make"));
                r.setVehicleModel(rs.getString("vehicle_model"));
                r.setVehiclePlate(rs.getString("vehicle_plate"));
                list.add(r);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Cancels a ride as administrative action.
     */
    public boolean cancelRide(int rideId) {
        String sql = "UPDATE rides SET status = 'CANCELLED' WHERE id = ?";
        try (Connection conn = DbConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, rideId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private int querySingleInt(Connection conn, String sql) {
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private BigDecimal querySingleDecimal(Connection conn, String sql) {
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                BigDecimal val = rs.getBigDecimal(1);
                return val != null ? val : BigDecimal.ZERO;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return BigDecimal.ZERO;
    }
}
