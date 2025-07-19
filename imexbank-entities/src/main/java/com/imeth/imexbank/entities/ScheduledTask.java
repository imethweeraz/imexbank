package com.imeth.imexbank.entities;

import com.imeth.imexbank.common.enums.TimerTaskType;
import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "scheduled_tasks")
@NamedQueries({
        @NamedQuery(name = "ScheduledTask.findAll", query = "SELECT s FROM ScheduledTask s"),
        @NamedQuery(name = "ScheduledTask.findActive",
                query = "SELECT s FROM ScheduledTask s WHERE s.isActive = true"),
        @NamedQuery(name = "ScheduledTask.findByType",
                query = "SELECT s FROM ScheduledTask s WHERE s.taskType = :taskType"),
        @NamedQuery(name = "ScheduledTask.findPending",
                query = "SELECT s FROM ScheduledTask s WHERE s.nextExecutionTime <= :currentTime AND s.isActive = true")
})
public class ScheduledTask implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "task_id")
    private Long id;

    @Column(name = "task_name", nullable = false, length = 100)
    private String taskName;

    @Enumerated(EnumType.STRING)
    @Column(name = "task_type", nullable = false)
    private TimerTaskType taskType;

    @Column(name = "cron_expression", length = 100)
    private String cronExpression;

    @Column(name = "interval_minutes")
    private Integer intervalMinutes;

    @Column(name = "last_execution_time")
    private LocalDateTime lastExecutionTime;

    @Column(name = "next_execution_time")
    private LocalDateTime nextExecutionTime;

    @Column(name = "execution_count")
    private Integer executionCount = 0;

    @Column(name = "success_count")
    private Integer successCount = 0;

    @Column(name = "failure_count")
    private Integer failureCount = 0;

    @Column(name = "last_error_message", length = 1000)
    private String lastErrorMessage;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Column(name = "parameters", length = 2000)
    private String parameters;

    @Column(name = "created_date", nullable = false)
    private LocalDateTime createdDate;

    @Column(name = "modified_date")
    private LocalDateTime modifiedDate;

    @Version
    @Column(name = "version")
    private Long version;

    @PrePersist
    protected void onCreate() {
        createdDate = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        modifiedDate = LocalDateTime.now();
    }

    // Constructors
    public ScheduledTask() {
    }

    public ScheduledTask(String taskName, TimerTaskType taskType) {
        this.taskName = taskName;
        this.taskType = taskType;
    }

    // Business methods
    public void recordExecution(boolean success, String errorMessage) {
        this.executionCount++;
        this.lastExecutionTime = LocalDateTime.now();

        if (success) {
            this.successCount++;
            this.lastErrorMessage = null;
        } else {
            this.failureCount++;
            this.lastErrorMessage = errorMessage;
        }
    }

    public void calculateNextExecutionTime() {
        if (intervalMinutes != null && intervalMinutes > 0) {
            this.nextExecutionTime = LocalDateTime.now().plusMinutes(intervalMinutes);
        }
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public TimerTaskType getTaskType() {
        return taskType;
    }

    public void setTaskType(TimerTaskType taskType) {
        this.taskType = taskType;
    }

    public String getCronExpression() {
        return cronExpression;
    }

    public void setCronExpression(String cronExpression) {
        this.cronExpression = cronExpression;
    }

    public Integer getIntervalMinutes() {
        return intervalMinutes;
    }

    public void setIntervalMinutes(Integer intervalMinutes) {
        this.intervalMinutes = intervalMinutes;
    }

    public LocalDateTime getLastExecutionTime() {
        return lastExecutionTime;
    }

    public void setLastExecutionTime(LocalDateTime lastExecutionTime) {
        this.lastExecutionTime = lastExecutionTime;
    }

    public LocalDateTime getNextExecutionTime() {
        return nextExecutionTime;
    }

    public void setNextExecutionTime(LocalDateTime nextExecutionTime) {
        this.nextExecutionTime = nextExecutionTime;
    }

    public Integer getExecutionCount() {
        return executionCount;
    }

    public void setExecutionCount(Integer executionCount) {
        this.executionCount = executionCount;
    }

    public Integer getSuccessCount() {
        return successCount;
    }

    public void setSuccessCount(Integer successCount) {
        this.successCount = successCount;
    }

    public Integer getFailureCount() {
        return failureCount;
    }

    public void setFailureCount(Integer failureCount) {
        this.failureCount = failureCount;
    }

    public String getLastErrorMessage() {
        return lastErrorMessage;
    }

    public void setLastErrorMessage(String lastErrorMessage) {
        this.lastErrorMessage = lastErrorMessage;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public String getParameters() {
        return parameters;
    }

    public void setParameters(String parameters) {
        this.parameters = parameters;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public LocalDateTime getModifiedDate() {
        return modifiedDate;
    }

    public Long getVersion() {
        return version;
    }
}