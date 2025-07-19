package com.imeth.imexbank.dao;

import com.imeth.imexbank.common.enums.AccountType;
import com.imeth.imexbank.entities.InterestRate;
import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class InterestRateDao {

    private static final Logger logger = LoggerFactory.getLogger(InterestRateDao.class);

    @PersistenceContext(unitName = "ImexBankPU")
    private EntityManager entityManager;

    public InterestRate create(InterestRate rate) {
        logger.debug("Creating new interest rate for {}", rate.getAccountType());
        entityManager.persist(rate);
        entityManager.flush();
        return rate;
    }

    public InterestRate update(InterestRate rate) {
        logger.debug("Updating interest rate: {}", rate.getId());
        InterestRate merged = entityManager.merge(rate);
        entityManager.flush();
        return merged;
    }

    public Optional<InterestRate> findById(Long id) {
        logger.debug("Finding interest rate by ID: {}", id);
        InterestRate rate = entityManager.find(InterestRate.class, id);
        return Optional.ofNullable(rate);
    }

    public List<InterestRate> findByAccountType(AccountType accountType) {
        logger.debug("Finding interest rates by account type: {}", accountType);
        TypedQuery<InterestRate> query = entityManager.createNamedQuery(
                "InterestRate.findByAccountType", InterestRate.class);
        query.setParameter("accountType", accountType);
        return query.getResultList();
    }

    public Optional<InterestRate> findEffectiveRate(AccountType accountType,
                                                    BigDecimal balance,
                                                    LocalDate date) {
        logger.debug("Finding effective rate for {} with balance {} on {}",
                accountType, balance, date);

        TypedQuery<InterestRate> query = entityManager.createQuery(
                "SELECT r FROM InterestRate r WHERE r.accountType = :accountType " +
                        "AND r.isActive = true " +
                        "AND r.effectiveDate <= :date " +
                        "AND (r.expiryDate IS NULL OR r.expiryDate > :date) " +
                        "AND (r.minimumBalance IS NULL OR r.minimumBalance <= :balance) " +
                        "AND (r.maximumBalance IS NULL OR r.maximumBalance >= :balance) " +
                        "ORDER BY r.effectiveDate DESC", InterestRate.class);

        query.setParameter("accountType", accountType);
        query.setParameter("date", date);
        query.setParameter("balance", balance);
        query.setMaxResults(1);

        try {
            return Optional.of(query.getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }
}