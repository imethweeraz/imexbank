package com.imeth.imexbank.services.impl;

import com.imeth.imexbank.common.enums.TimerTaskType;
import com.imeth.imexbank.dao.ScheduledTaskDao;
import com.imeth.imexbank.entities.ScheduledTask;
import com.imeth.imexbank.services.interfaces.TimerService;
import jakarta.annotation.Resource;
import jakarta.ejb.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collection;
import java.util.Date;

@Singleton
@Startup
public class TimerServiceBean implements TimerService {

    private static final Logger logger = LoggerFactory.getLogger(TimerServiceBean.class);

    @Resource
    private TimerService timerService;

    @EJB
    private ScheduledTaskDao scheduledTaskDao;

    @Override
    public void createTimer(String timerName, TimerTaskType taskType,
                            String cronExpression, String parameters) {
        logger.info("Creating timer: {} with cron: {}", timerName, cronExpression);

        ScheduleExpression schedule = parseCronExpression(cronExpression);
        TimerConfig timerConfig = new TimerConfig(timerName, true);
        timerConfig.setInfo(new TimerInfo(timerName, taskType, parameters));

        timerService.createCalendarTimer(schedule, timerConfig);

        // Save to database
        ScheduledTask task = new ScheduledTask(timerName, taskType);
        task.setCronExpression(cronExpression);
        task.setParameters(parameters);
        task.setIsActive(true);
        scheduledTaskDao.create(task);
    }

    @Override
    public void createIntervalTimer(String timerName, TimerTaskType taskType,
                                    long intervalMinutes, String parameters) {
        logger.info("Creating interval timer: {} with interval: {} minutes",
                timerName, intervalMinutes);

        TimerConfig timerConfig = new TimerConfig(timerName, true);
        timerConfig.setInfo(new TimerInfo(timerName, taskType, parameters));

        long intervalMillis = intervalMinutes * 60 * 1000;
        timerService.createIntervalTimer(intervalMillis, intervalMillis, timerConfig);

        // Save to database
        ScheduledTask task = new ScheduledTask(timerName, taskType);
        task.setIntervalMinutes((int) intervalMinutes);
        task.setParameters(parameters);
        task.setIsActive(true);
        task.setNextExecutionTime(LocalDateTime.now().plusMinutes(intervalMinutes));
        scheduledTaskDao.create(task);
    }

    @Override
    public void createSingleActionTimer(String timerName, TimerTaskType taskType,
                                        LocalDateTime executionTime, String parameters) {
        logger.info("Creating single action timer: {} for {}", timerName, executionTime);

        TimerConfig timerConfig = new TimerConfig(timerName, false);
        timerConfig.setInfo(new TimerInfo(timerName, taskType, parameters));

        Date executionDate = Date.from(executionTime.atZone(ZoneId.systemDefault()).toInstant());
        timerService.createSingleActionTimer(executionDate, timerConfig);

        // Save to database
        ScheduledTask task = new ScheduledTask(timerName, taskType);
        task.setParameters(parameters);
        task.setIsActive(true);
        task.setNextExecutionTime(executionTime);
        scheduledTaskDao.create(task);
    }

    @Override
    public void cancelTimer(String timerName) {
        logger.info("Cancelling timer: {}", timerName);

        Collection<Timer> timers = timerService.getTimers();
        for (Timer timer : timers) {
            if (timer.getInfo() instanceof TimerInfo) {
                TimerInfo info = (TimerInfo) timer.getInfo();
                if (info.getTimerName().equals(timerName)) {
                    timer.cancel();
                    break;
                }
            }
        }

        // Update database
        scheduledTaskDao.findByType(null).stream()
                .filter(task -> task.getTaskName().equals(timerName))
                .findFirst()
                .ifPresent(task -> {
                    task.setIsActive(false);
                    scheduledTaskDao.update(task);
                });
    }

    @Override
    public void pauseTimer(String timerName) {
        // Implementation would depend on requirements
        logger.info("Pausing timer: {}", timerName);
        cancelTimer(timerName);
    }

