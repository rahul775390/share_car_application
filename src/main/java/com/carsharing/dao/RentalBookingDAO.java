package com.carsharing.dao;

import com.carsharing.model.RentalBooking;
import com.carsharing.util.DbConnectionManager;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class RentalBookingDAO {

    /**
     * Checks if overlapping confirmed booking exists for this rental listing.
     */
    public boolean hasDateConflict(Connection conn, int listingId, Date startDate, Date endDate) throws SQLException {
        String sql = "SELECT COUNT(*) FROM rental_bookings " +
                     "WHERE rental_listing_id = ? " +
                     "AND status IN ('CONFIRMED', 'ONGOING') " +
                     "AND (start_date <= ? AND end_date >= ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, listingId);
            ps.setDate(2, endDate);
            ps.setDate(3, startDate);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    /**
     * Inserts a rental booking inside a transactional connection context.
     */
    public boolean insert(Connection conn, RentalBooking booking) throws SQLException {
        String sql = "INSERT INTO rental_bookings (rental_listing_id, renter_id, start_date, end_date, total_price, status) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, booking.getRentalListingId());
            ps.setInt(2, booking.getRenterId());
            ps.setDate(3, booking.getStartDate());
            ps.setDate(4, booking.getEndDate());
            ps.setBigDecimal(5, booking.getTotalPrice());
            ps.setString(6, booking.getStatus() != null ? booking.getStatus() : "CONFIRMED");

            int rows = ps.executeUpdate();
            if (rows > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        booking.setId(rs.getInt(1));
                    }
                }
                return true;
            }
        }
        return false;
    }

    /**
     * Finds rental booking by ID with joined details.
     */
    public RentalBooking findById(int id) {
        String sql = "SELECT rb.*, u.name AS renter_name, u.phone AS renter_phone, " +
                     "rl.location, rl.price_per_day, rl.security_deposit, " +
                     "v.make AS vehicle_make, v.model AS vehicle_model, v.license_plate AS vehicle_plate, " +
                     "ow.name AS owner_name, ow.phone AS owner_phone " +
                     "FROM rental_bookings rb " +
                     "JOIN users u ON rb.renter_id = u.id " +
                     "JOIN rental_listings rl ON rb.rental_listing_id = rl.id " +
                     "JOIN vehicles v ON rl.vehicle_id = v.id " +
                     "JOIN users ow ON rl.owner_id = ow.id " +
                     "WHERE rb.id = ?";
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
     * Finds bookings created by a renter.
     */
    public List<RentalBooking> findByRenterId(int renterId) {
        List<RentalBooking> list = new ArrayList<>();
        String sql = "SELECT rb.*, u.name AS renter_name, u.phone AS renter_phone, " +
                     "rl.location, rl.price_per_day, rl.security_deposit, " +
                     "v.make AS vehicle_make, v.model AS vehicle_model, v.license_plate AS vehicle_plate, " +
                     "ow.name AS owner_name, ow.phone AS owner_phone " +
                     "FROM rental_bookings rb " +
                     "JOIN users u ON rb.renter_id = u.id " +
                     "JOIN rental_listings rl ON rb.rental_listing_id = rl.id " +
                     "JOIN vehicles v ON rl.vehicle_id = v.id " +
                     "JOIN users ow ON rl.owner_id = ow.id " +
                     "WHERE rb.renter_id = ? " +
                     "ORDER BY rb.id DESC";
        try (Connection conn = DbConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, renterId);
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

    public boolean updateStatus(Connection conn, int id, String status) throws SQLException {
        String sql = "UPDATE rental_bookings SET status = ? WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status.toUpperCase());
            ps.setInt(2, id);
            return ps.executeUpdate() > 0;
        }
    }

    private RentalBooking extractBookingFromResultSet(ResultSet rs) throws SQLException {
        RentalBooking rb = new RentalBooking(
            rs.getInt("id"),
            rs.getInt("rental_listing_id"),
            rs.getInt("renter_id"),
            rs.getDate("start_date"),
            rs.getDate("end_date"),
            rs.getBigDecimal("total_price"),
            rs.getString("status"),
            rs.getTimestamp("created_at")
        );

        try {
            rb.setRenterName(rs.getString("renter_name"));
            rb.setRenterPhone(rs.getString("renter_phone"));
            rb.setLocation(rs.getString("location"));
            rb.setPricePerDay(rs.getBigDecimal("price_per_day"));
            rb.setSecurityDeposit(rs.getBigDecimal("security_deposit"));
            rb.setVehicleMake(rs.getString("vehicle_make"));
            rb.setVehicleModel(rs.getString("vehicle_model"));
            rb.setVehiclePlate(rs.getString("vehicle_plate"));
            rb.setOwnerName(rs.getString("owner_name"));
            rb.setOwnerPhone(rs.getString("owner_phone"));
        } catch (SQLException ignored) {}

        return rb;
    }
}
