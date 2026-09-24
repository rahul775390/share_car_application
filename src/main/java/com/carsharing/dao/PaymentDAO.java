package com.carsharing.dao;

import com.carsharing.model.Payment;
import com.carsharing.util.DbConnectionManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;

public class PaymentDAO {

    /**
     * Inserts a payment record within a transaction context.
     */
    public boolean insert(Connection conn, Payment payment) throws SQLException {
        String sql = "INSERT INTO payments (user_id, booking_type, ride_booking_id, rental_booking_id, amount, payment_method, status, transaction_id) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, payment.getUserId());
            ps.setString(2, payment.getBookingType());

            if (payment.getRideBookingId() != null) {
                ps.setInt(3, payment.getRideBookingId());
            } else {
                ps.setNull(3, Types.INTEGER);
            }

            if (payment.getRentalBookingId() != null) {
                ps.setInt(4, payment.getRentalBookingId());
            } else {
                ps.setNull(4, Types.INTEGER);
            }

            ps.setBigDecimal(5, payment.getAmount());
            ps.setString(6, payment.getPaymentMethod());
            ps.setString(7, payment.getStatus() != null ? payment.getStatus() : "COMPLETED");
            ps.setString(8, payment.getTransactionId());

            int rows = ps.executeUpdate();
            if (rows > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        payment.setId(rs.getInt(1));
                    }
                }
                return true;
            }
        }
        return false;
    }

    public Payment findByRideBookingId(int rideBookingId) {
        String sql = "SELECT * FROM payments WHERE ride_booking_id = ?";
        try (Connection conn = DbConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, rideBookingId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return extractPaymentFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Payment findByRentalBookingId(int rentalBookingId) {
        String sql = "SELECT * FROM payments WHERE rental_booking_id = ?";
        try (Connection conn = DbConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, rentalBookingId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return extractPaymentFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean updateStatus(Connection conn, int id, String status) throws SQLException {
        String sql = "UPDATE payments SET status = ? WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status.toUpperCase());
            ps.setInt(2, id);
            return ps.executeUpdate() > 0;
        }
    }

    private Payment extractPaymentFromResultSet(ResultSet rs) throws SQLException {
        Integer rideId = rs.getObject("ride_booking_id") != null ? rs.getInt("ride_booking_id") : null;
        Integer rentalId = rs.getObject("rental_booking_id") != null ? rs.getInt("rental_booking_id") : null;
        return new Payment(
            rs.getInt("id"),
            rs.getInt("user_id"),
            rs.getString("booking_type"),
            rideId,
            rentalId,
            rs.getBigDecimal("amount"),
            rs.getString("payment_method"),
            rs.getString("status"),
            rs.getString("transaction_id"),
            rs.getTimestamp("created_at")
        );
    }
}
