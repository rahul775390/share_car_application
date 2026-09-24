package com.carsharing.service;

import com.carsharing.dao.AdminDAO;
import com.carsharing.model.Ride;
import com.carsharing.model.User;
import com.carsharing.model.Vehicle;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdminService {
    private final AdminDAO adminDAO;

    public AdminService() {
        this.adminDAO = new AdminDAO();
    }

    public AdminService(AdminDAO adminDAO) {
        this.adminDAO = adminDAO;
    }

    /**
     * Aggregates stats, users, vehicles, and rides for the admin dashboard.
     */
    public Map<String, Object> getDashboardData() {
        Map<String, Object> data = new HashMap<>();
        data.put("stats", adminDAO.getSystemStatistics());
        data.put("users", adminDAO.getAllUsers());
        data.put("vehicles", adminDAO.getAllVehiclesWithOwners());
        data.put("rides", adminDAO.getAllRidesWithDetails());
        return data;
    }

    public boolean toggleUserStatus(int userId, String status) {
        if (!"ACTIVE".equalsIgnoreCase(status) && !"SUSPENDED".equalsIgnoreCase(status)) {
            return false;
        }
        return adminDAO.updateUserStatus(userId, status);
    }

    public boolean changeUserRole(int userId, String role) {
        if (!"USER".equalsIgnoreCase(role) && !"ADMIN".equalsIgnoreCase(role)) {
            return false;
        }
        return adminDAO.updateUserRole(userId, role);
    }

    public boolean cancelRideAsAdmin(int rideId) {
        return adminDAO.cancelRide(rideId);
    }
}
