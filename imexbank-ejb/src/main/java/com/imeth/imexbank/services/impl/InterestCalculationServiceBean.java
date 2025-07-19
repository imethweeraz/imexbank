package com.imeth.imexbank.services.impl;

import com.imeth.imexbank.common.dto.InterestCalculationDto;
import com.imeth.imexbank.common.enums.AccountType;
import com.imeth.imexbank.common.enums.TransactionType;
import com.imeth.imexbank.dao.AccountDao;
import com.imeth.imexbank.dao.InterestRateDao;
import com.imeth.imexbank.dao.TransactionDao;
import com.imeth.imexbank.entities.Account;
import com.imeth.imexbank.entities.InterestRate;
import com.imeth.imexbank.entities.Transaction;
import com.imeth.imexbank.services.interfaces.InterestCalculationService;
import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Stateless
public class InterestCalculationServiceBean implements InterestCalculationService {

    private static final Logger logger = LoggerFactory.getLogger(InterestCalculationServiceBean.class);

    @EJB
    private AccountDao accountDao;

    @EJB
    private InterestRateDao interestRateDao;

    @EJB
    private TransactionDao transactionDao;

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void calculateDailyInterest() {
        logger.info("Starting daily interest calculation");

        LocalDate today = LocalDate.now();

        // Process savings accounts
        List<InterestCalculationDto> savingsCalculations =
                calculateInterestForAccountType(AccountType.SAVINGS, today);
        creditInterestToAccounts(savingsCalculations);

        // Process fixed deposits
        List<InterestCalculationDto> fdCalculations =
                calculateInterestForAccountType(AccountType.FIXED_DEPOSIT, today);
        creditInterestToAccounts(fdCalculations);

        logger.info("Daily interest calculation completed. Processed {} accounts",
                savingsCalculations.size() + fdCalculations.size());
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void calculateMonthlyInterest() {
        logger.info("Starting monthly interest calculation");

        LocalDate today = LocalDate.now();
        LocalDate firstDayOfMonth = today.withDayOfMonth(1);

        // Process recurring deposits
        List<InterestCalculationDto> rdCalculations =
                calculateInterestForAccountType(AccountType.RECURRING_DEPOSIT, today);
        creditInterestToAccounts(rdCalculations);

        logger.info("Monthly interest calculation completed");
    }

    @Override
    public InterestCalculationDto calculateInterestForAccount(Long accountId,
                                                              LocalDate calculationDate) {
        logger.debug("Calculating interest for account: {}", accountId);

        Account account = accountDao.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        InterestRate rate = interestRateDao.findEffectiveRate(
                account.getAccountType(),
                account.getBalance(),
                calculationDate
        ).orElse(null);

        if (rate == null) {
            logger.warn("No interest rate found for account: {}", accountId);
            return null;
        }

        InterestCalculationDto calculation = new InterestCalculationDto();
        calculation.setAccountId(account.getId());
        calculation.setAccountNumber(account.getAccountNumber());
        calculation.setAccountType(account.getAccountType());
        calculation.setPrincipalAmount(account.getBalance());
        calculation.setInterestRate(rate.getRatePercentage());
        calculation.setCalculationDate(calculationDate);

        // Calculate days since last interest credit
        LocalDate lastInterestDate = getLastInterestCreditDate(account);
        int days = (int) ChronoUnit.DAYS.between(lastInterestDate, calculationDate);
        calculation.setNumberOfDays(days);

        // Calculate interest
        if (account.getAccountType() == AccountType.FIXED_DEPOSIT) {
            calculation.calculateCompoundInterest(12); // Monthly compounding
        } else {
            calculation.calculateSimpleInterest();
        }

        return calculation;
    }

    @Override
    public List<InterestCalculationDto> calculateInterestForAccountType(
            AccountType accountType, LocalDate calculationDate) {
        logger.debug("Calculating interest for all {} accounts", accountType);

        List<InterestCalculationDto> calculations = new ArrayList<>();
        List<Account> accounts = accountDao.findAccountsForInterestCalculation(accountType);

        for (Account account : accounts) {
            try {
                InterestCalculationDto calculation =
                        calculateInterestForAccount(account.getId(), calculationDate);
                if (calculation != null &&
                        calculation.getInterestAmount().compareTo(BigDecimal.ZERO) > 0) {
                    calculations.add(calculation);
                }
            } catch (Exception e) {
                logger.error("Error calculating interest for account: {}",
                        account.getAccountNumber(), e);
            }
        }

        return calculations;
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void creditInterestToAccounts(List<InterestCalculationDto> calculations) {
        logger.info("Crediting interest to {} accounts", calculations.size());

        for (InterestCalculationDto calculation : calculations) {
            try {
                Account account = accountDao.findById(calculation.getAccountId())
                        .orElseThrow(() -> new RuntimeException("Account not found"));

                // Create interest credit transaction
                Transaction transaction = new Transaction();
                transaction.setReferenceNumber(generateInterestReference());
                transaction.setTransactionType(TransactionType.INTEREST_CREDIT);
                transaction.setTargetAccount(account);
                transaction.setAmount(calculation.getInterestAmount());
                transaction.setDescription(String.format(
                        "Interest credit for %d days at %.2f%%",
                        calculation.getNumberOfDays(),
                        calculation.getInterestRate()
                ));
                transaction.setCreatedBy("SYSTEM");

                // Credit the account
                account.credit(calculation.getInterestAmount());

                // Save transaction and update account
                transactionDao.create(transaction);
                accountDao.update(account);

                logger.debug("Interest credited to account: {} - Amount: {}",
                        account.getAccountNumber(), calculation.getInterestAmount());

            } catch (Exception e) {
                logger.error("Error crediting interest for account: {}",
                        calculation.getAccountNumber(), e);
            }
        }
    }

    @Override
    public void updateInterestRates(AccountType accountType, BigDecimal newRate,
                                    LocalDate effectiveDate) {
        logger.info("Updating interest rate for {} to {}% effective {}",
                accountType, newRate, effectiveDate);

        // Expire existing rates
        List<InterestRate> existingRates = interestRateDao.findByAccountType(accountType);
        for (InterestRate rate : existingRates) {
            if (rate.getIsActive() &&
                    (rate.getExpiryDate() == null || rate.getExpiryDate().isAfter(effectiveDate))) {
                rate.setExpiryDate(effectiveDate.minusDays(1));
                interestRateDao.update(rate);
            }
        }

        // Create new rate
        InterestRate newInterestRate = new InterestRate(accountType, newRate, effectiveDate);
        interestRateDao.create(newInterestRate);
    }

    private LocalDate getLastInterestCreditDate(Account account) {
        // Find the last interest credit transaction
        List<Transaction> transactions = transactionDao.findByAccountId(account.getId());

        return transactions.stream()
                .filter(t -> t.getTransactionType() == TransactionType.INTEREST_CREDIT)
                .filter(t -> t.getTargetAccount().getId().equals(account.getId()))
                .map(t -> t.getTransactionDate().toLocalDate())
                .max(LocalDate::compareTo)
                .orElse(account.getCreatedDate().toLocalDate());
    }

    private String generateInterestReference() {
        return "INT" + System.currentTimeMillis();
    }
}