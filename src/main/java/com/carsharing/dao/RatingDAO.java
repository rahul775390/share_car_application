package com.carsharing.dao;

import com.carsharing.model.Rating;
import com.carsharing.util.DbConnectionManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class RatingDAO {

    public boolean insert(Rating rating) {
        String sql = "INSERT INTO ratings (rater_id, ratee_id, booking_type, ride_booking_id, rental_booking_id, rating_value, comment) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DbConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, rating.getRaterId());
            ps.setInt(2, rating.getRateeId());
            ps.setString(3, rating.getBookingType());

            if (rating.getRideBookingId() != null) {
                ps.setInt(4, rating.getRideBookingId());
            } else {
                ps.setNull(4, Types.INTEGER);
            }

            if (rating.getRentalBookingId() != null) {
                ps.setInt(5, rating.getRentalBookingId());
            } else {
                ps.setNull(5, Types.INTEGER);
            }

            ps.setInt(6, rating.getRatingValue());
            ps.setString(7, rating.getComment());

            int rows = ps.executeUpdate();
            if (rows > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        rating.setId(rs.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<Rating> findByRateeId(int rateeId) {
        List<Rating> list = new ArrayList<>();
        String sql = "SELECT r.*, u.name AS rater_name FROM ratings r " +
                     "JOIN users u ON r.rater_id = u.id " +
                     "WHERE r.ratee_id = ? ORDER BY r.id DESC";
        try (Connection conn = DbConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, rateeId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Rating r = new Rating(
                        rs.getInt("id"),
                        rs.getInt("rater_id"),
                        rs.getInt("ratee_id"),
                        rs.getString("booking_type"),
                        (Integer) rs.getObject("ride_booking_id"),
                        (Integer) rs.getObject("rental_booking_id"),
                        rs.getInt("rating_value"),
                        rs.getString("comment"),
                        rs.getTimestamp("created_at")
                    );
                    r.setRaterName(rs.getString("rater_name"));
                    list.add(r);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public double getAverageRating(int rateeId) {
        String sql = "SELECT AVG(rating_value) FROM ratings WHERE ratee_id = ?";
        try (Connection conn = DbConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, rateeId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Math.round(rs.getDouble(1) * 10.0) / 10.0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0;
    }

    public boolean hasUserRatedRide(int raterId, int rideBookingId) {
        String sql = "SELECT COUNT(*) FROM ratings WHERE rater_id = ? AND ride_booking_id = ?";
        try (Connection conn = DbConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, raterId);
            ps.setInt(2, rideBookingId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
