package com.imeth.imexbank.interceptors;

import jakarta.annotation.Resource;
import jakarta.ejb.SessionContext;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.InvocationContext;
import jakarta.transaction.Status;
import jakarta.transaction.UserTransaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;

public class TransactionInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(TransactionInterceptor.class);

    @Resource
    private SessionContext sessionContext;

    @Resource
    private UserTransaction userTransaction;

    @AroundInvoke
    public Object manageTransaction(InvocationContext context) throws Exception {
        Method method = context.getMethod();
        TransactionAttribute txAttribute = method.getAnnotation(TransactionAttribute.class);

        if (txAttribute != null &&
                txAttribute.value() == TransactionAttributeType.REQUIRES_NEW) {

            logger.debug("Starting new transaction for method: {}", method.getName());

            // Check if there's an existing transaction
            boolean existingTransaction = false;
            try {
                existingTransaction = userTransaction.getStatus() != Status.STATUS_NO_TRANSACTION;
            } catch (Exception e) {
                logger.error("Error checking transaction status", e);
            }

            if (existingTransaction) {
                logger.debug("Suspending existing transaction");
            }
        }

        try {
            Object result = context.proceed();
            logger.trace("Transaction completed successfully for method: {}",
                    method.getName());
            return result;
        } catch (Exception e) {
            logger.error("Transaction failed for method: {}", method.getName(), e);
            sessionContext.setRollbackOnly();
            throw e;
        }
    }
}