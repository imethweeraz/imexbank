package com.imeth.imexbank.timers;

import com.imeth.imexbank.services.interfaces.AccountService;
import jakarta.ejb.EJB;
import jakarta.ejb.Schedule;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
@Startup
public class DailyBalanceUpdateTimer {

    private static final Logger logger = LoggerFactory.getLogger(DailyBalanceUpdateTimer.class);

    @EJB
    private AccountService accountService;

    @Schedule(
        hour = "23", 
        minute = "59", 
        persistent = true,
        info = "Daily balance update at end of day"
    )
    public void updateDailyBalances() {
        logger.info("Starting daily balance update process");
        
        try {
            accountService.updateDailyBalances();
            logger.info("Daily balance update completed successfully");
        } catch (Exception e) {
            logger.error("Error during daily balance update", e);
        }
    }
}
