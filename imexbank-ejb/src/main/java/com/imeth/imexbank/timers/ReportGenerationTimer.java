package com.imeth.imexbank.timers;

import com.imeth.imexbank.services.interfaces.ReportService;
import jakarta.ejb.EJB;
import jakarta.ejb.Schedule;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.Map;

@Singleton
@Startup
public class ReportGenerationTimer {

    private static final Logger logger = LoggerFactory.getLogger(ReportGenerationTimer.class);

    @EJB
    private ReportService reportService;

    @Schedule(hour = "6", minute = "0", persistent = true,
            info = "Generate daily reports at 6:00 AM")
    public void generateDailyReports() {
        logger.info("Starting daily report generation");

        try {
            LocalDate yesterday = LocalDate.now().minusDays(1);

            // Generate transaction report
            Map<String, Object> transactionReport =
                    reportService.generateDailyTransactionReport(yesterday);

            logger.info("Daily transaction report generated with {} transactions",
                    transactionReport.get("transactionCount"));

            // Generate other daily reports
            // Could save to file system or send via email

        } catch (Exception e) {
            logger.error("Error in daily report generation", e);
        }
    }

    @Schedule(dayOfMonth = "1", hour = "7", minute = "0", persistent = true,
            info = "Generate monthly reports on 1st of every month at 7:00 AM")
    public void generateMonthlyReports() {
        logger.info("Starting monthly report generation");

        try {
            LocalDate now = LocalDate.now();
            int previousMonth = now.minusMonths(1).getMonthValue();
            int year = now.minusMonths(1).getYear();

            Map<String, Object> monthlySummary =
                    reportService.generateMonthlyAccountSummary(year, previousMonth);

            logger.info("Monthly report generated for {}/{}", previousMonth, year);

        } catch (Exception e) {
            logger.error("Error in monthly report generation", e);
        }
    }

    @Schedule(dayOfWeek = "Mon", hour = "5", minute = "0", persistent = true,
            info = "Generate weekly reports every Monday at 5:00 AM")
    public void generateWeeklyReports() {
        logger.info("Starting weekly report generation");

        try {
            LocalDate endDate = LocalDate.now().minusDays(1);
            LocalDate startDate = endDate.minusDays(6);

            Map<String, Object> auditReport =
                    reportService.generateAuditReport(startDate, endDate);

            logger.info("Weekly audit report generated for {} to {}",
                    startDate, endDate);

        } catch (Exception e) {
            logger.error("Error in weekly report generation", e);
        }
    }
}