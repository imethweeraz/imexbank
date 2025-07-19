package com.imeth.imexbank.timers;

import com.imeth.imexbank.common.dto.TransferRequestDto;
import com.imeth.imexbank.common.enums.TransactionStatus;
import com.imeth.imexbank.dao.TransactionDao;
import com.imeth.imexbank.entities.Transaction;
import com.imeth.imexbank.services.interfaces.TransactionService;
import jakarta.ejb.EJB;
import jakarta.ejb.Schedule;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Singleton
@Startup
public class ScheduledFundTransferTimer {

    private static final Logger logger = LoggerFactory.getLogger(ScheduledFundTransferTimer.class);

    @EJB
    private TransactionDao transactionDao;

    @EJB
    private TransactionService transactionService;

    @Schedule(minute = "*/5", hour = "*", persistent = true,
            info = "Process scheduled fund transfers every 5 minutes")
    public void processScheduledTransfers() {
        logger.info("Starting scheduled fund transfer processing");

        try {
            List<Transaction> scheduledTransactions =
                    transactionDao.findScheduledTransactions();

            logger.info("Found {} scheduled transactions to process",
                    scheduledTransactions.size());

            for (Transaction transaction : scheduledTransactions) {
                try {
                    processScheduledTransaction(transaction);
                } catch (Exception e) {
                    logger.error("Error processing scheduled transaction: {}",
                            transaction.getReferenceNumber(), e);
                    transaction.markAsFailed(e.getMessage());
                    transactionDao.update(transaction);
                }
            }

            logger.info("Scheduled fund transfer processing completed");

        } catch (Exception e) {
            logger.error("Error in scheduled fund transfer timer", e);
        }
    }

    private void processScheduledTransaction(Transaction transaction) throws Exception {
        logger.debug("Processing scheduled transaction: {}",
                transaction.getReferenceNumber());

        // Update status to processing
        transaction.setStatus(TransactionStatus.PROCESSING);
        transactionDao.update(transaction);

        // Create transfer request
        TransferRequestDto transferRequest = new TransferRequestDto();
        transferRequest.setSourceAccountNumber(
                transaction.getSourceAccount().getAccountNumber());
        transferRequest.setTargetAccountNumber(
                transaction.getTargetAccount().getAccountNumber());
        transferRequest.setAmount(transaction.getAmount());
        transferRequest.setDescription(transaction.getDescription());

        // Process the transfer
        transactionService.processTransfer(transferRequest);

        logger.info("Scheduled transaction processed successfully: {}",
                transaction.getReferenceNumber());
    }
}