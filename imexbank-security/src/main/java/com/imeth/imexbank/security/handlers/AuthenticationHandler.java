package com.imeth.imexbank.security.handlers;

import com.imeth.imexbank.services.interfaces.SecurityService;

/**
 * Handles authentication-related logic.
 * This could be injected or used by security filters or REST endpoints.
 */
public class AuthenticationHandler {

    private final SecurityService securityService;

    public AuthenticationHandler(SecurityService securityService) {
        this.securityService = securityService;
    }

    public boolean authenticateUser(String username, String password) {
        return securityService.authenticate(username, password);
    }
}