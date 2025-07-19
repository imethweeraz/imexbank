package com.imeth.imexbank.common.exceptions;

import com.imeth.imexbank.common.enums.TimerTaskType;
import jakarta.ejb.ApplicationException;

@ApplicationException(rollback = true)
public class TimerServiceException extends BankingException {

    private static final long serialVersionUID = 1L;

    private TimerTaskType taskType;
    private String taskName;

    public TimerServiceException() {
        super("TIMER_SERVICE_ERROR", "Timer service error");
    }

    public TimerServiceException(String message) {
        super("TIMER_SERVICE_ERROR", message);
    }

    public TimerServiceException(String message, Throwable cause) {
        super("TIMER_SERVICE_ERROR", message, cause);
    }

    public TimerServiceException(TimerTaskType taskType, String taskName, String message) {
        super("TIMER_SERVICE_ERROR",
                String.format("Timer service error for task %s (%s): %s",
                        taskName, taskType, message));
        this.taskType = taskType;
        this.taskName = taskName;
    }

    public TimerServiceException(TimerTaskType taskType, String taskName,
                                 String message, Throwable cause) {
        super("TIMER_SERVICE_ERROR",
                String.format("Timer service error for task %s (%s): %s",
                        taskName, taskType, message), cause);
        this.taskType = taskType;
        this.taskName = taskName;
    }

    public TimerTaskType getTaskType() {
        return taskType;
    }

    public String getTaskName() {
        return taskName;
    }
}