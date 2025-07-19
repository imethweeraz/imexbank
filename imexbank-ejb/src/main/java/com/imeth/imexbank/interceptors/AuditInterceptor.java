package com.imeth.imexbank.interceptors;

import com.imeth.imexbank.dao.AuditLogDao;
import jakarta.annotation.Resource;
import jakarta.ejb.EJB;
import jakarta.ejb.SessionContext;
import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.InvocationContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.Arrays;

public class AuditInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(AuditInterceptor.class);

    @EJB
    private AuditLogDao auditLogDao;

    @Resource
    private SessionContext sessionContext;

    @AroundInvoke
    public Object auditMethodInvocation(InvocationContext context) throws Exception {
        Method method = context.getMethod();
        String className = context.getTarget().getClass().getSimpleName();
        String methodName = method.getName();
        String username = sessionContext.getCallerPrincipal().getName();

        // Check if method should be audited
        if (shouldAudit(methodName)) {
            String action = className + "." + methodName;
            String parameters = Arrays.toString(context.getParameters());

            logger.debug("Auditing method call: {} by user {}", action, username);

            try {
                Object result = context.proceed();

                // Log successful execution
                auditLogDao.createAsync(className, null, action, username,
                        parameters, "SUCCESS");

                return result;
            } catch (Exception e) {
                // Log failed execution
                auditLogDao.createAsync(className, null, action, username,
                        parameters, "FAILED: " + e.getMessage());
                throw e;
            }
        } else {
            return context.proceed();
        }
    }

    private boolean shouldAudit(String methodName) {
        // Audit create, update, delete, and transfer operations
        return methodName.startsWith("create") ||
                methodName.startsWith("update") ||
                methodName.startsWith("delete") ||
                methodName.startsWith("transfer") ||
                methodName.startsWith("process") ||
                methodName.startsWith("activate") ||
                methodName.startsWith("deactivate");
    }
}