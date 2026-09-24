package com.carsharing.service;

import com.carsharing.dao.PaymentDAO;
import com.carsharing.dao.RentalBookingDAO;
import com.carsharing.dao.RentalListingDAO;
import com.carsharing.dao.VehicleDAO;
import com.carsharing.model.Payment;
import com.carsharing.model.RentalBooking;
import com.carsharing.model.RentalListing;
import com.carsharing.model.Vehicle;
import com.carsharing.util.DbConnectionManager;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

public class RentalService {
    private final RentalListingDAO rentalListingDAO;
    private final RentalBookingDAO rentalBookingDAO;
    private final VehicleDAO vehicleDAO;
    private final PaymentDAO paymentDAO;

    public RentalService() {
        this.rentalListingDAO = new RentalListingDAO();
        this.rentalBookingDAO = new RentalBookingDAO();
        this.vehicleDAO = new VehicleDAO();
        this.paymentDAO = new PaymentDAO();
    }

    public RentalService(RentalListingDAO rentalListingDAO, RentalBookingDAO rentalBookingDAO, 
                         VehicleDAO vehicleDAO, PaymentDAO paymentDAO) {
        this.rentalListingDAO = rentalListingDAO;
        this.rentalBookingDAO = rentalBookingDAO;
        this.vehicleDAO = vehicleDAO;
        this.paymentDAO = paymentDAO;
    }

    protected Connection getConnection() throws SQLException {
        return DbConnectionManager.getConnection();
    }

    /**
     * Publishes a vehicle listing for rental after verifying owner authorization.
     */
    public boolean createListing(RentalListing listing, int userId) {
        if (listing == null) return false;

        Vehicle vehicle = vehicleDAO.findById(listing.getVehicleId());
        if (vehicle == null || vehicle.getOwnerId() != userId || !"AVAILABLE".equals(vehicle.getStatus())) {
            return false;
        }

        if (listing.getLocation() == null || listing.getLocation().trim().isEmpty() ||
            listing.getAvailableFrom() == null || listing.getAvailableUntil() == null ||
            listing.getPricePerDay() == null || listing.getPricePerDay().compareTo(BigDecimal.ZERO) < 0) {
            return false;
        }

        if (listing.getAvailableUntil().before(listing.getAvailableFrom())) {
            return false;
        }

        listing.setOwnerId(userId);
        listing.setStatus("ACTIVE");
        return rentalListingDAO.insert(listing);
    }

    public List<RentalListing> searchListings(String location, Date fromDate, Date untilDate, BigDecimal maxPrice) {
        return rentalListingDAO.search(location, fromDate, untilDate, maxPrice);
    }

    public RentalListing getListingById(int id) {
        return rentalListingDAO.findById(id);
    }

    public List<RentalListing> getListingsByOwner(int ownerId) {
        return rentalListingDAO.findByOwnerId(ownerId);
    }

    public boolean toggleListingStatus(int id, String status, int userId) {
        RentalListing listing = rentalListingDAO.findById(id);
        if (listing == null || listing.getOwnerId() != userId) {
            return false;
        }
        return rentalListingDAO.updateStatus(id, status);
    }

    /**
     * Executes transactional rental reservation, date conflict validation, and payment recording.
     */
    public boolean bookRental(int listingId, int renterId, Date startDate, Date endDate, String paymentMethod) {
        if (startDate == null || endDate == null || endDate.before(startDate)) {
            return false;
        }

        RentalListing listing = rentalListingDAO.findById(listingId);
        if (listing == null || !"ACTIVE".equals(listing.getStatus())) {
            return false;
        }

        // Owner cannot rent their own car
        if (listing.getOwnerId() == renterId) {
            return false;
        }

        // Ensure dates fall within the listing's available window
        if (startDate.before(listing.getAvailableFrom()) || endDate.after(listing.getAvailableUntil())) {
            return false;
        }

        // Calculate days (inclusive)
        long diffMs = endDate.getTime() - startDate.getTime();
        long days = (diffMs / (1000 * 60 * 60 * 24)) + 1;
        if (days <= 0) days = 1;

        BigDecimal dailyTotal = listing.getPricePerDay().multiply(BigDecimal.valueOf(days));
        BigDecimal deposit = listing.getSecurityDeposit() != null ? listing.getSecurityDeposit() : BigDecimal.ZERO;
        BigDecimal totalAmount = dailyTotal.add(deposit);

        Connection conn = null;
        try {
            conn = getConnection();
            conn.setAutoCommit(false); // Begin transaction

            // 1. Check for overlapping reservations
            boolean hasConflict = rentalBookingDAO.hasDateConflict(conn, listingId, startDate, endDate);
            if (hasConflict) {
                conn.rollback();
                return false;
            }

            // 2. Insert booking
            RentalBooking booking = new RentalBooking();
            booking.setRentalListingId(listingId);
            booking.setRenterId(renterId);
            booking.setStartDate(startDate);
            booking.setEndDate(endDate);
            booking.setTotalPrice(totalAmount);
            booking.setStatus("CONFIRMED");

            boolean bookingSaved = rentalBookingDAO.insert(conn, booking);
            if (!bookingSaved) {
                conn.rollback();
                return false;
            }

            // 3. Record payment transaction
            Payment payment = new Payment();
            payment.setUserId(renterId);
            payment.setBookingType("RENTAL");
            payment.setRentalBookingId(booking.getId());
            payment.setAmount(totalAmount);
            payment.setPaymentMethod(paymentMethod != null && !paymentMethod.isEmpty() ? paymentMethod : "CARD");
            payment.setStatus("COMPLETED");
            payment.setTransactionId("TXN-RENT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());

            boolean paymentSaved = paymentDAO.insert(conn, payment);
            if (!paymentSaved) {
                conn.rollback();
                return false;
            }

            conn.commit(); // Commit transaction
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ignored) {}
            }
            return false;
        } finally {
            if (conn != null) {
                try { conn.close(); } catch (SQLException ignored) {}
            }
        }
    }

    /**
     * Cancels a rental booking and flags payment refund.
     */
    public boolean cancelBooking(int bookingId, int renterId) {
        RentalBooking booking = rentalBookingDAO.findById(bookingId);
        if (booking == null || booking.getRenterId() != renterId || !"CONFIRMED".equals(booking.getStatus())) {
            return false;
        }

        Connection conn = null;
        try {
            conn = getConnection();
            conn.setAutoCommit(false);

            boolean cancelled = rentalBookingDAO.updateStatus(conn, bookingId, "CANCELLED");
            if (!cancelled) {
                conn.rollback();
                return false;
            }

            Payment payment = paymentDAO.findByRentalBookingId(bookingId);
            if (payment != null) {
                paymentDAO.updateStatus(conn, payment.getId(), "REFUNDED");
            }

            conn.commit();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ignored) {}
            }
            return false;
        } finally {
            if (conn != null) {
                try { conn.close(); } catch (SQLException ignored) {}
            }
        }
    }

    public List<RentalBooking> getRenterBookings(int renterId) {
        return rentalBookingDAO.findByRenterId(renterId);
    }

    public RentalBooking getBookingById(int id) {
        return rentalBookingDAO.findById(id);
    }

    public Payment getPaymentByRentalBookingId(int bookingId) {
        return paymentDAO.findByRentalBookingId(bookingId);
    }
}
