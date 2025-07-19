package com.imeth.imexbank.web.servlets;

import com.imeth.imexbank.common.constants.SecurityConstants;
import com.imeth.imexbank.common.enums.UserRole;
import com.imeth.imexbank.services.interfaces.SecurityService;
import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Set;

@WebServlet(name = "LoginServlet", urlPatterns = "/login")
public class LoginServlet extends HttpServlet {

    @EJB
    private SecurityService securityService;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // If user is already logged in, redirect to dashboard
        if (request.getSession(false) != null && request.getSession(false).getAttribute(SecurityConstants.USER_SESSION_KEY) != null) {
            response.sendRedirect(request.getContextPath() + "/dashboard.jsp");
            return;
        }
        request.getRequestDispatcher("/login.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String ipAddress = request.getRemoteAddr();

        try {
            if (securityService.authenticate(username, password)) {
                // Authentication successful
                HttpSession session = request.getSession(true);
                Set<UserRole> roles = securityService.getUserRoles(username);

                session.setAttribute(SecurityConstants.USER_SESSION_KEY, username);
                session.setAttribute(SecurityConstants.USER_ROLES_KEY, roles);
                session.setAttribute(SecurityConstants.LOGIN_TIME_KEY, LocalDateTime.now());
                session.setAttribute(SecurityConstants.IP_ADDRESS_KEY, ipAddress);

                securityService.recordLoginAttempt(username, true, ipAddress);

                // Redirect based on role
                if (roles.contains(UserRole.ADMIN)) {
                    response.sendRedirect(request.getContextPath() + "/admin/admin-dashboard.jsp");
                } else {
                    response.sendRedirect(request.getContextPath() + "/dashboard.jsp");
                }
            } else {
                // Authentication failed
                securityService.recordLoginAttempt(username, false, ipAddress);
                request.setAttribute("error", "Invalid username or password.");
                request.getRequestDispatcher("/login.jsp").forward(request, response);
            }
        } catch (Exception e) {
            request.setAttribute("error", "An error occurred during login. Please contact support.");
            request.getRequestDispatcher("/login.jsp").forward(request, response);
        }
    }
}