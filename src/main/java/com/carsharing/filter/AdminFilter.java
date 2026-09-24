package com.carsharing.filter;

import com.carsharing.model.User;

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

@WebFilter(urlPatterns = {"/admin", "/admin/*"})
public class AdminFilter implements Filter {

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

        boolean isAdmin = false;
        if (session != null) {
            User user = (User) session.getAttribute("user");
            if (user != null && "ADMIN".equalsIgnoreCase(user.getRole())) {
                isAdmin = true;
            }
        }

        if (isAdmin) {
            // User is Admin, allow request to pass through
            chain.doFilter(request, response);
        } else {
            // Redirect to home page with an unauthorized error parameter
            httpResponse.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied: Administrative privileges required.");
        }
    }

    @Override
    public void destroy() {
        // Clean up actions
    }
}
