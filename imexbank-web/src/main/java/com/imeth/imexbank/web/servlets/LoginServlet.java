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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Set;

@WebServlet(name = "LoginServlet", urlPatterns = {"/login"})
public class LoginServlet extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(LoginServlet.class);

    @EJB
    private SecurityService securityService;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // If already logged in, redirect to dashboard
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute(SecurityConstants.USER_SESSION_KEY) != null) {
            response.sendRedirect(request.getContextPath() + "/dashboard");
            return;
        }

        request.getRequestDispatcher("/login.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String username = request.getParameter("username");
        String password = request.getParameter("password");

        if (username == null || password == null ||
                username.trim().isEmpty() || password.trim().isEmpty()) {
            request.setAttribute("error", "Username and password are required");
            request.getRequestDispatcher("/login.jsp").forward(request, response);
            return;
        }

        try {
            // Record login attempt
            String ipAddress = request.getRemoteAddr();

            if (securityService.authenticate(username, password)) {
                // Authentication successful
                HttpSession session = request.getSession(true);
                session.setAttribute(SecurityConstants.USER_SESSION_KEY, username);

                // Get user roles
                Set<UserRole> roles = securityService.getUserRoles(username);
                session.setAttribute(SecurityConstants.USER_ROLES_KEY, roles);

                // Store login time and IP
                session.setAttribute(SecurityConstants.LOGIN_TIME_KEY, System.currentTimeMillis());
                session.setAttribute(SecurityConstants.IP_ADDRESS_KEY, ipAddress);

                // Set session timeout
                session.setMaxInactiveInterval(SecurityConstants.SESSION_TIMEOUT_MINUTES * 60);

                logger.info("User {} logged in successfully from IP: {}", username, ipAddress);

                // Record successful login
                securityService.recordLoginAttempt(username, true, ipAddress);

                // Redirect based on role
                if (roles.contains(UserRole.ADMIN)) {
                    response.sendRedirect(request.getContextPath() + "/admin/dashboard");
                } else if (roles.contains(UserRole.MANAGER) || roles.contains(UserRole.TELLER)) {
                    response.sendRedirect(request.getContextPath() + "/dashboard");
                } else {
                    response.sendRedirect(request.getContextPath() + "/customer/dashboard");
                }

            } else {
                // Authentication failed
                logger.warn("Failed login attempt for user: {} from IP: {}", username, ipAddress);

                // Record failed login
                securityService.recordLoginAttempt(username, false, ipAddress);

                // Check if account is locked
                if (securityService.isUserLocked(username)) {
                    request.setAttribute("error", "Account is locked due to multiple failed login attempts");
                } else {
                    request.setAttribute("error", "Invalid username or password");
                }

                request.getRequestDispatcher("/login.jsp").forward(request, response);
            }

        } catch (Exception e) {
            logger.error("Error during login", e);
            request.setAttribute("error", "An error occurred during login. Please try again.");
            request.getRequestDispatcher("/login.jsp").forward(request, response);
        }
    }
}