package com.imeth.imexbank.common.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TransferRequestDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private String sourceAccountNumber;
    private String targetAccountNumber;
    private BigDecimal amount;
    private String description;
    private LocalDateTime scheduledExecutionTime;
    private String pin;
    private String otp;

    // Validation
    public boolean isValid() {
        return sourceAccountNumber != null && !sourceAccountNumber.isEmpty() &&
                targetAccountNumber != null && !targetAccountNumber.isEmpty() &&
                amount != null && amount.compareTo(BigDecimal.ZERO) > 0 &&
                !sourceAccountNumber.equals(targetAccountNumber);
    }

    public boolean isScheduled() {
        return scheduledExecutionTime != null &&
                scheduledExecutionTime.isAfter(LocalDateTime.now());
    }

    // Getters and Setters
    public String getSourceAccountNumber() {
        return sourceAccountNumber;
    }

    public void setSourceAccountNumber(String sourceAccountNumber) {
        this.sourceAccountNumber = sourceAccountNumber;
    }

    public String getTargetAccountNumber() {
        return targetAccountNumber;
    }

    public void setTargetAccountNumber(String targetAccountNumber) {
        this.targetAccountNumber = targetAccountNumber;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getScheduledExecutionTime() {
        return scheduledExecutionTime;
    }

    public void setScheduledExecutionTime(LocalDateTime scheduledExecutionTime) {
        this.scheduledExecutionTime = scheduledExecutionTime;
    }

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }
}