package com.imeth.imexbank.security.utils;

import jakarta.ejb.EJBContext;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.security.Principal;

/**
 * General security-related utility methods.
 */
public final class SecurityUtils {

    private SecurityUtils() {}

    /**
     * Gets the current user's principal from the EJB security context.
     * @return The Principal of the logged-in user, or null if not available.
     */
    public static Principal getCurrentUserPrincipal() {
        try {
            InitialContext ctx = new InitialContext();
            EJBContext ejbContext = (EJBContext) ctx.lookup("java:comp/EJBContext");
            return ejbContext.getCallerPrincipal();
        } catch (NamingException e) {
            // This might fail if called outside of an EJB container context.
            return null;
        }
    }

    /**
     * Gets the current user's name from the EJB security context.
     * @return The username string, or null if not available.
     */
    public static String getCurrentUsername() {
        Principal principal = getCurrentUserPrincipal();
        return (principal != null) ? principal.getName() : null;
    }
}