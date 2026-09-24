package com.carsharing.service;

import com.carsharing.dao.PaymentDAO;
import com.carsharing.dao.RatingDAO;
import com.carsharing.dao.RideBookingDAO;
import com.carsharing.dao.RideDAO;
import com.carsharing.dao.VehicleDAO;
import com.carsharing.model.Payment;
import com.carsharing.model.Rating;
import com.carsharing.model.Ride;
import com.carsharing.model.RideBooking;
import com.carsharing.model.Vehicle;
import com.carsharing.util.DbConnectionManager;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Time;
import java.util.List;
import java.util.UUID;

public class RideService {
    private final RideDAO rideDAO;
    private final RideBookingDAO rideBookingDAO;
    private final VehicleDAO vehicleDAO;
    private final PaymentDAO paymentDAO;
    private final RatingDAO ratingDAO;

    public RideService() {
        this.rideDAO = new RideDAO();
        this.rideBookingDAO = new RideBookingDAO();
        this.vehicleDAO = new VehicleDAO();
        this.paymentDAO = new PaymentDAO();
        this.ratingDAO = new RatingDAO();
    }

    // Constructor for testing / dependency injection
    public RideService(RideDAO rideDAO, RideBookingDAO rideBookingDAO, VehicleDAO vehicleDAO) {
        this.rideDAO = rideDAO;
        this.rideBookingDAO = rideBookingDAO;
        this.vehicleDAO = vehicleDAO;
        this.paymentDAO = new PaymentDAO();
        this.ratingDAO = new RatingDAO();
    }

    public RideService(RideDAO rideDAO, RideBookingDAO rideBookingDAO, VehicleDAO vehicleDAO, 
                       PaymentDAO paymentDAO, RatingDAO ratingDAO) {
        this.rideDAO = rideDAO;
        this.rideBookingDAO = rideBookingDAO;
        this.vehicleDAO = vehicleDAO;
        this.paymentDAO = paymentDAO;
        this.ratingDAO = ratingDAO;
    }

    protected Connection getConnection() throws SQLException {
        return DbConnectionManager.getConnection();
    }

    /**
     * Validates details and creates a new Ride.
     */
    public boolean createRide(Ride ride, int userId) {
        if (ride == null) return false;

        // Check vehicle existence and ownership
        Vehicle vehicle = vehicleDAO.findById(ride.getVehicleId());
        if (vehicle == null || vehicle.getOwnerId() != userId || !"AVAILABLE".equals(vehicle.getStatus())) {
            return false;
        }

        if (!validateRideDetails(ride, vehicle.getSeatingCapacity())) {
            return false;
        }

        ride.setDriverId(userId);
        ride.setAvailableSeats(ride.getTotalSeats());
        ride.setStatus("ACTIVE");

        return rideDAO.insert(ride);
    }

    /**
     * Searches active rides.
     */
    public List<Ride> searchRides(String source, String destination, Date date, 
                                  BigDecimal maxPrice, Integer minSeats, Time maxTime) {
        return rideDAO.searchRides(source, destination, date, maxPrice, minSeats, maxTime);
    }

    public Ride getRideById(int id) {
        return rideDAO.findById(id);
    }

    public List<Ride> getRidesByDriver(int driverId) {
        return rideDAO.findByDriverId(driverId);
    }

    /**
     * Updates an existing Ride, checking driver ownership.
     */
    public boolean updateRide(Ride ride, int userId) {
        if (ride == null) return false;

        Ride dbRide = rideDAO.findById(ride.getId());
        if (dbRide == null || dbRide.getDriverId() != userId) {
            return false;
        }

        // Validate basic update details
        if (ride.getSource() == null || ride.getSource().trim().isEmpty() ||
            ride.getDestination() == null || ride.getDestination().trim().isEmpty() ||
            ride.getSource().trim().equalsIgnoreCase(ride.getDestination().trim()) ||
            ride.getRideDate() == null || ride.getRideTime() == null) {
            return false;
        }

        // Keep vehicle, totalSeats, availableSeats, price, and status unchanged in this simple edit
        ride.setVehicleId(dbRide.getVehicleId());
        ride.setTotalSeats(dbRide.getTotalSeats());
        ride.setAvailableSeats(dbRide.getAvailableSeats());
        ride.setPricePerSeat(dbRide.getPricePerSeat());
        ride.setStatus(dbRide.getStatus());

        return rideDAO.update(ride);
    }

    /**
     * Cancels a ride, verifying driver authorization.
     */
    public boolean cancelRide(int id, int userId) {
        Ride dbRide = rideDAO.findById(id);
        if (dbRide == null || dbRide.getDriverId() != userId) {
            return false;
        }
        return rideDAO.updateStatus(id, "CANCELLED");
    }

    /**
     * Performs transactional seat booking with default payment method.
     */
    public boolean bookSeats(int rideId, int passengerId, int seatsToBook) {
        return bookSeats(rideId, passengerId, seatsToBook, "UPI");
    }

