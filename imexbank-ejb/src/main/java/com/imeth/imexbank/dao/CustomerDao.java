package com.imeth.imexbank.dao;

import com.imeth.imexbank.entities.Customer;
import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class CustomerDao {

    private static final Logger logger = LoggerFactory.getLogger(CustomerDao.class);

    @PersistenceContext(unitName = "ImexBankPU")
    private EntityManager entityManager;

    public Customer create(Customer customer) {
        logger.debug("Creating new customer: {}", customer.getCustomerNumber());
        entityManager.persist(customer);
        entityManager.flush();
        return customer;
    }

    public Customer update(Customer customer) {
        logger.debug("Updating customer: {}", customer.getCustomerNumber());
        Customer merged = entityManager.merge(customer);
        entityManager.flush();
        return merged;
    }

    public Optional<Customer> findById(Long id) {
        logger.debug("Finding customer by ID: {}", id);
        Customer customer = entityManager.find(Customer.class, id);
        return Optional.ofNullable(customer);
    }

    public Optional<Customer> findByNic(String nicNumber) {
        logger.debug("Finding customer by NIC: {}", nicNumber);
        try {
            TypedQuery<Customer> query = entityManager.createNamedQuery(
                    "Customer.findByNic", Customer.class);
            query.setParameter("nicNumber", nicNumber);
            return Optional.of(query.getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    public Optional<Customer> findByEmail(String email) {
        logger.debug("Finding customer by email: {}", email);
        try {
            TypedQuery<Customer> query = entityManager.createNamedQuery(
                    "Customer.findByEmail", Customer.class);
            query.setParameter("email", email);
            return Optional.of(query.getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    public List<Customer> findActiveCustomers() {
        logger.debug("Finding all active customers");
        TypedQuery<Customer> query = entityManager.createNamedQuery(
                "Customer.findActive", Customer.class);
        return query.getResultList();
    }

    public List<Customer> searchCustomers(String searchTerm) {
        logger.debug("Searching customers with term: {}", searchTerm);
        TypedQuery<Customer> query = entityManager.createQuery(
                "SELECT c FROM Customer c WHERE " +
                        "LOWER(c.firstName) LIKE LOWER(:term) OR " +
                        "LOWER(c.lastName) LIKE LOWER(:term) OR " +
                        "LOWER(c.email) LIKE LOWER(:term) OR " +
                        "c.customerNumber LIKE :term", Customer.class);
        query.setParameter("term", "%" + searchTerm + "%");
        return query.getResultList();
    }

    public Long countActiveCustomers() {
        TypedQuery<Long> query = entityManager.createQuery(
                "SELECT COUNT(c) FROM Customer c WHERE c.isActive = true", Long.class);
        return query.getSingleResult();
    }

    public boolean existsByNic(String nicNumber) {
        TypedQuery<Long> query = entityManager.createQuery(
                "SELECT COUNT(c) FROM Customer c WHERE c.nicNumber = :nicNumber",
                Long.class);
        query.setParameter("nicNumber", nicNumber);
        return query.getSingleResult() > 0;
    }

    public boolean existsByEmail(String email) {
        TypedQuery<Long> query = entityManager.createQuery(
                "SELECT COUNT(c) FROM Customer c WHERE c.email = :email", Long.class);
        query.setParameter("email", email);
        return query.getSingleResult() > 0;
    }
}