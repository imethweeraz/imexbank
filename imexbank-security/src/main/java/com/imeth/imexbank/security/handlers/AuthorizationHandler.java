package com.imeth.imexbank.security.handlers;

import com.imeth.imexbank.common.enums.UserRole;
import com.imeth.imexbank.services.interfaces.SecurityService;

/**
 * Handles authorization-related logic, such as checking user roles.
 */
public class AuthorizationHandler {

    private final SecurityService securityService;

    public AuthorizationHandler(SecurityService securityService) {
        this.securityService = securityService;
    }

    public boolean hasRole(String username, UserRole role) {
        return securityService.hasRole(username, role);
    }
}