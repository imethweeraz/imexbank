package com.imeth.imexbank.interceptors;

import com.imeth.imexbank.common.exceptions.SecurityException;
import jakarta.annotation.Resource;
import jakarta.ejb.SessionContext;
import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.InvocationContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SecurityInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(SecurityInterceptor.class);

    @Resource
    private SessionContext sessionContext;

    @AroundInvoke
    public Object checkSecurity(InvocationContext context) throws Exception {
        String methodName = context.getMethod().getName();
        String username = sessionContext.getCallerPrincipal().getName();

        logger.debug("Security check for user {} invoking method {}", username, methodName);

        // Check if user is authenticated
        if (username == null || "anonymous".equals(username)) {
            throw new SecurityException("User not authenticated");
        }

        // Additional security checks can be implemented here
        // For example, checking specific permissions based on method annotations

        return context.proceed();
    }
}