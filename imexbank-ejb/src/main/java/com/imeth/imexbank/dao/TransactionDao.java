package com.imeth.imexbank.dao;

import com.imeth.imexbank.common.enums.TransactionStatus;
import com.imeth.imexbank.common.enums.TransactionType;
import com.imeth.imexbank.entities.Transaction;
import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class TransactionDao {

    private static final Logger logger = LoggerFactory.getLogger(TransactionDao.class);

    @PersistenceContext(unitName = "ImexBankPU")
    private EntityManager entityManager;

    public Transaction create(Transaction transaction) {
        logger.debug("Creating new transaction: {}", transaction.getReferenceNumber());
        entityManager.persist(transaction);
        entityManager.flush();
        return transaction;
    }

    public Transaction update(Transaction transaction) {
        logger.debug("Updating transaction: {}", transaction.getReferenceNumber());
        Transaction merged = entityManager.merge(transaction);
        entityManager.flush();
        return merged;
    }

    public Optional<Transaction> findById(Long id) {
        logger.debug("Finding transaction by ID: {}", id);
        Transaction transaction = entityManager.find(Transaction.class, id);
        return Optional.ofNullable(transaction);
    }

    public Optional<Transaction> findByReferenceNumber(String referenceNumber) {
        logger.debug("Finding transaction by reference: {}", referenceNumber);
        try {
            TypedQuery<Transaction> query = entityManager.createNamedQuery(
                    "Transaction.findByReferenceNumber", Transaction.class);
            query.setParameter("referenceNumber", referenceNumber);
            return Optional.of(query.getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    public List<Transaction> findByAccountId(Long accountId) {
        logger.debug("Finding transactions by account ID: {}", accountId);
        TypedQuery<Transaction> query = entityManager.createNamedQuery(
                "Transaction.findByAccount", Transaction.class);
        query.setParameter("accountId", accountId);
        return query.getResultList();
    }

    public List<Transaction> findByDateRange(LocalDateTime startDate,
                                             LocalDateTime endDate) {
        logger.debug("Finding transactions between {} and {}", startDate, endDate);
        TypedQuery<Transaction> query = entityManager.createNamedQuery(
                "Transaction.findByDateRange", Transaction.class);
        query.setParameter("startDate", startDate);
        query.setParameter("endDate", endDate);
        return query.getResultList();
    }

    public List<Transaction> findByStatus(TransactionStatus status) {
        logger.debug("Finding transactions by status: {}", status);
        TypedQuery<Transaction> query = entityManager.createNamedQuery(
                "Transaction.findByStatus", Transaction.class);
        query.setParameter("status", status);
        return query.getResultList();
    }

    public List<Transaction> findScheduledTransactions() {
        logger.debug("Finding scheduled transactions ready for execution");
        TypedQuery<Transaction> query = entityManager.createQuery(
                "SELECT t FROM Transaction t WHERE t.status = :status " +
                        "AND t.scheduledExecutionTime <= :currentTime", Transaction.class);
        query.setParameter("status", TransactionStatus.SCHEDULED);
        query.setParameter("currentTime", LocalDateTime.now());
        return query.getResultList();
    }

    public List<Transaction> findPendingTransactions() {
        logger.debug("Finding pending transactions");
        TypedQuery<Transaction> query = entityManager.createQuery(
                "SELECT t FROM Transaction t WHERE t.status = :status",
                Transaction.class);
        query.setParameter("status", TransactionStatus.PENDING);
        return query.getResultList();
    }

    public List<Transaction> searchTransactions(Long accountId,
                                                TransactionType type,
                                                TransactionStatus status,
                                                LocalDateTime startDate,
                                                LocalDateTime endDate) {
        logger.debug("Searching transactions with criteria");

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Transaction> cq = cb.createQuery(Transaction.class);
        Root<Transaction> transaction = cq.from(Transaction.class);

        List<Predicate> predicates = new ArrayList<>();

        if (accountId != null) {
            predicates.add(cb.or(
                    cb.equal(transaction.get("sourceAccount").get("id"), accountId),
                    cb.equal(transaction.get("targetAccount").get("id"), accountId)
            ));
        }

        if (type != null) {
            predicates.add(cb.equal(transaction.get("transactionType"), type));
        }

        if (status != null) {
            predicates.add(cb.equal(transaction.get("status"), status));
        }

        if (startDate != null) {
            predicates.add(cb.greaterThanOrEqualTo(
                    transaction.get("transactionDate"), startDate));
        }

        if (endDate != null) {
            predicates.add(cb.lessThanOrEqualTo(
                    transaction.get("transactionDate"), endDate));
        }

        cq.where(predicates.toArray(new Predicate[0]));
        cq.orderBy(cb.desc(transaction.get("transactionDate")));

        return entityManager.createQuery(cq).getResultList();
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void updateStatus(Long transactionId, TransactionStatus newStatus) {
        logger.debug("Updating transaction {} status to {}", transactionId, newStatus);
        Transaction transaction = entityManager.find(Transaction.class, transactionId);
        if (transaction != null) {
            transaction.setStatus(newStatus);
            entityManager.merge(transaction);
            entityManager.flush();
        }
    }
}