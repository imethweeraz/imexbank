package com.imeth.imexbank.validators;

import com.imeth.imexbank.common.constants.BankingConstants;
import com.imeth.imexbank.common.dto.TransferRequestDto;
import jakarta.ejb.Stateless;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Stateless
public class TransactionValidator {

    public List<String> validateTransferRequest(TransferRequestDto request) {
        List<String> errors = new ArrayList<>();

        // Validate account numbers
        if (request.getSourceAccountNumber() == null ||
                request.getSourceAccountNumber().trim().isEmpty()) {
            errors.add("Source account number is required");
        }

        if (request.getTargetAccountNumber() == null ||
                request.getTargetAccountNumber().trim().isEmpty()) {
            errors.add("Target account number is required");
        }

        if (request.getSourceAccountNumber() != null &&
                request.getTargetAccountNumber() != null &&
                request.getSourceAccountNumber().equals(request.getTargetAccountNumber())) {
            errors.add("Source and target accounts cannot be the same");
        }

        // Validate amount
        if (request.getAmount() == null) {
            errors.add("Transfer amount is required");
        } else {
            if (request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
                errors.add("Transfer amount must be positive");
            }
            if (request.getAmount().compareTo(BankingConstants.MIN_TRANSACTION_AMOUNT) < 0) {
                errors.add("Transfer amount is below minimum limit");
            }
            if (request.getAmount().compareTo(BankingConstants.MAX_TRANSACTION_AMOUNT) > 0) {
                errors.add("Transfer amount exceeds maximum limit");
            }
        }

        // Validate scheduled time
        if (request.getScheduledExecutionTime() != null) {
            if (request.getScheduledExecutionTime().isBefore(LocalDateTime.now())) {
                errors.add("Scheduled time cannot be in the past");
            }
            if (request.getScheduledExecutionTime().isAfter(
                    LocalDateTime.now().plusDays(365))) {
                errors.add("Scheduled time cannot be more than 1 year in the future");
            }
        }

        return errors;
    }
}