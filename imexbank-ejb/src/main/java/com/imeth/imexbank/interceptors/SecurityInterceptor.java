package com.imeth.imexbank.interceptors;

import com.imeth.imexbank.common.exceptions.SecurityException;
import com.imeth.imexbank.security.annotations.SecureMethod;
import com.imeth.imexbank.security.utils.SecurityUtils;
import jakarta.annotation.Priority;
import jakarta.ejb.EJB;
import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.Interceptor;
import jakarta.interceptor.InvocationContext;
import java.lang.reflect.Method;

@SecureMethod
@Interceptor
@Priority(Interceptor.Priority.APPLICATION)
public class SecurityInterceptor {

    @EJB
    private SecurityUtils securityUtils;

    @AroundInvoke
    public Object authorize(InvocationContext context) throws Exception {
        Method method = context.getMethod();
        SecureMethod annotation = method.getAnnotation(SecureMethod.class);
        
        if (annotation != null) {
            String[] requiredRoles = annotation.roles();
            if (requiredRoles.length > 0) {
                if (!securityUtils.hasAnyRole(requiredRoles)) {
                    String errorMessage = "User does not have required role for method: " + method.getName();
                    throw new SecurityException(errorMessage);
                }
            }
            
            String permission = annotation.permission();
            if (!permission.isEmpty()) {
                if (!securityUtils.hasPermission(permission)) {
                    String errorMessage = "User does not have permission: " + permission;
                    throw new SecurityException(errorMessage);
                }
            }
        }
        
        return context.proceed();
    }
}
