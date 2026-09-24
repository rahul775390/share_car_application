package com.carsharing.filter;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebFilter(urlPatterns = {
    "/profile",
    "/profile/*",
    "/vehicles",
    "/vehicles/*",
    "/rides",
    "/rides/*",
    "/bookings",
    "/bookings/*",
    "/rentals",
    "/rentals/*"
})
public class AuthFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // No init configurations required
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) 
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        HttpSession session = httpRequest.getSession(false);

        boolean loggedIn = (session != null && session.getAttribute("user") != null);
        String servletPath = httpRequest.getServletPath();
        String pathInfo = httpRequest.getPathInfo();

        // Allow public browsing for ride/rental searches and viewing details
        boolean isPublicRide = "/rides".equals(servletPath) && ("/search".equals(pathInfo) || "/details".equals(pathInfo));
        boolean isPublicRental = "/rentals".equals(servletPath) && (pathInfo == null || "/".equals(pathInfo) || "/search".equals(pathInfo) || "/details".equals(pathInfo));

        if (loggedIn || isPublicRide || isPublicRental) {
            // Authorized or public route
            chain.doFilter(request, response);
        } else {
            // Redirect unauthorized users to login page
            httpResponse.sendRedirect(httpRequest.getContextPath() + "/auth/login");
        }
    }

    @Override
    public void destroy() {
        // Clean up actions
    }
}
