package com.imeth.imexbank.web.servlets;

import com.imeth.imexbank.common.constants.TimerConstants;
import com.imeth.imexbank.common.enums.TimerTaskType;
import com.imeth.imexbank.services.interfaces.TimerService;
import com.imeth.imexbank.web.utils.ResponseUtil;
import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/admin/timers/*")
public class TimerManagementServlet extends HttpServlet {

    @EJB
    private TimerService timerService;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String action = request.getPathInfo();
        
        if (action == null || action.equals("/")) {
            // Show timer management page
            request.setAttribute("timers", timerService.getAllTimers());
            request.getRequestDispatcher("/admin/timer-management.jsp").forward(request, response);
        } else if (action.equals("/execute")) {
            executeTimer(request, response);
        } else if (action.equals("/pause")) {
            pauseTimer(request, response);
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    private void executeTimer(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        String timerName = request.getParameter("timerName");
        if (timerName == null || timerName.isEmpty()) {
            ResponseUtil.sendError(response, "Timer name is required", HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        
        try {
            timerService.executeTimerImmediately(timerName);
            ResponseUtil.sendSuccess(response, "Timer executed successfully: " + timerName);
        } catch (Exception e) {
            ResponseUtil.sendError(response, "Error executing timer: " + e.getMessage(), 
                                 HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    private void pauseTimer(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        String timerName = request.getParameter("timerName");
        if (timerName == null || timerName.isEmpty()) {
            ResponseUtil.sendError(response, "Timer name is required", HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        
        try {
            timerService.pauseTimer(timerName);
            ResponseUtil.sendSuccess(response, "Timer paused successfully: " + timerName);
        } catch (Exception e) {
            ResponseUtil.sendError(response, "Error pausing timer: " + e.getMessage(), 
                                 HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}
