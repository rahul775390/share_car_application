package com.carsharing.controller;

import com.carsharing.service.AdminService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

@WebServlet(urlPatterns = {"/admin", "/admin/*"})
public class AdminServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private AdminService adminService;

    @Override
    public void init() throws ServletException {
        this.adminService = new AdminService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String pathInfo = request.getPathInfo();

        if (pathInfo == null || "/".equals(pathInfo) || "/dashboard".equals(pathInfo)) {
            Map<String, Object> data = adminService.getDashboardData();
            request.setAttribute("stats", data.get("stats"));
            request.setAttribute("users", data.get("users"));
            request.setAttribute("vehicles", data.get("vehicles"));
            request.setAttribute("rides", data.get("rides"));
            request.getRequestDispatcher("/WEB-INF/views/admin/dashboard.jsp").forward(request, response);

        } else if ("/user-status".equals(pathInfo)) {
            String userIdStr = request.getParameter("id");
            String status = request.getParameter("status");
            if (userIdStr != null && status != null) {
                int userId = Integer.parseInt(userIdStr);
                adminService.toggleUserStatus(userId, status);
            }
            response.sendRedirect(request.getContextPath() + "/admin/dashboard?userUpdated=true");

        } else if ("/user-role".equals(pathInfo)) {
            String userIdStr = request.getParameter("id");
            String role = request.getParameter("role");
            if (userIdStr != null && role != null) {
                int userId = Integer.parseInt(userIdStr);
                adminService.changeUserRole(userId, role);
            }
            response.sendRedirect(request.getContextPath() + "/admin/dashboard?roleUpdated=true");

        } else if ("/cancel-ride".equals(pathInfo)) {
            String rideIdStr = request.getParameter("id");
            if (rideIdStr != null) {
                int rideId = Integer.parseInt(rideIdStr);
                adminService.cancelRideAsAdmin(rideId);
            }
            response.sendRedirect(request.getContextPath() + "/admin/dashboard?rideCancelled=true");

        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        doGet(request, response);
    }
}
