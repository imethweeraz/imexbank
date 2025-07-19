package com.imeth.imexbank.timers;

import com.imeth.imexbank.common.constants.TimerConstants;
import com.imeth.imexbank.common.enums.AccountType;
import com.imeth.imexbank.services.interfaces.InterestCalculationService;
import jakarta.ejb.EJB;
import jakarta.ejb.Schedule;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
@Startup
public class InterestCalculationTimer {

    private static final Logger logger = LoggerFactory.getLogger(InterestCalculationTimer.class);

    @EJB
    private InterestCalculationService interestCalculationService;

    @Schedule(
        hour = "2", 
        minute = "0", 
        persistent = TimerConstants.TIMER_PERSISTENT,
        info = "Daily interest calculation at 2 AM"
    )
    public void calculateInterest() {
        logger.info("Starting daily interest calculation");
        
        try {
            // Calculate interest for savings accounts
            interestCalculationService.calculateDailyInterest(AccountType.SAVINGS);
            
            // Calculate interest for fixed deposit accounts
            interestCalculationService.calculateDailyInterest(AccountType.FIXED_DEPOSIT);
            
            logger.info("Interest calculation completed successfully");
        } catch (Exception e) {
            logger.error("Error during interest calculation", e);
        }
    }
}
