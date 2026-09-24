package com.carsharing.service;

import com.carsharing.dao.PaymentDAO;
import com.carsharing.dao.RentalBookingDAO;
import com.carsharing.dao.RentalListingDAO;
import com.carsharing.dao.VehicleDAO;
import com.carsharing.model.Payment;
import com.carsharing.model.RentalBooking;
import com.carsharing.model.RentalListing;
import com.carsharing.model.Vehicle;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class RentalServiceTest {

    private RentalService rentalService;
    private FakeRentalListingDAO fakeListingDAO;
    private FakeRentalBookingDAO fakeBookingDAO;
    private FakeVehicleDAO fakeVehicleDAO;
    private FakePaymentDAO fakePaymentDAO;

    private static class FakeVehicleDAO extends VehicleDAO {
        private final Map<Integer, Vehicle> map = new HashMap<>();
        @Override
        public Vehicle findById(int id) { return map.get(id); }
        public void save(Vehicle v) { map.put(v.getId(), v); }
    }

    private static class FakeRentalListingDAO extends RentalListingDAO {
        private final Map<Integer, RentalListing> map = new HashMap<>();
        private int seq = 1;

        @Override
        public boolean insert(RentalListing listing) {
            listing.setId(seq++);
            map.put(listing.getId(), listing);
            return true;
        }

        @Override
        public RentalListing findById(int id) {
            return map.get(id);
        }

        @Override
        public List<RentalListing> findByOwnerId(int ownerId) {
            List<RentalListing> list = new ArrayList<>();
            for (RentalListing l : map.values()) {
                if (l.getOwnerId() == ownerId) list.add(l);
            }
            return list;
        }
    }

    private static class FakeRentalBookingDAO extends RentalBookingDAO {
        private final Map<Integer, RentalBooking> map = new HashMap<>();
        private int seq = 1;

        @Override
        public boolean hasDateConflict(Connection conn, int listingId, Date startDate, Date endDate) {
            for (RentalBooking b : map.values()) {
                if (b.getRentalListingId() == listingId && "CONFIRMED".equals(b.getStatus())) {
                    if (b.getStartDate().compareTo(endDate) <= 0 && b.getEndDate().compareTo(startDate) >= 0) {
                        return true;
                    }
                }
            }
            return false;
        }

        @Override
        public boolean insert(Connection conn, RentalBooking booking) {
            booking.setId(seq++);
            map.put(booking.getId(), booking);
            return true;
        }

        @Override
        public RentalBooking findById(int id) {
            return map.get(id);
        }

        @Override
        public boolean updateStatus(Connection conn, int id, String status) {
            RentalBooking b = map.get(id);
            if (b != null) {
                b.setStatus(status);
                return true;
            }
            return false;
        }
    }

    private static class FakePaymentDAO extends PaymentDAO {
        private final Map<Integer, Payment> map = new HashMap<>();
        private int seq = 1;

        @Override
        public boolean insert(Connection conn, Payment payment) {
            payment.setId(seq++);
            map.put(payment.getId(), payment);
            return true;
        }

        @Override
        public Payment findByRentalBookingId(int rentalBookingId) {
            for (Payment p : map.values()) {
                if (p.getRentalBookingId() != null && p.getRentalBookingId() == rentalBookingId) {
                    return p;
                }
            }
            return null;
        }

        @Override
        public boolean updateStatus(Connection conn, int id, String status) {
            Payment p = map.get(id);
            if (p != null) {
                p.setStatus(status);
                return true;
            }
            return false;
        }
    }

    // Subclass overriding getConnection so no real database is needed for transactions
    private static class TestableRentalService extends RentalService {
        public TestableRentalService(RentalListingDAO listingDAO, RentalBookingDAO bookingDAO, 
                                     VehicleDAO vehicleDAO, PaymentDAO paymentDAO) {
            super(listingDAO, bookingDAO, vehicleDAO, paymentDAO);
        }

        @Override
        protected Connection getConnection() throws SQLException {
            return (Connection) java.lang.reflect.Proxy.newProxyInstance(
                Connection.class.getClassLoader(),
                new Class<?>[]{Connection.class},
                (proxy, method, args) -> null
            );
        }
    }

    @BeforeEach
    public void setUp() {
        fakeListingDAO = new FakeRentalListingDAO();
        fakeBookingDAO = new FakeRentalBookingDAO();
        fakeVehicleDAO = new FakeVehicleDAO();
        fakePaymentDAO = new FakePaymentDAO();

        rentalService = new TestableRentalService(fakeListingDAO, fakeBookingDAO, fakeVehicleDAO, fakePaymentDAO);

        // Pre-populate sample vehicle
        Vehicle vehicle = new Vehicle(1, 10, "Honda", "City", 2021, "MH-01-AB-1234", 
                                      "Sedan", "Petrol", "Automatic", "White", 5, "AVAILABLE", null);
        fakeVehicleDAO.save(vehicle);
    }

    @Test
    public void testCreateRentalListingSuccess() {
        RentalListing listing = new RentalListing();
        listing.setVehicleId(1);
        listing.setLocation("Mumbai");
        listing.setAvailableFrom(Date.valueOf("2026-10-01"));
        listing.setAvailableUntil(Date.valueOf("2026-10-15"));
        listing.setPricePerDay(new BigDecimal("2000.00"));
        listing.setSecurityDeposit(new BigDecimal("3000.00"));

        boolean result = rentalService.createListing(listing, 10);
        assertTrue(result);
        assertEquals("ACTIVE", listing.getStatus());
        assertEquals(10, listing.getOwnerId());
    }

    @Test
    public void testCreateRentalListingNonOwner() {
        RentalListing listing = new RentalListing();
        listing.setVehicleId(1); // owned by 10
        listing.setLocation("Mumbai");
        listing.setAvailableFrom(Date.valueOf("2026-10-01"));
        listing.setAvailableUntil(Date.valueOf("2026-10-15"));
        listing.setPricePerDay(new BigDecimal("2000.00"));

        boolean result = rentalService.createListing(listing, 99); // user 99 is not owner
        assertFalse(result);
    }

    @Test
    public void testCreateRentalListingInvalidDateWindow() {
        RentalListing listing = new RentalListing();
        listing.setVehicleId(1);
        listing.setLocation("Mumbai");
        listing.setAvailableFrom(Date.valueOf("2026-10-15"));
        listing.setAvailableUntil(Date.valueOf("2026-10-01")); // until before from
        listing.setPricePerDay(new BigDecimal("2000.00"));

        boolean result = rentalService.createListing(listing, 10);
        assertFalse(result);
    }

    @Test
    public void testBookRentalSuccess() {
        RentalListing listing = new RentalListing();
        listing.setVehicleId(1);
        listing.setLocation("Mumbai");
        listing.setAvailableFrom(Date.valueOf("2026-10-01"));
        listing.setAvailableUntil(Date.valueOf("2026-10-15"));
        listing.setPricePerDay(new BigDecimal("1000.00"));
        listing.setSecurityDeposit(new BigDecimal("2000.00"));
        rentalService.createListing(listing, 10);

        // Book 3 days: Oct 02 to Oct 04 (3 days inclusive = 3000 + 2000 deposit = 5000)
        boolean booked = rentalService.bookRental(listing.getId(), 20, 
                                                 Date.valueOf("2026-10-02"), 
                                                 Date.valueOf("2026-10-04"), "UPI");
        assertTrue(booked);

        RentalBooking b = fakeBookingDAO.findById(1);
        assertNotNull(b);
        assertEquals(new BigDecimal("5000.00"), b.getTotalPrice());
        assertEquals("CONFIRMED", b.getStatus());
    }

    @Test
    public void testBookRentalOwnerCannotRentOwnCar() {
        RentalListing listing = new RentalListing();
        listing.setVehicleId(1);
        listing.setLocation("Mumbai");
        listing.setAvailableFrom(Date.valueOf("2026-10-01"));
        listing.setAvailableUntil(Date.valueOf("2026-10-15"));
        listing.setPricePerDay(new BigDecimal("1000.00"));
        rentalService.createListing(listing, 10);

        // User 10 is the owner
        boolean booked = rentalService.bookRental(listing.getId(), 10, 
                                                 Date.valueOf("2026-10-02"), 
                                                 Date.valueOf("2026-10-04"), "CARD");
        assertFalse(booked);
    }

    @Test
    public void testBookRentalDateConflict() {
        RentalListing listing = new RentalListing();
        listing.setVehicleId(1);
        listing.setLocation("Mumbai");
        listing.setAvailableFrom(Date.valueOf("2026-10-01"));
        listing.setAvailableUntil(Date.valueOf("2026-10-15"));
        listing.setPricePerDay(new BigDecimal("1000.00"));
        rentalService.createListing(listing, 10);

        // First booking Oct 02 to Oct 05
        rentalService.bookRental(listing.getId(), 20, 
                                 Date.valueOf("2026-10-02"), 
                                 Date.valueOf("2026-10-05"), "CARD");

        // Overlapping booking Oct 04 to Oct 07 -> should fail
        boolean conflictBooked = rentalService.bookRental(listing.getId(), 25, 
                                                         Date.valueOf("2026-10-04"), 
                                                         Date.valueOf("2026-10-07"), "UPI");
        assertFalse(conflictBooked);
    }

    @Test
    public void testCancelRentalBooking() {
        RentalListing listing = new RentalListing();
        listing.setVehicleId(1);
        listing.setLocation("Mumbai");
        listing.setAvailableFrom(Date.valueOf("2026-10-01"));
        listing.setAvailableUntil(Date.valueOf("2026-10-15"));
        listing.setPricePerDay(new BigDecimal("1000.00"));
        rentalService.createListing(listing, 10);

        rentalService.bookRental(listing.getId(), 20, 
                                 Date.valueOf("2026-10-02"), 
                                 Date.valueOf("2026-10-05"), "UPI");

        boolean cancelled = rentalService.cancelBooking(1, 20);
        assertTrue(cancelled);
        assertEquals("CANCELLED", fakeBookingDAO.findById(1).getStatus());

        // Non-renter cannot cancel
        assertFalse(rentalService.cancelBooking(1, 99));
    }
}
