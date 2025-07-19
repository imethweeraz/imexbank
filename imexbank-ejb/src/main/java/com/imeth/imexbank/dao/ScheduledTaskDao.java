package com.imeth.imexbank.dao;

import com.imeth.imexbank.common.enums.TimerTaskType;
import com.imeth.imexbank.entities.ScheduledTask;
import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class ScheduledTaskDao {

    private static final Logger logger = LoggerFactory.getLogger(ScheduledTaskDao.class);

    @PersistenceContext(unitName = "ImexBankPU")
    private EntityManager entityManager;

    public ScheduledTask create(ScheduledTask task) {
        logger.debug("Creating new scheduled task: {}", task.getTaskName());
        entityManager.persist(task);
        entityManager.flush();
        return task;
    }

    public ScheduledTask update(ScheduledTask task) {
        logger.debug("Updating scheduled task: {}", task.getTaskName());
        ScheduledTask merged = entityManager.merge(task);
        entityManager.flush();
        return merged;
    }

    public Optional<ScheduledTask> findById(Long id) {
        logger.debug("Finding scheduled task by ID: {}", id);
        ScheduledTask task = entityManager.find(ScheduledTask.class, id);
        return Optional.ofNullable(task);
    }

    public List<ScheduledTask> findActiveTask() {
        logger.debug("Finding all active scheduled tasks");
        TypedQuery<ScheduledTask> query = entityManager.createNamedQuery(
                "ScheduledTask.findActive", ScheduledTask.class);
        return query.getResultList();
    }

    public List<ScheduledTask> findByType(TimerTaskType taskType) {
        logger.debug("Finding scheduled tasks by type: {}", taskType);
        TypedQuery<ScheduledTask> query = entityManager.createNamedQuery(
                "ScheduledTask.findByType", ScheduledTask.class);
        query.setParameter("taskType", taskType);
        return query.getResultList();
    }

    public List<ScheduledTask> findPendingTasks() {
        logger.debug("Finding pending scheduled tasks");
        TypedQuery<ScheduledTask> query = entityManager.createNamedQuery(
                "ScheduledTask.findPending", ScheduledTask.class);
        query.setParameter("currentTime", LocalDateTime.now());
        return query.getResultList();
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void recordExecution(Long taskId, boolean success, String errorMessage) {
        logger.debug("Recording execution for task ID: {}, success: {}", taskId, success);
        ScheduledTask task = entityManager.find(ScheduledTask.class, taskId);
        if (task != null) {
            task.recordExecution(success, errorMessage);
            task.calculateNextExecutionTime();
            entityManager.merge(task);
            entityManager.flush();
        }
    }
}