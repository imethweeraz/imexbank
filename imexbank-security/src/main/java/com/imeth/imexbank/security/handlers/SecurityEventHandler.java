package com.imeth.imexbank.security.handlers;

import com.imeth.imexbank.services.interfaces.SecurityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Listens for and responds to security-related application events.
 * This would typically be implemented using CDI Events.
 */
public class SecurityEventHandler {

    private static final Logger logger = LoggerFactory.getLogger(SecurityEventHandler.class);

    private final SecurityService securityService;

    public SecurityEventHandler(SecurityService securityService) {
        this.securityService = securityService;
    }

    public void handleFailedLogin(String username, String ipAddress) {
        logger.warn("Failed login event for user: {} from IP: {}", username, ipAddress);
        securityService.recordLoginAttempt(username, false, ipAddress);
    }

    public void handleSuccessfulLogin(String username, String ipAddress) {
        logger.info("Successful login event for user: {} from IP: {}", username, ipAddress);
        securityService.recordLoginAttempt(username, true, ipAddress);
    }
}