package com.imeth.imexbank.common.exceptions;

import jakarta.ejb.ApplicationException;

@ApplicationException(rollback = false)
public class SecurityException extends BankingException {

    private static final long serialVersionUID = 1L;

    private String username;
    private String operation;
    private String ipAddress;

    public SecurityException() {
        super("SECURITY_VIOLATION", "Security violation detected");
    }

    public SecurityException(String message) {
        super("SECURITY_VIOLATION", message);
    }

    public SecurityException(String username, String operation) {
        super("SECURITY_VIOLATION",
                String.format("Security violation: User %s attempted unauthorized operation: %s",
                        username, operation));
        this.username = username;
        this.operation = operation;
    }

    public SecurityException(String username, String operation, String ipAddress) {
        super("SECURITY_VIOLATION",
                String.format("Security violation: User %s from IP %s attempted unauthorized operation: %s",
                        username, ipAddress, operation));
        this.username = username;
        this.operation = operation;
        this.ipAddress = ipAddress;
    }

    public String getUsername() {
        return username;
    }

    public String getOperation() {
        return operation;
    }

    public String getIpAddress() {
        return ipAddress;
    }
}