    /**
     * Performs transactional seat booking with chosen payment method.
     */
    public boolean bookSeats(int rideId, int passengerId, int seatsToBook, String paymentMethod) {
        if (seatsToBook <= 0) {
            return false;
        }

        Ride ride = rideDAO.findById(rideId);
        if (ride == null || !"ACTIVE".equals(ride.getStatus())) {
            return false;
        }

        // Driver cannot book their own ride
        if (ride.getDriverId() == passengerId) {
            return false;
        }

        // Bookings cannot exceed available seats
        if (seatsToBook > ride.getAvailableSeats()) {
            return false;
        }

        Connection conn = null;
        try {
            conn = getConnection();
            conn.setAutoCommit(false); // Begin transaction

            // 1. Deduct seats atomically
            boolean seatsDeducted = rideDAO.deductSeatsAtomic(conn, rideId, seatsToBook);
            if (!seatsDeducted) {
                conn.rollback();
                return false;
            }

            // 2. Insert booking record
            BigDecimal totalAmount = ride.getPricePerSeat().multiply(BigDecimal.valueOf(seatsToBook));
            RideBooking booking = new RideBooking();
            booking.setRideId(rideId);
            booking.setPassengerId(passengerId);
            booking.setSeatsBooked(seatsToBook);
            booking.setTotalPrice(totalAmount);
            booking.setStatus("CONFIRMED");

            boolean bookingSaved = rideBookingDAO.insert(conn, booking);
            if (!bookingSaved) {
                conn.rollback();
                return false;
            }

            // 3. Record payment record
            Payment payment = new Payment();
            payment.setUserId(passengerId);
            payment.setBookingType("RIDE");
            payment.setRideBookingId(booking.getId());
            payment.setAmount(totalAmount);
            payment.setPaymentMethod(paymentMethod != null && !paymentMethod.isEmpty() ? paymentMethod : "UPI");
            payment.setStatus("COMPLETED");
            payment.setTransactionId("TXN-RIDE-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());

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
                try {
                    conn.rollback(); // Rollback transaction on failure
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            return false;
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    /**
     * Performs transactional booking cancellation and refund.
     */
    public boolean cancelBooking(int bookingId, int passengerId) {
        RideBooking booking = rideBookingDAO.findById(bookingId);
        if (booking == null || booking.getPassengerId() != passengerId || !"CONFIRMED".equals(booking.getStatus())) {
            return false;
        }

        Connection conn = null;
        try {
            conn = getConnection();
            conn.setAutoCommit(false); // Begin transaction

            // 1. Update booking status
            boolean bookingCancelled = rideBookingDAO.updateStatus(conn, bookingId, "CANCELLED");
            if (!bookingCancelled) {
                conn.rollback();
                return false;
            }

            // 2. Restore seats atomically
            boolean seatsRestored = rideDAO.restoreSeatsAtomic(conn, booking.getRideId(), booking.getSeatsBooked());
            if (!seatsRestored) {
                conn.rollback();
                return false;
            }

            // 3. Update payment status to REFUNDED
            Payment payment = paymentDAO.findByRideBookingId(bookingId);
            if (payment != null) {
                paymentDAO.updateStatus(conn, payment.getId(), "REFUNDED");
            }

            conn.commit(); // Commit transaction
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            if (conn != null) {
                try {
                    conn.rollback(); // Rollback transaction on failure
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            return false;
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    public Payment getPaymentByBookingId(int bookingId) {
        return paymentDAO.findByRideBookingId(bookingId);
    }

    public boolean submitRating(int raterId, int rateeId, int rideBookingId, int ratingValue, String comment) {
        if (ratingValue < 1 || ratingValue > 5) return false;
        if (ratingDAO.hasUserRatedRide(raterId, rideBookingId)) return false;

        Rating r = new Rating();
        r.setRaterId(raterId);
        r.setRateeId(rateeId);
        r.setBookingType("RIDE");
        r.setRideBookingId(rideBookingId);
        r.setRatingValue(ratingValue);
        r.setComment(comment != null ? comment.trim() : "");
        return ratingDAO.insert(r);
    }

    public double getDriverAverageRating(int driverId) {
        return ratingDAO.getAverageRating(driverId);
    }

    public boolean hasUserRatedRide(int raterId, int rideBookingId) {
        return ratingDAO.hasUserRatedRide(raterId, rideBookingId);
    }

    public List<RideBooking> getPassengerBookings(int passengerId) {
        return rideBookingDAO.findByPassengerId(passengerId);
    }

    public RideBooking getBookingById(int bookingId) {
        return rideBookingDAO.findById(bookingId);
    }

    private boolean validateRideDetails(Ride r, int maxCapacity) {
        if (r.getSource() == null || r.getSource().trim().isEmpty() ||
            r.getDestination() == null || r.getDestination().trim().isEmpty() ||
            r.getSource().trim().equalsIgnoreCase(r.getDestination().trim()) ||
            r.getRideDate() == null || r.getRideTime() == null ||
            r.getPricePerSeat() == null) {
            return false;
        }

        // Available seats checking
        if (r.getTotalSeats() <= 0 || r.getTotalSeats() > maxCapacity) {
            return false;
        }

        // Price per seat checking
        if (r.getPricePerSeat().compareTo(BigDecimal.ZERO) < 0) {
            return false;
        }

        return true;
    }
}
