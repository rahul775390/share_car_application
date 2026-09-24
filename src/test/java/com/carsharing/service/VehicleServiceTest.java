package com.carsharing.service;

import com.carsharing.dao.VehicleDAO;
import com.carsharing.model.Vehicle;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class VehicleServiceTest {

    private VehicleService vehicleService;
    private FakeVehicleDAO fakeVehicleDAO;

    private static class FakeVehicleDAO extends VehicleDAO {
        private final Map<Integer, Vehicle> idMap = new HashMap<>();
        private final Map<String, Vehicle> plateMap = new HashMap<>();
        private int sequence = 1;

        @Override
        public boolean insert(Vehicle vehicle) {
            if (plateMap.containsKey(vehicle.getLicensePlate())) {
                Vehicle existing = plateMap.get(vehicle.getLicensePlate());
                if (!"REMOVED".equals(existing.getStatus())) {
                    return false;
                }
            }
            vehicle.setId(sequence++);
            idMap.put(vehicle.getId(), vehicle);
            plateMap.put(vehicle.getLicensePlate(), vehicle);
            return true;
        }

        @Override
        public Vehicle findById(int id) {
            return idMap.get(id);
        }

        @Override
        public Vehicle findByLicensePlate(String licensePlate) {
            return plateMap.get(licensePlate);
        }

        @Override
        public List<Vehicle> findByOwnerId(int ownerId) {
            List<Vehicle> list = new ArrayList<>();
            for (Vehicle v : idMap.values()) {
                if (v.getOwnerId() == ownerId && !"REMOVED".equals(v.getStatus())) {
                    list.add(v);
                }
            }
            return list;
        }

        @Override
        public boolean update(Vehicle vehicle) {
            if (!idMap.containsKey(vehicle.getId())) {
                return false;
            }
            idMap.put(vehicle.getId(), vehicle);
            plateMap.put(vehicle.getLicensePlate(), vehicle);
            return true;
        }

        @Override
        public boolean delete(int id) {
            if (!idMap.containsKey(id)) {
                return false;
            }
            idMap.get(id).setStatus("REMOVED");
            return true;
        }

        @Override
        public boolean updateStatus(int id, String status) {
            if (!idMap.containsKey(id)) {
                return false;
            }
            idMap.get(id).setStatus(status.toUpperCase());
            return true;
        }
    }

    @BeforeEach
    public void setUp() {
        fakeVehicleDAO = new FakeVehicleDAO();
        vehicleService = new VehicleService(fakeVehicleDAO);
    }

    @Test
    public void testAddValidVehicle() {
        Vehicle vehicle = new Vehicle();
        vehicle.setOwnerId(10);
        vehicle.setMake("Toyota");
        vehicle.setModel("Camry");
        vehicle.setYear(2021);
        vehicle.setLicensePlate("MH-12-AB-1234");
        vehicle.setType("Sedan");
        vehicle.setFuelType("Petrol");
        vehicle.setTransmission("Automatic");
        vehicle.setColor("Red");
        vehicle.setSeatingCapacity(5);

        boolean result = vehicleService.addVehicle(vehicle);
        assertTrue(result);
        assertEquals("AVAILABLE", vehicle.getStatus());
        assertEquals(1, vehicle.getId());
    }

    @Test
    public void testDuplicateRegistrationNumber() {
        Vehicle vehicle1 = new Vehicle();
        vehicle1.setOwnerId(10);
        vehicle1.setMake("Toyota");
        vehicle1.setModel("Camry");
        vehicle1.setYear(2021);
        vehicle1.setLicensePlate("MH-12-AB-1234");
        vehicle1.setType("Sedan");
        vehicle1.setFuelType("Petrol");
        vehicle1.setTransmission("Automatic");
        vehicle1.setColor("Red");
        vehicle1.setSeatingCapacity(5);
        vehicleService.addVehicle(vehicle1);

        Vehicle vehicle2 = new Vehicle();
        vehicle2.setOwnerId(11);
        vehicle2.setMake("Honda");
        vehicle2.setModel("Civic");
        vehicle2.setYear(2022);
        vehicle2.setLicensePlate("MH-12-AB-1234"); // Duplicate Plate
        vehicle2.setType("Sedan");
        vehicle2.setFuelType("Petrol");
        vehicle2.setTransmission("Automatic");
        vehicle2.setColor("Blue");
        vehicle2.setSeatingCapacity(5);

        boolean result = vehicleService.addVehicle(vehicle2);
        assertFalse(result);
    }

    @Test
    public void testInvalidVehicleData() {
        // Seating capacity out of bounds (> 20)
        Vehicle vehicle = new Vehicle();
        vehicle.setOwnerId(10);
        vehicle.setMake("Toyota");
        vehicle.setModel("Camry");
        vehicle.setYear(2021);
        vehicle.setLicensePlate("MH-12-AB-9999");
        vehicle.setType("Sedan");
        vehicle.setFuelType("Petrol");
        vehicle.setTransmission("Automatic");
        vehicle.setColor("Red");
        vehicle.setSeatingCapacity(25); // Invalid capacity

        assertFalse(vehicleService.addVehicle(vehicle));

        // Manufacturing year in future
        vehicle.setSeatingCapacity(5);
        vehicle.setYear(2050); // Future Year
        assertFalse(vehicleService.addVehicle(vehicle));
    }

    @Test
    public void testViewUsersVehicles() {
        Vehicle v1 = new Vehicle(0, 10, "Toyota", "Camry", 2021, "PL-1", "Sedan", "Petrol", "Auto", "Red", 5, "AVAILABLE", null);
        Vehicle v2 = new Vehicle(0, 10, "Honda", "Civic", 2022, "PL-2", "Sedan", "Petrol", "Auto", "Blue", 5, "AVAILABLE", null);
        Vehicle v3 = new Vehicle(0, 11, "Suzuki", "Swift", 2020, "PL-3", "Hatchback", "Petrol", "Manual", "Gray", 5, "AVAILABLE", null);

        vehicleService.addVehicle(v1);
        vehicleService.addVehicle(v2);
        vehicleService.addVehicle(v3);

        List<Vehicle> list = vehicleService.getVehiclesByOwner(10);
        assertEquals(2, list.size());
    }

    @Test
    public void testViewVehicleDetails() {
        Vehicle vehicle = new Vehicle(0, 10, "Toyota", "Camry", 2021, "MH-12-AB-1234", "Sedan", "Petrol", "Auto", "Red", 5, "AVAILABLE", null);
        vehicleService.addVehicle(vehicle);

        Vehicle details = vehicleService.getVehicleById(vehicle.getId());
        assertNotNull(details);
        assertEquals("Camry", details.getModel());
    }

    @Test
    public void testEditOwnVehicle() {
        Vehicle vehicle = new Vehicle(0, 10, "Toyota", "Camry", 2021, "MH-12-AB-1234", "Sedan", "Petrol", "Auto", "Red", 5, "AVAILABLE", null);
        vehicleService.addVehicle(vehicle);

        vehicle.setMake("Toyota Updated");
        boolean result = vehicleService.updateVehicle(vehicle, 10); // Edited by owner 10
        assertTrue(result);
        assertEquals("Toyota Updated", fakeVehicleDAO.findById(vehicle.getId()).getMake());
    }

    @Test
    public void testEditAnotherUsersVehicleFails() {
        Vehicle vehicle = new Vehicle(0, 10, "Toyota", "Camry", 2021, "MH-12-AB-1234", "Sedan", "Petrol", "Auto", "Red", 5, "AVAILABLE", null);
        vehicleService.addVehicle(vehicle);

        vehicle.setMake("Hack Attempt");
        boolean result = vehicleService.updateVehicle(vehicle, 11); // Edited by user 11 (Not owner)
        assertFalse(result);
    }

    @Test
    public void testDeleteOwnVehicle() {
        Vehicle vehicle = new Vehicle(0, 10, "Toyota", "Camry", 2021, "MH-12-AB-1234", "Sedan", "Petrol", "Auto", "Red", 5, "AVAILABLE", null);
        vehicleService.addVehicle(vehicle);

        boolean result = vehicleService.deleteVehicle(vehicle.getId(), 10); // Deleted by owner 10
        assertTrue(result);
        assertEquals("REMOVED", fakeVehicleDAO.findById(vehicle.getId()).getStatus());
    }

    @Test
    public void testDeleteAnotherUsersVehicleFails() {
        Vehicle vehicle = new Vehicle(0, 10, "Toyota", "Camry", 2021, "MH-12-AB-1234", "Sedan", "Petrol", "Auto", "Red", 5, "AVAILABLE", null);
        vehicleService.addVehicle(vehicle);

        boolean result = vehicleService.deleteVehicle(vehicle.getId(), 11); // Deleted by user 11
        assertFalse(result);
    }

    @Test
    public void testChangeVehicleStatus() {
        Vehicle vehicle = new Vehicle(0, 10, "Toyota", "Camry", 2021, "MH-12-AB-1234", "Sedan", "Petrol", "Auto", "Red", 5, "AVAILABLE", null);
        vehicleService.addVehicle(vehicle);

        boolean result = vehicleService.updateVehicleStatus(vehicle.getId(), "UNAVAILABLE", 10);
        assertTrue(result);
        assertEquals("UNAVAILABLE", fakeVehicleDAO.findById(vehicle.getId()).getStatus());
    }
}
