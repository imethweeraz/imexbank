package com.imeth.imexbank.web.servlets;

import com.imeth.imexbank.common.enums.UserRole;
import com.imeth.imexbank.services.interfaces.SecurityService;
import com.imeth.imexbank.services.interfaces.TimerService;
import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

@WebServlet(name = "AdminServlet", urlPatterns = {"/admin/*"})
public class AdminServlet extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(AdminServlet.class);

    @EJB
    private SecurityService securityService;

    @EJB
    private TimerService timerService;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String pathInfo = request.getPathInfo();

        if (pathInfo == null || pathInfo.equals("/") || pathInfo.equals("/dashboard")) {
            request.getRequestDispatcher("/admin/admin-dashboard.jsp").forward(request, response);
        } else if (pathInfo.equals("/users")) {
            handleUserManagement(request, response);
        } else if (pathInfo.equals("/timers")) {
            handleTimerManagement(request, response);
        } else if (pathInfo.equals("/configuration")) {
            handleSystemConfiguration(request, response);
        } else if (pathInfo.equals("/security-logs")) {
            handleSecurityLogs(request, response);
        } else if (pathInfo.equals("/monitoring")) {
            handleSystemMonitoring(request, response);
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String pathInfo = request.getPathInfo();

        if (pathInfo == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        switch (pathInfo) {
            case "/users/lock":
                handleLockUser(request, response);
                break;
            case "/users/unlock":
                handleUnlockUser(request, response);
                break;
            case "/users/role":
                handleUpdateUserRole(request, response);
                break;
            case "/timers/execute":
                handleExecuteTimer(request, response);
                break;
            case "/timers/pause":
                handlePauseTimer(request, response);
                break;
            case "/timers/resume":
                handleResumeTimer(request, response);
                break;
            default:
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    private void handleUserManagement(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Implementation would load users and display management page
        request.getRequestDispatcher("/admin/user-management.jsp").forward(request, response);
    }

    private void handleTimerManagement(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Implementation would load timer status and display management page
        request.getRequestDispatcher("/admin/timer-management.jsp").forward(request, response);
    }

    private void handleSystemConfiguration(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.getRequestDispatcher("/admin/system-configuration.jsp").forward(request, response);
    }

    private void handleSecurityLogs(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.getRequestDispatcher("/admin/security-logs.jsp").forward(request, response);
    }

    private void handleSystemMonitoring(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.getRequestDispatcher("/admin/system-monitoring.jsp").forward(request, response);
    }

    private void handleLockUser(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            String username = request.getParameter("username");
            securityService.lockUser(username);

            response.sendRedirect(request.getContextPath() +
                    "/admin/users?success=User locked successfully");

        } catch (Exception e) {
            logger.error("Error locking user", e);
            response.sendRedirect(request.getContextPath() +
                    "/admin/users?error=" + e.getMessage());
        }
    }

    private void handleUnlockUser(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            String username = request.getParameter("username");
            securityService.unlockUser(username);

            response.sendRedirect(request.getContextPath() +
                    "/admin/users?success=User unlocked successfully");

        } catch (Exception e) {
            logger.error("Error unlocking user", e);
            response.sendRedirect(request.getContextPath() +
                    "/admin/users?error=" + e.getMessage());
        }
    }

    private void handleUpdateUserRole(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            String username = request.getParameter("username");
            String roleStr = request.getParameter("role");
            String action = request.getParameter("action");

            UserRole role = UserRole.valueOf(roleStr);

            if ("add".equals(action)) {
                securityService.assignRole(username, role);
            } else if ("remove".equals(action)) {
                securityService.removeRole(username, role);
            }

            response.sendRedirect(request.getContextPath() +
                    "/admin/users?success=Role updated successfully");

        } catch (Exception e) {
            logger.error("Error updating user role", e);
            response.sendRedirect(request.getContextPath() +
                    "/admin/users?error=" + e.getMessage());
        }
    }

    private void handleExecuteTimer(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            String timerName = request.getParameter("timerName");
            // Execute timer logic

            response.sendRedirect(request.getContextPath() +
                    "/admin/timers?success=Timer executed successfully");

        } catch (Exception e) {
            logger.error("Error executing timer", e);
            response.sendRedirect(request.getContextPath() +
                    "/admin/timers?error=" + e.getMessage());
        }
    }

    private void handlePauseTimer(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            String timerName = request.getParameter("timerName");
            timerService.pauseTimer(timerName);

            response.sendRedirect(request.getContextPath() +
                    "/admin/timers?success=Timer paused successfully");

        } catch (Exception e) {
            logger.error("Error pausing timer", e);
            response.sendRedirect(request.getContextPath() +
                    "/admin/timers?error=" + e.getMessage());
        }
    }

    private void handleResumeTimer(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            String timerName = request.getParameter("timerName");
            timerService.resumeTimer(timerName);

            response.sendRedirect(request.getContextPath() +
                    "/admin/timers?success=Timer resumed successfully");

        } catch (Exception e) {
            logger.error("Error resuming timer", e);
            response.sendRedirect(request.getContextPath() +
                    "/admin/timers?error=" + e.getMessage());
        }
    }
}