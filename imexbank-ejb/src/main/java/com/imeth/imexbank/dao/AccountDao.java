package com.imeth.imexbank.dao;

import com.imeth.imexbank.common.enums.AccountType;
import com.imeth.imexbank.entities.Account;
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
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class AccountDao {

    private static final Logger logger = LoggerFactory.getLogger(AccountDao.class);

    @PersistenceContext(unitName = "ImexBankPU")
    private EntityManager entityManager;

    public Account create(Account account) {
        logger.debug("Creating new account: {}", account.getAccountNumber());
        entityManager.persist(account);
        entityManager.flush();
        return account;
    }

    public Account update(Account account) {
        logger.debug("Updating account: {}", account.getAccountNumber());
        Account merged = entityManager.merge(account);
        entityManager.flush();
        return merged;
    }

    public Optional<Account> findById(Long id) {
        logger.debug("Finding account by ID: {}", id);
        Account account = entityManager.find(Account.class, id);
        return Optional.ofNullable(account);
    }

    public Optional<Account> findByAccountNumber(String accountNumber) {
        logger.debug("Finding account by number: {}", accountNumber);
        try {
            TypedQuery<Account> query = entityManager.createNamedQuery(
                    "Account.findByAccountNumber", Account.class);
            query.setParameter("accountNumber", accountNumber);
            return Optional.of(query.getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    public List<Account> findByCustomerId(Long customerId) {
        logger.debug("Finding accounts by customer ID: {}", customerId);
        TypedQuery<Account> query = entityManager.createNamedQuery(
                "Account.findByCustomer", Account.class);
        query.setParameter("customerId", customerId);
        return query.getResultList();
    }

    public List<Account> findByAccountType(AccountType accountType) {
        logger.debug("Finding accounts by type: {}", accountType);
        TypedQuery<Account> query = entityManager.createNamedQuery(
                "Account.findByType", Account.class);
        query.setParameter("accountType", accountType);
        return query.getResultList();
    }

    public List<Account> findActiveAccounts() {
        logger.debug("Finding all active accounts");
        TypedQuery<Account> query = entityManager.createQuery(
                "SELECT a FROM Account a WHERE a.isActive = true", Account.class);
        return query.getResultList();
    }

    public List<Account> findAccountsForInterestCalculation(AccountType accountType) {
        logger.debug("Finding accounts for interest calculation: {}", accountType);
        TypedQuery<Account> query = entityManager.createQuery(
                "SELECT a FROM Account a WHERE a.accountType = :accountType " +
                        "AND a.isActive = true AND a.balance > 0", Account.class);
        query.setParameter("accountType", accountType);
        return query.getResultList();
    }

    public Long countActiveAccounts() {
        TypedQuery<Long> query = entityManager.createQuery(
                "SELECT COUNT(a) FROM Account a WHERE a.isActive = true", Long.class);
        return query.getSingleResult();
    }

    public BigDecimal getTotalBalance() {
        TypedQuery<BigDecimal> query = entityManager.createQuery(
                "SELECT SUM(a.balance) FROM Account a WHERE a.isActive = true",
                BigDecimal.class);
        BigDecimal result = query.getSingleResult();
        return result != null ? result : BigDecimal.ZERO;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void updateBalance(Long accountId, BigDecimal newBalance) {
        logger.debug("Updating balance for account ID: {} to {}", accountId, newBalance);
        Account account = entityManager.find(Account.class, accountId);
        if (account != null) {
            account.setBalance(newBalance);
            account.setLastTransactionDate(LocalDateTime.now());
            entityManager.merge(account);
            entityManager.flush();
        }
    }

    public void delete(Account account) {
        logger.warn("Deleting account: {}", account.getAccountNumber());
        Account managed = entityManager.merge(account);
        entityManager.remove(managed);
        entityManager.flush();
    }
}