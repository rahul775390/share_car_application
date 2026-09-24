package com.carsharing.controller;

import com.carsharing.model.User;
import com.carsharing.model.Vehicle;
import com.carsharing.model.Ride;
import com.carsharing.service.RideService;
import com.carsharing.service.VehicleService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Time;
import java.util.List;

@WebServlet(urlPatterns = {"/rides", "/rides/*"})
public class RideServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private RideService rideService;
    private VehicleService vehicleService;

    @Override
    public void init() throws ServletException {
        this.rideService = new RideService();
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

        if (pathInfo == null || "/".equals(pathInfo) || "/my".equals(pathInfo)) {
            // View My Rides (Driver)
            List<Ride> rides = rideService.getRidesByDriver(user.getId());
            request.setAttribute("rides", rides);
            request.getRequestDispatcher("/WEB-INF/views/rides/my.jsp").forward(request, response);
            
        } else if ("/create".equals(pathInfo)) {
            // Create Ride page
            List<Vehicle> vehicles = vehicleService.getVehiclesByOwner(user.getId());
            request.setAttribute("vehicles", vehicles);
            request.getRequestDispatcher("/WEB-INF/views/rides/create.jsp").forward(request, response);
            
        } else if ("/search".equals(pathInfo)) {
            // Search Ride page
            String src = request.getParameter("source");
            String dest = request.getParameter("destination");
            String dateStr = request.getParameter("date");
            String maxPriceStr = request.getParameter("maxPrice");

            List<Ride> rides = null;
            if (src != null || dest != null || dateStr != null) {
                Date date = null;
                if (dateStr != null && !dateStr.trim().isEmpty()) {
                    date = Date.valueOf(dateStr);
                }
                BigDecimal maxPrice = null;
                if (maxPriceStr != null && !maxPriceStr.trim().isEmpty()) {
                    maxPrice = new BigDecimal(maxPriceStr.trim());
                }
                rides = rideService.searchRides(src, dest, date, maxPrice, null, null);
            }
            request.setAttribute("rides", rides);
            request.getRequestDispatcher("/WEB-INF/views/rides/search.jsp").forward(request, response);
            
        } else if ("/edit".equals(pathInfo)) {
            // Edit Ride page
            String idStr = request.getParameter("id");
            if (idStr == null) {
                response.sendRedirect(request.getContextPath() + "/rides");
                return;
            }
            int id = Integer.parseInt(idStr);
            Ride ride = rideService.getRideById(id);
            if (ride == null || ride.getDriverId() != user.getId()) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied: You are not the driver.");
                return;
            }
            request.setAttribute("ride", ride);
            request.getRequestDispatcher("/WEB-INF/views/rides/edit.jsp").forward(request, response);
            
        } else if ("/details".equals(pathInfo)) {
            // Ride Details
            String idStr = request.getParameter("id");
            if (idStr == null) {
                response.sendRedirect(request.getContextPath() + "/rides");
                return;
            }
            int id = Integer.parseInt(idStr);
            Ride ride = rideService.getRideById(id);
            if (ride == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Ride not found.");
                return;
            }
            request.setAttribute("ride", ride);
            request.getRequestDispatcher("/WEB-INF/views/rides/details.jsp").forward(request, response);
            
        } else if ("/cancel".equals(pathInfo)) {
            // Cancel ride
            String idStr = request.getParameter("id");
            if (idStr != null) {
                int id = Integer.parseInt(idStr);
                boolean success = rideService.cancelRide(id, user.getId());
                if (!success) {
                    response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied or Cancellation Failed.");
                    return;
                }
            }
            response.sendRedirect(request.getContextPath() + "/rides?cancelled=true");
            
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

        if ("/create".equals(pathInfo)) {
            String vehicleIdStr = request.getParameter("vehicleId");
            String source = request.getParameter("source");
            String destination = request.getParameter("destination");
            String dateStr = request.getParameter("date");
            String timeStr = request.getParameter("time");
            String seatsStr = request.getParameter("seats");
            String priceStr = request.getParameter("price");
            String description = request.getParameter("description");

            if (isEmpty(vehicleIdStr) || isEmpty(source) || isEmpty(destination) || 
                isEmpty(dateStr) || isEmpty(timeStr) || isEmpty(seatsStr) || isEmpty(priceStr)) {
                
                request.setAttribute("error", "All fields are required.");
                request.setAttribute("vehicles", vehicleService.getVehiclesByOwner(user.getId()));
                request.getRequestDispatcher("/WEB-INF/views/rides/create.jsp").forward(request, response);
                return;
            }

            try {
                int vehicleId = Integer.parseInt(vehicleIdStr.trim());
                int totalSeats = Integer.parseInt(seatsStr.trim());
                BigDecimal price = new BigDecimal(priceStr.trim());
                Date rideDate = Date.valueOf(dateStr.trim());
                Time rideTime = Time.valueOf(timeStr.trim() + ":00");

                Ride ride = new Ride();
                ride.setVehicleId(vehicleId);
                ride.setSource(source.trim());
                ride.setDestination(destination.trim());
                ride.setRideDate(rideDate);
                ride.setRideTime(rideTime);
                ride.setTotalSeats(totalSeats);
                ride.setPricePerSeat(price);
                ride.setDescription(description != null ? description.trim() : "");

                boolean success = rideService.createRide(ride, user.getId());
                if (success) {
                    response.sendRedirect(request.getContextPath() + "/rides?added=true");
                } else {
                    request.setAttribute("error", "Failed to create ride. Check vehicle capacity, ownership or unavailable status.");
                    request.setAttribute("vehicles", vehicleService.getVehiclesByOwner(user.getId()));
                    request.getRequestDispatcher("/WEB-INF/views/rides/create.jsp").forward(request, response);
                }
            } catch (Exception e) {
                request.setAttribute("error", "Invalid inputs. Make sure seat count, price, dates, and times are correctly formatted.");
                request.setAttribute("vehicles", vehicleService.getVehiclesByOwner(user.getId()));
                request.getRequestDispatcher("/WEB-INF/views/rides/create.jsp").forward(request, response);
            }

        } else if ("/edit".equals(pathInfo)) {
            String idStr = request.getParameter("id");
            String source = request.getParameter("source");
            String destination = request.getParameter("destination");
            String dateStr = request.getParameter("date");
            String timeStr = request.getParameter("time");
            String description = request.getParameter("description");

            if (isEmpty(idStr) || isEmpty(source) || isEmpty(destination) || isEmpty(dateStr) || isEmpty(timeStr)) {
                request.setAttribute("error", "All fields are required.");
                int id = Integer.parseInt(idStr);
                request.setAttribute("ride", rideService.getRideById(id));
                request.getRequestDispatcher("/WEB-INF/views/rides/edit.jsp").forward(request, response);
                return;
            }

            try {
                int id = Integer.parseInt(idStr.trim());
                Date rideDate = Date.valueOf(dateStr.trim());
                Time rideTime = Time.valueOf(timeStr.trim().length() == 5 ? timeStr.trim() + ":00" : timeStr.trim());

                Ride ride = new Ride();
                ride.setId(id);
                ride.setSource(source.trim());
                ride.setDestination(destination.trim());
                ride.setRideDate(rideDate);
                ride.setRideTime(rideTime);
                ride.setDescription(description != null ? description.trim() : "");

                boolean success = rideService.updateRide(ride, user.getId());
                if (success) {
                    response.sendRedirect(request.getContextPath() + "/rides?updated=true");
                } else {
                    request.setAttribute("error", "Failed to update ride. Ensure source and destination are different.");
                    request.setAttribute("ride", rideService.getRideById(id));
                    request.getRequestDispatcher("/WEB-INF/views/rides/edit.jsp").forward(request, response);
                }
            } catch (Exception e) {
                request.setAttribute("error", "Failed to update ride. Please verify date and time formatting.");
                int id = Integer.parseInt(idStr);
                request.setAttribute("ride", rideService.getRideById(id));
                request.getRequestDispatcher("/WEB-INF/views/rides/edit.jsp").forward(request, response);
            }
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    private boolean isEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }
}
