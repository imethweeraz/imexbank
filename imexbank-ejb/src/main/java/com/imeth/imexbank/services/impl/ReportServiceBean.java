package com.imeth.imexbank.services.impl;

import com.imeth.imexbank.common.enums.TransactionStatus;
import com.imeth.imexbank.common.enums.TransactionType;
import com.imeth.imexbank.dao.AccountDao;
import com.imeth.imexbank.dao.AuditLogDao;
import com.imeth.imexbank.dao.CustomerDao;
import com.imeth.imexbank.dao.TransactionDao;
import com.imeth.imexbank.entities.Account;
import com.imeth.imexbank.entities.AuditLog;
import com.imeth.imexbank.entities.Customer;
import com.imeth.imexbank.entities.Transaction;
import com.imeth.imexbank.services.interfaces.ReportService;
import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class ReportServiceBean implements ReportService {

    private static final Logger logger = LoggerFactory.getLogger(ReportServiceBean.class);

    @EJB
    private TransactionDao transactionDao;

    @EJB
    private AccountDao accountDao;

    @EJB
    private CustomerDao customerDao;

    @EJB
    private AuditLogDao auditLogDao;

    @Override
    public Map<String, Object> generateDailyTransactionReport(LocalDate date) {
        logger.info("Generating daily transaction report for {}", date);

        Map<String, Object> report = new HashMap<>();

        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(23, 59, 59);

        List<Transaction> transactions = transactionDao.findByDateRange(startOfDay, endOfDay);

        // Calculate totals by type
        Map<TransactionType, BigDecimal> totalsByType = transactions.stream()
                .collect(Collectors.groupingBy(
                        Transaction::getTransactionType,
                        Collectors.reducing(BigDecimal.ZERO,
                                Transaction::getAmount,
                                BigDecimal::add)
                ));

        // Calculate totals by status
        Map<TransactionStatus, Long> countByStatus = transactions.stream()
                .collect(Collectors.groupingBy(
                        Transaction::getStatus,
                        Collectors.counting()
                ));

        // Overall statistics
        BigDecimal totalAmount = transactions.stream()
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        report.put("date", date);
        report.put("transactionCount", transactions.size());
        report.put("totalAmount", totalAmount);
        report.put("totalsByType", totalsByType);
        report.put("countByStatus", countByStatus);
        report.put("transactions", transactions);

        logger.info("Daily transaction report generated successfully");

        return report;
    }

    @Override
    public Map<String, Object> generateMonthlyAccountSummary(int year, int month) {
        logger.info("Generating monthly account summary for {}/{}", month, year);

        Map<String, Object> report = new HashMap<>();

        // Get all active accounts
        List<Account> accounts = accountDao.findActiveAccounts();

        // Calculate total balances
        BigDecimal totalSavingsBalance = accounts.stream()
                .filter(a -> a.getAccountType() == com.imeth.imexbank.common.enums.AccountType.SAVINGS)
                .map(Account::getBalance)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalCurrentBalance = accounts.stream()
                .filter(a -> a.getAccountType() == com.imeth.imexbank.common.enums.AccountType.CURRENT)
                .map(Account::getBalance)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Account statistics
        Map<com.imeth.imexbank.common.enums.AccountType, Long> accountCountByType =
                accounts.stream()
                        .collect(Collectors.groupingBy(
                                Account::getAccountType,
                                Collectors.counting()
                        ));

        report.put("year", year);
        report.put("month", month);
        report.put("totalAccounts", accounts.size());
        report.put("totalSavingsBalance", totalSavingsBalance);
        report.put("totalCurrentBalance", totalCurrentBalance);
        report.put("accountCountByType", accountCountByType);
        report.put("generatedDate", LocalDateTime.now());

        return report;
    }

    @Override
    public Map<String, Object> generateCustomerReport(Long customerId) {
        logger.info("Generating customer report for customer ID: {}", customerId);

        Map<String, Object> report = new HashMap<>();

        Customer customer = customerDao.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        List<Account> accounts = accountDao.findByCustomerId(customerId);

        BigDecimal totalBalance = accounts.stream()
                .map(Account::getBalance)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Get recent transactions
        List<Transaction> recentTransactions = accounts.stream()
                .flatMap(account -> transactionDao.findByAccountId(account.getId()).stream())
                .sorted((t1, t2) -> t2.getTransactionDate().compareTo(t1.getTransactionDate()))
                .limit(50)
                .collect(Collectors.toList());

        report.put("customer", customer);
        report.put("accounts", accounts);
        report.put("totalBalance", totalBalance);
        report.put("recentTransactions", recentTransactions);
        report.put("generatedDate", LocalDateTime.now());

        return report;
    }

    @Override
    public Map<String, Object> generateInterestReport(LocalDate startDate, LocalDate endDate) {
        logger.info("Generating interest report from {} to {}", startDate, endDate);

        Map<String, Object> report = new HashMap<>();

        // Find all interest credit transactions
        List<Transaction> interestTransactions = transactionDao
                .findByDateRange(startDate.atStartOfDay(), endDate.atTime(23, 59, 59))
                .stream()
                .filter(t -> t.getTransactionType() == TransactionType.INTEREST_CREDIT)
                .collect(Collectors.toList());

        BigDecimal totalInterestPaid = interestTransactions.stream()
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Map<com.imeth.imexbank.common.enums.AccountType, BigDecimal> interestByAccountType =
                new HashMap<>();

        for (Transaction transaction : interestTransactions) {
            if (transaction.getTargetAccount() != null) {
                com.imeth.imexbank.common.enums.AccountType type =
                        transaction.getTargetAccount().getAccountType();
                interestByAccountType.merge(type, transaction.getAmount(), BigDecimal::add);
            }
        }

        report.put("startDate", startDate);
        report.put("endDate", endDate);
        report.put("totalInterestPaid", totalInterestPaid);
        report.put("interestTransactionCount", interestTransactions.size());
        report.put("interestByAccountType", interestByAccountType);
        report.put("interestTransactions", interestTransactions);

        return report;
    }

    @Override
    public Map<String, Object> generateAuditReport(LocalDate startDate, LocalDate endDate) {
        logger.info("Generating audit report from {} to {}", startDate, endDate);

        Map<String, Object> report = new HashMap<>();

        List<AuditLog> auditLogs = auditLogDao.findByDateRange(
                startDate.atStartOfDay(),
                endDate.atTime(23, 59, 59)
        );

        // Group by action
        Map<String, Long> actionCounts = auditLogs.stream()
                .collect(Collectors.groupingBy(
                        AuditLog::getAction,
                        Collectors.counting()
                ));

        // Group by user
        Map<String, Long> userActionCounts = auditLogs.stream()
                .collect(Collectors.groupingBy(
                        AuditLog::getUsername,
                        Collectors.counting()
                ));

        report.put("startDate", startDate);
        report.put("endDate", endDate);
        report.put("totalAuditEntries", auditLogs.size());
        report.put("actionCounts", actionCounts);
        report.put("userActionCounts", userActionCounts);
        report.put("auditLogs", auditLogs);

        return report;
    }

    @Override
    public byte[] exportReportToPdf(Map<String, Object> reportData, String reportType) {
        logger.info("Exporting {} report to PDF", reportType);

        // Implementation would use a PDF generation library like iText or Apache PDFBox
        // This is a placeholder implementation

        return new byte[0];
    }

    @Override
    public byte[] exportReportToExcel(Map<String, Object> reportData, String reportType) {
        logger.info("Exporting {} report to Excel", reportType);

        // Implementation would use Apache POI or similar library
        // This is a placeholder implementation

        return new byte[0];
    }
}