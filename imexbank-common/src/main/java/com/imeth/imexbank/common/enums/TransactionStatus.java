package com.imeth.imexbank.common.enums;

public enum TransactionStatus {
    PENDING("Pending", "Transaction is pending processing"),
    PROCESSING("Processing", "Transaction is being processed"),
    COMPLETED("Completed", "Transaction completed successfully"),
    FAILED("Failed", "Transaction failed"),
    CANCELLED("Cancelled", "Transaction was cancelled"),
    REVERSED("Reversed", "Transaction has been reversed"),
    SCHEDULED("Scheduled", "Transaction is scheduled for future execution");

    private final String displayName;
    private final String description;

    TransactionStatus(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    public boolean isFinal() {
        return this == COMPLETED || this == FAILED || this == CANCELLED || this == REVERSED;
    }

    public boolean canBeProcessed() {
        return this == PENDING || this == SCHEDULED;
    }
}