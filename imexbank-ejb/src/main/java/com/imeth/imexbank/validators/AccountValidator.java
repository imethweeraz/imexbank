package com.imeth.imexbank.validators;

import com.imeth.imexbank.common.constants.BankingConstants;
import com.imeth.imexbank.common.dto.AccountDto;
import com.imeth.imexbank.common.enums.AccountType;
import jakarta.ejb.Stateless;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Stateless
public class AccountValidator {

    public List<String> validateAccountCreation(AccountDto accountDto) {
        List<String> errors = new ArrayList<>();

        if (accountDto.getAccountType() == null) {
            errors.add("Account type is required");
        }

        if (accountDto.getCustomerId() == null) {
            errors.add("Customer ID is required");
        }

        // Validate initial balance
        if (accountDto.getBalance() != null) {
            BigDecimal minBalance = getMinimumBalance(accountDto.getAccountType());
            if (accountDto.getBalance().compareTo(minBalance) < 0) {
                errors.add("Initial balance must be at least " + minBalance);
            }
        }

        // Validate overdraft limit
        if (accountDto.getOverdraftLimit() != null &&
                accountDto.getOverdraftLimit().compareTo(BigDecimal.ZERO) < 0) {
            errors.add("Overdraft limit cannot be negative");
        }

        return errors;
    }

    public List<String> validateAccountUpdate(AccountDto accountDto) {
        List<String> errors = new ArrayList<>();

        if (accountDto.getId() == null) {
            errors.add("Account ID is required for update");
        }

        // Validate interest rate
        if (accountDto.getInterestRate() != null) {
            if (accountDto.getInterestRate().compareTo(BigDecimal.ZERO) < 0) {
                errors.add("Interest rate cannot be negative");
            }
            if (accountDto.getInterestRate().compareTo(new BigDecimal("20")) > 0) {
                errors.add("Interest rate cannot exceed 20%");
            }
        }

        return errors;
    }

    private BigDecimal getMinimumBalance(AccountType accountType) {
        if (accountType == null) {
            return BigDecimal.ZERO;
        }

        switch (accountType) {
            case SAVINGS:
                return BankingConstants.MIN_BALANCE_SAVINGS;
            case CURRENT:
                return BankingConstants.MIN_BALANCE_CURRENT;
            default:
                return BigDecimal.ZERO;
        }
    }
}