package com.imeth.imexbank.common.constants;

import java.math.BigDecimal;

public final class BankingConstants {

    private BankingConstants() {
        // Private constructor to prevent instantiation
    }

    // Account Number Formats
    public static final String ACCOUNT_NUMBER_PREFIX = "ACC";
    public static final String ACCOUNT_NUMBER_FORMAT = "ACC%010d";
    public static final int ACCOUNT_NUMBER_LENGTH = 13;

    // Customer Number Formats
    public static final String CUSTOMER_NUMBER_PREFIX = "CUST";
    public static final String CUSTOMER_NUMBER_FORMAT = "CUST%08d";
    public static final int CUSTOMER_NUMBER_LENGTH = 12;

    // Transaction Reference Formats
    public static final String TRANSACTION_REF_PREFIX = "TXN";
    public static final String TRANSACTION_REF_FORMAT = "TXN%s%010d";
    public static final int TRANSACTION_REF_LENGTH = 20;

    // Minimum and Maximum Amounts
    public static final BigDecimal MIN_TRANSACTION_AMOUNT = new BigDecimal("1.00");
    public static final BigDecimal MAX_TRANSACTION_AMOUNT = new BigDecimal("1000000.00");
    public static final BigDecimal MIN_BALANCE_SAVINGS = new BigDecimal("500.00");
    public static final BigDecimal MIN_BALANCE_CURRENT = new BigDecimal("1000.00");

    // Transaction Limits
    public static final int MAX_DAILY_TRANSACTIONS = 50;
    public static final BigDecimal MAX_DAILY_WITHDRAWAL = new BigDecimal("50000.00");
    public static final BigDecimal MAX_DAILY_TRANSFER = new BigDecimal("100000.00");

    // Interest Rates (Annual Percentage)
    public static final BigDecimal DEFAULT_SAVINGS_INTEREST_RATE = new BigDecimal("3.50");
    public static final BigDecimal DEFAULT_CURRENT_INTEREST_RATE = new BigDecimal("0.00");
    public static final BigDecimal DEFAULT_FD_INTEREST_RATE = new BigDecimal("6.50");

    // Fee Structure
    public static final BigDecimal TRANSFER_FEE_PERCENTAGE = new BigDecimal("0.1");
    public static final BigDecimal MIN_TRANSFER_FEE = new BigDecimal("10.00");
    public static final BigDecimal MAX_TRANSFER_FEE = new BigDecimal("500.00");
    public static final BigDecimal ATM_WITHDRAWAL_FEE = new BigDecimal("20.00");

    // Time Constants
    public static final int SESSION_TIMEOUT_MINUTES = 30;
    public static final int PASSWORD_EXPIRY_DAYS = 90;
    public static final int TRANSACTION_RETENTION_DAYS = 2555; // 7 years
    public static final int AUDIT_LOG_RETENTION_DAYS = 3650; // 10 years

    // Pagination
    public static final int DEFAULT_PAGE_SIZE = 20;
    public static final int MAX_PAGE_SIZE = 100;

    // Validation Patterns
    public static final String EMAIL_PATTERN =
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
    public static final String PHONE_PATTERN =
            "^\\+?[0-9]{10,15}$";
    public static final String NIC_PATTERN =
            "^[0-9]{9}[vVxX]$|^[0-9]{12}$";

    // Status Messages
    public static final String SUCCESS_MESSAGE = "Operation completed successfully";
    public static final String FAILURE_MESSAGE = "Operation failed";
    public static final String PENDING_MESSAGE = "Operation is pending";

    // Error Messages
    public static final String INVALID_ACCOUNT = "Invalid account number";
    public static final String INVALID_AMOUNT = "Invalid transaction amount";
    public static final String INSUFFICIENT_BALANCE = "Insufficient account balance";
    public static final String ACCOUNT_INACTIVE = "Account is inactive";
    public static final String TRANSACTION_LIMIT_EXCEEDED = "Transaction limit exceeded";
}