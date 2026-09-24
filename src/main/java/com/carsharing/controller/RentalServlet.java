package com.carsharing.controller;

import com.carsharing.model.RentalBooking;
import com.carsharing.model.RentalListing;
import com.carsharing.model.User;
import com.carsharing.model.Vehicle;
import com.carsharing.service.RentalService;
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
import java.util.List;

@WebServlet(urlPatterns = {"/rentals", "/rentals/*"})
public class RentalServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private RentalService rentalService;
    private VehicleService vehicleService;

    @Override
    public void init() throws ServletException {
        this.rentalService = new RentalService();
        this.vehicleService = new VehicleService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        HttpSession session = request.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("user") : null;

        if (pathInfo == null || "/".equals(pathInfo) || "/search".equals(pathInfo)) {
            // Search / browse rental cars
            String loc = request.getParameter("location");
            String fromStr = request.getParameter("from");
            String untilStr = request.getParameter("until");
            String maxPriceStr = request.getParameter("maxPrice");

            Date fromDate = (fromStr != null && !fromStr.trim().isEmpty()) ? Date.valueOf(fromStr.trim()) : null;
            Date untilDate = (untilStr != null && !untilStr.trim().isEmpty()) ? Date.valueOf(untilStr.trim()) : null;
            BigDecimal maxPrice = (maxPriceStr != null && !maxPriceStr.trim().isEmpty()) ? new BigDecimal(maxPriceStr.trim()) : null;

            List<RentalListing> listings = rentalService.searchListings(loc, fromDate, untilDate, maxPrice);
            request.setAttribute("listings", listings);
            request.getRequestDispatcher("/WEB-INF/views/rentals/search.jsp").forward(request, response);

        } else if ("/details".equals(pathInfo)) {
            // Rental Listing Details
            String idStr = request.getParameter("id");
            if (idStr == null) {
                response.sendRedirect(request.getContextPath() + "/rentals");
                return;
            }
            int id = Integer.parseInt(idStr);
            RentalListing listing = rentalService.getListingById(id);
            if (listing == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Rental listing not found.");
                return;
            }
            request.setAttribute("listing", listing);
            request.getRequestDispatcher("/WEB-INF/views/rentals/details.jsp").forward(request, response);

        } else if ("/create".equals(pathInfo)) {
            // Put vehicle up for rent
            if (user == null) {
                response.sendRedirect(request.getContextPath() + "/auth/login");
                return;
            }
            List<Vehicle> vehicles = vehicleService.getVehiclesByOwner(user.getId());
            request.setAttribute("vehicles", vehicles);
            request.getRequestDispatcher("/WEB-INF/views/rentals/create.jsp").forward(request, response);

        } else if ("/my".equals(pathInfo)) {
            // Owner's rental listings
            if (user == null) {
                response.sendRedirect(request.getContextPath() + "/auth/login");
                return;
            }
            List<RentalListing> listings = rentalService.getListingsByOwner(user.getId());
            request.setAttribute("listings", listings);
            request.getRequestDispatcher("/WEB-INF/views/rentals/my.jsp").forward(request, response);

        } else if ("/bookings".equals(pathInfo)) {
            // Renter's reservations
            if (user == null) {
                response.sendRedirect(request.getContextPath() + "/auth/login");
                return;
            }
            List<RentalBooking> bookings = rentalService.getRenterBookings(user.getId());
            request.setAttribute("bookings", bookings);
            request.getRequestDispatcher("/WEB-INF/views/rentals/history.jsp").forward(request, response);

        } else if ("/booking-details".equals(pathInfo)) {
            // Individual reservation details & receipt
            if (user == null) {
                response.sendRedirect(request.getContextPath() + "/auth/login");
                return;
            }
            String idStr = request.getParameter("id");
            if (idStr == null) {
                response.sendRedirect(request.getContextPath() + "/rentals/bookings");
                return;
            }
            int id = Integer.parseInt(idStr);
            RentalBooking booking = rentalService.getBookingById(id);
            if (booking == null || booking.getRenterId() != user.getId()) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied: You do not own this reservation.");
                return;
            }
            request.setAttribute("booking", booking);
            request.setAttribute("payment", rentalService.getPaymentByRentalBookingId(id));
            request.getRequestDispatcher("/WEB-INF/views/rentals/booking_details.jsp").forward(request, response);

        } else if ("/cancel".equals(pathInfo)) {
            // Cancel rental booking
            if (user == null) {
                response.sendRedirect(request.getContextPath() + "/auth/login");
                return;
            }
            String idStr = request.getParameter("id");
            if (idStr != null) {
                int id = Integer.parseInt(idStr);
                boolean success = rentalService.cancelBooking(id, user.getId());
                if (!success) {
                    response.sendError(HttpServletResponse.SC_FORBIDDEN, "Cancellation failed.");
                    return;
                }
            }
            response.sendRedirect(request.getContextPath() + "/rentals/bookings?cancelled=true");

        } else if ("/status".equals(pathInfo)) {
            // Toggle rental listing status
            if (user == null) {
                response.sendRedirect(request.getContextPath() + "/auth/login");
                return;
            }
            String idStr = request.getParameter("id");
            String status = request.getParameter("status");
            if (idStr != null && status != null) {
                int id = Integer.parseInt(idStr);
                rentalService.toggleListingStatus(id, status, user.getId());
            }
            response.sendRedirect(request.getContextPath() + "/rentals/my?statusUpdated=true");

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
            String location = request.getParameter("location");
            String fromStr = request.getParameter("availableFrom");
            String untilStr = request.getParameter("availableUntil");
            String priceStr = request.getParameter("pricePerDay");
            String depositStr = request.getParameter("securityDeposit");
            String description = request.getParameter("description");

            if (isEmpty(vehicleIdStr) || isEmpty(location) || isEmpty(fromStr) || isEmpty(untilStr) || isEmpty(priceStr)) {
                request.setAttribute("error", "Vehicle, Location, Dates, and Price per Day are required.");
                request.setAttribute("vehicles", vehicleService.getVehiclesByOwner(user.getId()));
                request.getRequestDispatcher("/WEB-INF/views/rentals/create.jsp").forward(request, response);
                return;
            }

            try {
                int vehicleId = Integer.parseInt(vehicleIdStr.trim());
                Date availableFrom = Date.valueOf(fromStr.trim());
                Date availableUntil = Date.valueOf(untilStr.trim());
                BigDecimal pricePerDay = new BigDecimal(priceStr.trim());
                BigDecimal deposit = !isEmpty(depositStr) ? new BigDecimal(depositStr.trim()) : BigDecimal.ZERO;

                RentalListing listing = new RentalListing();
                listing.setVehicleId(vehicleId);
                listing.setLocation(location.trim());
                listing.setAvailableFrom(availableFrom);
                listing.setAvailableUntil(availableUntil);
                listing.setPricePerDay(pricePerDay);
                listing.setSecurityDeposit(deposit);
                listing.setDescription(description != null ? description.trim() : "");

                boolean success = rentalService.createListing(listing, user.getId());
                if (success) {
                    response.sendRedirect(request.getContextPath() + "/rentals/my?added=true");
                } else {
                    request.setAttribute("error", "Failed to create rental listing. Verify dates (end date must be on/after start date) and vehicle availability.");
                    request.setAttribute("vehicles", vehicleService.getVehiclesByOwner(user.getId()));
                    request.getRequestDispatcher("/WEB-INF/views/rentals/create.jsp").forward(request, response);
                }
            } catch (Exception e) {
                request.setAttribute("error", "Invalid inputs. Please verify dates and pricing numbers.");
                request.setAttribute("vehicles", vehicleService.getVehiclesByOwner(user.getId()));
                request.getRequestDispatcher("/WEB-INF/views/rentals/create.jsp").forward(request, response);
            }

        } else if ("/book".equals(pathInfo)) {
            String listingIdStr = request.getParameter("listingId");
            String startStr = request.getParameter("startDate");
            String endStr = request.getParameter("endDate");
            String paymentMethod = request.getParameter("paymentMethod");

            if (isEmpty(listingIdStr) || isEmpty(startStr) || isEmpty(endStr)) {
                response.sendRedirect(request.getContextPath() + "/rentals");
                return;
            }

            try {
                int listingId = Integer.parseInt(listingIdStr.trim());
                Date startDate = Date.valueOf(startStr.trim());
                Date endDate = Date.valueOf(endStr.trim());

                boolean success = rentalService.bookRental(listingId, user.getId(), startDate, endDate, 
                                                           paymentMethod != null ? paymentMethod : "CARD");
                if (success) {
                    response.sendRedirect(request.getContextPath() + "/rentals/bookings?added=true");
                } else {
                    response.sendRedirect(request.getContextPath() + "/rentals/details?id=" + listingId + 
                                          "&error=Booking failed. Dates might be unavailable, overlapping with another booking, or out of range.");
                }
            } catch (Exception e) {
                response.sendRedirect(request.getContextPath() + "/rentals?error=Invalid reservation dates.");
            }

        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    private boolean isEmpty(String s) {
        return s == null || s.trim().isEmpty();
    }
}
