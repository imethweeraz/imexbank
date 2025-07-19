package com.imeth.imexbank.services.impl;

import com.imeth.imexbank.common.constants.BankingConstants;
import com.imeth.imexbank.common.dto.AccountDto;
import com.imeth.imexbank.common.enums.AccountType;
import com.imeth.imexbank.common.exceptions.AccountNotFoundException;
import com.imeth.imexbank.common.exceptions.BankingException;
import com.imeth.imexbank.common.exceptions.InsufficientFundsException;
import com.imeth.imexbank.dao.AccountDao;
import com.imeth.imexbank.dao.AuditLogDao;
import com.imeth.imexbank.dao.CustomerDao;
import com.imeth.imexbank.entities.Account;
import com.imeth.imexbank.entities.Customer;
import com.imeth.imexbank.services.interfaces.AccountService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.interceptor.Interceptors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRED)
@Interceptors({LoggingInterceptor.class, AuditInterceptor.class})
public class AccountServiceBean implements AccountService {

    private static final Logger logger = LoggerFactory.getLogger(AccountServiceBean.class);

    @EJB
    private AccountDao accountDao;

    @EJB
    private CustomerDao customerDao;

    @EJB
    private AuditLogDao auditLogDao;

    @Override
    @RolesAllowed({SecurityConstants.ROLE_TELLER, SecurityConstants.ROLE_MANAGER})
    public AccountDto createAccount(AccountDto accountDto) throws BankingException {
        logger.info("Creating new account for customer: {}", accountDto.getCustomerId());

        // Validate customer exists
        Customer customer = customerDao.findById(accountDto.getCustomerId())
                .orElseThrow(() -> new BankingException("Customer not found"));

        // Generate account number
        String accountNumber = generateAccountNumber(accountDto.getAccountType());

        // Create account entity
        Account account = new Account(accountNumber, accountDto.getAccountType(), customer);
        account.setInterestRate(getDefaultInterestRate(accountDto.getAccountType()));
        account.setOverdraftLimit(accountDto.getOverdraftLimit() != null ?
                accountDto.getOverdraftLimit() : BigDecimal.ZERO);

        // Set initial balance
        if (accountDto.getBalance() != null &&
                accountDto.getBalance().compareTo(BigDecimal.ZERO) > 0) {
            account.setBalance(accountDto.getBalance());
        }

        // Save account
        account = accountDao.create(account);

        // Audit log
        auditLogDao.createAsync("Account", account.getId(), "CREATE",
                getCurrentUsername(), null, account.getAccountNumber());

        return convertToDto(account);
    }

    @Override
    @RolesAllowed({SecurityConstants.ROLE_TELLER, SecurityConstants.ROLE_MANAGER})
    public AccountDto updateAccount(AccountDto accountDto) throws BankingException {
        logger.info("Updating account: {}", accountDto.getId());

        Account account = accountDao.findById(accountDto.getId())
                .orElseThrow(() -> new AccountNotFoundException(accountDto.getId()));

        String oldValue = account.toString();

        // Update allowed fields
        if (accountDto.getInterestRate() != null) {
            account.setInterestRate(accountDto.getInterestRate());
        }
        if (accountDto.getOverdraftLimit() != null) {
            account.setOverdraftLimit(accountDto.getOverdraftLimit());
        }

        account = accountDao.update(account);

        // Audit log
        auditLogDao.createAsync("Account", account.getId(), "UPDATE",
                getCurrentUsername(), oldValue, account.toString());

        return convertToDto(account);
    }

    @Override
    public AccountDto getAccount(Long accountId) throws AccountNotFoundException {
        logger.debug("Fetching account: {}", accountId);

        Account account = accountDao.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException(accountId));

