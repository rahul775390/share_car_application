package com.carsharing.controller;

import com.carsharing.model.User;
import com.carsharing.model.RideBooking;
import com.carsharing.service.RideService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

@WebServlet(urlPatterns = {"/bookings", "/bookings/*"})
public class BookingServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private RideService rideService;

    @Override
    public void init() throws ServletException {
        this.rideService = new RideService();
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
            // View My Bookings (Passenger)
            List<RideBooking> bookings = rideService.getPassengerBookings(user.getId());
            request.setAttribute("bookings", bookings);
            request.getRequestDispatcher("/WEB-INF/views/bookings/history.jsp").forward(request, response);
            
        } else if ("/details".equals(pathInfo)) {
            // View Booking details
            String idStr = request.getParameter("id");
            if (idStr == null) {
                response.sendRedirect(request.getContextPath() + "/bookings");
                return;
            }
            int id = Integer.parseInt(idStr);
            RideBooking booking = rideService.getBookingById(id);
            if (booking == null || booking.getPassengerId() != user.getId()) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied: You did not make this booking.");
                return;
            }
            request.setAttribute("booking", booking);
            request.setAttribute("payment", rideService.getPaymentByBookingId(id));
            request.setAttribute("hasRated", rideService.hasUserRatedRide(user.getId(), id));
            request.getRequestDispatcher("/WEB-INF/views/bookings/details.jsp").forward(request, response);
            
        } else if ("/cancel".equals(pathInfo)) {
            // Cancel booking
            String idStr = request.getParameter("id");
            if (idStr != null) {
                int id = Integer.parseInt(idStr);
                boolean success = rideService.cancelBooking(id, user.getId());
                if (!success) {
                    response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied or Cancellation Failed.");
                    return;
                }
            }
            response.sendRedirect(request.getContextPath() + "/bookings?cancelled=true");
            
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

        if ("/book".equals(pathInfo)) {
            String rideIdStr = request.getParameter("rideId");
            String seatsStr = request.getParameter("seats");
            String paymentMethod = request.getParameter("paymentMethod");

            if (rideIdStr == null || seatsStr == null || rideIdStr.trim().isEmpty() || seatsStr.trim().isEmpty()) {
                response.sendRedirect(request.getContextPath() + "/rides");
                return;
            }

            try {
                int rideId = Integer.parseInt(rideIdStr.trim());
                int seats = Integer.parseInt(seatsStr.trim());

                boolean success = rideService.bookSeats(rideId, user.getId(), seats, paymentMethod != null ? paymentMethod : "UPI");
                if (success) {
                    response.sendRedirect(request.getContextPath() + "/bookings?added=true");
                } else {
                    response.sendRedirect(request.getContextPath() + "/rides/details?id=" + rideId + "&error=Booking failed. Insufficient seats, invalid status, or trying to book your own ride.");
                }
            } catch (Exception e) {
                response.sendRedirect(request.getContextPath() + "/rides?error=Invalid booking inputs.");
            }

        } else if ("/rate".equals(pathInfo)) {
            String bookingIdStr = request.getParameter("bookingId");
            String ratingStr = request.getParameter("rating");
            String comment = request.getParameter("comment");

            if (bookingIdStr != null && ratingStr != null) {
                try {
                    int bookingId = Integer.parseInt(bookingIdStr.trim());
                    int ratingValue = Integer.parseInt(ratingStr.trim());

                    RideBooking booking = rideService.getBookingById(bookingId);
                    if (booking != null && booking.getPassengerId() == user.getId()) {
                        com.carsharing.model.Ride ride = rideService.getRideById(booking.getRideId());
                        if (ride != null) {
                            rideService.submitRating(user.getId(), ride.getDriverId(), bookingId, ratingValue, comment);
                        }
                    }
                    response.sendRedirect(request.getContextPath() + "/bookings/details?id=" + bookingId + "&rated=true");
                    return;
                } catch (Exception ignored) {}
            }
            response.sendRedirect(request.getContextPath() + "/bookings");

        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }
}
