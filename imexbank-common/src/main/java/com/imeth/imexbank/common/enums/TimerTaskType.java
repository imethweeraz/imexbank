package com.imeth.imexbank.common.enums;

public enum TimerTaskType {
    SCHEDULED_TRANSFER("Scheduled Transfer", "Process scheduled fund transfers"),
    INTEREST_CALCULATION("Interest Calculation", "Calculate and credit interest"),
    DAILY_BALANCE_UPDATE("Daily Balance Update", "Update daily account balances"),
    REPORT_GENERATION("Report Generation", "Generate periodic reports"),
    ACCOUNT_MAINTENANCE("Account Maintenance", "Perform account maintenance tasks"),
    SYSTEM_CLEANUP("System Cleanup", "Clean up old data and logs"),
    NOTIFICATION_PROCESSING("Notification Processing", "Process pending notifications");

    private final String displayName;
    private final String description;

    TimerTaskType(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }
}