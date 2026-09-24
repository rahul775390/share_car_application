package com.carsharing.dao;

import com.carsharing.model.Vehicle;
import com.carsharing.util.DbConnectionManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class VehicleDAO {

    /**
     * Inserts a new vehicle record.
     */
    public boolean insert(Vehicle vehicle) {
        String sql = "INSERT INTO vehicles (owner_id, make, model, year, license_plate, type, fuel_type, transmission, color, seating_capacity, status) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DbConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            ps.setInt(1, vehicle.getOwnerId());
            ps.setString(2, vehicle.getMake());
            ps.setString(3, vehicle.getModel());
            ps.setInt(4, vehicle.getYear());
            ps.setString(5, vehicle.getLicensePlate());
            ps.setString(6, vehicle.getType());
            ps.setString(7, vehicle.getFuelType());
            ps.setString(8, vehicle.getTransmission());
            ps.setString(9, vehicle.getColor());
            ps.setInt(10, vehicle.getSeatingCapacity());
            ps.setString(11, vehicle.getStatus() != null ? vehicle.getStatus() : "AVAILABLE");
            
            int rowsAffected = ps.executeUpdate();
            if (rowsAffected > 0) {
                try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        vehicle.setId(generatedKeys.getInt(1));
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
     * Finds a vehicle by ID.
     */
    public Vehicle findById(int id) {
        String sql = "SELECT * FROM vehicles WHERE id = ?";
        try (Connection conn = DbConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return extractVehicleFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Finds a vehicle by license plate (registration number).
     */
    public Vehicle findByLicensePlate(String licensePlate) {
        String sql = "SELECT * FROM vehicles WHERE license_plate = ?";
        try (Connection conn = DbConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, licensePlate);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return extractVehicleFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Finds all active (non-removed) vehicles by owner.
     */
    public List<Vehicle> findByOwnerId(int ownerId) {
        List<Vehicle> list = new ArrayList<>();
        String sql = "SELECT * FROM vehicles WHERE owner_id = ? AND status != 'REMOVED' ORDER BY id DESC";
        try (Connection conn = DbConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, ownerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(extractVehicleFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Updates an existing vehicle's detail properties.
     */
    public boolean update(Vehicle vehicle) {
        String sql = "UPDATE vehicles SET make = ?, model = ?, year = ?, license_plate = ?, type = ?, fuel_type = ?, transmission = ?, color = ?, seating_capacity = ?, status = ? WHERE id = ?";
        try (Connection conn = DbConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, vehicle.getMake());
            ps.setString(2, vehicle.getModel());
            ps.setInt(3, vehicle.getYear());
            ps.setString(4, vehicle.getLicensePlate());
            ps.setString(5, vehicle.getType());
            ps.setString(6, vehicle.getFuelType());
            ps.setString(7, vehicle.getTransmission());
            ps.setString(8, vehicle.getColor());
            ps.setInt(9, vehicle.getSeatingCapacity());
            ps.setString(10, vehicle.getStatus());
            ps.setInt(11, vehicle.getId());
            
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Sets vehicle status to REMOVED (soft delete).
     */
    public boolean delete(int id) {
        String sql = "UPDATE vehicles SET status = 'REMOVED' WHERE id = ?";
        try (Connection conn = DbConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Updates status of a vehicle (AVAILABLE/UNAVAILABLE).
     */
    public boolean updateStatus(int id, String status) {
        String sql = "UPDATE vehicles SET status = ? WHERE id = ?";
        try (Connection conn = DbConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, status);
            ps.setInt(2, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private Vehicle extractVehicleFromResultSet(ResultSet rs) throws SQLException {
        return new Vehicle(
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
    }
}
