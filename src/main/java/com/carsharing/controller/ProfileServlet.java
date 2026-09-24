package com.carsharing.controller;

import com.carsharing.model.User;
import com.carsharing.service.UserService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/profile")
public class ProfileServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private UserService userService;

    @Override
    public void init() throws ServletException {
        this.userService = new UserService();
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
        
        // Load latest details from database to ensure fresh data
        User latestUser = userService.getUserById(user.getId());
        if (latestUser != null) {
            session.setAttribute("user", latestUser);
        }

        if ("true".equals(request.getParameter("updated"))) {
            request.setAttribute("message", "Profile updated successfully!");
        }

        request.getRequestDispatcher("/WEB-INF/views/user/profile.jsp").forward(request, response);
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
        String name = request.getParameter("name");
        String phone = request.getParameter("phone");
        String city = request.getParameter("city");

        if (name == null || name.trim().isEmpty() || 
            phone == null || phone.trim().isEmpty() ||
            city == null || city.trim().isEmpty()) {
            
            request.setAttribute("error", "Name, Phone and City are required.");
            request.getRequestDispatcher("/WEB-INF/views/user/profile.jsp").forward(request, response);
            return;
        }

        boolean success = userService.updateProfile(user.getId(), name.trim(), phone.trim(), city.trim());
        if (success) {
            response.sendRedirect(request.getContextPath() + "/profile?updated=true");
        } else {
            request.setAttribute("error", "Failed to update profile. Please try again.");
            request.getRequestDispatcher("/WEB-INF/views/user/profile.jsp").forward(request, response);
        }
    }
}
