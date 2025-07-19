package com.imeth.imexbank.dao;

import com.imeth.imexbank.entities.AuditLog;
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

@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
public class AuditLogDao {

    private static final Logger logger = LoggerFactory.getLogger(AuditLogDao.class);

    @PersistenceContext(unitName = "ImexBankPU")
    private EntityManager entityManager;

    public AuditLog create(AuditLog auditLog) {
        logger.debug("Creating audit log entry: {}", auditLog.getAction());
        entityManager.persist(auditLog);
        entityManager.flush();
        return auditLog;
    }

    public List<AuditLog> findByEntity(String entityName) {
        logger.debug("Finding audit logs for entity: {}", entityName);
        TypedQuery<AuditLog> query = entityManager.createNamedQuery(
                "AuditLog.findByEntity", AuditLog.class);
        query.setParameter("entityName", entityName);
        return query.getResultList();
    }

    public List<AuditLog> findByUser(String username) {
        logger.debug("Finding audit logs for user: {}", username);
        TypedQuery<AuditLog> query = entityManager.createNamedQuery(
                "AuditLog.findByUser", AuditLog.class);
        query.setParameter("username", username);
        return query.getResultList();
    }

    public List<AuditLog> findByDateRange(LocalDateTime startDate,
                                          LocalDateTime endDate) {
        logger.debug("Finding audit logs between {} and {}", startDate, endDate);
        TypedQuery<AuditLog> query = entityManager.createNamedQuery(
                "AuditLog.findByDateRange", AuditLog.class);
        query.setParameter("startDate", startDate);
        query.setParameter("endDate", endDate);
        return query.getResultList();
    }

    public void createAsync(String entityName, Long entityId, String action,
                            String username, String oldValue, String newValue) {
        try {
            AuditLog log = new AuditLog(entityName, entityId, action, username);
            log.setOldValue(oldValue);
            log.setNewValue(newValue);
            create(log);
        } catch (Exception e) {
            logger.error("Failed to create audit log", e);
            // Don't throw exception to avoid disrupting main transaction
        }
    }
}