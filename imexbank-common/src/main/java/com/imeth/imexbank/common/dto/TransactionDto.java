package com.imeth.imexbank.common.dto;

import com.imeth.imexbank.common.enums.TransactionStatus;
import com.imeth.imexbank.common.enums.TransactionType;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TransactionDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String referenceNumber;
    private TransactionType transactionType;
    private Long sourceAccountId;
    private String sourceAccountNumber;
    private Long targetAccountId;
    private String targetAccountNumber;
    private BigDecimal amount;
    private String description;
    private TransactionStatus status;
    private LocalDateTime transactionDate;
    private LocalDateTime scheduledExecutionTime;
    private String failureReason;
    private String createdBy;

    // Constructors
    public TransactionDto() {
    }

    public TransactionDto(String referenceNumber, TransactionType transactionType,
                          BigDecimal amount, TransactionStatus status) {
        this.referenceNumber = referenceNumber;
        this.transactionType = transactionType;
        this.amount = amount;
        this.status = status;
    }

    // Validation methods
    public boolean isValid() {
        return referenceNumber != null && !referenceNumber.isEmpty() &&
                transactionType != null &&
                amount != null && amount.compareTo(BigDecimal.ZERO) > 0 &&
                (transactionType != TransactionType.TRANSFER ||
                        (sourceAccountId != null && targetAccountId != null));
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getReferenceNumber() {
        return referenceNumber;
    }

    public void setReferenceNumber(String referenceNumber) {
        this.referenceNumber = referenceNumber;
    }

    public TransactionType getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(TransactionType transactionType) {
        this.transactionType = transactionType;
    }

    public Long getSourceAccountId() {
        return sourceAccountId;
    }

    public void setSourceAccountId(Long sourceAccountId) {
        this.sourceAccountId = sourceAccountId;
    }

    public String getSourceAccountNumber() {
        return sourceAccountNumber;
    }

    public void setSourceAccountNumber(String sourceAccountNumber) {
        this.sourceAccountNumber = sourceAccountNumber;
    }

    public Long getTargetAccountId() {
        return targetAccountId;
    }

    public void setTargetAccountId(Long targetAccountId) {
        this.targetAccountId = targetAccountId;
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

    public TransactionStatus getStatus() {
        return status;
    }

    public void setStatus(TransactionStatus status) {
        this.status = status;
    }

    public LocalDateTime getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(LocalDateTime transactionDate) {
        this.transactionDate = transactionDate;
    }

    public LocalDateTime getScheduledExecutionTime() {
        return scheduledExecutionTime;
    }

    public void setScheduledExecutionTime(LocalDateTime scheduledExecutionTime) {
        this.scheduledExecutionTime = scheduledExecutionTime;
    }

    public String getFailureReason() {
        return failureReason;
    }

    public void setFailureReason(String failureReason) {
        this.failureReason = failureReason;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }
}