    @Override
    public void resumeTimer(String timerName) {
        // Implementation would depend on requirements
        logger.info("Resuming timer: {}", timerName);
    }

    @Override
    public void executeTimerTask(TimerTaskType taskType) {
        logger.info("Manually executing timer task: {}", taskType);

        switch (taskType) {
            case SCHEDULED_TRANSFER:
                processScheduledTransfers();
                break;
            case INTEREST_CALCULATION:
                calculateInterest();
                break;
            case DAILY_BALANCE_UPDATE:
                updateDailyBalances();
                break;
            case REPORT_GENERATION:
                generateReports();
                break;
            case ACCOUNT_MAINTENANCE:
                performMaintenance();
                break;
            default:
                logger.warn("Unknown timer task type: {}", taskType);
        }
    }

    @Override
    public boolean isTimerActive(String timerName) {
        return timerService.getTimers().stream()
                .anyMatch(timer -> {
                    if (timer.getInfo() instanceof TimerInfo) {
                        TimerInfo info = (TimerInfo) timer.getInfo();
                        return info.getTimerName().equals(timerName);
                    }
                    return false;
                });
    }

    @Override
    public LocalDateTime getNextExecutionTime(String timerName) {
        return timerService.getTimers().stream()
                .filter(timer -> {
                    if (timer.getInfo() instanceof TimerInfo) {
                        TimerInfo info = (TimerInfo) timer.getInfo();
                        return info.getTimerName().equals(timerName);
                    }
                    return false;
                })
                .findFirst()
                .map(timer -> LocalDateTime.ofInstant(
                        timer.getNextTimeout().toInstant(),
                        ZoneId.systemDefault()))
                .orElse(null);
    }

    @Timeout
    public void handleTimeout(Timer timer) {
        if (timer.getInfo() instanceof TimerInfo) {
            TimerInfo info = (TimerInfo) timer.getInfo();
            logger.info("Timer expired: {} - Type: {}",
                    info.getTimerName(), info.getTaskType());

            executeTimerTask(info.getTaskType());

            // Record execution
            scheduledTaskDao.findByType(info.getTaskType()).stream()
                    .filter(task -> task.getTaskName().equals(info.getTimerName()))
                    .findFirst()
                    .ifPresent(task -> {
                        scheduledTaskDao.recordExecution(task.getId(), true, null);
                    });
        }
    }

    private ScheduleExpression parseCronExpression(String cronExpression) {
        // Simplified cron parsing - in production, use a proper parser
        String[] parts = cronExpression.split(" ");
        ScheduleExpression schedule = new ScheduleExpression();

        if (parts.length >= 5) {
            schedule.minute(parts[0]);
            schedule.hour(parts[1]);
            schedule.dayOfMonth(parts[2]);
            schedule.month(parts[3]);
            schedule.dayOfWeek(parts[4]);
        }

        return schedule;
    }

    private void processScheduledTransfers() {
        logger.info("Processing scheduled transfers");
        // Implementation delegated to transaction service
    }

    private void calculateInterest() {
        logger.info("Calculating interest");
        // Implementation delegated to interest calculation service
    }

    private void updateDailyBalances() {
        logger.info("Updating daily balances");
        // Implementation
    }

    private void generateReports() {
        logger.info("Generating reports");
        // Implementation delegated to report service
    }

    private void performMaintenance() {
        logger.info("Performing system maintenance");
        // Implementation
    }

    // Inner class for timer information
    private static class TimerInfo implements java.io.Serializable {
        private final String timerName;
        private final TimerTaskType taskType;
        private final String parameters;

        public TimerInfo(String timerName, TimerTaskType taskType, String parameters) {
            this.timerName = timerName;
            this.taskType = taskType;
            this.parameters = parameters;
        }

        public String getTimerName() {
            return timerName;
        }

        public TimerTaskType getTaskType() {
            return taskType;
        }

        public String getParameters() {
            return parameters;
        }
    }
}