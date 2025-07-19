package com.imeth.imexbank.interceptors;

import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.InvocationContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

public class LoggingInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(LoggingInterceptor.class);

    @AroundInvoke
    public Object logMethodInvocation(InvocationContext context) throws Exception {
        String className = context.getTarget().getClass().getSimpleName();
        String methodName = context.getMethod().getName();
        Object[] parameters = context.getParameters();

        logger.debug("Entering {}.{} with parameters: {}",
                className, methodName, Arrays.toString(parameters));

        long startTime = System.currentTimeMillis();

        try {
            Object result = context.proceed();

            long duration = System.currentTimeMillis() - startTime;
            logger.debug("Exiting {}.{} after {} ms", className, methodName, duration);

            return result;
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            logger.error("Exception in {}.{} after {} ms: {}",
                    className, methodName, duration, e.getMessage());
            throw e;
        }
    }
}