package com.carsharing.dao;

import com.carsharing.model.RentalListing;
import com.carsharing.util.DbConnectionManager;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class RentalListingDAO {

    /**
     * Inserts a new vehicle rental listing.
     */
    public boolean insert(RentalListing listing) {
        String sql = "INSERT INTO rental_listings (owner_id, vehicle_id, location, available_from, available_until, price_per_day, security_deposit, description, status) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DbConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, listing.getOwnerId());
            ps.setInt(2, listing.getVehicleId());
            ps.setString(3, listing.getLocation());
            ps.setDate(4, listing.getAvailableFrom());
            ps.setDate(5, listing.getAvailableUntil());
            ps.setBigDecimal(6, listing.getPricePerDay());
            ps.setBigDecimal(7, listing.getSecurityDeposit() != null ? listing.getSecurityDeposit() : BigDecimal.ZERO);
            ps.setString(8, listing.getDescription());
            ps.setString(9, listing.getStatus() != null ? listing.getStatus() : "ACTIVE");

            int rows = ps.executeUpdate();
            if (rows > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        listing.setId(rs.getInt(1));
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
     * Finds a rental listing by ID with joined owner and vehicle attributes.
     */
    public RentalListing findById(int id) {
        String sql = "SELECT rl.*, u.name AS owner_name, u.phone AS owner_phone, " +
                     "v.make AS vehicle_make, v.model AS vehicle_model, v.year AS vehicle_year, " +
                     "v.license_plate AS vehicle_plate, v.type AS vehicle_type, " +
                     "v.fuel_type, v.transmission, v.seating_capacity " +
                     "FROM rental_listings rl " +
                     "JOIN users u ON rl.owner_id = u.id " +
                     "JOIN vehicles v ON rl.vehicle_id = v.id " +
                     "WHERE rl.id = ?";
        try (Connection conn = DbConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return extractListingFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Finds all rental listings created by an owner.
     */
    public List<RentalListing> findByOwnerId(int ownerId) {
        List<RentalListing> list = new ArrayList<>();
        String sql = "SELECT rl.*, u.name AS owner_name, u.phone AS owner_phone, " +
                     "v.make AS vehicle_make, v.model AS vehicle_model, v.year AS vehicle_year, " +
                     "v.license_plate AS vehicle_plate, v.type AS vehicle_type, " +
                     "v.fuel_type, v.transmission, v.seating_capacity " +
                     "FROM rental_listings rl " +
                     "JOIN users u ON rl.owner_id = u.id " +
                     "JOIN vehicles v ON rl.vehicle_id = v.id " +
                     "WHERE rl.owner_id = ? " +
                     "ORDER BY rl.id DESC";
        try (Connection conn = DbConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, ownerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(extractListingFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Searches active rental listings based on location, dates, and max daily price.
     */
    public List<RentalListing> search(String location, Date fromDate, Date untilDate, BigDecimal maxPrice) {
        List<RentalListing> list = new ArrayList<>();
        StringBuilder sb = new StringBuilder(
            "SELECT rl.*, u.name AS owner_name, u.phone AS owner_phone, " +
            "v.make AS vehicle_make, v.model AS vehicle_model, v.year AS vehicle_year, " +
            "v.license_plate AS vehicle_plate, v.type AS vehicle_type, " +
            "v.fuel_type, v.transmission, v.seating_capacity " +
            "FROM rental_listings rl " +
            "JOIN users u ON rl.owner_id = u.id " +
            "JOIN vehicles v ON rl.vehicle_id = v.id " +
            "WHERE rl.status = 'ACTIVE' "
        );

        List<Object> params = new ArrayList<>();

        if (location != null && !location.trim().isEmpty()) {
            sb.append("AND LOWER(rl.location) LIKE ? ");
            params.add("%" + location.trim().toLowerCase() + "%");
        }
        if (fromDate != null) {
            sb.append("AND rl.available_from <= ? ");
            params.add(fromDate);
        }
        if (untilDate != null) {
            sb.append("AND rl.available_until >= ? ");
            params.add(untilDate);
        }
        if (maxPrice != null) {
            sb.append("AND rl.price_per_day <= ? ");
            params.add(maxPrice);
        }

        sb.append("ORDER BY rl.price_per_day ASC, rl.id DESC");

        try (Connection conn = DbConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sb.toString())) {

            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(extractListingFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean updateStatus(int id, String status) {
        String sql = "UPDATE rental_listings SET status = ? WHERE id = ?";
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

    private RentalListing extractListingFromResultSet(ResultSet rs) throws SQLException {
        RentalListing rl = new RentalListing(
            rs.getInt("id"),
            rs.getInt("owner_id"),
            rs.getInt("vehicle_id"),
            rs.getString("location"),
            rs.getDate("available_from"),
            rs.getDate("available_until"),
            rs.getBigDecimal("price_per_day"),
            rs.getBigDecimal("security_deposit"),
            rs.getString("description"),
            rs.getString("status"),
            rs.getTimestamp("created_at")
        );

        try {
            rl.setOwnerName(rs.getString("owner_name"));
            rl.setOwnerPhone(rs.getString("owner_phone"));
            rl.setVehicleMake(rs.getString("vehicle_make"));
            rl.setVehicleModel(rs.getString("vehicle_model"));
            rl.setVehicleYear(rs.getInt("vehicle_year"));
            rl.setVehiclePlate(rs.getString("vehicle_plate"));
            rl.setVehicleType(rs.getString("vehicle_type"));
            rl.setFuelType(rs.getString("fuel_type"));
            rl.setTransmission(rs.getString("transmission"));
            rl.setSeatingCapacity(rs.getInt("seating_capacity"));
        } catch (SQLException ignored) {}

        return rl;
    }
}