        return convertToDto(account);
    }

    @Override
    public AccountDto getAccountByNumber(String accountNumber)
            throws AccountNotFoundException {
        logger.debug("Fetching account by number: {}", accountNumber);

        Account account = accountDao.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountNotFoundException(accountNumber));

        return convertToDto(account);
    }

    @Override
    public List<AccountDto> getCustomerAccounts(Long customerId) {
        logger.debug("Fetching accounts for customer: {}", customerId);

        return accountDao.findByCustomerId(customerId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<AccountDto> getAccountsByType(AccountType accountType) {
        logger.debug("Fetching accounts by type: {}", accountType);

        return accountDao.findByAccountType(accountType).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public BigDecimal getBalance(String accountNumber) throws AccountNotFoundException {
        logger.debug("Getting balance for account: {}", accountNumber);

        Account account = accountDao.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountNotFoundException(accountNumber));

        return account.getBalance();
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void deposit(String accountNumber, BigDecimal amount)
            throws BankingException {
        logger.info("Depositing {} to account: {}", amount, accountNumber);

        validateAmount(amount);

        Account account = accountDao.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountNotFoundException(accountNumber));

        if (!account.getIsActive()) {
            throw new BankingException("Account is inactive");
        }

        account.credit(amount);
        accountDao.update(account);

        logger.info("Deposit successful. New balance: {}", account.getBalance());
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void withdraw(String accountNumber, BigDecimal amount)
            throws BankingException {
        logger.info("Withdrawing {} from account: {}", amount, accountNumber);

        validateAmount(amount);

        Account account = accountDao.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountNotFoundException(accountNumber));

        if (!account.getIsActive()) {
            throw new BankingException("Account is inactive");
        }

        BigDecimal availableBalance = account.getAvailableBalance();
        if (amount.compareTo(availableBalance) > 0) {
            throw new InsufficientFundsException(accountNumber, amount, availableBalance);
        }

        account.debit(amount);
        accountDao.update(account);

        logger.info("Withdrawal successful. New balance: {}", account.getBalance());
    }

    @Override
    @RolesAllowed(SecurityConstants.ROLE_MANAGER)
    public void activateAccount(Long accountId) throws BankingException {
        logger.info("Activating account: {}", accountId);

        Account account = accountDao.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException(accountId));

        account.setIsActive(true);
        accountDao.update(account);

        auditLogDao.createAsync("Account", accountId, "ACTIVATE",
                getCurrentUsername(), "false", "true");
    }

    @Override
    @RolesAllowed(SecurityConstants.ROLE_MANAGER)
    public void deactivateAccount(Long accountId) throws BankingException {
        logger.info("Deactivating account: {}", accountId);

        Account account = accountDao.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException(accountId));

        account.setIsActive(false);
        accountDao.update(account);

        auditLogDao.createAsync("Account", accountId, "DEACTIVATE",
                getCurrentUsername(), "true", "false");
    }

    @Override
    public String generateAccountNumber(AccountType accountType) {
        String prefix = accountType.getCode();
        long timestamp = System.currentTimeMillis() % 1000000000L;
        int random = ThreadLocalRandom.current().nextInt(1000, 9999);
        return String.format("%s%09d%04d", prefix, timestamp, random);
    }

    private AccountDto convertToDto(Account account) {
        return new AccountDto.Builder()
                .id(account.getId())
                .accountNumber(account.getAccountNumber())
                .accountType(account.getAccountType())
                .balance(account.getBalance())
                .availableBalance(account.getAvailableBalance())
                .interestRate(account.getInterestRate())
                .overdraftLimit(account.getOverdraftLimit())
                .customerId(account.getCustomer().getId())
                .customerName(account.getCustomer().getFullName())
                .isActive(account.getIsActive())
                .createdDate(account.getCreatedDate())
                .lastTransactionDate(account.getLastTransactionDate())
                .build();
    }

    private void validateAmount(BigDecimal amount) throws BankingException {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BankingException("Amount must be positive");
        }
        if (amount.compareTo(BankingConstants.MAX_TRANSACTION_AMOUNT) > 0) {
            throw new BankingException("Amount exceeds maximum limit");
        }
    }

    private BigDecimal getDefaultInterestRate(AccountType accountType) {
        switch (accountType) {
            case SAVINGS:
                return BankingConstants.DEFAULT_SAVINGS_INTEREST_RATE;
            case FIXED_DEPOSIT:
                return BankingConstants.DEFAULT_FD_INTEREST_RATE;
            default:
                return BankingConstants.DEFAULT_CURRENT_INTEREST_RATE;
        }
    }

    private String getCurrentUsername() {
        // This would be retrieved from security context
        return "SYSTEM";
    }
}