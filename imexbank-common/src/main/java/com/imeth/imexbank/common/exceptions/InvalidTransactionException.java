package com.imeth.imexbank.common.exceptions;

import jakarta.ejb.ApplicationException;

@ApplicationException(rollback = true)
public class InvalidTransactionException extends BankingException {

    private static final long serialVersionUID = 1L;

    private String transactionReference;
    private String validationError;

    public InvalidTransactionException() {
        super("INVALID_TRANSACTION", "Invalid transaction");
    }

    public InvalidTransactionException(String message) {
        super("INVALID_TRANSACTION", message);
    }

    public InvalidTransactionException(String transactionReference, String validationError) {
        super("INVALID_TRANSACTION",
                String.format("Invalid transaction %s: %s", transactionReference, validationError));
        this.transactionReference = transactionReference;
        this.validationError = validationError;
    }

    public String getTransactionReference() {
        return transactionReference;
    }

    public String getValidationError() {
        return validationError;
    }
}