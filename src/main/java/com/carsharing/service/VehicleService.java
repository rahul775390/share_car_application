package com.carsharing.service;

import com.carsharing.dao.VehicleDAO;
import com.carsharing.model.Vehicle;

import java.util.List;

public class VehicleService {
    private final VehicleDAO vehicleDAO;

    public VehicleService() {
        this.vehicleDAO = new VehicleDAO();
    }

    // Constructor for testing / dependency injection
    public VehicleService(VehicleDAO vehicleDAO) {
        this.vehicleDAO = vehicleDAO;
    }

    /**
     * Validates and inserts a new vehicle.
     */
    public boolean addVehicle(Vehicle vehicle) {
        if (vehicle == null) return false;
        
        if (!validateVehicleDetails(vehicle)) {
            return false;
        }

        // Check duplicate license plate
        Vehicle existing = vehicleDAO.findByLicensePlate(vehicle.getLicensePlate().trim());
        if (existing != null && !"REMOVED".equals(existing.getStatus())) {
            return false;
        }

        vehicle.setLicensePlate(vehicle.getLicensePlate().trim());
        vehicle.setStatus("AVAILABLE"); // default initial status
        return vehicleDAO.insert(vehicle);
    }

    /**
     * Retrieves all active vehicles belonging to an owner.
     */
    public List<Vehicle> getVehiclesByOwner(int ownerId) {
        return vehicleDAO.findByOwnerId(ownerId);
    }

    /**
     * Retrieves a vehicle by ID.
     */
    public Vehicle getVehicleById(int id) {
        return vehicleDAO.findById(id);
    }

    /**
     * Updates an existing vehicle's detail properties, verifying ownership first.
     */
    public boolean updateVehicle(Vehicle vehicle, int userId) {
        if (vehicle == null) return false;
        
        Vehicle dbVehicle = vehicleDAO.findById(vehicle.getId());
        if (dbVehicle == null || dbVehicle.getOwnerId() != userId) {
            return false; // Not owner or not found
        }

        if (!validateVehicleDetails(vehicle)) {
            return false;
        }

        // Check duplicate license plate for other vehicles
        Vehicle existing = vehicleDAO.findByLicensePlate(vehicle.getLicensePlate().trim());
        if (existing != null && existing.getId() != vehicle.getId() && !"REMOVED".equals(existing.getStatus())) {
            return false;
        }

        // Keep database status and ownerId intact
        vehicle.setOwnerId(dbVehicle.getOwnerId());
        vehicle.setStatus(dbVehicle.getStatus());
        vehicle.setLicensePlate(vehicle.getLicensePlate().trim());
        
        return vehicleDAO.update(vehicle);
    }

    /**
     * Soft deletes a vehicle, verifying ownership.
     */
    public boolean deleteVehicle(int id, int userId) {
        Vehicle dbVehicle = vehicleDAO.findById(id);
        if (dbVehicle == null || dbVehicle.getOwnerId() != userId) {
            return false; // Not owner or not found
        }
        return vehicleDAO.delete(id);
    }

    /**
     * Toggles a vehicle's status (AVAILABLE/UNAVAILABLE), verifying ownership.
     */
    public boolean updateVehicleStatus(int id, String status, int userId) {
        Vehicle dbVehicle = vehicleDAO.findById(id);
        if (dbVehicle == null || dbVehicle.getOwnerId() != userId) {
            return false; // Not owner or not found
        }
        
        if (!"AVAILABLE".equalsIgnoreCase(status) && !"UNAVAILABLE".equalsIgnoreCase(status)) {
            return false; // Arbitrary status not allowed
        }
        
        return vehicleDAO.updateStatus(id, status.toUpperCase());
    }

    private boolean validateVehicleDetails(Vehicle v) {
        if (v.getMake() == null || v.getMake().trim().isEmpty() ||
            v.getModel() == null || v.getModel().trim().isEmpty() ||
            v.getType() == null || v.getType().trim().isEmpty() ||
            v.getFuelType() == null || v.getFuelType().trim().isEmpty() ||
            v.getTransmission() == null || v.getTransmission().trim().isEmpty() ||
            v.getColor() == null || v.getColor().trim().isEmpty() ||
            v.getLicensePlate() == null || v.getLicensePlate().trim().isEmpty()) {
            return false;
        }

        // Manufacturing Year check (between 1900 and current year + 1)
        int currentYear = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR);
        if (v.getYear() < 1900 || v.getYear() > currentYear + 1) {
            return false;
        }

        // Seating Capacity check (between 1 and 20)
        if (v.getSeatingCapacity() < 1 || v.getSeatingCapacity() > 20) {
            return false;
        }

        return true;
    }
}
