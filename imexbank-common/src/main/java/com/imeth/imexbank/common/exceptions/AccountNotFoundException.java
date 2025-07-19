package com.imeth.imexbank.common.exceptions;

import jakarta.ejb.ApplicationException;

@ApplicationException(rollback = true)
public class AccountNotFoundException extends BankingException {

    private static final long serialVersionUID = 1L;

    private String accountNumber;
    private Long accountId;

    public AccountNotFoundException() {
        super("ACCOUNT_NOT_FOUND", "Account not found");
    }

    public AccountNotFoundException(String accountNumber) {
        super("ACCOUNT_NOT_FOUND", "Account not found: " + accountNumber);
        this.accountNumber = accountNumber;
    }

    public AccountNotFoundException(Long accountId) {
        super("ACCOUNT_NOT_FOUND", "Account not found with ID: " + accountId);
        this.accountId = accountId;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public Long getAccountId() {
        return accountId;
    }
}