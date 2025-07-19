package com.imeth.imexbank.timers;

import com.imeth.imexbank.services.interfaces.InterestCalculationService;
import jakarta.ejb.EJB;
import jakarta.ejb.Schedule;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;

@Singleton
@Startup
public class InterestCalculationTimer {

    private static final Logger logger = LoggerFactory.getLogger(InterestCalculationTimer.class);

    @EJB
    private InterestCalculationService interestCalculationService;

    @Schedule(hour = "2", minute = "0", persistent = true,
            info = "Calculate daily interest at 2:00 AM")
    public void calculateDailyInterest() {
        logger.info("Starting daily interest calculation timer");

        try {
            interestCalculationService.calculateDailyInterest();
            logger.info("Daily interest calculation completed successfully");
        } catch (Exception e) {
            logger.error("Error in daily interest calculation", e);
        }
    }

    @Schedule(dayOfMonth = "1", hour = "3", minute = "0", persistent = true,
            info = "Calculate monthly interest on 1st of every month at 3:00 AM")
    public void calculateMonthlyInterest() {
        logger.info("Starting monthly interest calculation timer");

        try {
            interestCalculationService.calculateMonthlyInterest();
            logger.info("Monthly interest calculation completed successfully");
        } catch (Exception e) {
            logger.error("Error in monthly interest calculation", e);
        }
    }

    @Schedule(dayOfMonth = "Last", hour = "23", minute = "45", persistent = true,
            info = "End of month interest accrual")
    public void endOfMonthInterestAccrual() {
        logger.info("Starting end of month interest accrual");

        try {
            LocalDate today = LocalDate.now();
            LocalDate lastDayOfMonth = today.withDayOfMonth(
                    today.getMonth().length(today.isLeapYear()));

            if (today.equals(lastDayOfMonth)) {
                logger.info("Processing end of month interest accrual");
                // Additional end of month processing
            }
        } catch (Exception e) {
            logger.error("Error in end of month interest accrual", e);
        }
    }
}