package com.imeth.imexbank.timers;

import com.imeth.imexbank.dao.AccountDao;
import com.imeth.imexbank.entities.Account;
import jakarta.ejb.EJB;
import jakarta.ejb.Schedule;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Singleton
@Startup
public class DailyBalanceUpdateTimer {

    private static final Logger logger = LoggerFactory.getLogger(DailyBalanceUpdateTimer.class);

    @EJB
    private AccountDao accountDao;

    @Schedule(hour = "0", minute = "30", persistent = true,
            info = "Update daily balances at 12:30 AM")
    public void updateDailyBalances() {
        logger.info("Starting daily balance update");

        try {
            List<Account> activeAccounts = accountDao.findActiveAccounts();
            int processedCount = 0;
            BigDecimal totalBalance = BigDecimal.ZERO;

            for (Account account : activeAccounts) {
                try {
                    // Store daily balance snapshot
                    // This could be saved to a separate historical table
                    totalBalance = totalBalance.add(account.getBalance());
                    processedCount++;

                    // Additional daily processing
                    checkMinimumBalance(account);

                } catch (Exception e) {
                    logger.error("Error updating daily balance for account: {}",
                            account.getAccountNumber(), e);
                }
            }

            logger.info("Daily balance update completed. Processed {} accounts. Total balance: {}",
                    processedCount, totalBalance);

        } catch (Exception e) {
            logger.error("Error in daily balance update timer", e);
        }
    }

    private void checkMinimumBalance(Account account) {
        BigDecimal minimumBalance = getMinimumBalance(account);

        if (account.getBalance().compareTo(minimumBalance) < 0) {
            logger.warn("Account {} is below minimum balance. Current: {}, Minimum: {}",
                    account.getAccountNumber(), account.getBalance(), minimumBalance);
            // Could trigger notifications or apply penalties
        }
    }

    private BigDecimal getMinimumBalance(Account account) {
        switch (account.getAccountType()) {
            case SAVINGS:
                return new BigDecimal("500.00");
            case CURRENT:
                return new BigDecimal("1000.00");
            default:
                return BigDecimal.ZERO;
        }
    }
}