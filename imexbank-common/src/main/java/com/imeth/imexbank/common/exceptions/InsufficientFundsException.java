package com.imeth.imexbank.common.exceptions;

import jakarta.ejb.ApplicationException;
import java.math.BigDecimal;

@ApplicationException(rollback = true)
public class InsufficientFundsException extends BankingException {

    private static final long serialVersionUID = 1L;

    private BigDecimal requestedAmount;
    private BigDecimal availableBalance;
    private String accountNumber;

    public InsufficientFundsException() {
        super("INSUFFICIENT_FUNDS", "Insufficient funds in account");
    }

    public InsufficientFundsException(String accountNumber, BigDecimal requestedAmount,
                                      BigDecimal availableBalance) {
        super("INSUFFICIENT_FUNDS",
                String.format("Insufficient funds in account %s. Requested: %s, Available: %s",
                        accountNumber, requestedAmount, availableBalance));
        this.accountNumber = accountNumber;
        this.requestedAmount = requestedAmount;
        this.availableBalance = availableBalance;
    }

    public BigDecimal getRequestedAmount() {
        return requestedAmount;
    }

    public BigDecimal getAvailableBalance() {
        return availableBalance;
    }

    public String getAccountNumber() {
        return accountNumber;
    }
}