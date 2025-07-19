package com.imeth.imexbank.services.interfaces;

import com.imeth.imexbank.common.enums.TimerTaskType;
import jakarta.ejb.Local;

import java.time.LocalDateTime;

@Local
public interface TimerService {

    void createTimer(String timerName, TimerTaskType taskType,
                     String cronExpression, String parameters);

    void createIntervalTimer(String timerName, TimerTaskType taskType,
                             long intervalMinutes, String parameters);

    void createSingleActionTimer(String timerName, TimerTaskType taskType,
                                 LocalDateTime executionTime, String parameters);

    void cancelTimer(String timerName);

    void pauseTimer(String timerName);

    void resumeTimer(String timerName);

    void executeTimerTask(TimerTaskType taskType);

    boolean isTimerActive(String timerName);

    LocalDateTime getNextExecutionTime(String timerName);
}