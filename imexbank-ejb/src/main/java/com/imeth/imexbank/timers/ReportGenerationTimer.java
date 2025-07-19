package com.imeth.imexbank.timers;

import com.imeth.imexbank.services.interfaces.ReportService;
import jakarta.ejb.EJB;
import jakarta.ejb.Schedule;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
@Startup
public class ReportGenerationTimer {

    private static final Logger logger = LoggerFactory.getLogger(ReportGenerationTimer.class);

    @EJB
    private ReportService reportService;

    @Schedule(
        dayOfMonth = "1", 
        hour = "3", 
        minute = "0", 
        persistent = true,
        info = "Monthly report generation on the first day of month"
    )
    public void generateMonthlyReports() {
        logger.info("Starting monthly report generation");
        
        try {
            // Generate monthly account summary
            reportService.generateMonthlyAccountSummary();
            
            // Generate interest report for previous month
            reportService.generateMonthlyInterestReport();
            
            // Generate audit report
            reportService.generateMonthlyAuditReport();
            
            logger.info("Monthly reports generated successfully");
        } catch (Exception e) {
            logger.error("Error during report generation", e);
        }
    }
}
