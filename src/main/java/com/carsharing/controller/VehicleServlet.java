package com.carsharing.controller;

import com.carsharing.model.User;
import com.carsharing.model.Vehicle;
import com.carsharing.service.VehicleService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

@WebServlet(urlPatterns = {"/vehicles", "/vehicles/*"})
public class VehicleServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private VehicleService vehicleService;

    @Override
    public void init() throws ServletException {
        this.vehicleService = new VehicleService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/auth/login");
            return;
        }

        User user = (User) session.getAttribute("user");
        String pathInfo = request.getPathInfo();

        if (pathInfo == null || "/".equals(pathInfo)) {
            // View My Vehicles
            List<Vehicle> vehicles = vehicleService.getVehiclesByOwner(user.getId());
            request.setAttribute("vehicles", vehicles);
            request.getRequestDispatcher("/WEB-INF/views/user/vehicles.jsp").forward(request, response);
            
        } else if ("/add".equals(pathInfo)) {
            // Add vehicle page
            request.getRequestDispatcher("/WEB-INF/views/user/vehicle_add.jsp").forward(request, response);
            
        } else if ("/edit".equals(pathInfo)) {
            // Edit vehicle page
            String idStr = request.getParameter("id");
            if (idStr == null) {
                response.sendRedirect(request.getContextPath() + "/vehicles");
                return;
            }
            int id = Integer.parseInt(idStr);
            Vehicle vehicle = vehicleService.getVehicleById(id);
            if (vehicle == null || vehicle.getOwnerId() != user.getId()) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied: You do not own this vehicle.");
                return;
            }
            request.setAttribute("vehicle", vehicle);
            request.getRequestDispatcher("/WEB-INF/views/user/vehicle_edit.jsp").forward(request, response);
            
        } else if ("/details".equals(pathInfo)) {
            // Details page
            String idStr = request.getParameter("id");
            if (idStr == null) {
                response.sendRedirect(request.getContextPath() + "/vehicles");
                return;
            }
            int id = Integer.parseInt(idStr);
            Vehicle vehicle = vehicleService.getVehicleById(id);
            if (vehicle == null || "REMOVED".equals(vehicle.getStatus())) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Vehicle not found.");
                return;
            }
            request.setAttribute("vehicle", vehicle);
            request.getRequestDispatcher("/WEB-INF/views/user/vehicle_details.jsp").forward(request, response);
            
        } else if ("/delete".equals(pathInfo)) {
            // Delete action
            String idStr = request.getParameter("id");
            if (idStr != null) {
                int id = Integer.parseInt(idStr);
                boolean success = vehicleService.deleteVehicle(id, user.getId());
                if (!success) {
                    response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied or Delete Failed.");
                    return;
                }
            }
            response.sendRedirect(request.getContextPath() + "/vehicles?deleted=true");
            
        } else if ("/status".equals(pathInfo)) {
            // Change status action
            String idStr = request.getParameter("id");
            String status = request.getParameter("status");
            if (idStr != null && status != null) {
                int id = Integer.parseInt(idStr);
                boolean success = vehicleService.updateVehicleStatus(id, status, user.getId());
                if (!success) {
                    response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied or Status Change Failed.");
                    return;
                }
            }
            response.sendRedirect(request.getContextPath() + "/vehicles?statusUpdated=true");
            
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/auth/login");
            return;
        }

        User user = (User) session.getAttribute("user");
        String pathInfo = request.getPathInfo();

        if ("/add".equals(pathInfo)) {
            String make = request.getParameter("make");
            String model = request.getParameter("model");
            String yearStr = request.getParameter("year");
            String licensePlate = request.getParameter("licensePlate");
            String type = request.getParameter("type");
            String fuelType = request.getParameter("fuelType");
            String transmission = request.getParameter("transmission");
            String color = request.getParameter("color");
            String seatingCapacityStr = request.getParameter("seatingCapacity");

            if (isEmpty(make) || isEmpty(model) || isEmpty(yearStr) || isEmpty(licensePlate) || 
                isEmpty(type) || isEmpty(fuelType) || isEmpty(transmission) || isEmpty(color) || isEmpty(seatingCapacityStr)) {
                
                request.setAttribute("error", "All fields are required.");
                request.getRequestDispatcher("/WEB-INF/views/user/vehicle_add.jsp").forward(request, response);
                return;
            }

            try {
                int year = Integer.parseInt(yearStr.trim());
                int seatingCapacity = Integer.parseInt(seatingCapacityStr.trim());

                Vehicle vehicle = new Vehicle();
                vehicle.setOwnerId(user.getId());
                vehicle.setMake(make.trim());
                vehicle.setModel(model.trim());
                vehicle.setYear(year);
                vehicle.setLicensePlate(licensePlate.trim());
                vehicle.setType(type.trim());
                vehicle.setFuelType(fuelType.trim());
                vehicle.setTransmission(transmission.trim());
                vehicle.setColor(color.trim());
                vehicle.setSeatingCapacity(seatingCapacity);

                boolean success = vehicleService.addVehicle(vehicle);
                if (success) {
                    response.sendRedirect(request.getContextPath() + "/vehicles?added=true");
                } else {
                    request.setAttribute("error", "Failed to add vehicle. License plate might be duplicate or invalid data.");
                    request.getRequestDispatcher("/WEB-INF/views/user/vehicle_add.jsp").forward(request, response);
                }
            } catch (NumberFormatException e) {
                request.setAttribute("error", "Year and Seating Capacity must be valid integers.");
                request.getRequestDispatcher("/WEB-INF/views/user/vehicle_add.jsp").forward(request, response);
            }

        } else if ("/edit".equals(pathInfo)) {
            String idStr = request.getParameter("id");
            String make = request.getParameter("make");
            String model = request.getParameter("model");
            String yearStr = request.getParameter("year");
            String licensePlate = request.getParameter("licensePlate");
            String type = request.getParameter("type");
            String fuelType = request.getParameter("fuelType");
            String transmission = request.getParameter("transmission");
            String color = request.getParameter("color");
            String seatingCapacityStr = request.getParameter("seatingCapacity");

            if (isEmpty(idStr) || isEmpty(make) || isEmpty(model) || isEmpty(yearStr) || isEmpty(licensePlate) || 
                isEmpty(type) || isEmpty(fuelType) || isEmpty(transmission) || isEmpty(color) || isEmpty(seatingCapacityStr)) {
                
                request.setAttribute("error", "All fields are required.");
                // Reload vehicle
                int id = Integer.parseInt(idStr);
                request.setAttribute("vehicle", vehicleService.getVehicleById(id));
                request.getRequestDispatcher("/WEB-INF/views/user/vehicle_edit.jsp").forward(request, response);
                return;
            }

            try {
                int id = Integer.parseInt(idStr.trim());
                int year = Integer.parseInt(yearStr.trim());
                int seatingCapacity = Integer.parseInt(seatingCapacityStr.trim());

                Vehicle vehicle = new Vehicle();
                vehicle.setId(id);
                vehicle.setMake(make.trim());
                vehicle.setModel(model.trim());
                vehicle.setYear(year);
                vehicle.setLicensePlate(licensePlate.trim());
                vehicle.setType(type.trim());
                vehicle.setFuelType(fuelType.trim());
                vehicle.setTransmission(transmission.trim());
                vehicle.setColor(color.trim());
                vehicle.setSeatingCapacity(seatingCapacity);

                boolean success = vehicleService.updateVehicle(vehicle, user.getId());
                if (success) {
                    response.sendRedirect(request.getContextPath() + "/vehicles?updated=true");
                } else {
                    request.setAttribute("error", "Failed to update vehicle. License plate might be duplicate or invalid data.");
                    request.setAttribute("vehicle", vehicleService.getVehicleById(id));
                    request.getRequestDispatcher("/WEB-INF/views/user/vehicle_edit.jsp").forward(request, response);
                }
            } catch (NumberFormatException e) {
                request.setAttribute("error", "Year and Seating Capacity must be valid integers.");
                int id = Integer.parseInt(idStr);
                request.setAttribute("vehicle", vehicleService.getVehicleById(id));
                request.getRequestDispatcher("/WEB-INF/views/user/vehicle_edit.jsp").forward(request, response);
            }
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    private boolean isEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }
}
