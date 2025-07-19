package com.imeth.imexbank.web.servlets;

import com.imeth.imexbank.services.interfaces.ReportService;
import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@WebServlet(name = "ReportServlet", urlPatterns = {"/report/*"})
public class ReportServlet extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(ReportServlet.class);

    @EJB
    private ReportService reportService;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String pathInfo = request.getPathInfo();

        if (pathInfo == null || pathInfo.equals("/")) {
            request.getRequestDispatcher("/reports/report-dashboard.jsp").forward(request, response);
        } else if (pathInfo.equals("/daily")) {
            handleDailyReport(request, response);
        } else if (pathInfo.equals("/monthly")) {
            handleMonthlyReport(request, response);
        } else if (pathInfo.equals("/customer")) {
            handleCustomerReport(request, response);
        } else if (pathInfo.equals("/interest")) {
            handleInterestReport(request, response);
        } else if (pathInfo.equals("/audit")) {
            handleAuditReport(request, response);
        } else if (pathInfo.startsWith("/export/")) {
            handleExportReport(request, response);
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    private void handleDailyReport(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            String dateStr = request.getParameter("date");
            LocalDate date = dateStr != null ?
                    LocalDate.parse(dateStr) : LocalDate.now().minusDays(1);

            Map<String, Object> reportData = reportService.generateDailyTransactionReport(date);

            request.setAttribute("reportData", reportData);
            request.setAttribute("reportDate", date);
            request.getRequestDispatcher("/reports/daily-report.jsp").forward(request, response);

        } catch (Exception e) {
            logger.error("Error generating daily report", e);
            request.setAttribute("error", "Error generating report");
            request.getRequestDispatcher("/reports/report-dashboard.jsp").forward(request, response);
        }
    }

    private void handleMonthlyReport(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            String yearStr = request.getParameter("year");
            String monthStr = request.getParameter("month");

            int year = yearStr != null ? Integer.parseInt(yearStr) : LocalDate.now().getYear();
            int month = monthStr != null ? Integer.parseInt(monthStr) : LocalDate.now().getMonthValue();

            Map<String, Object> reportData = reportService.generateMonthlyAccountSummary(year, month);

            request.setAttribute("reportData", reportData);
            request.setAttribute("year", year);
            request.setAttribute("month", month);
            request.getRequestDispatcher("/reports/monthly-report.jsp").forward(request, response);

        } catch (Exception e) {
            logger.error("Error generating monthly report", e);
            request.setAttribute("error", "Error generating report");
            request.getRequestDispatcher("/reports/report-dashboard.jsp").forward(request, response);
        }
    }

    private void handleCustomerReport(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            Long customerId = Long.parseLong(request.getParameter("customerId"));
            Map<String, Object> reportData = reportService.generateCustomerReport(customerId);

            request.setAttribute("reportData", reportData);
            request.getRequestDispatcher("/reports/customer-report.jsp").forward(request, response);

        } catch (Exception e) {
            logger.error("Error generating customer report", e);
            request.setAttribute("error", "Error generating report");
            request.getRequestDispatcher("/reports/report-dashboard.jsp").forward(request, response);
        }
    }

    private void handleInterestReport(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            String startDateStr = request.getParameter("startDate");
            String endDateStr = request.getParameter("endDate");

            LocalDate startDate = startDateStr != null ?
                    LocalDate.parse(startDateStr) : LocalDate.now().minusMonths(1);
            LocalDate endDate = endDateStr != null ?
                    LocalDate.parse(endDateStr) : LocalDate.now();

            Map<String, Object> reportData = reportService.generateInterestReport(startDate, endDate);

            request.setAttribute("reportData", reportData);
            request.setAttribute("startDate", startDate);
            request.setAttribute("endDate", endDate);
            request.getRequestDispatcher("/reports/interest-report.jsp").forward(request, response);

        } catch (Exception e) {
            logger.error("Error generating interest report", e);
            request.setAttribute("error", "Error generating report");
            request.getRequestDispatcher("/reports/report-dashboard.jsp").forward(request, response);
        }
    }

    private void handleAuditReport(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            String startDateStr = request.getParameter("startDate");
            String endDateStr = request.getParameter("endDate");

            LocalDate startDate = startDateStr != null ?
                    LocalDate.parse(startDateStr) : LocalDate.now().minusDays(7);
            LocalDate endDate = endDateStr != null ?
                    LocalDate.parse(endDateStr) : LocalDate.now();

            Map<String, Object> reportData = reportService.generateAuditReport(startDate, endDate);

            request.setAttribute("reportData", reportData);
            request.setAttribute("startDate", startDate);
            request.setAttribute("endDate", endDate);
            request.getRequestDispatcher("/reports/audit-report.jsp").forward(request, response);

        } catch (Exception e) {
            logger.error("Error generating audit report", e);
            request.setAttribute("error", "Error generating report");
            request.getRequestDispatcher("/reports/report-dashboard.jsp").forward(request, response);
        }
    }

    private void handleExportReport(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            String pathInfo = request.getPathInfo();
            String format = pathInfo.substring("/export/".length());

            String reportType = request.getParameter("type");
            Map<String, Object> reportData = (Map<String, Object>)
                    request.getSession().getAttribute("lastReportData");

            if (reportData == null) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "No report data available");
                return;
            }

            byte[] exportData;
            String contentType;
            String fileName;

            if ("pdf".equals(format)) {
                exportData = reportService.exportReportToPdf(reportData, reportType);
                contentType = "application/pdf";
                fileName = reportType + "-report.pdf";
            } else if ("excel".equals(format)) {
                exportData = reportService.exportReportToExcel(reportData, reportType);
                contentType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
                fileName = reportType + "-report.xlsx";
            } else {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid export format");
                return;
            }

            response.setContentType(contentType);
            response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
            response.getOutputStream().write(exportData);

        } catch (Exception e) {
            logger.error("Error exporting report", e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}