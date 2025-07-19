package com.imeth.imexbank.common.constants;

public final class TimerConstants {

    private TimerConstants() {
        // Private constructor to prevent instantiation
    }

    // Timer Names
    public static final String SCHEDULED_TRANSFER_TIMER = "ScheduledTransferTimer";
    public static final String INTEREST_CALCULATION_TIMER = "InterestCalculationTimer";
    public static final String DAILY_BALANCE_UPDATE_TIMER = "DailyBalanceUpdateTimer";
    public static final String REPORT_GENERATION_TIMER = "ReportGenerationTimer";
    public static final String MAINTENANCE_TIMER = "MaintenanceTimer";

    // Timer Intervals (in milliseconds)
    public static final long ONE_MINUTE = 60000L;
    public static final long FIVE_MINUTES = 300000L;
    public static final long TEN_MINUTES = 600000L;
    public static final long THIRTY_MINUTES = 1800000L;
    public static final long ONE_HOUR = 3600000L;
    public static final long ONE_DAY = 86400000L;

    // Cron Expressions
    public static final String EVERY_MINUTE = "0 * * * * ?";
    public static final String EVERY_FIVE_MINUTES = "0 */5 * * * ?";
    public static final String EVERY_HOUR = "0 0 * * * ?";
    public static final String DAILY_AT_MIDNIGHT = "0 0 0 * * ?";
    public static final String DAILY_AT_2AM = "0 0 2 * * ?";
    public static final String DAILY_AT_END_OF_DAY = "0 59 23 * * ?";
    public static final String MONTHLY_FIRST_DAY = "0 0 0 1 * ?";

    // Schedule Expressions for @Schedule annotation
    public static final String SCHEDULE_EVERY_MINUTE = "*/1 * * * *";
    public static final String SCHEDULE_EVERY_5_MINUTES = "*/5 * * * *";
    public static final String SCHEDULE_EVERY_HOUR = "0 * * * *";
    public static final String SCHEDULE_DAILY_MIDNIGHT = "0 0 * * *";
    public static final String SCHEDULE_DAILY_2AM = "0 2 * * *";
    public static final String SCHEDULE_DAILY_EOD = "59 23 * * *";

    // Timer Configuration
    public static final boolean TIMER_PERSISTENT = true;
    public static final int MAX_RETRY_ATTEMPTS = 3;
    public static final long RETRY_INTERVAL = FIVE_MINUTES;

    // Batch Processing
    public static final int BATCH_SIZE = 100;
    public static final int MAX_CONCURRENT_TIMERS = 5;

    // Timer Status Messages
    public static final String TIMER_STARTED = "Timer started successfully";
    public static final String TIMER_COMPLETED = "Timer completed successfully";
    public static final String TIMER_FAILED = "Timer execution failed";
    public static final String TIMER_CANCELLED = "Timer cancelled";
    public static final String TIMER_TIMEOUT = "Timer execution timeout";
}