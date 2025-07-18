package com.imeth.imexbank.common.exceptions;

public class BankingException extends RuntimeException {
    public BankingException(String message) {
        super(message);
    }
}
