package com.carsharing.service;

import com.carsharing.dao.RideBookingDAO;
import com.carsharing.dao.RideDAO;
import com.carsharing.dao.VehicleDAO;
import com.carsharing.model.Ride;
import com.carsharing.model.RideBooking;
import com.carsharing.model.Vehicle;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Time;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class RideServiceTest {

    private RideService rideService;
    private FakeRideDAO fakeRideDAO;
    private FakeRideBookingDAO fakeRideBookingDAO;
    private FakeVehicleDAO fakeVehicleDAO;

    private static class FakeVehicleDAO extends VehicleDAO {
        private final Map<Integer, Vehicle> map = new HashMap<>();
        @Override
        public Vehicle findById(int id) { return map.get(id); }
        public void save(Vehicle v) { map.put(v.getId(), v); }
    }

    private static class FakeRideDAO extends RideDAO {
        private final Map<Integer, Ride> map = new HashMap<>();
        private int seq = 1;

        @Override
        public boolean insert(Ride ride) {
            ride.setId(seq++);
            map.put(ride.getId(), ride);
            return true;
        }

        @Override
        public Ride findById(int id) {
            return map.get(id);
        }

        @Override
        public List<Ride> findByDriverId(int driverId) {
            List<Ride> list = new ArrayList<>();
            for (Ride r : map.values()) {
                if (r.getDriverId() == driverId) list.add(r);
            }
            return list;
        }

        @Override
        public List<Ride> searchRides(String source, String destination, Date date, 
                                      BigDecimal maxPrice, Integer minSeats, Time maxTime) {
            List<Ride> list = new ArrayList<>();
            for (Ride r : map.values()) {
                if (source != null && !r.getSource().equalsIgnoreCase(source)) continue;
                if (destination != null && !r.getDestination().equalsIgnoreCase(destination)) continue;
                if (date != null && !r.getRideDate().toString().equals(date.toString())) continue;
                list.add(r);
            }
            return list;
        }

        @Override
        public boolean update(Ride ride) {
            map.put(ride.getId(), ride);
            return true;
        }

        @Override
        public boolean updateStatus(int id, String status) {
            if (map.containsKey(id)) {
                map.get(id).setStatus(status);
                return true;
            }
            return false;
        }

        @Override
        public boolean deductSeatsAtomic(Connection conn, int id, int seatsToBook) throws SQLException {
            Ride r = map.get(id);
            if (r == null || !"ACTIVE".equals(r.getStatus()) || r.getAvailableSeats() < seatsToBook) {
                return false;
            }
            r.setAvailableSeats(r.getAvailableSeats() - seatsToBook);
            if (r.getAvailableSeats() == 0) {
                r.setStatus("FULL");
            }
            return true;
        }

        @Override
        public boolean restoreSeatsAtomic(Connection conn, int id, int seatsToRestore) throws SQLException {
            Ride r = map.get(id);
            if (r == null || r.getAvailableSeats() + seatsToRestore > r.getTotalSeats()) {
                return false;
            }
            r.setAvailableSeats(r.getAvailableSeats() + seatsToRestore);
            r.setStatus("ACTIVE");
            return true;
        }
    }

    private static class FakeRideBookingDAO extends RideBookingDAO {
        private final Map<Integer, RideBooking> map = new HashMap<>();
        private int seq = 1;

        @Override
        public boolean insert(Connection conn, RideBooking booking) throws SQLException {
            booking.setId(seq++);
            map.put(booking.getId(), booking);
            return true;
        }

        @Override
        public RideBooking findById(int id) {
            return map.get(id);
        }

        @Override
        public List<RideBooking> findByPassengerId(int passengerId) {
            List<RideBooking> list = new ArrayList<>();
            for (RideBooking b : map.values()) {
                if (b.getPassengerId() == passengerId) list.add(b);
            }
            return list;
        }

        @Override
        public boolean updateStatus(Connection conn, int id, String status) throws SQLException {
            if (map.containsKey(id)) {
                map.get(id).setStatus(status);
                return true;
            }
            return false;
        }
    }

    private static class FakeConnection {
        public static Connection create() {
            return (Connection) java.lang.reflect.Proxy.newProxyInstance(
                Connection.class.getClassLoader(),
                new Class<?>[]{Connection.class},
                (proxy, method, args) -> {
                    if ("setAutoCommit".equals(method.getName())) return null;
                    if ("commit".equals(method.getName())) return null;
                    if ("rollback".equals(method.getName())) return null;
                    if ("close".equals(method.getName())) return null;
                    return null;
                }
            );
        }
    }

    private FakePaymentDAO fakePaymentDAO;
    private FakeRatingDAO fakeRatingDAO;

    private static class FakePaymentDAO extends com.carsharing.dao.PaymentDAO {
        private final Map<Integer, com.carsharing.model.Payment> map = new HashMap<>();
        private int seq = 1;

        @Override
        public boolean insert(Connection conn, com.carsharing.model.Payment payment) {
            payment.setId(seq++);
            map.put(payment.getId(), payment);
            return true;
        }

        @Override
        public com.carsharing.model.Payment findByRideBookingId(int rideBookingId) {
            for (com.carsharing.model.Payment p : map.values()) {
                if (p.getRideBookingId() != null && p.getRideBookingId() == rideBookingId) {
                    return p;
                }
            }
            return null;
        }

        @Override
        public boolean updateStatus(Connection conn, int id, String status) {
            com.carsharing.model.Payment p = map.get(id);
            if (p != null) {
                p.setStatus(status);
                return true;
            }
            return false;
        }
    }

    private static class FakeRatingDAO extends com.carsharing.dao.RatingDAO {
    }

    @BeforeEach
    public void setUp() {
        fakeRideDAO = new FakeRideDAO();
        fakeRideBookingDAO = new FakeRideBookingDAO();
        fakeVehicleDAO = new FakeVehicleDAO();
        fakePaymentDAO = new FakePaymentDAO();
        fakeRatingDAO = new FakeRatingDAO();
        
        // Setup Vehicle
        Vehicle vehicle = new Vehicle(1, 10, "Toyota", "Camry", 2021, "MH-12-AB-1234", "Sedan", "Petrol", "Auto", "Red", 5, "AVAILABLE", null);
        fakeVehicleDAO.save(vehicle);

        rideService = new RideService(fakeRideDAO, fakeRideBookingDAO, fakeVehicleDAO, fakePaymentDAO, fakeRatingDAO) {
            @Override
            protected Connection getConnection() throws SQLException {
                return FakeConnection.create();
            }
        };
    }

    @Test
    public void testCreateValidRide() {
        Ride ride = new Ride();
        ride.setVehicleId(1);
        ride.setSource("Pune");
        ride.setDestination("Mumbai");
        ride.setRideDate(Date.valueOf("2026-09-01"));
        ride.setRideTime(Time.valueOf("08:00:00"));
        ride.setTotalSeats(4);
        ride.setPricePerSeat(new BigDecimal("300"));
        ride.setDescription("Commute route");

        boolean result = rideService.createRide(ride, 10); // user 10 is owner
        assertTrue(result);
        assertEquals("ACTIVE", ride.getStatus());
        assertEquals(4, ride.getAvailableSeats());
    }

    @Test
    public void testInvalidSourceDestinationIdentical() {
        Ride ride = new Ride();
        ride.setVehicleId(1);
        ride.setSource("Pune");
        ride.setDestination("Pune"); // Identical
        ride.setRideDate(Date.valueOf("2026-09-01"));
        ride.setRideTime(Time.valueOf("08:00:00"));
        ride.setTotalSeats(4);
        ride.setPricePerSeat(new BigDecimal("300"));

        boolean result = rideService.createRide(ride, 10);
        assertFalse(result);
    }

    @Test
    public void testSeatsExceedVehicleCapacity() {
        Ride ride = new Ride();
        ride.setVehicleId(1); // Capacity is 5
        ride.setSource("Pune");
        ride.setDestination("Mumbai");
        ride.setRideDate(Date.valueOf("2026-09-01"));
        ride.setRideTime(Time.valueOf("08:00:00"));
        ride.setTotalSeats(6); // Capacity exceeded (6 > 5)
        ride.setPricePerSeat(new BigDecimal("300"));

        boolean result = rideService.createRide(ride, 10);
        assertFalse(result);
    }

    @Test
    public void testVehicleOwnershipValidation() {
        Ride ride = new Ride();
        ride.setVehicleId(1); // Owned by 10
        ride.setSource("Pune");
        ride.setDestination("Mumbai");
        ride.setRideDate(Date.valueOf("2026-09-01"));
        ride.setRideTime(Time.valueOf("08:00:00"));
        ride.setTotalSeats(4);
        ride.setPricePerSeat(new BigDecimal("300"));

        // Try creating with driver 11 (does not own vehicle 1)
        boolean result = rideService.createRide(ride, 11);
        assertFalse(result);
    }

    @Test
    public void testSearchRides() {
        Ride r1 = new Ride(0, 10, 1, "Pune", "Mumbai", Date.valueOf("2026-09-01"), Time.valueOf("08:00:00"), 4, 4, new BigDecimal("300"), "", "ACTIVE", null);
        fakeRideDAO.insert(r1);

        List<Ride> list = rideService.searchRides("Pune", "Mumbai", Date.valueOf("2026-09-01"), null, null, null);
        assertEquals(1, list.size());
    }

    @Test
    public void testEditOwnRide() {
        Ride ride = new Ride(0, 10, 1, "Pune", "Mumbai", Date.valueOf("2026-09-01"), Time.valueOf("08:00:00"), 4, 4, new BigDecimal("300"), "", "ACTIVE", null);
        fakeRideDAO.insert(ride);

        ride.setSource("Pune Updated");
        boolean result = rideService.updateRide(ride, 10); // user 10 is driver
        assertTrue(result);
        assertEquals("Pune Updated", fakeRideDAO.findById(ride.getId()).getSource());
    }

    @Test
    public void testPreventEditingAnotherUsersRide() {
        Ride ride = new Ride(0, 10, 1, "Pune", "Mumbai", Date.valueOf("2026-09-01"), Time.valueOf("08:00:00"), 4, 4, new BigDecimal("300"), "", "ACTIVE", null);
        fakeRideDAO.insert(ride);

        ride.setSource("Hack source");
        boolean result = rideService.updateRide(ride, 11); // user 11 is not driver
        assertFalse(result);
    }

    @Test
    public void testCancelOwnRide() {
        Ride ride = new Ride(0, 10, 1, "Pune", "Mumbai", Date.valueOf("2026-09-01"), Time.valueOf("08:00:00"), 4, 4, new BigDecimal("300"), "", "ACTIVE", null);
        fakeRideDAO.insert(ride);

        boolean result = rideService.cancelRide(ride.getId(), 10);
        assertTrue(result);
        assertEquals("CANCELLED", fakeRideDAO.findById(ride.getId()).getStatus());
    }

    @Test
    public void testPreventUnauthorizedCancellation() {
        Ride ride = new Ride(0, 10, 1, "Pune", "Mumbai", Date.valueOf("2026-09-01"), Time.valueOf("08:00:00"), 4, 4, new BigDecimal("300"), "", "ACTIVE", null);
        fakeRideDAO.insert(ride);

        boolean result = rideService.cancelRide(ride.getId(), 11); // user 11
        assertFalse(result);
    }

    @Test
    public void testValidSeatBooking() {
        Ride ride = new Ride(0, 10, 1, "Pune", "Mumbai", Date.valueOf("2026-09-01"), Time.valueOf("08:00:00"), 4, 4, new BigDecimal("300"), "", "ACTIVE", null);
        fakeRideDAO.insert(ride);

        // Book 2 seats by passenger 12
        boolean result = rideService.bookSeats(ride.getId(), 12, 2);
        assertTrue(result);
        assertEquals(2, fakeRideDAO.findById(ride.getId()).getAvailableSeats());
    }

    @Test
    public void testBookingMoreSeatsThanAvailable() {
        Ride ride = new Ride(0, 10, 1, "Pune", "Mumbai", Date.valueOf("2026-09-01"), Time.valueOf("08:00:00"), 4, 4, new BigDecimal("300"), "", "ACTIVE", null);
        fakeRideDAO.insert(ride);

        boolean result = rideService.bookSeats(ride.getId(), 12, 5); // Available is 4, requesting 5
        assertFalse(result);
    }

    @Test
    public void testPreventDriverFromBookingOwnRide() {
        Ride ride = new Ride(0, 10, 1, "Pune", "Mumbai", Date.valueOf("2026-09-01"), Time.valueOf("08:00:00"), 4, 4, new BigDecimal("300"), "", "ACTIVE", null);
        fakeRideDAO.insert(ride);

        boolean result = rideService.bookSeats(ride.getId(), 10, 2); // Driver is 10
        assertFalse(result);
    }

    @Test
    public void testBookingStatusChangesToFullWhenZero() {
        Ride ride = new Ride(0, 10, 1, "Pune", "Mumbai", Date.valueOf("2026-09-01"), Time.valueOf("08:00:00"), 4, 4, new BigDecimal("300"), "", "ACTIVE", null);
        fakeRideDAO.insert(ride);

        boolean result = rideService.bookSeats(ride.getId(), 12, 4); // Book all 4
        assertTrue(result);
        assertEquals(0, fakeRideDAO.findById(ride.getId()).getAvailableSeats());
        assertEquals("FULL", fakeRideDAO.findById(ride.getId()).getStatus());
    }

    @Test
    public void testBookingCancellationRestoresSeats() {
        Ride ride = new Ride(0, 10, 1, "Pune", "Mumbai", Date.valueOf("2026-09-01"), Time.valueOf("08:00:00"), 4, 4, new BigDecimal("300"), "", "ACTIVE", null);
        fakeRideDAO.insert(ride);

        rideService.bookSeats(ride.getId(), 12, 2);
        // Find generated booking
        List<RideBooking> bookings = fakeRideBookingDAO.findByPassengerId(12);
        assertEquals(1, bookings.size());
        RideBooking booking = bookings.get(0);

        boolean cancelResult = rideService.cancelBooking(booking.getId(), 12);
        assertTrue(cancelResult);
        assertEquals(4, fakeRideDAO.findById(ride.getId()).getAvailableSeats());
        assertEquals("CANCELLED", fakeRideBookingDAO.findById(booking.getId()).getStatus());
    }
}
